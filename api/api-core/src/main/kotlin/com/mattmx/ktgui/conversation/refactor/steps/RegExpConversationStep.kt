package com.mattmx.ktgui.conversation.refactor.steps

import org.bukkit.conversations.Conversable
import java.util.*

class RegExpConversationStep<C : Conversable> : StringConversationStep<C>() {
    lateinit var regex: Regex

    override fun validate(input: String?): Optional<String> {
        if (input == null) return Optional.empty()

        return if (::regex.isInitialized) {
            if (regex.matches(input)) Optional.of(input) else Optional.empty()
        } else Optional.empty()
    }

}