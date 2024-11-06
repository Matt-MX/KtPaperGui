package com.mattmx.ktgui

import com.mattmx.ktgui.components.button.named
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.title
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.dsl.refresh
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.pretty
import org.bukkit.Material
import java.time.Duration
import java.time.Instant

fun createMenu(): GuiScreen {
    return gui(3) {
        title { !"Balls" }

        val guiOpened = Instant.now()
        var x = 1

        val testButton = button(Material.RED_STAINED_GLASS) {
            named { !"Test ${x.pretty()}" }

            click.left {
                x++

                update()
            }

            click.drop {
                reply(!"&dmeow :3")
            }

            click.right {
                x--

                update()
            }

        } slot slots.center

        button(Material.COMPARATOR) {
            named(!"&9Something sexy")

            lore {
                +!"&fLore Test"
            }

            click.any {
                isCancelled = false
            }
        } slot slots.before(testButton) - 1

        refresh(20L) {
            button(Material.CLOCK) {
                named { !"&aOpen for ${Duration.between(guiOpened, Instant.now()).pretty()}" }
            } slot slots.after(testButton) + 1
        }

    }
}