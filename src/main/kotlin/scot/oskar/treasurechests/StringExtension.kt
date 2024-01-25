package scot.oskar.treasurechests

import net.kyori.adventure.text.minimessage.MiniMessage

fun String.toMiniMessage() = MiniMessage.miniMessage().deserialize(this)