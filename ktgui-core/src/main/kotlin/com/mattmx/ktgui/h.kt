package com.mattmx.ktgui

import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.screen.InventoryTypes
import com.mattmx.ktgui.screen.refresh
import com.mattmx.ktgui.util.not
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

fun createMultiPlatformGui(): GuiScreen<*, *> {
    val stone = Key.key("minecraft:stone")
    val diamond = Key.key("minecraft:diamond")

    return multiPlatformGui(!"Multi-Platform gui impl", GuiType.type(InventoryTypes.HOPPER)) {

        this[guiType.first] = multiPlatformButton(stone) {
            name = !"<white>A rock"
            lore += !"<gray>It's pretty useless."

            click(ClickTypes.LEFT) {
                getPlayer<Audience>().sendMessage(!"This is a working multi-platform gui!")
            }
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        refresh(1.seconds) { refresh ->
            this[guiType.last] = multiPlatformButton(diamond) {
                name = !"<blue>A shiny diamond!"
                lore {
                    +!"<gray>It's also useless alone..."
                    +Component.empty()
                    +!"<white>${formatter.format(LocalDateTime.now())}"
                }

                click(ClickTypes.LEFT) {
                    val player = getPlayer<Audience>()

                    if (refresh.isActive()) {
                        player.sendMessage(!"<red>Stopped!")
                        refresh.stop()
                    } else {
                        player.sendMessage(!"<green>Resumed!")
                        refresh.resume()
                    }
                }
            }
        }
    }
}