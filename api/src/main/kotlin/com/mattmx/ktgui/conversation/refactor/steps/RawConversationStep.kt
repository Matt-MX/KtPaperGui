package com.mattmx.ktgui.conversation.refactor.steps

import com.mattmx.ktgui.conversation.refactor.result.ConversationResult
import com.mattmx.ktgui.utils.legacy
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.conversations.Conversable
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

open class RawConversationStep<T : Any, C : Conversable> : StringPrompt(), Step {
    var next: Optional<Step> = Optional.empty()
        private set

    var message: Component = Component.empty()
    var messageCallback: Optional<(ConversationContext) -> Component> = Optional.empty()
    var title: Component = Component.empty()
    var subtitle: Component = Component.empty()
    var callback: Optional<(ConversationResult<T, C>) -> Unit> = Optional.empty()
    var check: Optional<(ConversationResult<T, C>) -> Boolean> = Optional.empty()
    var invalid: Optional<(ConversationResult<T, C>) -> Unit> = Optional.empty()
    var repeatIfInvalid = true

    override fun getPromptText(context: ConversationContext): String {

        if (messageCallback.isPresent) {
            return messageCallback.get().invoke(context).legacy()
        }

        if (context.forWhom is Player) {
            (context.forWhom as Player).showTitle(
                Title.title(
                    title,
                    subtitle,
                    Title.Times.times(
                        Duration.ofMillis(0),
                        Duration.ofDays(100),
                        Duration.ofMillis(0)
                    )
                )
            )
        }

        return message.legacy()
    }

    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        val validated = validate(input)
        val result = ConversationResult(context, context.forWhom as C, validated)

        val isValid = validated.isPresent && check.isPresent && check.get().invoke(result)

        if (isValid) {
            callback.ifPresent {
                it.invoke(result)
            }
        } else {
            invalid.ifPresent {
                it.invoke(result)
            }
            // Repeat if this is invalid
            if (repeatIfInvalid) {
                return this
            }
        }

        return next.orElse(null)
    }

    infix fun runs(block: ConversationResult<T, C>.() -> Unit) = apply {
        this.callback = Optional.of(block)
    }

    infix fun message(block: ConversationContext.() -> Component) = apply {
        this.messageCallback = Optional.of(block)
    }

    infix fun matches(block: ConversationResult<T, C>.() -> Boolean) = apply {
        this.check = Optional.of(block)
    }

    infix fun invalid(block: ConversationResult<T, C>.() -> Unit) = apply {
        this.callback = Optional.of(block)
    }

    open fun validate(input: String?): Optional<T> = Optional.ofNullable(input as T?)

    override fun next(step: Step) {
        this.next = Optional.of(step)
    }

}