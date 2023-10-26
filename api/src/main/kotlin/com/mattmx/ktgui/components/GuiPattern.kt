package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class GuiPattern(
    private var pattern: String
) {
    private val items = hashMapOf<Char, IGuiButton>()
    var blankSpaceChar = '-'

    /**
     * Applies the generated gui pattern to a gui screen.
     *
     * @param guiScreen the gui we want to modify.
     */
    fun apply(guiScreen: GuiScreen) {
        // Can't effectively apply the Gui Pattern to a non-row gui
        if (guiScreen.type != null) return
        val map = build(guiScreen.rows)
        map.entries.forEach {
            it.key childOf guiScreen slots it.value
        }
    }

    /**
     * Mostly internal method to create a map of the gui buttons
     * we want to create.
     *
     * @param rows the number of rows the gui will have.
     * @return a compiled map of Buttons to their respective slots in the gui.
     */
    fun build(rows: Int) : Map<IGuiButton, ArrayList<Int>> {
        val map = mutableMapOf<IGuiButton, ArrayList<Int>>()
        val maxSlot = rows * 9
        trimPattern()
        val charArr = pattern.toCharArray()
        repeat(maxSlot) { slot ->
            val index = maxSlot - slot - 1
            val char = charArr.getOrNull(index) ?: return map
            if (char != blankSpaceChar) {
                val item = items[char] ?: GuiButton(Material.AIR)
                // Add the item to the list
                map[item]?.also { it.add(index) }
                    ?: run { map[item] = arrayListOf(index) }
            }
        }
        return map
    }

    fun setPattern(pattern: String) {
        this.pattern = pattern
    }

    fun getPattern() = pattern

    private fun trimPattern() {
        pattern = pattern.replace("\n", "")
            .replace("\r\n", "")
    }

    /**
     * Sets a slot to a specified [ItemStack].
     *
     * @param char target character specified in the [pattern]
     * @param item that the char will be set to
     */
    operator fun set(char: Char, item: ItemStack) = items.put(char, GuiButton(item = item))

    /**
     * Sets a slot to a specified [IGuiButton].
     *
     * @param char target character specified in the [pattern]
     * @param button that the char will be set to
     */
    operator fun set(char: Char, button: IGuiButton) = items.put(char, button)

    /**
     * Returns the gui button of a character
     *
     * @param char
     * @return gui button of a character, or null if it hasn't been set yet
     */
    operator fun get(char: Char) : IGuiButton? = items[char]
}

/**
 * Extension method to apply the gui pattern in the context of a [GuiScreen]
 *
 * @param pattern the pattern to apply
 */
fun GuiScreen.applyPattern(pattern: GuiPattern) {
    pattern.apply(this)
}