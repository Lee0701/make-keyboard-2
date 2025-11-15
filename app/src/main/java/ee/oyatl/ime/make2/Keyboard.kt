package ee.oyatl.ime.make2

import android.graphics.Rect

class Keyboard(
    template: KeyboardTemplate,
    params: Params
) {
    val width: Int = params.width
    val height: Int = params.height

    val keys: List<Key>
    val keycodeMap: Map<Int, Int>

    init {
        val keys = mutableListOf<Key>()
        val keycodeMap = mutableMapOf<Int, Int>()

        val unitHeight = params.height / template.rows.map { it.height }.sum()
        var x = 0
        var y = 0
        template.rows.forEach { row ->
            val height = (unitHeight * row.height).toInt()
            val unitWidth = params.width / row.items.map { it.width }.sum()
            x = 0
            row.items.forEach { item ->
                val width = (unitWidth * item.width).toInt()
                if(item is KeyboardTemplate.Key) {
                    val key = Key(
                        item.codePoint,
                        item.keyCode,
                        item.label,
                        Rect(x, y, x + width, y + height)
                    )
                    keys += key
                    if(item.keyCode != 0 && item.codePoint != 0) {
                        keycodeMap += item.keyCode to item.codePoint
                    }
                }
                x += width
            }
            y += height
        }

        this.keys = keys.toList()
        this.keycodeMap = keycodeMap.toMap()
    }

    fun findKey(x: Int, y: Int): Key? {
        return keys.find { it.rect.contains(x, y) }
    }

    class Key(
        val codePoint: Int,
        val keyCode: Int,
        val label: String?,
        val rect: Rect
    ) {
        var pressed: Boolean = false
    }

    data class Params(
        val width: Int,
        val height: Int
    )
}
