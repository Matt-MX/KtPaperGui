package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import com.mattmx.ktgui.utils.not
import org.bukkit.Axis
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

class RelativeCoordinateArgument(
    name: String,
    typeName: String
) : Argument<Location>(name, typeName) {
    val x by relativePositionOfAxis(Axis.X)
    val y by relativePositionOfAxis(Axis.Y)
    val z by relativePositionOfAxis(Axis.Z)

    init {
        this.consumes(ArgumentConsumer.variable(3))
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder?,
        context: BaseCommandContext<*>?,
        stringValue: String?
    ): Location? {
        stringValue ?: return null

        val loc = (context?.sender as? Entity)?.location ?: return null

        val split = stringValue.split(" ")
        if (split.size != 3) return null

        val xV = x.getValueOfString(cmd, context, split.getOrNull(0)) ?: return null
        val yV = y.getValueOfString(cmd, context, split.getOrNull(1)) ?: return null
        val zV = z.getValueOfString(cmd, context, split.getOrNull(2)) ?: return null

        return Location(loc.world, xV, yV, zV, loc.yaw, loc.pitch)
    }

    private fun Vector.list() = listOf(x, y, z)

    infix fun blockPos(value: Boolean) = apply {
        x blockPos value
        y blockPos value
        z blockPos value
    }

    infix fun allowRelative(value: Boolean) = apply {
        x allowRelative value
        y allowRelative value
        z allowRelative value
    }

    infix fun allowRelativeX(value: Boolean) = apply {
        x allowRelative value
    }

    infix fun allowRelativeY(value: Boolean) = apply {
        y allowRelative value
    }

    infix fun allowRelativeZ(value: Boolean) = apply {
        z allowRelative value
    }

}