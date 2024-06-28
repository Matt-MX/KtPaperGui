package com.mattmx.ktgui.commands.declarative.syntax

class VariableDeclarationSyntax(
    private val openDiamondBracesToken: SyntaxToken,
    private val varNameToken: SyntaxToken,
    private val colonToken: SyntaxToken,
    private val typeToken: SyntaxToken,
    private val ellipsisToken: SyntaxToken,
    private val optional: SyntaxToken,
    private val closeDiamondBracesToken: SyntaxToken,
) : CommandBuilderSyntax() {
    override fun kind() = SyntaxKind.VARIABLE_DECLARATION

    fun getName() = varNameToken.text!!

    fun getType() = typeToken.text!!

    fun isGreedy() = ellipsisToken.text != null

    fun isOptional() = optional.text != null

    override fun children() =
        listOf(openDiamondBracesToken, varNameToken, colonToken, typeToken, closeDiamondBracesToken)
}