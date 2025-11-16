package ee.oyatl.ime.make2

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View

class IMEService: InputMethodService() {
    lateinit var keyboardView: KeyboardView
    lateinit var keyboards: List<Keyboard>

    private val layout = listOf(
        "qwertyuiop",
        "asdfghjkl",
        "zxcvbnm"
    )

    private fun buildTemplate(layout: List<String>): KeyboardTemplate {
        val rows = layout.map { row -> row.map { KeyboardTemplate.Key(it.code) }.toTypedArray() }
        return KeyboardTemplate(listOf(
            KeyboardTemplate.Row(listOf(
                *rows[0]
            )),
            KeyboardTemplate.Row(listOf(
                KeyboardTemplate.Spacer(0.5f),
                *rows[1],
                KeyboardTemplate.Spacer(0.5f)
            )),
            KeyboardTemplate.Row(listOf(
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SHIFT_LEFT, width = 1.5f),
                *rows[2],
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_DEL, width = 1.5f)
            )),
            KeyboardTemplate.Row(listOf(
                KeyboardTemplate.Spacer(3f),
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SPACE, width = 4f),
                KeyboardTemplate.Spacer(3f)
            ))
        ))
    }

    private val keyboardTheme: DefaultKeyboardStyle.Theme = DefaultKeyboardStyle.Theme(
        keyboardBackground = 0xffe8e8e8.toInt(),
        alphabeticKeyBackground = Color.WHITE,
        functionalKeyBackground = 0xffd0d0d0.toInt(),
        pressedKeyBackground = 0xffc0c0c0.toInt(),
        alphabeticKeyForeground = Color.BLACK,
        functionalKeyForeground = Color.BLACK,
        keyRadius = 20,
        horizontalGap = 5,
        verticalGap = 15,
        keyTextSize = 60f
    )

    private val keyboardListener: KeyboardListener = object: KeyboardListener {
        override fun onKeyDown(codePoint: Int, keyCode: Int) {
            shiftHandler.onKeyDown(codePoint, keyCode)
        }

        override fun onKeyUp(codePoint: Int, keyCode: Int) {
            val ic = currentInputConnection ?: return
            shiftHandler.onKeyUp(codePoint, keyCode)
            if(codePoint != 0) {
                ic.commitText(codePoint.toChar().toString(), 1)
                return
            }
            when(keyCode) {
                KeyEvent.KEYCODE_DEL -> {
                    ic.deleteSurroundingText(1, 0)
                }
                KeyEvent.KEYCODE_SPACE -> {
                    ic.commitText(" ", 1)
                }
                KeyEvent.KEYCODE_ENTER -> {
                    sendDefaultEditorAction(true)
                }
            }
        }
    }

    private val modifierStateListener: ModifierKeyHandler.Listener = object: ModifierKeyHandler.Listener {
        override fun onModifierStateChanged(
            keyCode: Int,
            state: ModifierKeyHandler.ModifierState
        ) {
            when (state) {
                ModifierKeyHandler.ModifierState.Released -> {
                    keyboardView.keyboard = keyboards[0]
                }
                ModifierKeyHandler.ModifierState.Pressed -> {
                    keyboardView.keyboard = keyboards[1]
                }
                ModifierKeyHandler.ModifierState.Locked -> {
                    keyboardView.keyboard = keyboards[1]
                }
            }
        }
    }

    private val shiftHandler = object: DoubleTapToLockModifierHandler(300, modifierStateListener) {
        override fun isModifier(codePoint: Int, keyCode: Int): Boolean {
            return keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
        }
    }

    override fun onCreate() {
        super.onCreate()
        val params = Keyboard.Params(
            width = resources.displayMetrics.widthPixels,
            height = 700
        )
        val keyboards = listOf(
            Keyboard(buildTemplate(layout), params),
            Keyboard(buildTemplate(layout.map { it.uppercase() }), params)
        )
        this.keyboards = keyboards
        keyboardView = KeyboardView(this, null)
        keyboardView.keyboard = keyboards[0]
        keyboardView.motionHandler = SweepMotionHandler()
        keyboardView.style = DefaultKeyboardStyle(keyboardTheme)
        keyboardView.listener = keyboardListener
    }

    override fun onCreateInputView(): View {
        return keyboardView
    }
}