package scot.oskar.treasurechests.command

import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import scot.oskar.treasurechests.data.TreasureChestData
import scot.oskar.treasurechests.config.PluginConfiguration
import scot.oskar.treasurechests.toMiniMessage
import java.util.*
import kotlin.time.Duration

class RegisterChestCommand(private val pluginConfiguration: PluginConfiguration): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        // Check if the sender is a player
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command")
            return true
        }

        if(args.isNullOrEmpty()) {
            sender.sendMessage("<gray>Usage: <yellow>/registerchest [interval]".toMiniMessage())
            return true
        }

        if(args.size > 1) {
            sender.sendMessage("<gray>Usage: <yellow>/registerchest [interval]".toMiniMessage())
            return true
        }

        // check what block the player is looking at
        val block = sender.getTargetBlock(null, 5)
        if (block.type != pluginConfiguration.chests.chestItem.type) {
            sender.sendMessage("<gray>You must be looking at a <yellow>${pluginConfiguration.chests.chestItem.type}</yellow> to register it".toMiniMessage())
            return true
        }
        if(pluginConfiguration.chests.savedChests.any { it.location == block.location }) {
            sender.sendMessage("<gray>This chest is already registered".toMiniMessage())
            return true
        }
        val chest: Chest = block.state as Chest

        pluginConfiguration.chests.savedChests.add(
            TreasureChestData(
                id = UUID.randomUUID(),
                location = block.location,
                openInterval = Duration.parse(args[0]),
                inventoryRows = 3,
                contents = chest.inventory.contents.toList()
            )
        )
        //pluginConfiguration.save()
        return true
    }
}