package com.mattmx.ktgui.conversation.refactor.steps

import org.bukkit.conversations.Prompt

interface Step : Prompt {

    fun next(step: Step)

}