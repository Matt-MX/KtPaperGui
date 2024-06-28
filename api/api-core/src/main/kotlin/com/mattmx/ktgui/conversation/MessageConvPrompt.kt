package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.MessagePrompt
import org.bukkit.conversations.Prompt

class MessageConvPrompt(
    private val message: String
) : MessagePrompt(), BuildablePrompt {
    var next: Prompt? = null

    override fun getNextPrompt(context: ConversationContext): Prompt? {
        return next
    }

    override fun getPromptText(context: ConversationContext): String {
        return message
    }

    override fun next(prompt: Prompt) {
        next = prompt
    }


}