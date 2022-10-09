package com.mattmx.ktgui.conversation

import org.bukkit.conversations.Prompt

interface BuildablePrompt {
    fun next(prompt: Prompt)
}