package scot.oskar.treasurechests

import java.time.format.DateTimeParseException
import kotlin.time.Duration

object TimeHelper {

    fun formatDuration(isoDurationString: String): String {
        try {
            val duration = Duration.parse(isoDurationString)
            val hours = duration.inWholeHours
            val minutes = duration.inWholeMinutes
            val seconds = duration.inWholeSeconds

            val formattedString = buildString {
                if (hours > 0) append("$hours ${if (hours == 1L) "hour" else "hours"} ")
                if (minutes > 0) append("$minutes ${if (minutes == 1L) "minute" else "minutes"} ")
                if (seconds > 0) append("$seconds ${if (seconds == 1L) "second" else "seconds"} ")
            }.trim()

            return formattedString.ifEmpty { "0 seconds" }

        } catch (e: DateTimeParseException) {
            return "Invalid duration format"
        }
    }

}