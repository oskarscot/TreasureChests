package scot.oskar.treasurechests

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import scot.oskar.treasurechests.command.RegisterChestCommand
import scot.oskar.treasurechests.config.PluginConfiguration
import scot.oskar.treasurechests.listener.PlayerInteractListener
import scot.oskar.treasurechests.model.PlayerInteractions
import java.io.File

open class TreasureChests: JavaPlugin() {

    private lateinit var pluginConfiguration: PluginConfiguration
    private val configFile = File(this.dataFolder, "config.yml")

    override fun onEnable() {
        logger.info("Loading configuration...")
        pluginConfiguration = ConfigManager.create(PluginConfiguration::class.java) {
            it.withConfigurer(YamlBukkitConfigurer(), SerdesCommons())
            it.withSerdesPack(SerdesBukkit())
            it.withBindFile(configFile)
            it.saveDefaults()
            it.load(true)
        }

        logger.info("Connecting to database...")
        Database.connect(
            url = pluginConfiguration.database.url,
            driver = pluginConfiguration.database.driver,
            user = pluginConfiguration.database.user,
            password = pluginConfiguration.database.password
        )


        // Create tables if they don't exist, and log SQL queries if enabled
        transaction {
            if (pluginConfiguration.database.logger) {
                addLogger(StdOutSqlLogger)
            }
            SchemaUtils.create(PlayerInteractions)
        }

        this.server.getPluginCommand("registerchest")?.setExecutor(RegisterChestCommand(pluginConfiguration))
        this.server.pluginManager.registerEvents(PlayerInteractListener(pluginConfiguration), this)
    }
}