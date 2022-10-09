package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.conversation.conversation
import org.bukkit.Material
import org.bukkit.conversations.PlayerNamePrompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ConversationGuiExample(private val plugin: JavaPlugin) : GuiScreen("No GUI Name set!", 1) {
    init {
        GuiButton()
            .click {
                left = { e ->
                    forceClose(e.whoClicked as Player)
                    conversation(plugin) {
                        stringPrompt("What's your fave fruit") { c, i ->
                            if (i == "oranges") {
                                c.forWhom.sendRawMessage("I love oranges")
                            } else c.forWhom.sendRawMessage("Ew?")
                        }
                        numberPrompt("How many fruit do you eat a day?") { c, i ->
                            c.forWhom.sendRawMessage("You inputted $i")
                        }
                        finish("Nice talking to you!") {
                            open(e.whoClicked as Player)
                        }
                    }.abandon {
                        (it.context.forWhom as Player).sendMessage("&cExited Conversation")
                    }.build(e.whoClicked as Player)
                }
            } named "&6&lChange GUI Name" childOf this slot 4 material Material.NAME_TAG
    }
}