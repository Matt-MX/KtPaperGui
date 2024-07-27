package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.MessagePrompt
import org.bukkit.conversations.Prompt

class DummyEndOfConversationPrompt : MessagePrompt(), BuildablePrompt {

    override fun getNextPrompt(context: ConversationContext): Prompt? {
        return Prompt.END_OF_CONVERSATION
    }

    override fun getPromptText(context: ConversationContext): String {
        return ""
    }

    override fun next(prompt: Prompt) {
    }
}