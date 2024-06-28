package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

open class GuiToggleButton(
    val enabledItem: ItemStack,
    val disabledItem: ItemStack = enabledItem
) : GuiButton<GuiToggleButton>() {
    private var state = false
    lateinit var changedCallback: (ButtonClickedEvent<GuiToggleButton>) -> Unit
        protected set

    init {
        this.item = if (state) enabledItem else disabledItem
    }

    fun changeWithClickType(clickType: ClickType) = apply {
        click {
            clickType {
                changeState()
                if (::changedCallback.isInitialized)
                    changedCallback.invoke(this)
            }
        }
    }

    fun changeState() {
        state = !state
        this.item = if (state) enabledItem else disabledItem
        update()
    }

    fun enabledOnDefault(state: Boolean): GuiToggleButton {
        this.state = state
        this.item = if (this.state) enabledItem else disabledItem
        return this
    }

    fun changed(cb: ButtonClickedEvent<GuiToggleButton>.() -> Unit): GuiToggleButton {
        changedCallback = cb
        return this
    }

    fun enabled(): Boolean {
        return state
    }

    override fun copy(parent: IGuiScreen): GuiToggleButton {
        val button = GuiToggleButton(enabledItem, disabledItem)
        button.enabledOnDefault(state)
        button.changedCallback = changedCallback
        button.parent = parent
        return button
    }
}