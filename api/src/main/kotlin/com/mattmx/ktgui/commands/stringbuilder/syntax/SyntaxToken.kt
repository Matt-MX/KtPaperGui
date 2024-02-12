package com.mattmx.ktgui.commands.stringbuilder.syntax

class SyntaxToken(
    val kind: SyntaxKind,
    val start: Int,
    val text: String?,
    val value: Any?
) : SyntaxNode() {
    override fun kind() = kind
}