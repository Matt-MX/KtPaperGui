package com.mattmx.ktgui.example

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.button
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.click.ClickTypes.DROP
import com.mattmx.ktgui.click.ClickTypes.LEFT
import com.mattmx.ktgui.command.RegisteredPaperCommand
import com.mattmx.ktgui.command.arg.div
import com.mattmx.ktgui.command.arg.player
import com.mattmx.ktgui.command.command
import com.mattmx.ktgui.command.register
import com.mattmx.ktgui.command.runs
import com.mattmx.ktgui.impl.PacketGuiInventoryScreen
import com.mattmx.ktgui.onEvent
import com.mattmx.ktgui.onEventByPlayer
import com.mattmx.ktgui.renderingGui
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.util.not
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import kotlin.time.Duration.Companion.milliseconds

fun JavaPlugin.createInventorySeeCommand(): RegisteredPaperCommand {
    val player by player()
    return command("invsee" / player) {

        runs<Player> {
            createTrackingGui(player.first()).open(sender)
        }

    }.register(this)
}

fun createTrackingGui(target: Player): PacketGuiInventoryScreen<*> {
    return renderingGui(
        !"Tracking ${target.name}'s inventory",
        GuiType.ofRows(6),
        refresh = 100.milliseconds
    ) {
        for ((index, item) in target.inventory.contents.withIndex()) {
            if (item == null) {
                remove(index)
                continue
            }

            button(item) {
                click(DROP) {
                    // TODO(matt): Add cursor support
                    target.inventory.setItem(index, null)
                    target.updateInventory()
                }
            } slot index
        }

        button(ItemTypes.CYAN_STAINED_GLASS_PANE) {
            named(!"<aqua>Hot Bar")
        } slots (0..<9).toList().filter { get(it) == null }

        button(ItemTypes.WHITE_STAINED_GLASS_PANE) {
            named(!"<aqua>Inventory")
        } slots (9..<27 + 9).toList().filter { get(it) == null }

        button(ItemTypes.SPECTRAL_ARROW) {
            named(!"<red>Close")
            click.handle(LEFT) {
                forcefullyClose(getPlayer())
            }
        } slot guiType.row(6).middle
    }.apply {
        onEvent<PlayerQuitEvent> { event ->
            if (event.player != target) return@onEvent

            getAllPlayersWatchingInstance<Player>()
                .forEach { player -> player.sendMessage(!"<red>${target.name} left the server.") }
            forcefullyCloseAll()
        }
    }
}