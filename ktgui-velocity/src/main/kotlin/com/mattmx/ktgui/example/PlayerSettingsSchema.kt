package com.mattmx.ktgui.example

class PlayerSettingsSchema(
    @BooleanOption(
        name = "Some Value",
        icon = "minecraft:ender_pearl"
    )
    var optionOne: Boolean = false,

    @BooleanOption(
        name = "Some Other Value",
        icon = "minecraft:diamond_sword"
    )
    var optionTwo: Boolean = true,
)