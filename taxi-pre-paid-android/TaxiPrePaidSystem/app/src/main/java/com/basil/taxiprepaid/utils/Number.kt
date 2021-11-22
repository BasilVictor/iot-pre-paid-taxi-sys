package com.basil.taxiprepaid.utils

import java.text.Format
import java.text.NumberFormat
import java.util.*

fun Number.toCurrencyFormat() : String {
    val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
    return format.format(this)
}