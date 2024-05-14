package com.mattmx.ktgui.utils

import org.bukkit.entity.Player
import java.util.*

fun Player.highestPermissionLevel(node: String): Optional<Long> {
    val finalNode = if (node.endsWith(".")) node else "$node."
    var highest = Optional.empty<Long>()

    effectivePermissions
        .filter { it.permission.matches("$finalNode.\\d+".toRegex()) }
        .forEach {
            val thisVal = it.permission.substringAfterLast(".").toLong()
            highest = Optional.ofNullable(if (thisVal > highest.orElse(0L)) thisVal else highest.orElse(null))
        }
    return highest
}