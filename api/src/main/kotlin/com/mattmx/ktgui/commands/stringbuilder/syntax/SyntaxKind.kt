package com.mattmx.ktgui.commands.stringbuilder.syntax

enum class SyntaxKind(
    val chars: String? = null
) {
    IDENTIFIER,

    COLON(":"),
    FORWARD_SLASH("/"),
    OPEN_DIAMOND("<"),
    CLOSE_DIAMOND(">"),
    ELLIPSIS("..."),
    OPEN_SQUARE("["),
    CLOSE_SQUARE("]"),

    WHITESPACE,

    END_OF_FILE("\u0000"),
    BAD_TOKEN,

    COMMAND_DECLARATION,
    SUB_COMMAND_DECLARATION,
    VARIABLE_DECLARATION,
}