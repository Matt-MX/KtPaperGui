package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.conversation
import com.mattmx.ktgui.utils.legacyColor
import com.mattmx.ktgui.utils.not
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * An example of how we can use the Bukkit Conversation API to create
 * interaction through the chat
 */
class ConversationGuiExample() : GuiScreen(!"Conversation API", 1), Example {
    init {
        GuiButton()
            .click {
                ClickType.LEFT {
                    // Make sure to close the GUI, so they can type in chat
                    forceClose(player)
                    // Create a new conversation
                    player.showTitle(Title.title(!"&6&lConversation Started", !"&eUse the chat!"))
                    conversation(KotlinGui.plugin!!) {
                        /**
                         * The way you specify what you want to do is in order.
                         * We'll start with asking what their favorite fruit is.
                         */
                        stringPrompt("&6&lWhat's your fave fruit".legacyColor()) { c, i ->
                            if (i == "oranges") {
                                /**
                                 * If the input is "oranges" then reply with something else
                                 * KtGui blocks other messages to the client to not "clog" chat.
                                 * This means you should use "c.fromWhom.sendRawMessage" to send messages.
                                 */
                                c.forWhom.sendRawMessage("&eI love oranges".legacyColor())
                            } else c.forWhom.sendRawMessage("&eEwwww".legacyColor())
                        }
                        /**
                         * We can also get numeric inputs, and specify a range of valid responses.
                         * We are going to ask how many fruit they eat.
                         * If the response isn't between 5-10 inclusive then we should tell them off.
                         */
                        numberPrompt(
                            "&6&lHow many fruit do you eat a day?".legacyColor(),
                            "&cYou should be eating 5, go fix that".legacyColor(),
                            (5..10).toList()) { c, i ->
                            c.forWhom.sendRawMessage("&eYou inputted $i".legacyColor())
                        }
                        /**
                         * Finally let's add a custom finish statement.
                         * If you don't call this then KtGui will add one automatically.
                         */
                        finish("&cNice talking to you!".legacyColor()) {
                            // Open the GUI again
                            open(player)
                        }
                    }.abandon {
                        open(player)
                        player.sendMessage("&cExited Conversation".legacyColor())
                    }.exitOn("exit") // If the player types "exit" the conversation is ended.
                        .build(player).begin() // Build and begin the conversation.
                }
                ClickType.RIGHT {
                    forceClose(player)
                    conversation(KotlinGui.plugin!!) {
                        /**
                         * If the player right clicks then they can rename the current GUI title.
                         * They can type "exit" to cancel.
                         */
                        stringPrompt("&6&lEnter a GUI name".legacyColor()) { c, i ->
                            i?.let { title(!i) }
                        }
                        abandon {
                            openAndFormat(player)
                        }
                    }.exitOn("exit").build(player).begin()
                }
            }.lore {
                add(!"&eLeft &7for fruit questions")
                add(!"&eRight &7to change GUI name")
            } named !"&6&lConversation API example." material Material.NAME_TAG childOf this slot 4
    }

    override fun run(player: Player) = open(player)
}