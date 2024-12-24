package com.mattmx.ktgui.trait

abstract class AbstractTrait<O>(
    private val owner: O
) : Trait<O> {

    override fun getOwner(): O {
        return this.owner
    }

}