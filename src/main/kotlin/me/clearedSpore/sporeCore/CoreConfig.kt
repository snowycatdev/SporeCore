package me.clearedSpore.sporeCore

import de.exlll.configlib.Comment
import de.exlll.configlib.Configuration
import me.clearedSpore.sporeCore.features.eco.`object`.BalanceFormat


@Configuration
data class CoreConfig(

    var general: GeneralConfig = GeneralConfig(),

    @Comment(
        "Toggle features you don't want/need!",
        "NOTE: Any feature you toggle requires a restart to apply!",
        "Some features require ingame permissions to use them.",
        "These permissions are always registered ingame."
    )
    var features: FeaturesConfig = FeaturesConfig(),

    var economy: EconomyConfig = EconomyConfig(),

    var kits: KitsConfig = KitsConfig(),

    var join: JoinConfig = JoinConfig()
)

@Configuration
data class JoinConfig(

    @Comment(
        "Should players be teleported to spawn when they join?"
    )
    var spawnOnJoin: Boolean = true,

    @Comment(
        "What title should the players see when they join?",
        "You can use %player% to return the players name",
        "Leave it empty if you don't want a title"
    )
    var title: String = "&c&lWelcome %player%!",

    @Comment(
        "What message should the players receive when they join?",
        "You can use %player% to return the players name",
        "Leave it empty if you don't want to send messages"
    )
    var message: List<String> = listOf(
        "&cWelcome &b%player%",
        "&fRun &c/help &f to view the help menu!"
    ),

    @Comment(
        "The sound that should play (e.g., ENTITY_PLAYER_LEVELUP)",
        "when a player joins!"
    )
    var joinSound: String = "ENTITY_PLAYER_LEVELUP",

    @Comment(
        "What message should it broadcast when a player",
        "joins for the first time?",
        "You can use %player% to return the players name",
        "You can use %join_count% to see how many players",
        "have joined in total! This will add 1 to it since there",
        "is a new player that joined",
        "Leave it empty if you don't want it to broadcast it"
    )
    var firstJoinMessage: List<String> = listOf(
        "&b%player% &fhas joined for the first time!! &7%join_count%",
        "&cGive them a warm welcome!"
    ),

    @Comment(
        "What gamemode should the player be put in when they join?",
        "Leave it empty if you don't want it to set a gamemode"
    )
    var gamemode: String = "SURVIVAL"


)

@Configuration
data class KitsConfig(

    @Comment(
        "Which kit should a player receive when they join for the first time and when.",
        "They die leave this empty if you don't want a kit selected.",
        "If you want to clear the players inventory before giving the kit",
        "Add |clear after it. Example:",
        "starter|clear"
    )
    var firstJoinKit: String = "starter",
    var deathKit: String = ""
)

@Configuration
data class GeneralConfig(

    @Comment(
        "This will be the prefix for ingame and console logging!"
    )
    var prefix: String = "SporeCore",

    @Comment(
        "When a player wants to teleport for example to spawn and",
        "they don't have the bypass permission (sporecore.bypass.teleport) they will",
        "need to wait a few seconds before being teleported. With this value you can set",
        "how long a player has to wait before being teleported."
    )
    var teleportTime: Int = 5
)

@Configuration
data class EconomyConfig(

    @Comment(
        "Enable or disable the economy system entirely.",
        "If false, Vault will not register this economy provider.",
        "When changing this option you will have to restart your server!"
    )
    var enabled: Boolean = true,

    @Comment(
        "The display name of this economy. This is how it appears in /vault-info, etc."
    )
    var name: String = "Spore Economy",

    @Comment(
        "Number of decimal digits to show in balances:",
        "0 → Whole numbers only (100)",
        "1 → One decimal (100.5)",
        "2 → Two decimals (100.50), etc."
    )
    var digits: Int = 2,

    @Comment(
        "Currency symbol (prefix before amount).",
        "Set to empty string if you don't want one."
    )
    var symbol: String = "$",

    @Comment(
        "Should there be a space between the symbol and the number?",
        "true → '$ 100.00', false → '$100.00'"
    )
    var spaceAfterSymbol: Boolean = false,

    @Comment(
        "Singular and plural currency names, used for display formatting.",
        "Example: 1 Coin / 5 Coins"
    )
    var singularName: String = "Coin",
    var pluralName: String = "Coins",

    @Comment(
        "If true, the symbol is placed before the number.",
        "If false, it’s placed after (e.g., '100 Coins')."
    )
    var symbolBeforeAmount: Boolean = true,

    @Comment(
        "Whether to use comma separators for thousands (1,000 vs 1000)."
    )
    var useThousandSeparator: Boolean = true,

    @Comment(
        "Default starting balance for new players."
    )
    var starterBalance: Double = 100.0,

    @Comment(
        "If true, every transaction is logged to console."
    )
    var logging: Boolean = true,

    @Comment(
        "If true, /baltop opens a GUI instead of a chat list."
    )
    var topMenu: Boolean = true,

    @Comment(
        "How many players it will show in the baltop menu.",
        "If you set the max amount too high it can cause server lag",
        "This is not recommended to change!"
    )
    var maxPlayers: Int = 250,

    @Comment(
        "Allow players to pay each other via /pay."
    )
    var paying: Boolean = true,

    @Comment(
        "Choose how balances are displayed:",
        "PLAIN → 5000",
        "DECIMAL → 5000.00",
        "COMPACT → 5k, 5m, etc."
    )
    var balanceFormat: BalanceFormat = BalanceFormat.COMPACT,
)

@Configuration
data class FeaturesConfig(

    var teleportRequest: Boolean = true,

    @Comment("Examples: anvil, craftingtable, smithing, etc")
    var utilityMenus: Boolean = true,

    var privateMessages: Boolean = true,

    var spawn: Boolean = true,

    var settings: Boolean = true,

    var warps: Boolean = true,

    var homes: Boolean = true,

    @Comment(
        "Allows players with the 'sporecore.chat.colored' permission to use",
        "color codes (e.g. &c, &b) and RGB colors in their messages.",
        "You only need to run /sporecore reload to apply changes — no server restart required."
    )
    var coloredChat: Boolean = true,

    var kits: Boolean = true,

    var stats: Boolean = true,

    @Comment(
        "The currency feature is a separate currency that you",
        "can use as a 'premium' shop. You can modify the currency",
        "and add a shop in the (currency).yml file.",
        "If you change the file name and you don't have a file",
        "with that name it will automatically generate a new one."
    )
    var currency: CurrencyConfig = CurrencyConfig()
)

@Configuration
data class CurrencyConfig(

    var enabled: Boolean = true,

    var configFile: String = "credits.yml"
)


