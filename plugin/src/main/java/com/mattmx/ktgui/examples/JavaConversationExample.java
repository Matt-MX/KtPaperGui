package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.KotlinBukkitGui;
import com.mattmx.ktgui.conversation.ConversationBuilder;

import static com.mattmx.ktgui.dsl.ConversationKt.conversation;
import static com.mattmx.ktgui.utils.ColorKt.color;

public class JavaConversationExample {

    public static ConversationBuilder builder = conversation(KotlinBukkitGui.Companion.getPlugin(), (conversation) -> {
        conversation.stringPrompt(color("&c&lInput something"), (c, i) -> {
            // Send the input back to the player
            c.getForWhom().sendRawMessage(color(i));
            return null;
        });
        return null;
    }).abandon((e) -> {
        e.getContext().getForWhom().sendRawMessage(color("&4Conversation finished"));
        return null;
    }).exitOn("exit");

}
