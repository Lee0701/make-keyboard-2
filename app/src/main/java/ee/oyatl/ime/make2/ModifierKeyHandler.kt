package ee.oyatl.ime.make2

import android.view.KeyEvent

abstract class ModifierKeyHandler(
    protected val listener: Listener
) {
    fun onKeyDown(codePoint: Int, keyCode: Int) {
        if(isModifier(codePoint, keyCode)) onModifierDown(keyCode)
        else onNonModifierDown(codePoint, keyCode)
    }

    fun onKeyUp(codePoint: Int, keyCode: Int) {
        if(isModifier(codePoint, keyCode)) onModifierUp(keyCode)
        else onNonModifierUp(codePoint, keyCode)
    }

    protected abstract fun isModifier(codePoint: Int, keyCode: Int): Boolean
    protected abstract fun onModifierDown(keyCode: Int)
    protected abstract fun onModifierUp(keyCode: Int)
    protected abstract fun onNonModifierDown(codePoint: Int, keyCode: Int)
    protected abstract fun onNonModifierUp(codePoint: Int, keyCode: Int)

    enum class ModifierState {
        Pressed, Released, Locked
    }

    interface Listener {
        fun onModifierStateChanged(keyCode: Int, state: ModifierState)
    }

    abstract class DoubleTapToLock(
        val doubleTapThreshold: Int,
        listener: Listener
    ): ModifierKeyHandler(listener) {
        var state: ModifierState = ModifierState.Released
        var pressing: Boolean = false
        var pressTime: Long = 0L
        var input: Boolean = false

        override fun onModifierDown(keyCode: Int) {
            pressing = true
            if(state == ModifierState.Released) {
                changeState(keyCode, ModifierState.Pressed)
            } else if(state == ModifierState.Pressed) {
                if(System.currentTimeMillis() - pressTime < doubleTapThreshold) {
                    changeState(keyCode, ModifierState.Locked)
                } else {
                    changeState(keyCode, ModifierState.Released)
                }
            } else if(state == ModifierState.Locked) {
                changeState(keyCode, ModifierState.Released)
            }
        }

        override fun onModifierUp(keyCode: Int) {
            if(input && state == ModifierState.Pressed) {
                changeState(keyCode, ModifierState.Released)
            } else {
                pressTime = System.currentTimeMillis()
            }
            pressing = false
            input = false
        }

        override fun onNonModifierDown(codePoint: Int, keyCode: Int) {
            if(pressing) input = true
        }

        override fun onNonModifierUp(codePoint: Int, keyCode: Int) {
            if(keyCode == KeyEvent.KEYCODE_DEL) return
            if(state != ModifierState.Locked) {
                if(!pressing) changeState(keyCode, ModifierState.Released)
            }
        }

        private fun changeState(keyCode: Int, state: ModifierState) {
            this.state = state
            listener.onModifierStateChanged(keyCode, state)
        }
    }
}