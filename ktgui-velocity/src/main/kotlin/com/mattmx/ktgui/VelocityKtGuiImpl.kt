package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemRarity
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.util.Dummy
import com.google.inject.Inject
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.Slots
import com.mattmx.ktgui.screen.refresh
import com.mattmx.ktgui.util.not
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import org.slf4j.Logger
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

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

        val node = BrigadierCommand.literalArgumentBuilder("ktgui")
            .executes { invoc ->
                if (invoc.source !is Player) return@executes SINGLE_SUCCESS

                val g = gui(Component.text("Test GUI"), GuiType.ofRows(3)) {

                    button(ItemTypes.DIAMOND_SWORD) {
                        named(!"<aqua>Custom Item")

                        lore {
                            +!"<light_purple>Meow :3"
                        }

                        component(ComponentTypes.RARITY, ItemRarity.EPIC)
                        component(ComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Dummy.DUMMY)

                        click.handle(ClickTypes.LEFT) {
                            getPlayer<Player>().sendMessage(Component.text("Clicked!"))
                        }
                    } slot 0

                }
                g.open(invoc.source)

                SINGLE_SUCCESS
            }
            .then(
                BrigadierCommand.literalArgumentBuilder("debug")
                    .then(BrigadierCommand.requiredArgumentBuilder("username", StringArgumentType.word())
                        .executes { invoc ->
                            proxyServer.getPlayer(invoc.getArgument("username", String::class.java))
                                .ifPresent { player ->
                                    val openGui =
                                        GuiManager.getInstance<PacketEventsGuiManager>().getActiveGui(player)
                                    val named = openGui?.let { gui -> gui::class.java.simpleName } ?: "None"
                                    invoc.source.sendMessage(Component.text("${player.username}: $named"))
                                }

                            SINGLE_SUCCESS
                        })
            )
            .then(BrigadierCommand.literalArgumentBuilder("meow")
                .executes { invoc ->
                    val gui = GuiManager.getInstance()
                        .createPlatformGui(!"Non platform specific", GuiType.ofRows(3))

                    val button = GuiManager.getInstance()
                        .createPlatformButton(ItemTypes.STONE_SWORD)
                        .named(!"<gray>Item Name")
                        .lore {
                            +Component.empty()
                            +!"<dark_gray>Lore"
                            +Component.empty()
                        }
                        .click {
                            ClickTypes.LEFT {
                                getPlayer<Player>().sendMessage(!"Clicked!")
                            }
                        }
                    gui[Slots.ofRow(2).middle] = button

                    gui.openAsAny(invoc.source)

                    SINGLE_SUCCESS
                })
            .then(BrigadierCommand.literalArgumentBuilder("refresh")
                .executes { invoc ->
                    val player = invoc as? Player ?: return@executes SINGLE_SUCCESS

                    val timeOpen = LocalDate.now()
                    val formatter = DateTimeFormatter.ISO_DATE
                    gui(!"Refreshing", GuiType.ofRows(1)) {
                        refresh(20.seconds) {
                            button(ItemTypes.CLOCK) {
                                named(!"&f${LocalDate.now().format(formatter)}")
                                lore {
                                    +!"&7Open for ${Duration.between(timeOpen, LocalDate.now()).seconds}s"
                                }
                            }
                        }
                    }

                    SINGLE_SUCCESS
                })

        proxyServer.commandManager.register(
            proxyServer.commandManager
                .metaBuilder("ktgui")
                .plugin(this)
                .build(),
            BrigadierCommand(node)
        )
    }

    fun getGuiManager() = manager
}