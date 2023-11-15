package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.test
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

open class GuiCycleButton : GuiButton<GuiCycleButton>() {
    var selected: Int = 0
        protected set
    private val states = mutableMapOf<String, ItemStack>()
    lateinit var changedCallback: (ButtonClickedEvent<GuiCycleButton>) -> Unit
        protected set
    val selectedValue: String?
        get() = states.keys.toList().getOrNull(selected)

    operator fun set(key: String, item: ItemStack): GuiCycleButton {
        this.states[key] = item
        return this
    }

    operator fun get(key: String) = states[key]

    fun nextValue(): Int {
        selected = if (selected + 1 >= states.size) 0 else selected++
        return selected
    }

    fun previous(): Int {
        selected = if (selected - 1 < 0) states.size - 1 else selected--
        return selected
    }

    fun setSelected(index: Int): Int {
        selected =
            if (index < 0) 0
            else if (index >= states.size) states.size - 1
            else index
        return selected
    }

    infix fun changed(block: ButtonClickedEvent<GuiCycleButton>.() -> Unit): GuiCycleButton {
        this.changedCallback = block
        return this
    }
}

fun main(parent: GuiScreen) {
    GuiCycleButton()
        .set("something", ItemStack(Material.STONE))
        .childOf(parent)
        .slot(1)
        .click {
            ClickType.LEFT {
                println("currently selected: ${button.selectedValue}")
            }
        }.changed {

        }

    val b = B()
    b.foo().bar()
}

open class A<T> {
    fun foo() = this as T
}

class B : A<B>() {
    fun bar() = this
}