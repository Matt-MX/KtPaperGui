package com.mattmx.ktgui.conversation.refactor

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.conversation.refactor.result.ConversationEnd
import com.mattmx.ktgui.conversation.refactor.steps.Step
import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.extensions.setOpenGui
import org.bukkit.conversations.Conversable
import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.conversations.ConversationAbandonedListener
import org.bukkit.conversations.ConversationFactory
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class ConversationWrapper<T : Conversable>(
    plugin: JavaPlugin
) {
    private val factory = ConversationFactory(plugin)
    private val steps = arrayListOf<Step>()
    private var exit = Optional.empty<(ConversationAbandonedEvent) -> Unit>()
    private var start = Optional.empty<(T) -> Unit>()
    var closeGuiOnStart = true

    var exitOn: String
        get() = ""
        set(value) {
            factory.withEscapeSequence(value)
        }

    infix fun exit(block: ConversationAbandonedEvent.() -> Unit) = apply {
        this.exit = Optional.of(block)
        factory.addConversationAbandonedListener {
            block(it)
        }
    }

    infix fun start(block: T.() -> Unit) = apply {
        this.start = Optional.of(block)
    }

    fun <S : Step> add(step: S) = step.apply {
        steps.add(this)
    }

    private fun build() {
        if (steps.isNotEmpty()) {
            factory.withFirstPrompt(steps.first())
        }
        // todo add end prompt?

        for ((index, step) in steps.withIndex()) {
            val nextStep = steps.getOrNull(index + 1)

            if (nextStep != null) {
                step.next(nextStep)
            }
        }
    }

    fun begin(conversable: T): Conversation {
        build()
        val conversation = factory.buildConversation(conversable).apply { begin() }

        if (closeGuiOnStart && conversable is Player) {
            GuiManager.forceClose(conversable)
        }

        start.ifPresent { it.invoke(conversable) }

        return conversation
    }
}