package ee.oyatl.ime.make2

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View

class IMEService: InputMethodService() {
    var keyboardView: KeyboardView? = null
    var keyboards: List<Keyboard> = listOf()

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
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SHIFT_LEFT, width = 1.5f, isModifier = true),
                *rows[2],
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_DEL, width = 1.5f, isModifier = true)
            )),
            KeyboardTemplate.Row(listOf(
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SYM, width = 1.5f, isModifier = true),
                KeyboardTemplate.Key(codePoint = ','.code, isModifier = true),
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_LANGUAGE_SWITCH),
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SPACE, width = 4f),
                KeyboardTemplate.Key(codePoint = '.'.code, isModifier = true),
                KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_ENTER, width = 1.5f, isModifier = true)
            ))
        ))
    }

    private val keyboardTheme: DefaultKeyboardStyle.Theme = DefaultKeyboardStyle.Theme(
        keyRadius = 20,
        horizontalGap = 5,
        verticalGap = 15,
        keyTextSize = 60f,
        keyboardBackground = 0xffe8e8e8.toInt(),
        alphabeticKeyBackground = Color.WHITE,
        functionalKeyBackground = 0xffd0d0d0.toInt(),
        pressedKeyBackground = 0xffc0c0c0.toInt(),
        alphabeticKeyForeground = Color.BLACK,
        functionalKeyForeground = Color.BLACK
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
            keyboardView?.keyboard = keyboards[state.ordinal]
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
            Keyboard(buildTemplate(layout.map { it.uppercase() }), params),
            Keyboard(buildTemplate(layout.map { it.uppercase() }), params)
        )
        keyboards[0].setShiftState(on = false, locked = false)
        keyboards[1].setShiftState(on = true, locked = false)
        keyboards[2].setShiftState(on = true, locked = true)
        this.keyboards = keyboards
    }

    override fun onCreateInputView(): View {
        val keyboardView = KeyboardView(this, null)
        keyboardView.keyboard = keyboards.getOrNull(0)
        keyboardView.motionHandler = SweepMotionHandler()
        keyboardView.style = DefaultKeyboardStyle(this, keyboardTheme)
        keyboardView.listener = keyboardListener
        this.keyboardView = keyboardView
        return keyboardView
    }
}