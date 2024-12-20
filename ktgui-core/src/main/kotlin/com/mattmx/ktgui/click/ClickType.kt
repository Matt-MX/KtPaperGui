package com.mattmx.ktgui.click

// https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Protocol#Click_Container:~:text=on%20the%20item.-,Click%20Container,-%5Bedit%20source
data class ClickType(
    val mode: Int,
    val button: Int,
    val slot: Int = 0
) {
    init {
        ClickTypes.registerType(this)
    }
}

object ClickTypes {
    val ALL_CLICK_TYPES = mutableListOf<ClickType>()
    val NUMBER_KEY_RANGE = (0..8)

    val LEFT = ClickType(0, 0)
    val LEFT_OUTSIDE = ClickType(0, 0, -999)
    val SHIFT_LEFT = ClickType(1, 0)

    val RIGHT = ClickType(0, 1)
    val RIGHT_OUTSIDE = ClickType(0, 1, -999)
    val SHIFT_RIGHT = ClickType(1, 0)

    val OFFHAND = ClickType(2, 40)
    val MIDDLE = ClickType(3, 2)

    val DROP = ClickType(4, 0)
    val DROP_ALL = ClickType(4, 1)

    val DRAG_START_LEFT = ClickType(5, 0, -999)
    val DRAG_START_RIGHT = ClickType(5, 4, -999)
    val DRAG_START_MIDDLE = ClickType(5, 8, -999)
    val DRAG_ADD_SLOT_LEFT = ClickType(5, 1)
    val DRAG_ADD_SLOT_RIGHT = ClickType(5, 5)
    val DRAG_ADD_SLOT_MIDDLE = ClickType(5, 9)
    val DRAG_END_LEFT = ClickType(5, 2, -999)
    val DRAG_END_RIGHT = ClickType(5, 6, -999)
    val DRAG_END_MIDDLE = ClickType(5, 10, -999)

    val DOUBLE_CLICK = ClickType(6, 0)
    val PICKUP_IN_REVERSE = ClickType(6, 1) // ???

    val ANY_LEFT = arrayOf(LEFT, LEFT_OUTSIDE, SHIFT_LEFT)
    val ANY_RIGHT = arrayOf(RIGHT, RIGHT_OUTSIDE, SHIFT_RIGHT)
    val ANY_SHIFT = arrayOf(SHIFT_LEFT, SHIFT_RIGHT)

    val ANY_NUMBER_KEY = NUMBER_KEY_RANGE.map { numberKey(it) }
    val ANY_DROP = arrayOf(DROP, DROP_ALL)

    val ANY_DRAG = arrayOf(
        DRAG_START_LEFT,
        DRAG_START_RIGHT,
        DRAG_START_MIDDLE,
        DRAG_ADD_SLOT_LEFT,
        DRAG_ADD_SLOT_RIGHT,
        DRAG_ADD_SLOT_MIDDLE,
        DRAG_END_LEFT,
        DRAG_END_RIGHT,
        DRAG_END_MIDDLE
    )

    fun numberKey(number: Int): ClickType {
        assert(number in NUMBER_KEY_RANGE) { "Number keys range from ${NUMBER_KEY_RANGE.first}-${NUMBER_KEY_RANGE.last}!" }

        return ClickType(2, number, 0)
    }

    fun registerType(clickType: ClickType) {
        ALL_CLICK_TYPES.add(clickType)
    }

    fun match(mode: Int, button: Int, slot: Int): ClickType {
        val searching = ClickType(mode, button, slot)
        return ALL_CLICK_TYPES.firstOrNull { it == searching } ?: LEFT
    }
}