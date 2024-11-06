package com.mattmx.ktgui

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface Replyable {

    infix fun reply(component: Component) {
        getTarget().sendMessage(component)
    }

    fun getTarget() : Player

}