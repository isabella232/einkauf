package de.flapdoodle.einkauf

import java.text.DecimalFormat

typealias MONEY = Int

object Numbers {

    val formatter=DecimalFormat.getNumberInstance()

    fun amountAsEuro(amount: MONEY): String {
        return amountAsString(amount)+"€"
    }

    fun amountAsString(amount: MONEY?): String {
        return if (amount!=null) formatter.format(amount / 100.0) else ""
    }

    // input is 14,314
    fun parse(input: String): MONEY? {
        val result = formatter.parse(input)
        return result?.let { it.toDouble() * 100.0 }?.toInt()
    }
}