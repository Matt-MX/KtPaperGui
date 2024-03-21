package com.mattmx.ktgui.conversation.refactor.steps

import org.bukkit.conversations.Conversable
import java.util.*

class EnumConversationStep<E : Enum<E>, C : Conversable>(
    private val clazz: Class<E>
) : RawConversationStep<E, C>() {

    override fun validate(input: String?): Optional<E> {
        if (input == null) return Optional.empty()

        return Optional.ofNullable(clazz.enumConstants.firstOrNull { it.name == input })
    }

    enum class Empty
}