package ee.oyatl.ime.make2

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View

class IMEService: InputMethodService() {
    lateinit var keyboardView: KeyboardView

    private val template: KeyboardTemplate = KeyboardTemplate(listOf(
        KeyboardTemplate.Row(listOf(
            KeyboardTemplate.Key('q'.code),
            KeyboardTemplate.Key('w'.code),
            KeyboardTemplate.Key('e'.code),
            KeyboardTemplate.Key('r'.code),
            KeyboardTemplate.Key('t'.code),
            KeyboardTemplate.Key('y'.code),
            KeyboardTemplate.Key('u'.code),
            KeyboardTemplate.Key('i'.code),
            KeyboardTemplate.Key('o'.code),
            KeyboardTemplate.Key('p'.code)
        )),
        KeyboardTemplate.Row(listOf(
            KeyboardTemplate.Spacer(0.5f),
            KeyboardTemplate.Key('a'.code),
            KeyboardTemplate.Key('s'.code),
            KeyboardTemplate.Key('d'.code),
            KeyboardTemplate.Key('f'.code),
            KeyboardTemplate.Key('g'.code),
            KeyboardTemplate.Key('h'.code),
            KeyboardTemplate.Key('j'.code),
            KeyboardTemplate.Key('k'.code),
            KeyboardTemplate.Key('l'.code),
            KeyboardTemplate.Spacer(0.5f)
        )),
        KeyboardTemplate.Row(listOf(
            KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SHIFT_LEFT, width = 1.5f),
            KeyboardTemplate.Key('z'.code),
            KeyboardTemplate.Key('x'.code),
            KeyboardTemplate.Key('c'.code),
            KeyboardTemplate.Key('v'.code),
            KeyboardTemplate.Key('b'.code),
            KeyboardTemplate.Key('n'.code),
            KeyboardTemplate.Key('m'.code),
            KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_DEL, width = 1.5f)
        )),
        KeyboardTemplate.Row(listOf(
            KeyboardTemplate.Spacer(3f),
            KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SPACE, width = 4f),
            KeyboardTemplate.Spacer(3f)
        ))
    ))

    private val keyboardTheme: DefaultKeyboardStyle.Theme = DefaultKeyboardStyle.Theme(
        keyboardBackground = Color.valueOf(0xffe8e8e8.toInt()),
        alphabeticKeyBackground = Color.valueOf(Color.WHITE),
        functionalKeyBackground = Color.valueOf(0xffd0d0d0.toInt()),
        pressedKeyBackground = Color.valueOf(0xffc0c0c0.toInt()),
        alphabeticKeyForeground = Color.valueOf(Color.BLACK),
        functionalKeyForeground = Color.valueOf(Color.BLACK),
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
            if(state == ModifierKeyHandler.ModifierState.Released) {
                keyboardView.style = DefaultKeyboardStyle(keyboardTheme)
            } else if(state == ModifierKeyHandler.ModifierState.Pressed) {
                keyboardView.style = DefaultKeyboardStyle(keyboardTheme.copy(keyboardBackground = Color.valueOf(Color.DKGRAY)))
            } else {
                keyboardView.style = DefaultKeyboardStyle(keyboardTheme.copy(keyboardBackground = Color.valueOf(Color.BLACK)))
            }
        }
    }

    private val shiftHandler = object: ModifierKeyHandler.DoubleTapToLock(300, modifierStateListener) {
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
        keyboardView = KeyboardView(this, null)
        keyboardView.keyboard = Keyboard(template, params)
        keyboardView.style = DefaultKeyboardStyle(keyboardTheme)
        keyboardView.listener = keyboardListener
    }

    override fun onCreateInputView(): View {
        return keyboardView
    }
}