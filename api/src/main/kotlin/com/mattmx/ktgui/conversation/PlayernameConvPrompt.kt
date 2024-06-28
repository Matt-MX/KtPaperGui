package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.PlayerNamePrompt
import org.bukkit.conversations.Prompt
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlayernameConvPrompt(
    plugin: JavaPlugin,
    private val message: String,
    private val fail: String,
    private val cb: (ConversationContext, Player) -> Unit
) : PlayerNamePrompt(plugin), BuildablePrompt {
    var next: Prompt? = null

    override fun getPromptText(context: ConversationContext): String {
        return message
    }

    override fun getFailedValidationText(context: ConversationContext, invalidInput: String): String {
        return fail
    }

    override fun acceptValidatedInput(context: ConversationContext, input: Player): Prompt? {
        cb.invoke(context, input)
        return next
    }

    override fun next(prompt: Prompt) {
        next = prompt
    }
}