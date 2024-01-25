package scot.oskar.treasurechests.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PlayerInteractions : Table() {
    val player = uuid("player")
    val chestId = uuid("chest_id")
    val lastInteraction = datetime("last_interaction").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(player, chestId)
}