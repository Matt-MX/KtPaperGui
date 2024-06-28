package com.mattmx.ktgui.commands.declarative.syntax

class Parser(
    val source: String
) {
    private val lexer = Lexer(source)
    private val tokens = arrayListOf<SyntaxToken>()
    private var position = 0

    init {
        var token: SyntaxToken? = null

        while (token?.kind != SyntaxKind.END_OF_FILE && token?.kind != SyntaxKind.BAD_TOKEN) {
            token = lexer.lex()
            if (token.kind != SyntaxKind.WHITESPACE && token.kind != SyntaxKind.BAD_TOKEN) {
                tokens += token
            }
        }
    }

    private fun peek(i: Int): SyntaxToken {
        val index = position + i
        if (index >= tokens.size)
            return tokens.last()
        return tokens[index]
    }

    private fun current() = peek(0)

    private fun nextToken(): SyntaxToken {
        val current = current()
        position++
        return current
    }

    private fun matchToken(kind: SyntaxKind): SyntaxToken {
        if (current().kind == kind) {
            return nextToken()
        }
        return SyntaxToken(SyntaxKind.BAD_TOKEN, current().start, null, null)
    }

    fun parse(): ArrayList<CommandBuilderSyntax> {
        val parts = arrayListOf<CommandBuilderSyntax>()
        while (position < tokens.size - 1) {
            val part = when (current().kind()) {
                SyntaxKind.OPEN_DIAMOND -> parseVariableDeclaration()
                SyntaxKind.FORWARD_SLASH -> parseCommand()
                SyntaxKind.IDENTIFIER -> parseSubCommand()
                else -> error("Unexpected syntax token ${current().kind()}")
            }

            parts += part
        }
        return parts
    }

    /**
     * Variable declaration sysntax: '<var_name:type>'
     */
    private fun parseVariableDeclaration(): VariableDeclarationSyntax {
        val openDiamondBraces = matchToken(SyntaxKind.OPEN_DIAMOND)
        val varName = matchToken(SyntaxKind.IDENTIFIER)
        val colon = matchToken(SyntaxKind.COLON)
        val type = matchToken(SyntaxKind.IDENTIFIER)
        val ellipsis = matchToken(SyntaxKind.ELLIPSIS)
        val optional = matchToken(SyntaxKind.QUESTION)
        val closeDiamondBraces = matchToken(SyntaxKind.CLOSE_DIAMOND)

        return VariableDeclarationSyntax(openDiamondBraces, varName, colon, type, ellipsis, optional, closeDiamondBraces)
    }

    private fun parseCommand(): CommandDeclarationSyntax {
        val slash = matchToken(SyntaxKind.FORWARD_SLASH)
        val commandName = matchToken(SyntaxKind.IDENTIFIER)

        return CommandDeclarationSyntax(slash, commandName)
    }

    private fun parseSubCommand(): SubCommandDeclarationSyntax {
        val commandName = matchToken(SyntaxKind.IDENTIFIER)

        return SubCommandDeclarationSyntax(commandName)
    }
}