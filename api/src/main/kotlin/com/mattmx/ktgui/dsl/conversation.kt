package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.conversation.ConversationBuilder
import org.bukkit.conversations.ConversationFactory
import org.bukkit.plugin.java.JavaPlugin

/**
 * Allows for a conversation to be made via the kotlin DSL.
 *
 * @param plugin the conversation is being made in
 * @param block conversation builder
 *
 * @return the conversation created
 */
inline fun <T> T.conversation(block: ConversationBuilder.() -> Unit) : ConversationBuilder {
    val conversationFactory = ConversationFactory(GuiManager.getPlugin(this!!::class.java))
        .withLocalEcho(false)
        .withModality(true)
    val conversationBuilder = ConversationBuilder(conversationFactory)
    block.invoke(conversationBuilder)
    return conversationBuilder
}