package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.conversation.ConversationBuilder
import org.bukkit.conversations.ConversationFactory
import org.bukkit.plugin.java.JavaPlugin

inline fun conversation(plugin: JavaPlugin, block: ConversationBuilder.() -> Unit) : ConversationBuilder {
    val conversationFactory = ConversationFactory(plugin)
        .withLocalEcho(false)
        .withModality(true)
    val conversationBuilder = ConversationBuilder(conversationFactory)
    block.invoke(conversationBuilder)
    return conversationBuilder
}