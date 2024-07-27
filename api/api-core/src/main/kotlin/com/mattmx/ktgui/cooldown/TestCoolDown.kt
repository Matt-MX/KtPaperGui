package com.mattmx.ktgui.cooldown

import java.time.Duration

private class CoolDownTest(
    val name: String,
    val cooldown: ActionCoolDown<CoolDownTest>
) {
    override fun toString(): String {
        return "$name (${cooldown.test(this)})"
    }
}

fun main() {
    val cooldown = ActionCoolDown<CoolDownTest>(Duration.ofSeconds(2))
    val matt = CoolDownTest("Matt", cooldown)
    val jack = CoolDownTest("Jack", cooldown)

    println(matt)
    println(matt)
    println(jack)

    Thread.sleep(2000)
    println(matt)
}