package ee.oyatl.ime.make2

import android.view.KeyEvent

interface ModifierKeyHandler {
    val listener: Listener

    fun onKeyDown(codePoint: Int, keyCode: Int) {
        if(isModifier(codePoint, keyCode)) onModifierDown(keyCode)
        else onNonModifierDown(codePoint, keyCode)
    }

    fun onKeyUp(codePoint: Int, keyCode: Int) {
        if(isModifier(codePoint, keyCode)) onModifierUp(keyCode)
        else onNonModifierUp(codePoint, keyCode)
    }

    fun isModifier(codePoint: Int, keyCode: Int): Boolean
    fun onModifierDown(keyCode: Int)
    fun onModifierUp(keyCode: Int)
    fun onNonModifierDown(codePoint: Int, keyCode: Int)
    fun onNonModifierUp(codePoint: Int, keyCode: Int)

    enum class ModifierState {
        Released, Pressed, Locked
    }

    interface Listener {
        fun onModifierStateChanged(keyCode: Int, state: ModifierState)
    }
}