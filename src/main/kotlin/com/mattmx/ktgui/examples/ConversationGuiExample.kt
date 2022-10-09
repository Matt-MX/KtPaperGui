package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.conversation.conversation
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Material
import org.bukkit.conversations.PlayerNamePrompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.jvm.internal.Intrinsics.Kotlin

/**
 * An example of how we can use the Bukkit Conversation API to create
 * interaction through the chat
 */
class ConversationGuiExample() : GuiScreen("Conversation API", 1) {
    init {
        GuiButton()
            .click {
                left = { e ->
                    forceClose(e.whoClicked as Player)
                    conversation(KotlinBukkitGui.plugin!!) {
                        stringPrompt(Chat.color("&6&lWhat's your fave fruit")) { c, i ->
                            if (i == "oranges") {
                                c.forWhom.sendRawMessage(Chat.color("&eI love oranges"))
                            } else c.forWhom.sendRawMessage(Chat.color("&eEwwww"))
                        }
                        numberPrompt(Chat.color("&6&lHow many fruit do you eat a day?")) { c, i ->
                            c.forWhom.sendRawMessage(Chat.color("&eYou inputted $i"))
                        }
                        finish(Chat.color("&cNice talking to you!")) {
                            open(e.whoClicked as Player)
                        }
                    }.abandon {
                        open(e.whoClicked as Player)
                        (it.context.forWhom as Player).sendMessage(Chat.color("&cExited Conversation"))
                    }.exitOn("exit")
                        .build(e.whoClicked as Player).begin()
                }
                right = { e ->
                    forceClose(e.whoClicked as Player)
                    conversation(KotlinBukkitGui.plugin!!) {
                        stringPrompt(Chat.color("&6&lEnter a GUI name")) { c, i ->
                            i?.let { title(i) }
                        }
                        abandon {
                            openAndFormat(e.whoClicked as Player)
                        }
                    }.build(e.whoClicked as Player).begin()
                }
            } named "&6&lChange GUI Name" childOf this slot 4 material Material.NAME_TAG
    }
}