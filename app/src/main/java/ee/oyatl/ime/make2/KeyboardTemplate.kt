package ee.oyatl.ime.make2

data class KeyboardTemplate(
    val rows: List<Row> = listOf()
) {
    data class Row(
        val items: List<Item> = listOf(),
        val height: Float = 1f
    )

    interface Item {
        val width: Float
    }

    data class Spacer(
        override val width: Float = 1f
    ): Item

    data class Key(
        val codePoint: Int = 0,
        val label: String? = codePoint.toChar().toString(),
        val keyCode: Int = 0,
        override val width: Float = 1f,
        val isModifier: Boolean = false
    ): Item
}
