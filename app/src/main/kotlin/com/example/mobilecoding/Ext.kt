package com.example.mobilecoding

import java.text.SimpleDateFormat
import java.util.Locale

fun String.formatDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun Double.roundRating(): String {
    return String.format(Locale.US, "%.1f", this)
}

fun Int.formatRuntime(): String {
    val hours = this / 60
    val minutes = this % 60
    return String.format(Locale.US, "%02dh :%02dmin", hours, minutes)
}

fun Int.formatBudget(): String {
    val formatter = java.text.NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(this)
}