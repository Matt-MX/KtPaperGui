package com.mattmx.ktgui.conversation.refactor.result

import org.bukkit.conversations.Conversable

class ConversationEnd(
    val conversable: Conversable,
    val reason: Reason
) {
    enum class Reason {
        END_ABANDON,
        QUIT,
        COMPLETED
    }
}