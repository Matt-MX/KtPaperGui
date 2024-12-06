package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemRarity
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.google.inject.Inject
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.screen.GuiType
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.slf4j.Logger

@Plugin(
    id = "ktgui",
    version = "1.0.0",
    authors = ["MattMX"],
    dependencies = [
        Dependency(id = "packetevents", optional = false)
    ]
)
class VelocityKtGuiImpl @Inject constructor(
    val proxyServer: ProxyServer,
    val logger: Logger
) {
    private val manager = PacketEventsGuiManagerImpl(this, proxyServer)

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        manager.registerListeners()

        BrigadierCommand.literalArgumentBuilder("ktgui")
            .executes { invoc ->
                if (invoc.source !is Player) return@executes SINGLE_SUCCESS

                gui(Component.text("Test GUI"), GuiType.ofRows(3)) {

                    button(ItemTypes.DIAMOND_SWORD) {
                        named(Component.text("Custom Item").color(NamedTextColor.AQUA))

                        lore {
                            +Component.text("Meow :3").color(NamedTextColor.LIGHT_PURPLE)
                        }

                        component(ComponentTypes.RARITY, ItemRarity.EPIC)

                        click.handle(ClickTypes.allClickTypes) {
                            getPlayer<Player>().sendMessage(Component.text("Clicked!"))
                        }
                    } slot guiType.middle

                }

                SINGLE_SUCCESS
            }
    }

    fun getGuiManager() = manager
}