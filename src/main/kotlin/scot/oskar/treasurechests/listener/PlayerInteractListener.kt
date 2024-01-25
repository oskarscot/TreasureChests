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

        if (previousInteraction == null) {
            // Player has not interacted with the chest before
            transaction {
                PlayerInteractions.insert {
                    it[PlayerInteractions.player] = player.uniqueId
                    it[chestId] = chest.id
                }
            }
            player.sendMessage("<gray>You have interacted with the chest for the first time".toMiniMessage())
        } else {
            // Player has interacted with the chest before
            val lastInteraction = previousInteraction[PlayerInteractions.lastInteraction]
            val timeSinceLastInteraction = now.minus(lastInteraction.toInstant(TimeZone.currentSystemDefault()))

            if (timeSinceLastInteraction < chest.openInterval) {
                // Player has interacted with the chest too recently
                val remainingTime = chest.openInterval - timeSinceLastInteraction
                player.sendMessage("<gray>You can interact with the chest again in <yellow>$remainingTime</yellow>".toMiniMessage())
            } else {
                // Player has interacted with the chest recently enough
                player.server.createInventory(player, chest.inventoryRows * 9, Component.text(chest.id.toString())).apply {
                    chest.contents.forEachIndexed { index, itemStack ->
                        this.setItem(index, itemStack)
                    }
                    player.openInventory(this)
                }

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
}