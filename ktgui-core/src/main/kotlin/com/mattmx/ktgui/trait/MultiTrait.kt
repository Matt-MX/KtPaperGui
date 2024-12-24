package com.mattmx.ktgui.trait

import java.util.*

abstract class MultiTrait<O, T : Trait<O>>(owner: O) : AbstractTrait<O>(owner) {
    private val children = Collections.synchronizedSet(mutableSetOf<T>())

    fun addTrait(trait: T): Boolean {
        return this.children.add(trait)
    }

    fun hasTrait(trait: T) : Boolean {
        return this.children.contains(trait)
    }

    fun removeTrait(trait: T) : Boolean {
        return this.children.remove(trait)
    }

    override fun onDisable() {
        synchronized(this.children) {
            val it = this.children.iterator()
            while (it.hasNext()) {
                it.next().onDisable()
                it.remove()
            }
        }
    }

}