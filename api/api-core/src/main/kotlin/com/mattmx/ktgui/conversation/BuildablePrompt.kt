package com.mattmx.ktgui.conversation

import org.bukkit.conversations.Prompt

fun interface BuildablePrompt {
    fun next(prompt: Prompt)
}