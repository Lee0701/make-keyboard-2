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
            invalidate()
        }

    var listener: KeyboardListener? = null

    private val pointers: MutableMap<Int, TouchPointer> = mutableMapOf()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.LTGRAY)
        keyboard?.keys?.forEach { key ->
            if(key.pressed) paint.color = Color.DKGRAY
            else paint.color = Color.WHITE
            canvas.drawRect(key.rect, paint)
            if(key.label != null) {
                paint.color = Color.BLACK
                paint.textSize = key.rect.height() / 3f
                paint.textAlign = Paint.Align.CENTER
                val x = key.rect.centerX().toFloat()
                val y = key.rect.centerY() - ((paint.descent() + paint.ascent()) / 2)
                canvas.drawText(key.label, x, y, paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val keyboard = keyboard ?: return
        setMeasuredDimension(keyboard.width, keyboard.height)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)
        val x = event.getX(event.actionIndex).toInt()
        val y = event.getY(event.actionIndex).toInt()
        val id = event.getPointerId(event.actionIndex)
        val key = keyboard?.findKey(x, y)
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pointer = TouchPointer(x, y, key)
                pointers += id to pointer
                if(key != null) {
                    key.pressed = true
                    listener?.onKeyDown(key.codePoint, key.keyCode)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val pointer = pointers[id] ?: return true
                pointer.x = x
                pointer.y = y
            }
            MotionEvent.ACTION_UP -> {
                val pointer = pointers[id]
                val key = pointer?.key
                if(key != null) {
                    key.pressed = false
                    listener?.onKeyUp(key.codePoint, key.keyCode)
                }
                pointers -= id
            }
            else -> return super.onTouchEvent(event)
        }
        invalidate()
        return true
    }

    class TouchPointer(
        var x: Int,
        var y: Int,
        var key: Keyboard.Key?
    )
}