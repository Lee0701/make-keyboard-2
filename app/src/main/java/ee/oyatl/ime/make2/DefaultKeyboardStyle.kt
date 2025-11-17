package ee.oyatl.ime.make2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF

class DefaultKeyboardStyle(
    context: Context,
    val theme: Theme
): KeyboardStyle {

    private val paint = Paint()
    private val rect = Rect()

    private val keyIcons: Map<Int, Drawable?> = mapOf(
        KeyEvent.KEYCODE_SPACE to AppCompatResources.getDrawable(context, R.drawable.space_bar_24px),
        KeyEvent.KEYCODE_ENTER to AppCompatResources.getDrawable(context, R.drawable.keyboard_return_24px),
        KeyEvent.KEYCODE_DEL to AppCompatResources.getDrawable(context, R.drawable.backspace_24px),
        KeyEvent.KEYCODE_LANGUAGE_SWITCH to AppCompatResources.getDrawable(context, R.drawable.language_24px),
        KeyEvent.KEYCODE_SYM to AppCompatResources.getDrawable(context, R.drawable.keyboard_option_key_24px)
    )

    private val shiftKeyIcons: List<Drawable?> = listOf(
        AppCompatResources.getDrawable(context, R.drawable.shift_24px),
        AppCompatResources.getDrawable(context, R.drawable.shift_on_24px),
        AppCompatResources.getDrawable(context, R.drawable.shift_lock_24px)
    )

    override fun drawBackground(canvas: Canvas) {
        // Fill keyboard background
        canvas.drawColor(theme.keyboardBackground)
    }

    override fun drawKeyBackground(canvas: Canvas, key: Keyboard.Key) {
        // Pressed key background color
        if(key.pressed) paint.color = theme.pressedKeyBackground
        // Functional key background color
        else if(key.isModifier) paint.color = theme.functionalKeyBackground
        // Default unpressed key background color
        else paint.color = theme.alphabeticKeyBackground

        rect.set(key.rect)
        rect.inset(theme.horizontalGap, theme.verticalGap)
        // Key corner radius
        val radius = theme.keyRadius.toFloat()
        // Draw key background
        canvas.drawRoundRect(rect.toRectF(), radius, radius, paint)
    }

    override fun drawKeyForeground(canvas: Canvas, key: Keyboard.Key) {
        // Set foreground color by key type
        val color =
            if(key.isModifier) theme.functionalKeyForeground
            else theme.alphabeticKeyForeground

        // If the key has a text label, draw it
        if(key.label != null) {
            // Set text size and align
            paint.color = color
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
        val isShift = key.keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || key.keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
        val icon =
            if(isShift) shiftKeyIcons[if(key.locked) 2 else if(key.on) 1 else 0]
            else keyIcons[key.keyCode]
        if(icon != null) {
            DrawableCompat.setTint(icon, color)
            val bitmap = icon.toBitmap()
            val x = key.rect.centerX() - bitmap.width / 2
            val y = key.rect.centerY() - bitmap.height / 2
            canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), paint)
        }
    }

    data class Theme(
        val keyRadius: Int,
        val horizontalGap: Int,
        val verticalGap: Int,
        val keyTextSize: Float,
        val keyboardBackground: Int,
        val alphabeticKeyBackground: Int,
        val functionalKeyBackground: Int,
        val pressedKeyBackground: Int,
        val alphabeticKeyForeground: Int,
        val functionalKeyForeground: Int
    )
}