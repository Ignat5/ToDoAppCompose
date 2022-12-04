package com.example.todoappcompose.util

object Utils {
    fun getRoundedPercent(percent: Float, digits: Int) =
        String.format("%.${digits}f", percent)
}