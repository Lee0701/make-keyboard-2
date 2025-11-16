package ee.oyatl.ime.make2

import android.view.KeyEvent
import ee.oyatl.ime.make2.ModifierKeyHandler.Listener
import ee.oyatl.ime.make2.ModifierKeyHandler.ModifierState

abstract class DoubleTapToLockModifierHandler(
    val doubleTapThreshold: Int,
    override val listener: Listener
): ModifierKeyHandler {
    // State of the modifier
    var state: ModifierState = ModifierState.Released
    // If the user is physically pressing the modifier
    var pressing: Boolean = false
    // The time when the modifier is last pressed
    var pressTime: Long = 0L
    // If any input has received while pressing is true
    var input: Boolean = false

    override fun onModifierDown(keyCode: Int) {
        // User is pressing the modifier
        pressing = true
        if(state == ModifierState.Released) {
            // If state is released, change to pressed state
            changeState(keyCode, ModifierState.Pressed)
        } else if(state == ModifierState.Pressed) {
            if(System.currentTimeMillis() - pressTime < doubleTapThreshold) {
                // If this is double-tap, change to locked state
                changeState(keyCode, ModifierState.Locked)
            } else {
                // If not double-tap, reset the state
                changeState(keyCode, ModifierState.Released)
            }
        } else if(state == ModifierState.Locked) {
            // If state is locked, release it
            changeState(keyCode, ModifierState.Released)
        }
    }

    override fun onModifierUp(keyCode: Int) {
        if(input && state == ModifierState.Pressed) {
            // If there were any input while the modifier key is pressed, release
            changeState(keyCode, ModifierState.Released)
        } else {
            // Set the press time
            pressTime = System.currentTimeMillis()
        }
        // User released the modifier
        pressing = false
        // Reset input flag
        input = false
    }

    override fun onNonModifierDown(codePoint: Int, keyCode: Int) {
        // If this input received while pressing, set the input flag
        if(pressing) input = true
    }

    override fun onNonModifierUp(codePoint: Int, keyCode: Int) {
        // Exceptionally don't reset the state on delete is pressed
        if(keyCode == KeyEvent.KEYCODE_DEL) return
        if(state != ModifierState.Locked) {
            // If state is not locked and modifier key is not being pressed,
            // Release the state after current input
            if(!pressing) changeState(keyCode, ModifierState.Released)
        }
    }

    private fun changeState(keyCode: Int, state: ModifierState) {
        // Change modifier state and notify callback
        this.state = state
        listener.onModifierStateChanged(keyCode, state)
    }
}