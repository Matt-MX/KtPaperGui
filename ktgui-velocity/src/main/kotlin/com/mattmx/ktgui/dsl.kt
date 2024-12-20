package com.mattmx.ktgui

import com.mattmx.ktgui.tasks.VelocityKeyedTaskTrackerImpl
import com.mattmx.ktgui.tasks.VelocityTaskTrackerImpl
import com.velocitypowered.api.proxy.ProxyServer

fun ProxyServer.taskTracker(plugin: Any) = VelocityTaskTrackerImpl(plugin, this)
fun ProxyServer.keyedTaskTracker(plugin: Any) = VelocityKeyedTaskTrackerImpl(plugin, this)