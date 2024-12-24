package com.mattmx.ktgui.trait

import java.util.*
import java.util.function.Supplier

@Suppress("UNCHECKED_CAST")
class TraitHolder<O : Any>(
    private val owner: O
) {
    private val traits = Collections.synchronizedMap(hashMapOf<Class<*>, Trait<O>>())

    fun <T : Trait<O>> register(trait: T): Trait<O>? {
        return this.traits.put(trait::class.java, trait)
    }

    fun <T : Trait<O>> getOrCreate(clazz: Class<T>): T {
        return getOrCreate(clazz) { clazz.getConstructor(owner.javaClass).newInstance(owner) }
    }

    fun <T : Trait<O>> getOrCreate(clazz: Class<T>, supplier: Supplier<T>): T {
        return traits.getOrPut(clazz) {
            supplier.get().also { it.onEnable() }
        } as T
    }

    operator fun <T : Trait<O>> get(clazz: Class<T>) : Optional<T> {
        return Optional.ofNullable(traits[clazz] as? T)
    }

    fun <T : Trait<O>> remove(clazz: Class<T>) : T? {
        return traits.remove(clazz) as? T
    }

    fun <T : Trait<O>> removeAndDisable(clazz: Class<T>): T? {
        return traits.remove(clazz)?.also(Trait<O>::onDisable) as? T
    }

    fun all(): Map<Class<*>, Trait<O>> {
        return traits.toMap()
    }

    fun clear() {
        synchronized(traits) {
            val it = traits.iterator()

            while (it.hasNext()) {
                it.next().value.onDisable()
                it.remove()
            }
        }
    }
}