package com.mattmx.ktgui.conversation.refactor.steps

import org.bukkit.conversations.Conversable
import java.util.*

abstract class NumberConversationStep<T : Comparable<T>, C : Conversable> : RawConversationStep<T, C>() {
    lateinit var range: ClosedRange<T>

    abstract fun get(str: String) : T?

    override fun validate(input: String?): Optional<T> {
        if (input == null) return Optional.empty()

        val numValue = get(input)
            ?: return Optional.empty()

        if (::range.isInitialized) {
            return if (numValue in range) Optional.of(numValue) else Optional.empty()
        }

        return Optional.of(numValue)
    }

}

class IntegerConversationStep<C : Conversable> : NumberConversationStep<Int, C>() {
    override fun get(str: String) = str.toIntOrNull()
}

class LongConversationStep<C : Conversable> : NumberConversationStep<Long, C>() {
    override fun get(str: String) = str.toLongOrNull()
}

class DoubleConversationStep<C : Conversable> : NumberConversationStep<Double, C>() {
    override fun get(str: String) = str.toDoubleOrNull()
}

class FloatConversationStep<C : Conversable> : NumberConversationStep<Float, C>() {
    override fun get(str: String) = str.toFloatOrNull()
}