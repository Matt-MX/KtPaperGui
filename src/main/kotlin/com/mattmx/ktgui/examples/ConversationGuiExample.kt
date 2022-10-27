package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.conversation
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * An example of how we can use the Bukkit Conversation API to create
 * interaction through the chat
 */
class ConversationGuiExample() : GuiScreen("Conversation API", 1) {
    init {
        GuiButton()
            .click {
                left = { e ->
                    val player = e.whoClicked as Player
                    // Make sure to close the GUI, so they can type in chat
                    forceClose(player)
                    // Create a new conversation
                    player.sendTitle(Chat.color("&6&lConversation Started"), Chat.color("&eUse the chat!"))
                    conversation(KotlinBukkitGui.plugin!!) {
                        /**
                         * The way you specify what you want to do is in order.
                         * We'll start with asking what their favorite fruit is.
                         */
                        stringPrompt(Chat.color("&6&lWhat's your fave fruit")) { c, i ->
                            if (i == "oranges") {
                                /**
                                 * If the input is "oranges" then reply with something else
                                 * KtGui blocks other messages to the client to not "clog" chat.
                                 * This means you should use "c.fromWhom.sendRawMessage" to send messages.
                                 */
                                c.forWhom.sendRawMessage(Chat.color("&eI love oranges"))
                            } else c.forWhom.sendRawMessage(Chat.color("&eEwwww"))
                        }
                        /**
                         * We can also get numeric inputs, and specify a range of valid responses.
                         * We are going to ask how many fruit they eat.
                         * If the response isn't between 5-10 inclusive then we should tell them off.
                         */
                        numberPrompt(
                            Chat.color("&6&lHow many fruit do you eat a day?"),
                            Chat.color("&cYou should be eating 5, go fix that"),
                            (5..10).toList()) { c, i ->
                            c.forWhom.sendRawMessage(Chat.color("&eYou inputted $i"))
                        }
                        /**
                         * Finally let's add a custom finish statement.
                         * If you don't call this then KtGui will add one automatically.
                         */
                        finish(Chat.color("&cNice talking to you!")) {
                            // Open the GUI again
                            open(player)
                        }
                    }.abandon {
                        open(player)
                        player.sendMessage(Chat.color("&cExited Conversation"))
                    }.exitOn("exit") // If the player types "exit" the conversation is ended.
                        .build(player).begin() // Build and begin the conversation.
                }
                right = { e ->
                    val player = e.whoClicked as Player
                    forceClose(player)
                    conversation(KotlinBukkitGui.plugin!!) {
                        /**
                         * If the player right clicks then they can rename the current GUI title.
                         * They can type "exit" to cancel.
                         */
                        stringPrompt(Chat.color("&6&lEnter a GUI name")) { c, i ->
                            i?.let { title(i) }
                        }
                        abandon {
                            openAndFormat(player)
                        }
                    }.exitOn("exit").build(player).begin()
                }
            }.lore {
                add("&eLeft &7for fruit questions")
                add("&eRight &7to change GUI name")
            } named "&6&lConversation API example." childOf this slot 4 material Material.NAME_TAG
    }
}