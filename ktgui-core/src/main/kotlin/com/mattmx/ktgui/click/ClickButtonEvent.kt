package com.mattmx.ktgui.click

interface ClickButtonEvent {

    fun shouldContinueEventCallback() : Boolean

    fun isCancelled() : Boolean

    fun getClickType() : ClickType

    fun <T> getPlayer() : T

}