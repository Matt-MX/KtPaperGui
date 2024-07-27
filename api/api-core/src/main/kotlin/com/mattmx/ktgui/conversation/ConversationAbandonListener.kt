package com.mattmx.ktgui.conversation

import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.conversations.ConversationAbandonedListener

class ConversationAbandonListener(
    private val conv: ConversationBuilder
) : ConversationAbandonedListener {

    override fun conversationAbandoned(abandonedEvent: ConversationAbandonedEvent) {
        conv.abandon?.invoke(abandonedEvent)
    }

}