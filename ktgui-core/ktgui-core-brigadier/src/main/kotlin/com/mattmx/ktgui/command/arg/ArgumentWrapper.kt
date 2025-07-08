package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder

open class ArgumentWrapper<T>(
    val name: String,
    val clazz: Class<T>,
    val argumentType: ArgumentType<T>,
    val supplier: () -> ArgumentBuilder<Any, *>
) {
    var isOptional = false
}