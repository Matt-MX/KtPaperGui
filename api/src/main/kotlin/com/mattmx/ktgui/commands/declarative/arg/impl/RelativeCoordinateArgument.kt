package com.mattmx.ktgui.commands.declarative.arg.impl

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

class RelativeCoordinateArgument(
    name: String,
    typeName: String
) : Argument<Location>(name, typeName) {

    init {
        this.consumes(ArgumentConsumer.variable(3))
    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder?,
        context: BaseCommandContext<*>?,
        split: List<String>
    ): Location? {
        if (split.size != 3) return null

        val entity = (context?.sender as Entity?)
            ?: return null
        val location = entity.location.clone().toVector().list()

        var i = 0
        val coords = split.map { s ->
            val doubleValue = s.replace("~", "").toDoubleOrNull()
                ?: return null
            val finalValue = if (s.startsWith("~")) {
                doubleValue + location[i]
            } else {
                doubleValue
            }
            i++

            finalValue
        }

        return Location(entity.location.world, coords[0], coords[1], coords[2])
    }

    private fun Vector.list() = listOf(x, y, z)

}