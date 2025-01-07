package com.mattmx.ktgui.event

interface ContinuousEvent {

    fun shouldContinueCallback(value: Boolean)

    fun shouldContinueCallback() : Boolean

}