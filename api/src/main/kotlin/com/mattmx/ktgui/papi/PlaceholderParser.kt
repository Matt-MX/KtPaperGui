package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.arg.ArgumentContext
import com.mattmx.ktgui.commands.declarative.arg.ArgumentProcessor
import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import org.bukkit.entity.Player

class PlaceholderParser(
    val wrapper: PlaceholderExpansionWrapper
) {

    fun parse(player: Player?, params: String): Pair<Placeholder, HashMap<String, ArgumentContext<*>>>? {
        val paramsSplit = wrapper.splitArgs(params)

        val args = hashMapOf<String, ArgumentContext<*>>()
        val baseContext: StorageCommandContext<Player>? = if (player != null)
            StorageCommandContext(player, paramsSplit.firstOrNull() ?: "", paramsSplit)
        else null

        for (placeholder in wrapper.getPlaceholdersList().sortedByDescending { it.priority }) {
            val identifier = placeholder.match.name
            if (identifier != Placeholder.EMPTY_PLACEHOLDER && paramsSplit.getOrNull(0) != identifier)
                continue

            val argumentParser = ArgumentProcessor(
                PlaceholderExpansionWrapper.emptyCommand,
                baseContext,
                paramsSplit.subList(1, paramsSplit.size)
            )

            var invalid = false

            for (expArg in placeholder.match.arguments) {
                if (invalid) continue

                val consumeResult = expArg.consume(argumentParser)

                if (expArg.isRequired() && consumeResult.isEmpty()) {
                    invalid = true
                    if (wrapper.isDebug) {
                        wrapper.owner.logger.warning(
                            "Placeholder(${
                                identifier
                            }) Failed parsing for arg $expArg in placeholder ${wrapper.name} - $consumeResult"
                        )
                    }
                    continue
                } else {
                    args[expArg.name()] = expArg.createContext(
                        PlaceholderExpansionWrapper.emptyCommand,
                        baseContext,
                        consumeResult.args ?: emptyList()
                    )
                }
            }
            if (invalid) continue

            return placeholder to args
        }
        return null
    }

}