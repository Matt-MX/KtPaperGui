package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.conversation.refactor.conversation
import com.mattmx.ktgui.conversation.refactor.getInteger
import com.mattmx.ktgui.conversation.refactor.getString
import com.mattmx.ktgui.dsl.conversation
import com.mattmx.ktgui.utils.legacyColor
import com.mattmx.ktgui.scheduling.not
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
                    // Create a new conversation
                    conversation<Player> {
                        /**
                         * The way you specify what you want to do is in order.
                         * We'll start with asking what their favorite fruit is.
                         */
                        getString {
                            title = !"&6&lWhat's your fave fruit"
                            subtitle = !"&7Type in chat."

                            runs {
                                if (result.orElse(null) == "oranges") {
                                    /**
                                     * If the input is "oranges" then reply with something else
                                     * KtGui blocks other messages to the client to not "clog" chat.
                                     * This means you should use "c.fromWhom.sendRawMessage" to send messages.
                                     */
                                    conversable.sendMessage("&eI love oranges".legacyColor())
                                } else conversable.sendMessage("&eEwwww".legacyColor())
                                context.setSessionData("fruit", result)
                            }
                        }
                        /**
                         * We can also get numeric inputs, and specify a range of valid responses.
                         * We are going to ask how many fruit they eat.
                         * If the response isn't between 5-10 inclusive then we should tell them off.
                         */
                        getInteger {
                            range = (5..10)
                            runs {
                                val fruit = context.getSessionData("fruit") ?: "fruit"
                                conversable.sendMessage(!"&cYou ate $result ${fruit}s today.")
                            } invalid {
                                conversable.sendMessage(!"&cYou must be eating 5-10 fruit per day, go and fix that and come back.")
                            }
                        }

                        exitOn = "cancel"
                        exit {
                            player.sendMessage(!"&bExited Conversation")
                        }
                    } begin player
                }
                ClickType.RIGHT {
                    conversation<Player> {
                        /**
                         * If the player right clicks then they can rename the current GUI title.
                         * They can type "exit" to cancel.
                         */
                        getString {
                            message = !"&6&lEnter a new GUI name"
                            runs {
                                title = !result.get()
                            }
                        }
                        exit {
                            open(player)
                        }
                    } begin player
                }
            }.lore {
                add(!"&eLeft &7for fruit questions")
                add(!"&eRight &7to change GUI name")
            } named !"&6&lConversation API example." material Material.NAME_TAG childOf this slot 4
    }

    override fun run(player: Player) = open(player)
}