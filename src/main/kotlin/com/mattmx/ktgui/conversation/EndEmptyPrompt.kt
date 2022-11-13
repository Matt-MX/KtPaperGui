package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.MessagePrompt
import org.bukkit.conversations.Prompt

class EndEmptyPrompt(
    private val message: String? = null,
    private val after: (() -> Unit)? = null
) : MessagePrompt(), BuildablePrompt {

    override fun getNextPrompt(context: ConversationContext): Prompt {
        after?.invoke()
        return DummyEndOfConversationPrompt()
    }

    override fun getPromptText(context: ConversationContext): String {
        return message ?: ""
    }

    override fun next(prompt: Prompt) {
    }
}