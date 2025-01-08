package com.mattmx.ktgui.sound

data class LocationEmitter(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
) {
    var relative: Boolean = false
}