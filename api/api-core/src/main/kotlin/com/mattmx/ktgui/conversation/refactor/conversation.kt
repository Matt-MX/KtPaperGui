package com.mattmx.ktgui.conversation.refactor

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.conversation.refactor.steps.*
import org.bukkit.conversations.Conversable
import org.bukkit.plugin.java.JavaPlugin

fun <T : Conversable> conversation(plugin: JavaPlugin, block: ConversationWrapper<T>.() -> Unit) =
    ConversationWrapper<T>(plugin).apply(block)

fun <T : Conversable> conversation(block: ConversationWrapper<T>.() -> Unit) =
    ConversationWrapper<T>(GuiManager.owningPlugin).apply(block)

fun <T : Conversable> ConversationWrapper<T>.getString(block: StringConversationStep<T>.() -> Unit) =
    add(StringConversationStep<T>().apply(block))

fun <T : Conversable> ConversationWrapper<T>.getChoice(block: ChoiceConversationStep<T>.() -> Unit) =
    add(ChoiceConversationStep<T>().apply(block))

inline fun <reified E : Enum<E>, T : Conversable> ConversationWrapper<T>.getEnum(block: EnumConversationStep<E, T>.() -> Unit) =
    add(EnumConversationStep<E, T>(E::class.java).apply(block))

fun <T : Conversable> ConversationWrapper<T>.getRegExp(block: RegExpConversationStep<T>.() -> Unit) =
    add(RegExpConversationStep<T>().apply(block))

fun <T : Conversable> ConversationWrapper<T>.getInteger(block: IntegerConversationStep<T>.() -> Unit) =
    add(IntegerConversationStep<T>().apply(block))

fun <T : Conversable> ConversationWrapper<T>.getLong(block: LongConversationStep<T>.() -> Unit) =
    add(LongConversationStep<T>().apply(block))

fun <T : Conversable> ConversationWrapper<T>.getDouble(block: DoubleConversationStep<T>.() -> Unit) =
    add(DoubleConversationStep<T>().apply(block))

fun <T : Conversable> ConversationWrapper<T>.getFloat(block: FloatConversationStep<T>.() -> Unit) =
    add(FloatConversationStep<T>().apply(block))