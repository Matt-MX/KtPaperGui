package com.mattmx.ktgui.commands.declarative.arg

import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.consumer.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumer.VariableArgumentConsumer
import com.mattmx.ktgui.commands.declarative.invocation.BaseCommandContext
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

class RelativeCoordinateArgument(
    name: String,
    typeName: String
) : Argument<Location>(name, typeName, VariableArgumentConsumer(3)) {

    init {

    }

    override fun getValueOfString(
        cmd: DeclarativeCommandBuilder,
        context: BaseCommandContext<*>,
        split: List<String>
    ): Location? {
        if (split.size != 3) return null

        val entity = (context.sender as Entity)
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