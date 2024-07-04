package com.mattmx.ktgui.components.screen

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.event.EventCallback
import com.mattmx.ktgui.event.ScrollHotbarEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import java.util.*

open class InventoryHotbarScreen : GuiScreen(Component.empty(), type = InventoryType.PLAYER) {
    val holding = hashMapOf<Int, (Player) -> Unit>()
    val dropItem = EventCallback<PlayerDropItemEvent>()
    val scroll = EventCallback<ScrollHotbarEvent>()
    var startingHeldSlot = Optional.empty<Int>()
        private set

    override fun open(player: Player) {
        TODO()
    }

    infix fun startingHeldSlot(slot: Int) = apply {
        this.startingHeldSlot = Optional.of(slot)
    }

    infix fun GuiButton<*>.holding(block: Player.() -> Unit) = apply {
        slots().forEach { slot -> holding[slot] = block }
    }

    infix fun GuiButton<*>.holdingSendActionBar(component: Component) = holding {
        sendActionBar(component)
    }

    infix fun GuiButton<*>.holdingSendTitle(title: Title) = holding { showTitle(title) }

    fun hotbarMiddle() = 4
    fun hotbarLast() = 8
}

fun hotbar(block: InventoryHotbarScreen.() -> Unit) =
    InventoryHotbarScreen().apply(block)