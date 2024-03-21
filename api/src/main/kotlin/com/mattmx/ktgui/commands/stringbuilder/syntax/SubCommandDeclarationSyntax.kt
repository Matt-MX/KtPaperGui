package com.mattmx.ktgui.commands.stringbuilder.syntax

class SubCommandDeclarationSyntax(
    private val commandName: SyntaxToken
) : CommandBuilderSyntax() {
    override fun kind() = SyntaxKind.SUB_COMMAND_DECLARATION

    fun getName() = commandName.text!!

    override fun children() = listOf(commandName)
}