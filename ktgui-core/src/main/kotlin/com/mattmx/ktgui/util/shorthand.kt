package com.mattmx.ktgui.util

import net.kyori.adventure.text.minimessage.MiniMessage

operator fun String.not() = MiniMessage.miniMessage().deserialize(this)