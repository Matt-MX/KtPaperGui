package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt

class StringConvPrompt(
    private val q: String,
    private val cb: (ConversationContext, String?) -> Unit
) : StringPrompt(), BuildablePrompt {
    var next: Prompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return q
    }

    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        cb.invoke(context, input)
        return next
    }

    override fun next(prompt: Prompt) {
        next = prompt
    }
}