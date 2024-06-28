package com.mattmx.ktgui.extensions

fun Int.downTo(end: Int): List<Int> {
    return (this..end).toList().filter { (it - this) % 9 == 0 }
}