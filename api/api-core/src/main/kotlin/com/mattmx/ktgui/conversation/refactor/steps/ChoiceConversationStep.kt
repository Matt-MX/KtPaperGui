package com.mattmx.ktgui.conversation.refactor.steps

import org.bukkit.conversations.Conversable
import java.util.*

class ChoiceConversationStep<C : Conversable> : RawConversationStep<String, C>() {
    var choices = mutableListOf<String>()

    override fun validate(input: String?): Optional<String> {
        if (input == null) return Optional.empty()

        return Optional.ofNullable(choices.firstOrNull { it == input })
    }

}