package ee.oyatl.ime.make2

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.KeyEvent
import androidx.core.graphics.toRectF

class DefaultKeyboardStyle(
    val theme: Theme
): KeyboardStyle {

    val paint = Paint()
    val rect = Rect()

    override fun drawBackground(canvas: Canvas) {
        // Fill keyboard background
        canvas.drawColor(theme.keyboardBackground.toArgb())
    }

    override fun drawKey(canvas: Canvas, key: Keyboard.Key) {
        drawKeyBackground(canvas, key)
        drawKeyForeground(canvas, key)
    }

    private fun drawKeyBackground(canvas: Canvas, key: Keyboard.Key) {
        // Pressed key background color
        if(key.pressed) paint.color = theme.pressedKeyBackground.toArgb()
        // Functional key background color
        else if(isFunctionalKey(key)) paint.color = theme.functionalKeyBackground.toArgb()
        // Default unpressed key background color
        else paint.color = theme.alphabeticKeyBackground.toArgb()

        rect.set(key.rect)
        rect.inset(theme.horizontalGap, theme.verticalGap)
        // Key corner radius
        val radius = theme.keyRadius.toFloat()
        // Draw key background
        canvas.drawRoundRect(rect.toRectF(), radius, radius, paint)
    }

    private fun drawKeyForeground(canvas: Canvas, key: Keyboard.Key) {
        // If the key has a text label, draw it
        if(key.label != null) {
            // Set label text color by key type
            if(isFunctionalKey(key)) paint.color = theme.functionalKeyForeground.toArgb()
            else paint.color = theme.alphabeticKeyForeground.toArgb()

            // Set text size and align
            paint.textSize = theme.keyTextSize
            paint.textAlign = Paint.Align.CENTER

            // Set the text anchor
            // Anchor X on the key center
            val x = key.rect.centerX().toFloat()
            // Anchor Y on the key center,
            // offset by half text height
            val y = key.rect.centerY() - ((paint.descent() + paint.ascent()) / 2)
            // Draw key text label
            canvas.drawText(key.label, x, y, paint)
        }
    }

    private fun isFunctionalKey(key: Keyboard.Key): Boolean {
        when(key.keyCode) {
            KeyEvent.KEYCODE_SYM -> return true
            KeyEvent.KEYCODE_LANGUAGE_SWITCH -> return true
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> return true
            KeyEvent.KEYCODE_DEL -> return true
            KeyEvent.KEYCODE_ENTER -> return true
        }
        return false
    }

    data class Theme(
        val keyboardBackground: Color,
        val alphabeticKeyBackground: Color,
        val functionalKeyBackground: Color,
        val pressedKeyBackground: Color,
        val alphabeticKeyForeground: Color,
        val functionalKeyForeground: Color,
        val keyRadius: Int,
        val horizontalGap: Int,
        val verticalGap: Int,
        val keyTextSize: Float,
    )
}