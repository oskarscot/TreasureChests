package scot.oskar.treasurechests.command

import org.bukkit.block.Container
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
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command")
            return true
        }

        if (args.isNullOrEmpty()) {
            sender.sendMessage(pluginConfiguration.messages.usage.toMiniMessage().replaceText {
                it.match("<usage>").replacement("/registerchest [interval]")
            })
            return true
        }

        if(args.size > 1) {
            sender.sendMessage(pluginConfiguration.messages.usage.toMiniMessage().replaceText {
                it.match("<usage>").replacement("/registerchest [interval]")
            })
            return true
        }

        // check what block the player is looking at
        val block = sender.getTargetBlock(null, 5)
        if (block.type != pluginConfiguration.chests.chestItem.type) {
            sender.sendMessage("<gray>You must be looking at a <yellow>${pluginConfiguration.chests.chestItem.type}</yellow> to register it".toMiniMessage())
            return true
        }

        // check if the chest is already registered
        if(pluginConfiguration.chests.savedChests.any { it.location == block.location }) {
            sender.sendMessage("<gray>This chest is already registered".toMiniMessage())
            return true
        }

        val blockState = block.state

        // check if the block is a container
        if(blockState !is Container) {
            sender.sendMessage("<gray>This chest is not a container".toMiniMessage())
            return true
        }

        // add the chest to the list of saved chests
        pluginConfiguration.chests.savedChests.add(
            TreasureChestData(
                id = UUID.randomUUID(),
                location = block.location,
                openInterval = Duration.parse(args[0]),
                contents = blockState.inventory.contents.mapIndexedNotNull { index, itemStack ->
                    itemStack?.let { index to it }
                }.toMap()
            )
        )
        pluginConfiguration.save()
        sender.sendMessage("<gray>Chest registered".toMiniMessage())
        return true
    }
}