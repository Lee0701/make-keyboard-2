package ee.oyatl.ime.make2

import android.graphics.Canvas

interface KeyboardStyle {
    fun drawBackground(canvas: Canvas)
    fun drawKeyBackground(canvas: Canvas, key: Keyboard.Key)
    fun drawKeyForeground(canvas: Canvas, key: Keyboard.Key)
}
