package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.utils.dp
import org.bukkit.Axis
import org.bukkit.entity.Entity
import java.util.*
import kotlin.math.floor

class PositionalArgument(
    name: String,
    val axis: Axis
) : Argument<Double>(name, "rel${axis.name}") {
    var blockPos: Boolean = false
        private set
    var allowRelative: Boolean = true
        private set

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder?,
        context: BaseCommandContext<*>?,
        stringValue: String?
    ): Double? {
        stringValue ?: return null

        val value = if (stringValue.startsWith("~") && allowRelative) {
            val replaced = stringValue.replace("~", "")

            if (replaced.isEmpty()) relativeOf(context)
            else replaced.toDoubleOrNull()?.let { v -> relativeOf(context)?.plus(v) }

        } else stringValue.toDoubleOrNull()

        return if (blockPos) value?.let { floor(it) } else value
    }

    private fun relativeOf(context: BaseCommandContext<*>?) =
        (context?.sender as? Entity)?.location?.let { loc ->
            when (axis) {
                Axis.X -> loc.x
                Axis.Y -> loc.y
                Axis.Z -> loc.z
            }
        }

    override fun getSuggestions(invocation: StorageCommandContext<*>): Optional<Collection<String>> {
        val list = arrayListOf<String>()
        if (allowRelative) list.add("~")
        relativeOf(invocation)?.let { v ->
            list.add(if (blockPos) floor(v).toString() else v.dp(2).toString())
        }

        return Optional.of(list)
    }

    infix fun allowRelative(value: Boolean) = apply {
        this.allowRelative = value
    }

    infix fun blockPos(value: Boolean) = apply {
        this.blockPos = value
    }
}