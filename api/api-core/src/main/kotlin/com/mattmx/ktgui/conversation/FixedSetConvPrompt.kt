package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.FixedSetPrompt
import org.bukkit.conversations.Prompt

class FixedSetConvPrompt(
    val message: String,
    vararg accept: String,
    val cb: (ConversationContext, String) -> Unit
) : FixedSetPrompt(*accept), BuildablePrompt {
    var nextPrompt: Prompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return message
    }

    override fun acceptValidatedInput(context: ConversationContext, input: String): Prompt? {
        cb.invoke(context, input)
        return nextPrompt
    }

    override fun next(prompt: Prompt) {
        nextPrompt = prompt
    }
}