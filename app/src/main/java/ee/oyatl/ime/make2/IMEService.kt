package ee.oyatl.ime.make2

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
            KeyboardTemplate.Spacer(1.5f),
            KeyboardTemplate.Key('z'.code),
            KeyboardTemplate.Key('x'.code),
            KeyboardTemplate.Key('c'.code),
            KeyboardTemplate.Key('v'.code),
            KeyboardTemplate.Key('b'.code),
            KeyboardTemplate.Key('n'.code),
            KeyboardTemplate.Key('m'.code),
            KeyboardTemplate.Spacer(1.5f)
        )),
        KeyboardTemplate.Row(listOf(
            KeyboardTemplate.Spacer(3f),
            KeyboardTemplate.Key(keyCode = KeyEvent.KEYCODE_SPACE, width = 4f),
            KeyboardTemplate.Spacer(3f)
        ))
    ))

    private val keyboardListener: KeyboardListener = object: KeyboardListener {
        override fun onKeyDown(codePoint: Int, keyCode: Int) {
        }

        override fun onKeyUp(codePoint: Int, keyCode: Int) {
            val ic = currentInputConnection ?: return
            if(codePoint != 0) {
                ic.commitText(codePoint.toChar().toString(), 1)
                return
            }
            when(keyCode) {
                KeyEvent.KEYCODE_SPACE -> {
                    ic.commitText(" ", 1)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val params = Keyboard.Params(
            width = resources.displayMetrics.widthPixels,
            height = 600
        )
        keyboardView = KeyboardView(this, null)
        keyboardView.keyboard = Keyboard(template, params)
        keyboardView.listener = keyboardListener
    }

    override fun onCreateInputView(): View {
        return keyboardView
    }
}