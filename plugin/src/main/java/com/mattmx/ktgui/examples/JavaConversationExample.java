package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.KotlinGui;
import com.mattmx.ktgui.conversation.ConversationBuilder;

import static com.mattmx.ktgui.dsl.ConversationKt.conversation;
import static com.mattmx.ktgui.utils.ColorKt.legacyColor;

public class JavaConversationExample {

    public static ConversationBuilder builder = conversation(KotlinGui.Companion.getPlugin(), (conversation) -> {
        conversation.stringPrompt(legacyColor("&c&lInput something"), (c, i) -> {
            // Send the input back to the player
            c.getForWhom().sendRawMessage(legacyColor(i));
            return null;
        });
        return null;
    }).abandon((e) -> {
        e.getContext().getForWhom().sendRawMessage(legacyColor("&4Conversation finished"));
        return null;
    }).exitOn("exit");

}
