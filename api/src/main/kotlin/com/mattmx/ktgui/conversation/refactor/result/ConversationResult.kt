package com.mattmx.ktgui.conversation.refactor.result

import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationContext
import java.util.*

class ConversationResult<T, C>(
    val context: ConversationContext,
    val conversation: Conversation,
    val conversable: C,
    val result: Optional<T>
)