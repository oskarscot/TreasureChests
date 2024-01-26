package scot.oskar.treasurechests.serializer

import eu.okaeri.configs.schema.GenericsDeclaration
import eu.okaeri.configs.serdes.DeserializationData
import eu.okaeri.configs.serdes.ObjectSerializer
import eu.okaeri.configs.serdes.SerializationData
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import scot.oskar.treasurechests.data.TreasureChestData
import java.util.*
import kotlin.time.Duration

class TreasureChestSerializer: ObjectSerializer<TreasureChestData> {
    override fun supports(type: Class<in TreasureChestData>): Boolean =
        TreasureChestData::class.java.isAssignableFrom(type)

    override fun serialize(chest: TreasureChestData, data: SerializationData, generics: GenericsDeclaration) {
        data.add("id", chest.id)
        data.add("location", chest.location)
        data.add("openInterval", chest.openInterval)
        data.add("contents", chest.contents)
    }

    override fun deserialize(data: DeserializationData, generics: GenericsDeclaration): TreasureChestData {
        val id = data.get("id", UUID::class.java)
        val location = data.get("location", Location::class.java)
        val openInterval = data.get("openInterval", Duration::class.java)
        val contents = data.getAsMap("contents", Int::class.java, ItemStack::class.java)
        return TreasureChestData(id, location, openInterval, contents)
    }
}