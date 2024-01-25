package scot.oskar.treasurechests.data

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import java.util.UUID
import kotlin.time.Duration

data class TreasureChestData(
    val id: UUID,
    val location: Location,
    val openInterval: Duration,
    val inventoryRows: Int,
    val contents: List<ItemStack?>
)