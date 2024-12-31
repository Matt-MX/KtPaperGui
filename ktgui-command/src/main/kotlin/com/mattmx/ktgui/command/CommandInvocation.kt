package com.mattmx.ktgui.command

class CommandInvocation<T>(
    val sender: T,
    val args: Array<String>,
    val aliasUsed: String
)