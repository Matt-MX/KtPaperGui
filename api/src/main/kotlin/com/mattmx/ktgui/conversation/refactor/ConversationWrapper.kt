package com.mattmx.ktgui.conversation.refactor

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.conversation.refactor.result.ConversationEnd
import com.mattmx.ktgui.conversation.refactor.steps.Step
import com.mattmx.ktgui.extensions.getOpenGui
import com.mattmx.ktgui.extensions.setOpenGui
import com.mattmx.ktgui.scheduling.sync
import org.bukkit.conversations.Conversable
import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.conversations.ConversationAbandonedListener
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.ConversationPrefix
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.util.*

class ConversationWrapper<T : Conversable>(
    plugin: JavaPlugin
) {
    val factory = ConversationFactory(plugin)
        .withModality(true)
        .withLocalEcho(false)
    private val steps = arrayListOf<Step>()
    private var exit = Optional.empty<(ConversationAbandonedEvent) -> Unit>()
    private var start = Optional.empty<(T) -> Unit>()

    /**
     * More than often, the conversation is created as the result of a button
     * click in a gui. This means that we can skip boilerplate by initially
     * closing the gui, and re-opening it.
     */
    private var startingGui = Optional.empty<IGuiScreen>()
    var closeGuiOnStart = true
    var openGuiOnEnd = true

    var exitOn: String
        get() = ""
        set(value) {
            factory.withEscapeSequence(value)
        }

    infix fun exitOn(name: String) = apply {
        this.exitOn = name
    }

    infix fun exit(block: ConversationAbandonedEvent.() -> Unit) = apply {
        this.exit = Optional.of(block)
        factory.addConversationAbandonedListener {
            block(it)
            ongoingConversations.remove(it.context)

            // Next tick, in-case developer opened another
            sync {
                // Open previous gui if set and enabled
                if (openGuiOnEnd && startingGui.isPresent && it.context.forWhom is Player) {
                    val player = it.context.forWhom as Player

                    // If not already has one open, open the old one by default.
                    if (player.getOpenGui() == null) {
                        startingGui.get().open(player)
                    }
                }
            }
        }
    }

    infix fun timeout(duration: Duration) = apply {
        factory.withTimeout(duration.toSeconds().toInt())
    }

    infix fun timeout(seconds: Int) = apply {
        factory.withTimeout(seconds)
    }

    infix fun prefix(prefix: ConversationPrefix) = apply {
        factory.withPrefix(prefix)
    }

    infix fun initialSessionData(data: Map<Any, Any>) = apply {
        factory.withInitialSessionData(data)
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

    infix fun begin(conversable: T): Conversation {
        build()
        val conversation = factory.buildConversation(conversable).apply { begin() }

        if (conversable is Player) {
            if (closeGuiOnStart) {
                GuiManager.forceClose(conversable)
            }
            if (openGuiOnEnd) {
                startingGui = Optional.ofNullable(conversable.getOpenGui())
            }
        }

        start.ifPresent { it.invoke(conversable) }

        ongoingConversations[conversation.context] = conversation

        return conversation
    }

    companion object {
        // todo need to remove on any sort of ending!
        private val ongoingConversations = hashMapOf<ConversationContext, Conversation>()

        fun getConversation(context: ConversationContext) = ongoingConversations[context]
    }
}