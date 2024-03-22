package com.mattmx.ktgui.utils

import kotlin.math.min

class InstancePackageClassCache<V : Any> {
    private val packageList = hashMapOf<String, Any>()

    fun <T : Any> cacheInstance(clazz: Class<T>, instance: V) {
        val split = clazz.packageName.split(".")
        val three = split.subList(0, min(3, split.size))
        val packagePath = three.joinToString(".")

        packageList[packagePath] = instance
    }

    fun <T : Any> clearInstance(clazz: Class<T>) : V {
        return packageList.remove(getShortPackageName(clazz)) as V
    }

    fun <T : Any> getInstance(clazz: Class<T>): V {
        return packageList[getShortPackageName(clazz)]!! as V
    }

    private fun getShortPackageName(clazz: Class<*>): String {
        val split = clazz.packageName.split(".")
        val three = split.subList(0, min(3, split.size))
        return three.joinToString(".")
    }
}