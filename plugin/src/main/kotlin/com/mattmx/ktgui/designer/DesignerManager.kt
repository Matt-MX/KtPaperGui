package com.mattmx.ktgui.designer

import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.commands.declarative.arg.impl.*
import com.mattmx.ktgui.commands.declarative.arg.suggestsTopLevel
import com.mattmx.ktgui.commands.declarative.div
import com.mattmx.ktgui.commands.declarative.invoke
import com.mattmx.ktgui.utils.not
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class DesignerManager(
    private val plugin: KotlinGui
) {
    private val cachedDesigners = hashMapOf<String, GuiDesigner>()

    init {
        "designer" {
            buildAutomaticPermissions("ktgui.command")
            withDefaultUsageSubCommand(KotlinGui.defaultUsageOptions)

            val typeOrRowArgMessage = !"&cYou must provide an InventoryType or an amount of rows."
            val typeOrRow by multiChoiceStringArgument(
                (1..6).toList().map(Int::toString) +
                    InventoryType.entries.map { it.name.lowercase() }
            )
            typeOrRow invalid { reply(typeOrRowArgMessage) }

            val id by stringArgument()
            id matches "[0-9a-z]{3,16}".toRegex(RegexOption.IGNORE_CASE)
            id invalid { reply(!"&cMissing argument 'id'. Need an identifier for the designer UI.") }

            ("create" / typeOrRow / id).runs<Player> {
                val type = runCatching {
                    InventoryType.valueOf(typeOrRow().uppercase())
                }.getOrNull()
                val rows = typeOrRow().toIntOrNull()

                if (type == null && rows == null) {
                    return@runs reply(typeOrRowArgMessage)
                }

                if (cachedDesigners.containsKey(id())) {
                    return@runs reply("&cThere is already a designer by that name.")
                }

                val designer =
                    cachedDesigners.getOrPut(id()) { GuiDesigner(id(), type = type, rows = rows ?: 1) }
                designer.open(sender)
                reply(!"&aCreated a designer called ${id()}")
            }

            val existingDesigner by multiChoiceArgument { cachedDesigners }
            existingDesigner invalid { reply(!"&cInvalid id, create one using &7/designer create") }

            ("open" / existingDesigner).runs<Player> {
                existingDesigner().open(sender)
            }

            val newTitle by greedyStringArgument()
            ("setTitle" / existingDesigner / newTitle).runs<CommandSender> {
                existingDesigner().exportTitle = newTitle()
                reply(!"&aSet title of ${existingDesigner().id} to ${newTitle()}")
            }

            ("export" / existingDesigner).runs<CommandSender> {
                val file = existingDesigner().save(plugin)
                reply(!"&aSaved to /plugins/KtGUI/designer/${file.name}")
            }
        } register this
    }

}