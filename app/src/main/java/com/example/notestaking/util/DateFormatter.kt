package com.example.notestaking.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {

    private val displayFormat = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    fun formatDateTime(timestamp: Long): String = displayFormat.format(Date(timestamp))

    fun formatShort(timestamp: Long): String = shortFormat.format(Date(timestamp))
}
