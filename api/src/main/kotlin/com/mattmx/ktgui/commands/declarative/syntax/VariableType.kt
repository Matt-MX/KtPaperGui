package com.mattmx.ktgui.commands.declarative.syntax

data class VariableType(
    val typeName: String,
    val isVararg: Boolean,
    val isOptional: Boolean
)