package com.mattmx.ktgui.command.arg

import com.mojang.brigadier.builder.ArgumentBuilder

class ArgumentBuilderWrapper(
    val owner: ArgumentBuilder<Any, *>
) {

    operator fun <T : Any> ArgumentWrapper<*>.div(arg: ArgumentWrapper<T>): CommandArgumentBuilder {
        return CommandArgumentBuilder(owner).also { it.div(this).div(arg) }
    }

}