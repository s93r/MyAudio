package it.uninsubria.pdm.rizzi.myaudio

import java.util.*
import java.util.concurrent.TimeUnit

class CreationTime {

    fun timeOfCreation(duration: Long) : String {

        val date: Date = Date()

        val seconds = TimeUnit.MILLISECONDS.toSeconds(date.time - duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(date.time - duration)
        val hours = TimeUnit.MILLISECONDS.toHours(date.time - duration)
        val days = TimeUnit.MILLISECONDS.toDays(date.time - duration)

        if (seconds < 60) {
            return "created just now"
        } else if (minutes == 1L) {
            return "created a minute ago"
        } else if (minutes in 2..59) {
            return "created $minutes minutes ago"
        } else if (hours == 1L) {
            return "created an hour ago"
        } else if (hours in 2..23) {
            return "created $hours hours ago"
        } else if (days == 1L) {
            return "created a day ago"
        } else {
            return "created $days days ago"
        }
    }
}