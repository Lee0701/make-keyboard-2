package ee.oyatl.ime.make2

import ee.oyatl.ime.make2.KeyboardMotionHandler.Listener

class SweepMotionHandler: KeyboardMotionHandler {
    override var listener: Listener? = null

    private val pointers: MutableMap<Int, TouchPointer> = mutableMapOf()

    override fun onTouchDown(pointerId: Int, x: Int, y: Int) {
        // Find key at position
        val key = listener?.getKeyAt(x, y)
        // Add the new pointer to the list
        val pointer = TouchPointer(x, y, key)
        pointers += pointerId to pointer
        // If a key was found
        if(key != null) {
            // Make it look pressed
            key.pressed = true
            // Send key down callback
            listener?.keyboardListener?.onKeyDown(key.codePoint, key.keyCode)
        }
    }

    override fun onTouchMove(pointerId: Int, x: Int, y: Int) {
        // Find current pointer by ID, if exists
        val pointer = pointers[pointerId] ?: return
        // Find a key at the pointer's previous position
        val oldKey = listener?.getKeyAt(pointer.x, pointer.y)
        // Find a key at the current position
        val newKey = listener?.getKeyAt(x, y)
        // Update pointer position
        pointer.x = x
        pointer.y = y
        pointer.key = newKey
        // Unpress the old key, press the new
        oldKey?.pressed = false
        newKey?.pressed = true
    }

    override fun onTouchUp(pointerId: Int, x: Int, y: Int) {
        // Find current pointer if exists
        val pointer = pointers[pointerId]
        val key = pointer?.key
        // If the pointer exists with an associated key on keyboard
        if(key != null) {
            // Make the key look released
            key.pressed = false
            // Send key up callback
            listener?.keyboardListener?.onKeyUp(key.codePoint, key.keyCode)
        }
        // Remove released pointer from the list
        pointers -= pointerId
    }

    class TouchPointer(
        var x: Int,
        var y: Int,
        var key: Keyboard.Key?
    )
}