package ee.oyatl.ime.make2

interface KeyboardListener {
    fun onKeyDown(codePoint: Int, keyCode: Int)
    fun onKeyUp(codePoint: Int, keyCode: Int)
}