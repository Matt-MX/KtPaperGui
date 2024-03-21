package com.mattmx.ktgui.commands.stringbuilder.syntax

class CommandDeclarationSyntax(
    private val slashToken: SyntaxToken,
    private val commandNameToken: SyntaxToken
) : CommandBuilderSyntax() {
    override fun kind() = SyntaxKind.COMMAND_DECLARATION

    fun getName() = commandNameToken.text!!

    override fun children() = listOf(slashToken, commandNameToken)
}