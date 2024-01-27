package scot.oskar.treasurechests.serializer

import eu.okaeri.configs.schema.GenericsPair
import eu.okaeri.configs.serdes.BidirectionalTransformer
import eu.okaeri.configs.serdes.SerdesContext
import kotlin.time.Duration

class DurationTransformer: BidirectionalTransformer<String, Duration>() {

    override fun getPair(): GenericsPair<String, Duration> {
        return genericsPair(String::class.java, Duration::class.java)
    }

    override fun rightToLeft(data: Duration, serdesContext: SerdesContext): String {
        return data.toIsoString()
    }

    override fun leftToRight(data: String, serdesContext: SerdesContext): Duration {
        return Duration.parse(data)
    }
}