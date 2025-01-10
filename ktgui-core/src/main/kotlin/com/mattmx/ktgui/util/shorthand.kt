package com.mattmx.ktgui.util

import net.kyori.adventure.text.minimessage.MiniMessage

operator fun String.not() = MiniMessage.miniMessage().deserialize(this)

val String.minimessage
    get() = MiniMessage.miniMessage().deserialize(this)