package com.mattmx.ktgui.creator

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.conversation.refactor.conversation
import com.mattmx.ktgui.conversation.refactor.getString
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.utils.legacy
import com.mattmx.ktgui.utils.not
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.entity.Player

class GuiDesignerButtonCustomizer(
    val parent: GuiDesigner,
    val button: GuiButton<*>
) : GuiScreen(!"Customize Button", 3) {

    init {
        button(Material.SPECTRAL_ARROW) {
            named(!"&dBack")
            leftClick {
                openParent(player)
            }
        } slot 22

        button(Material.NAME_TAG) {
            named(!"&dName")
            lore {
                add(!"&fCurrently: ${button.getItemStack()?.displayName()?.legacy()}")
            }
            leftClick {
                conversation<Player> {
                    getString {
                        message = !"&dInput a new item name"

                        runs {
                            result.ifPresent { result ->
                                this@GuiDesignerButtonCustomizer.button.named(!result)
                            }
                            open(player)
                        }
                    }
                    exitOn = "cancel"
                    exit {
                        open(player)
                    }
                } begin player
            }
        } slot 10

        button(Material.MOJANG_BANNER_PATTERN) {
            named(!"&dLore")
            lore {
                val isEmpty = button.getItemStack()?.lore()?.isEmpty() ?: true
                add(!"&fCurrently: ${if (isEmpty) "&7*None*" else ""}")

                button.getItemStack()?.lore()?.forEach { line -> add(line) }
            }
            leftClick {
                conversation<Player> {
                    getString {
                        matches { result.isPresent && result.get().equals("cancel", true) }
                        invalid {
                            result.ifPresent {
                                this@GuiDesignerButtonCustomizer.button.lore(!it)
                                this@GuiDesignerButtonCustomizer.parent.refresh()
                            }
                            player.sendMessage(!"&fType 'cancel' to stop.")
                            player.sendMessage(!"&fCurrently:")
                            button.getItemStack()?.lore()?.forEach { line -> player.sendMessage(line) }
                        }
                        runs {
                            open(player)
                        }
                    }
                } begin player
            }
        } slot 13

        button(Material.ENCHANTED_BOOK) {
            named(!"&dEnchantments")
            lore {
                val isEmpty = button.getItemStack()?.enchantments?.isEmpty() ?: true
                add(!"&fCurrently: ${if (isEmpty) "&7*None*" else ""}")

                button.item?.enchantments?.forEach { line ->
                    line.key.displayName(line.value).color(TextColor.color(0xFFFFFF))
                }
            }
        } slot 16
    }

    fun openParent(player: Player) {
        sync {
            if (player.getOpenGui() == this@GuiDesignerButtonCustomizer) {
                parent.open(player)
            }
        }
    }

}