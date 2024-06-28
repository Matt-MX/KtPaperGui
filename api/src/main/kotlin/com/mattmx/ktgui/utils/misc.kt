package com.mattmx.ktgui.utils

import org.bukkit.entity.Player
import java.util.*

/**
 * Utility function to get the highest permission level of a specified node.
 *
 * e.g a player has:
 * - ktgui.permission.1
 * - ktgui.permission.4
 * - ktgui.permission.-1
 *
 * If we call this function with [node] as "ktgui.permission" then it would return 4.
 *
 * @param node the prefix of the permission node.
 * @return [Optional] with highest level or empty.
 */
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