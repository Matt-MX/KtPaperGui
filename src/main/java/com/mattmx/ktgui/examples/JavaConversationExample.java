package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.KotlinBukkitGui;
import com.mattmx.ktgui.conversation.ConversationBuilder;
import com.mattmx.ktgui.utils.Chat;
import org.bukkit.entity.Player;

import static com.mattmx.ktgui.conversation.ConversationKt.conversation;

public class JavaConversationExample {

    public static ConversationBuilder builder = conversation(KotlinBukkitGui.Companion.getPlugin(), (conversation) -> {
        conversation.stringPrompt(Chat.INSTANCE.color("&c&lInput something"), (c, i) -> {
            // Send the input back to the player
            c.getForWhom().sendRawMessage(Chat.INSTANCE.color(i));
            return null;
        });
        return null;
    }).abandon((e) -> {
        e.getContext().getForWhom().sendRawMessage(Chat.INSTANCE.color("&4Conversation finished"));
        return null;
    }).exitOn("exit");

}
