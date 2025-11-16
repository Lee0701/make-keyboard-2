package ee.oyatl.ime.make2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class KeyboardView(
    context: Context,
    attrs: AttributeSet?
): View(context, attrs) {
    val paint: Paint = Paint()

    var keyboard: Keyboard? = null
        set(v) {
            field = v
            // Redraw the view on keyboard changed
            invalidate()
        }

    var listener: KeyboardListener? = null

    private val pointers: MutableMap<Int, TouchPointer> = mutableMapOf()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Fill background
        canvas.drawColor(Color.LTGRAY)
        // Draw all keys
        keyboard?.keys?.forEach { key ->
            // Pressed key background color
            if(key.pressed) paint.color = Color.DKGRAY
            // Unpressed key background color
            else paint.color = Color.WHITE
            // Draw key background
            canvas.drawRect(key.rect, paint)

            // If the key has a text label, draw it
            if(key.label != null) {
                // Set label text color, size, and alignment
                paint.color = Color.BLACK
                paint.textSize = key.rect.height() / 3f
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
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val keyboard = keyboard ?: return
        // Set view width and height to the keyboard width and height
        setMeasuredDimension(keyboard.width, keyboard.height)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)
        // Get the current touch index
        val pointerIndex = event.actionIndex
        // Get touch position
        val x = event.getX(pointerIndex).toInt()
        val y = event.getY(pointerIndex).toInt()
        // Get unique ID for the touch pointer
        val id = event.getPointerId(pointerIndex)
        // Get a key under the touch pointer, if any
        val key = keyboard?.findKey(x, y)

        // TODO: Make the touch behaviour set modular, so that any type of touch keyboard could be implemented.
        // TODO: ex. Swipe input, Flick input, Flick combo
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                // Add the new pointer to the list
                val pointer = TouchPointer(x, y, key)
                pointers += id to pointer
                // If a key was found
                if(key != null) {
                    // Make it look pressed
                    key.pressed = true
                    // Send key down callback
                    listener?.onKeyDown(key.codePoint, key.keyCode)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // Find current pointer by ID, if exists
                val pointer = pointers[id] ?: return true
                // Update pointer position
                pointer.x = x
                pointer.y = y
            }
            MotionEvent.ACTION_UP -> {
                // Find current pointer if exists
                val pointer = pointers[id]
                val key = pointer?.key
                // If the pointer exists with an associated key on keyboard
                if(key != null) {
                    // Make the key look released
                    key.pressed = false
                    // Send key up callback
                    listener?.onKeyUp(key.codePoint, key.keyCode)
                }
                // Remove released pointer from the list
                pointers -= id
            }
            else -> return super.onTouchEvent(event)
        }
        // Redraw the keyboard
        invalidate()
        return true
    }

    class TouchPointer(
        var x: Int,
        var y: Int,
        var key: Keyboard.Key?
    )
}