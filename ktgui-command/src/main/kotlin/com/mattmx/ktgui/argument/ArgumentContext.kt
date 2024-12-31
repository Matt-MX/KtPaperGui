package com.mattmx.ktgui.argument

class ArgumentContext<T>(
    val value: T?,
    val stringValue: String
) {
    override fun toString(): String {
        return stringValue
    }
}