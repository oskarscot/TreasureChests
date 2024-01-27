package scot.oskar.treasurechests.config

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import eu.okaeri.configs.annotation.NameModifier
import eu.okaeri.configs.annotation.NameStrategy
import eu.okaeri.configs.annotation.Names
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import scot.oskar.treasurechests.data.TreasureChestData

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
class PluginConfiguration: OkaeriConfig() {

    var database = DatabaseConfiguration()
    var chests = ChestsConfiguration()
    var messages = MessagesConfiguration()

    class ChestsConfiguration: OkaeriConfig() {
        @Comment("Item to use for chests")
        var chestItem: ItemStack = ItemStack(Material.CHEST)
        @Comment("List of all chests in the world")
        var savedChests: MutableList<TreasureChestData> = mutableListOf()
    }

    class DatabaseConfiguration: OkaeriConfig() {
        @Comment("JDBC connection string for the database")
        var url = "jdbc:postgresql://localhost:5432/treasurechests"
        @Comment("JDBC database driver")
        var driver = "org.postgresql.Driver"
        @Comment("Username to use when connecting to the database")
        var user = "postgres"
        @Comment("Password to use when connecting to the database")
        var password = "admin"
        @Comment("Whether to log SQL queries to the console")
        var logger = true
    }

    class MessagesConfiguration: OkaeriConfig() {
        var usage = "<gray>Correct usage: <yellow><usage></yellow> </gray>"
        var mustBeLookingAtBlock = "<gray>You must be looking at a <yellow><block></yellow> to register it</gray>"
        var chestAlreadyRegistered = "<gray>This chest is already registered</gray>"
        var blockNotAContainer = "<gray>Target block is not a container</gray>"
        var chestRegistered = "<gray>Chest registered</gray>"
        var cantInteractMessage = "<gray>You can interact with this chest again in <yellow><time></yellow></gray>"
    }
}