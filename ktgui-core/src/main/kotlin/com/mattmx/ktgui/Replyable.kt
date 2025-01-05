package com.mattmx.ktgui

import net.kyori.adventure.text.Component

interface Replyable<T> {

    fun reply(component: Component)

}