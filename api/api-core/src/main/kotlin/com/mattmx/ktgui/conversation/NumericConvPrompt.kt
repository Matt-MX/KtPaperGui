package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.NumericPrompt
import org.bukkit.conversations.Prompt

class NumericConvPrompt(
    private val message: String,
    private val fail: String? = null,
    private val accept: List<Number>? = null,
    private var cb: (ConversationContext, Number) -> Unit
) : NumericPrompt(), BuildablePrompt {
    var next: Prompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return message
    }

    override fun getFailedValidationText(context: ConversationContext, invalidInput: Number): String? {
        return fail
    }

    override fun isNumberValid(context: ConversationContext, input: Number): Boolean {
        return accept?.contains(input) ?: true
    }

    override fun acceptValidatedInput(context: ConversationContext, input: Number): Prompt? {
        cb.invoke(context, input)
        return null
    }

    override fun next(prompt: Prompt) {
        next = prompt
    }
}