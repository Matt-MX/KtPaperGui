package com.mattmx.ktgui.commands.declarative.syntax

class Lexer(
    private val text: String
) {
    private var position: Int = 0
    private var start: Int = 0
    private var kind = SyntaxKind.BAD_TOKEN
    private var value: Any? = null

    private fun current() = peek(0)
    private fun next() = position++
    private fun peek(i: Int): Char {
        val index = position + i
        return if (index >= text.length) '\u0000'
        else text[index]
    }

    fun lex(): SyntaxToken {
        start = position
        kind = SyntaxKind.BAD_TOKEN
        value = null

        val current = current()
        if (current.isWhitespace()) {
            readWhitespaceToken()
        } else if (current.isLetter()) {
            readIdentifierToken()
        } else {
            var value: SyntaxKind? = null
            for (possible in SyntaxKind.values()) {
                var isMatch = true
                while (isMatch) {
                    if (possible.chars == null) {
                        isMatch = false
                        continue
                    }

                    val localIndex = position - start

                    if (localIndex >= possible.chars.length) {
                        value = possible
                        break
                    }

                    isMatch = current() == possible.chars[localIndex]

                    position++
                }

                if (value != null) break
                position = start
            }
            kind = value ?: kind
        }

        val subText = if (kind == SyntaxKind.END_OF_FILE) "\u0000" else text.substring(start, position)

        return SyntaxToken(kind, start, subText, value)
    }

    private fun readWhitespaceToken() {
        while (current().isWhitespace())
            next()
        kind = SyntaxKind.WHITESPACE
    }

    private fun readIdentifierToken() {
        while (isVarNameChar(current()))
            next()

        kind = SyntaxKind.IDENTIFIER
        this.value = text.substring(start, position)
    }

    private fun isVarNameChar(char: Char) =
        char.isLetterOrDigit() || char in listOf('_', '-')

}