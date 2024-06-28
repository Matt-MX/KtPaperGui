package com.mattmx.ktgui.utils

val cache = InstancePackageClassCache<MockPluginClass>()

inline fun <reified T : Any> T.test() {
    cache.getInstance(T::class.java).hello()
}

fun main() {
    val plugin = MockPluginClass()
    cache.cacheInstance(MockPluginClass::class.java, plugin)

    StopWatch().test()
}

class MockPluginClass {
    fun hello() {
        println("hello world")
    }
}