package scot.oskar.treasurechests.listener

import kotlinx.datetime.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import scot.oskar.treasurechests.TimeHelper
import scot.oskar.treasurechests.data.TreasureChestData
import scot.oskar.treasurechests.config.PluginConfiguration
import scot.oskar.treasurechests.model.PlayerInteractions
import scot.oskar.treasurechests.toMiniMessage

class PlayerInteractListener(private val pluginConfiguration: PluginConfiguration): Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.clickedBlock?.type != pluginConfiguration.chests.chestItem.type) {
            return
        }
        //check if the chest is in the list of chests from config
        val chest = pluginConfiguration.chests.savedChests.find {
            it.location == event.clickedBlock?.location
        } ?: return

        event.isCancelled = true

        //handle the chest interaction
        this.handleChestInteraction(event.player, chest)
    }

    /**
     * Handles the interaction between a player and a treasure chest
     */
    private fun handleChestInteraction(player: Player, chest: TreasureChestData) {
        val now = Clock.System.now()

        // Check if the player has interacted with the chest before
        val previousInteraction = transaction {
            PlayerInteractions.selectAll().where {
                (PlayerInteractions.player eq player.uniqueId) and
                (PlayerInteractions.chestId eq chest.id)
            }.firstOrNull()
        }

        // Player has not interacted with the chest before
        // Open the chest and save the new interaction
        if (previousInteraction == null) {

            // Save the new interaction with the chest before opening it
            transaction {
                PlayerInteractions.insert {
                    it[PlayerInteractions.player] = player.uniqueId
                    it[chestId] = chest.id
                }
            }

            this.openChest(player, chest)

        } else {
            val lastInteraction = previousInteraction[PlayerInteractions.lastInteraction]
            val timeSinceLastInteraction = now - lastInteraction.toInstant(TimeZone.currentSystemDefault())

            // Player has interacted with the chest too recently
            if (timeSinceLastInteraction < chest.openInterval) {
                val remainingTime = chest.openInterval - timeSinceLastInteraction
                player.sendMessage(pluginConfiguration.messages.cantInteractMessage.toMiniMessage().replaceText {
                    it.match("<time>").replacement(TimeHelper.formatDuration(remainingTime.toIsoString()))
                })
            } else {

                this.openChest(player, chest)

                // Update the last interaction time
                transaction {
                    PlayerInteractions.update({
                        (PlayerInteractions.player eq player.uniqueId) and (PlayerInteractions.chestId eq chest.id)
                    }) {
                        it[PlayerInteractions.lastInteraction] = now.toLocalDateTime(TimeZone.currentSystemDefault())
                    }
                }
            }
        }
    }

    /**
     * Opens the chest for the player
     */
    private fun openChest(player: Player, chest: TreasureChestData) {
        player.server.createInventory(player, 3 * 9, Component.text(chest.id.toString())).apply {
            chest.contents.forEach { (index, itemStack) ->
                this.setItem(index, itemStack)
            }
            player.openInventory(this)
        }
    }
}