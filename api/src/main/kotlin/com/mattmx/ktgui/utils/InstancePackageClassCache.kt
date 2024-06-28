package com.mattmx.ktgui.utils

import kotlin.math.min

/**
 * Stores a single object per unique package.
 *
 * e.g If we supplied [StopWatch] to the [cacheInstance] function
 * (with anything as the instance arg) then it would get the package
 * to a sublist of 3 (com.mattmx.ktgui) and then cache the instance
 * object.
 */
class InstancePackageClassCache<V : Any> {
    private val packageList = hashMapOf<String, Any>()

    /**
     * Stores the package name to a sublist of 3 with [instance].
     *
     * @param clazz any Class belonging to the package.
     * @param instance the object to store.
     */
    fun <T : Any> cacheInstance(clazz: Class<T>, instance: V) {
        val split = clazz.packageName.split(".")
        val three = split.subList(0, min(3, split.size))
        val packagePath = three.joinToString(".")

        packageList[packagePath] = instance
    }

    /**
     * Removes package name cache of [clazz] previously stored
     * by calling [cacheInstance].
     *
     * @param clazz any Class belonging to a package.
     * @return the cached instance if any.
     */
    fun <T : Any> clearInstance(clazz: Class<T>) : V? {
        return packageList.remove(getShortPackageName(clazz)) as V?
    }

    /**
     * Returns instance stored by package of [clazz]
     *
     * @param clazz any Class belonging to a package.
     * @return the cached instance.
     */
    fun <T : Any> getInstance(clazz: Class<T>): V {
        return packageList[getShortPackageName(clazz)]!! as V
    }

    /**
     * Returns instance stored by package of [clazz] or null.
     *
     * @param clazz any Class belonging to a package.
     * @return the cached instance or null.
     */
    fun <T : Any> getInstanceOrNull(clazz: Class<T>) : V? {
        return packageList[getShortPackageName(clazz)] as V?
    }

    private fun getShortPackageName(clazz: Class<*>): String {
        val split = clazz.packageName.split(".")
        val three = split.subList(0, min(3, split.size))
        return three.joinToString(".")
    }
}