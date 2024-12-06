package com.mattmx.ktgui.util

open class UnaryOperatorMap<K, V> : HashMap<K, V>() {

    operator fun Pair<K, V>.unaryPlus() = put(first, second)

    operator fun Pair<K, V>.unaryMinus() = remove(first, second)

    operator fun K.unaryMinus() = remove(this)

}