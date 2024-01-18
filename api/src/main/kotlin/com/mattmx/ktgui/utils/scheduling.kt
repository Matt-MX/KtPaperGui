package com.mattmx.ktgui.utils

import org.bukkit.Bukkit

fun isAsync() = !Bukkit.isPrimaryThread()