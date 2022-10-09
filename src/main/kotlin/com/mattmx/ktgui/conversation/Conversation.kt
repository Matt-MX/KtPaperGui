package com.mattmx.ktgui.conversation

import org.bukkit.conversations.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.NumberConversions

fun conversation(plugin: JavaPlugin, builder: ConversationBuilder.() -> Unit) : ConversationBuilder {
    val fac = ConversationFactory(plugin)
    val bui = ConversationBuilder(fac)
    builder.invoke(bui)
    return bui
}

class ConversationBuilder(val factory: ConversationFactory) {
    var abandon: ((ConversationAbandonedEvent) -> Unit)? = null
    var list = arrayListOf<BuildablePrompt>()

    fun stringPrompt(message: String, callback: (context: ConversationContext, input: String?) -> Unit) : ConversationBuilder {
        list.add(StringConvPrompt(message, callback))
        return this
    }

    fun fixedPrompt(message: String, vararg accept: String, cb: (ConversationContext, String) -> Unit) : ConversationBuilder {
        list.add(FixedSetConvPrompt(message, *accept, cb = cb))
        return this
    }

    fun numberPrompt(message: String, fail: String? = "", accept: List<Number>? = null, cb: (ConversationContext, Number) -> Unit) : ConversationBuilder {
        list.add(NumericConvPrompt(message, fail, accept, cb = cb))
        return this
    }

    fun playerPrompt(plugin: JavaPlugin, message: String, fail: String, cb: (ConversationContext, Player) -> Unit) : ConversationBuilder {
        list.add(PlayernameConvPrompt(plugin, message, fail, cb))
        return this
    }

    fun messagePrompt(message: String) : ConversationBuilder {
        list.add(MessageConvPrompt(message))
        return this
    }

    fun abandon(event: (ConversationAbandonedEvent) -> Unit) : ConversationBuilder {
        abandon = event
        factory.addConversationAbandonedListener(ConversationAbandonedListener(abandon!!))
        return this
    }

    fun exitOn(message: String) : ConversationBuilder {
        factory.withEscapeSequence(message)
        return this
    }

    fun timeout(time: Int) : ConversationBuilder {
        factory.withTimeout(time)
        return this
    }

    fun finish(message: String? = null, after: (() -> Unit)? = null) : ConversationBuilder {
        list.add(EndEmptyPrompt(message, after))
        return this
    }

    fun build(conversable: Conversable) : Conversation {
        if (list.isNotEmpty()) factory.withFirstPrompt(list[0] as Prompt)
        if (list.size - 1 > 0 && list[list.size - 1] !is EndEmptyPrompt) {
            list.add(EndEmptyPrompt())
        }
        list.forEachIndexed { i, p ->
            p.next(list[i + 1] as Prompt)
        }
        return factory.buildConversation(conversable)
    }
}