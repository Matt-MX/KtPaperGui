package com.mattmx.ktgui.commands.declarative.invocation

import org.bukkit.command.CommandSender

open class StorageCommandContext<T : CommandSender>(
    sender: T,
    alias: String,
    rawArgs: List<String>
) : BaseCommandContext<T>(sender, alias, rawArgs) {
    val storage = hashMapOf<String, Any?>()

    override fun clone(newList: List<String>): StorageCommandContext<T> {
        return StorageCommandContext(sender, alias, newList)
            .apply {
                storage += this@StorageCommandContext.storage
            }
    }
}