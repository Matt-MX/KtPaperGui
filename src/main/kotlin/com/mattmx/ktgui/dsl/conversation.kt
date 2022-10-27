package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.conversation.ConversationBuilder
import org.bukkit.conversations.ConversationFactory
import org.bukkit.plugin.java.JavaPlugin

fun conversation(plugin: JavaPlugin, builder: ConversationBuilder.() -> Unit) : ConversationBuilder {
    val fac = ConversationFactory(plugin)
        .withLocalEcho(false)
        .withModality(true)
    val bui = ConversationBuilder(fac)
    builder.invoke(bui)
    return bui
}