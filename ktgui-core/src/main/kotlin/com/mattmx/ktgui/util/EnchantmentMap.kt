package com.mattmx.ktgui.util

class EnchantmentMap<E> : UnaryOperatorMap<E, Int>() {

    infix fun E.lvl(level: Int) = put(this, level)

}