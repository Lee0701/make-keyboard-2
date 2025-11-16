package ee.oyatl.ime.make2

interface KeyboardMotionHandler {
    var listener: Listener?

    fun onTouchDown(pointerId: Int, x: Int, y: Int)
    fun onTouchMove(pointerId: Int, x: Int, y: Int)
    fun onTouchUp(pointerId: Int, x: Int, y: Int)

    interface Listener {
        val keyboardListener: KeyboardListener?
        fun getKeyAt(x: Int, y: Int): Keyboard.Key?
    }
}