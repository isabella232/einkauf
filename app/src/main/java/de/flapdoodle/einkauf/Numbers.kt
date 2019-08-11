package de.flapdoodle.einkauf

import java.text.DecimalFormat

object Numbers {

    val formatter=DecimalFormat.getNumberInstance()

    fun amountAsEuro(amount: Int): String {
        return formatter.format(amount / 100.0)+"€"
    }
}