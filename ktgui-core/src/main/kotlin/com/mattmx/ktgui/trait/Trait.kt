package com.mattmx.ktgui.trait

interface Trait<O> {

    fun onEnable() {

    }

    fun onDisable() {

    }

    fun getOwner() : O

}