package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.GuiScreen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

open class GuiCycleButton : GuiButton<GuiCycleButton>() {
    var selected = 0
        set(value) {
            field =
                if (value < 0) states.size - 1
                else if (value >= states.size) 0
                else value
            update()
        }
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

    fun withDefaultClickEvents() = apply {
        click {
            ClickType.LEFT {
                selected++
                if (::changedCallback.isInitialized)
                    changedCallback.invoke(this)
            }
            ClickType.RIGHT {
                selected--
                if (::changedCallback.isInitialized)
                    changedCallback.invoke(this)
            }
        }
    }

    override fun getItemStack() = states[selectedValue]

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