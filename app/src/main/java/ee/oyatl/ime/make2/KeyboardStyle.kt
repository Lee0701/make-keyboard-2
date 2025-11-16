package ee.oyatl.ime.make2

import android.graphics.Canvas

interface KeyboardStyle {
    fun drawBackground(canvas: Canvas)
    fun drawKey(canvas: Canvas, key: Keyboard.Key)
}
