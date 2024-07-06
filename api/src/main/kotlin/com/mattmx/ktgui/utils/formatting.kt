package com.mattmx.ktgui.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Duration
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Format a [Duration] into a pretty string.
 *
 * `e.g "2d 1h 30m 1s"`
 *
 * @return formatted string
 */
fun Duration.pretty(): String {
    val days = toDaysPart()
    val hours = toHoursPart()
    val minutes = toMinutesPart()
    val seconds = toSecondsPart()
    return (
            (if (days > 0) "${days}d " else "")
                    + (if (hours > 0) "${hours}h " else "")
                    + (if (minutes > 0) "${minutes}m " else "")
                    + "${seconds}s"
            )
}

/**
 * Round a [Double] to a decimal place.
 *
 * @param decimalPlace number of following digits.
 * @return rounded [Double]
 */
fun Double.dp(decimalPlace: Int = 2): Double {
    val df = DecimalFormat("#." + "#".repeat(decimalPlace))
    df.roundingMode = RoundingMode.CEILING

    return df.format(this).toDouble()
}

/**
 * Round a [Float] to a decimal place.
 *
 * @param decimalPlace number of following digits.
 * @return rounded [Float]
 */
fun Float.dp(decimalPlace: Int = 2) : Float {
    val df = DecimalFormat("#." + "#".repeat(decimalPlace))
    df.roundingMode = RoundingMode.CEILING

    return df.format(this).toFloat()
}

/**
 * Format larger numbers to be shorter.
 *
 * `e.g 142332 -> 142.3k`
 *
 * @return formatted number.
 */
fun Number.pretty(): String {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0")
            .apply { roundingMode = RoundingMode.FLOOR }
            .format(numValue / 10.toDouble().pow(base * 3)) + suffix[base]
    } else {
        DecimalFormat("#,##0")
            .apply { roundingMode = RoundingMode.FLOOR }
            .format(numValue)
    }
}

/**
 * Insert commas every 3 digits.
 *
 * `e.g 142332 -> 142,332`
 *
 * @return formatted [Int] as a [String]
 */
fun Int.commas() = "%,d".format(this)

/**
 * Insert commas every 3 digits.
 *
 * `e.g 142332 -> 142,332`
 *
 * @return formatted [Long] as a [String]
 */
fun Long.commas() = "%,d".format(this)