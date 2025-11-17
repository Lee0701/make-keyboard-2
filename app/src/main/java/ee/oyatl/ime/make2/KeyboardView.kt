package ee.oyatl.ime.make2

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class KeyboardView(
    context: Context,
    attrs: AttributeSet?
): View(context, attrs), KeyboardMotionHandler.Listener {
    var keyboard: Keyboard? = null
        set(v) {
            field = v
            // Redraw the view on keyboard changed
            invalidate()
        }

    var motionHandler: KeyboardMotionHandler? = null
        set(v) {
            field = v
            v?.listener = this
        }

    var style: KeyboardStyle? = null

    var listener: KeyboardListener? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        style?.drawBackground(canvas)
        // Draw all key backgrounds
        keyboard?.keys?.forEach { key ->
            style?.drawKeyBackground(canvas, key)
        }
        // Draw all key foregrounds
        keyboard?.keys?.forEach { key ->
            style?.drawKeyForeground(canvas, key)
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
        val pointerId = event.getPointerId(pointerIndex)

        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                motionHandler?.onTouchDown(pointerId, x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                motionHandler?.onTouchMove(pointerId, x, y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                motionHandler?.onTouchUp(pointerId, x, y)
            }
            else -> return super.onTouchEvent(event)
        }
        // Redraw the keyboard
        invalidate()
        return true
    }

    override val keyboardListener: KeyboardListener?
        get() = listener

    override fun getKeyAt(x: Int, y: Int): Keyboard.Key? = keyboard?.findKeyAt(x, y)

}