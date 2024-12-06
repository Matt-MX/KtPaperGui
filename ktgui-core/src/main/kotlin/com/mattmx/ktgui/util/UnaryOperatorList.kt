package com.mattmx.ktgui.util

open class UnaryOperatorList<T> : ArrayList<T>() {

    operator fun T.unaryPlus() = add(this)

    operator fun T.unaryMinus() = remove(this)

}