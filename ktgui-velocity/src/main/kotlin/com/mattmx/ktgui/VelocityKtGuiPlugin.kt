package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.google.inject.Inject
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.command.*
import com.mattmx.ktgui.command.arg.div
import com.mattmx.ktgui.command.arg.string
import com.mattmx.ktgui.example.PlayerSettingsSchema
import com.mattmx.ktgui.example.createOptionsGui
import com.mattmx.ktgui.example.createStatefulGui
import com.mattmx.ktgui.impl.PacketEventsGuiManager
import com.mattmx.ktgui.impl.VelocityGuiManagerImpl
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.Slots
import com.mattmx.ktgui.util.not
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.slf4j.Logger
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

@Plugin(
    id = "ktgui",
    version = "1.0.0",
    authors = ["MattMX"],
    dependencies = [
        Dependency(id = "packetevents", optional = false)
    ]
)
class VelocityKtGuiPlugin @Inject constructor(
    val proxyServer: ProxyServer,
    val logger: Logger
) {
    private val manager = VelocityGuiManagerImpl(this, proxyServer)

    init {
        instance = this

        CommandManager.inject(proxyServer)
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        manager.withDefaultLocationTracker()
        manager.registerListeners()

        command("ktgui") {
            opensGui { createStatefulGui() }

            val username by string()
            sub("debug" / username) {
                runs<Player> {
                    proxyServer.getPlayer(username())
                        .ifPresent { player ->
                            val openGui = GuiManager.getInstance<PacketEventsGuiManager>().getActiveGui(player)
                            val named = openGui?.let { gui -> gui::class.java.simpleName } ?: "None"
                            reply(Component.text("${player.username}: $named"))
                        }
                }
            }
            sub("example") {

                sub("meow") {
                    runs<Player> {
                        val gui = GuiManager.getInstance()
                            .createPlatformGui(!"Non platform specific", GuiType.ofRows(3))

                        val button = GuiManager.getInstance()
                            .createPlatformButtonOfType(Key.key("minecraft:stone_sword"))
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

                        gui.openAsAny(source)
                    }
                }
                sub("multi-platform") {
                    opensGui { createStatefulGui() }
                }
                sub("refresh") {
                    opensGui {
                        val timeOpened = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        renderingGui(!"Refreshing", GuiType.ofRows(1)) {
                            updateOnModify(true)

                            val now = LocalDateTime.now()
                            val timeOpen = Duration.between(timeOpened, now)

                            title = !"Refreshing ${timeOpen.seconds}"

                            button(ItemTypes.CLOCK) {
                                named(!"<white>${now.format(formatter)}")
                                lore {
                                    +!"<gray>Open for ${timeOpen.seconds}s"

                                    +!"<dark_gray><i>Italic</i> :3"
                                }
                                click {
                                    ClickTypes.LEFT {
                                        val refresh = this@renderingGui
                                            .traits[RefreshBlock::class.java]
                                            .orElseThrow()

                                        if (refresh.isActive()) {
                                            refresh.stop()
                                        } else {
                                            refresh.resume()
                                        }
                                    }
                                } slot guiType.middle
                            }

                        }
                    }
                }

                sub("pages") {
                    opensGui {
                        val version = PacketEvents.getAPI().playerManager.getClientVersion(source)

                        val test =
                            (0..100).map { if (Random.nextBoolean()) ItemTypes.DIRT else ItemTypes.STONE }

                        var page = 0
                        gui(!"All Items", GuiType.ofRows(6)) {
                            visiblePagesOverride = Optional.of {
                                val size = guiType.getTotalSlots()
                                val start = size * page
                                val end = start + size
                                (start..<end).toList()
                            }

                            var slot = 0
                            for (item in test) {
                                val clientVersionItemId = item.getId(version)
                                val finalItemType = ItemTypes.getById(version, clientVersionItemId)
                                    ?: continue

                                button(finalItemType) {
                                    click.handle(ClickTypes.ALL_CLICK_TYPES) {
                                        reply(!"<white>Clicked ${finalItemType.name.namespace}")
                                    }
                                } slot slot
                                slot++

                                if (slot >= Slots.ofRow(6).first) {
                                    slot += 9
                                }
                            }

                            button(ItemTypes.ARROW) {
                                named(!"Previous")
                                click.handle(ClickTypes.LEFT) {
                                    page--
                                    refresh()
                                }
                            } slot Slots.ofRow(6).first

                            button(ItemTypes.ARROW) {
                                named(!"Next")
                                click.handle(ClickTypes.LEFT) {
                                    page++
                                    refresh()
                                }
                            } slot Slots.ofRow(6).last
                        }
                    }
                }
                sub("buttons") {
                    opensGui { createOptionsGui(PlayerSettingsSchema()) }
                }
            }
        }.register(this)
    }

    fun getGuiManager() = manager

    companion object {
        private lateinit var instance: VelocityKtGuiPlugin
        fun getInstance() = instance
    }
}