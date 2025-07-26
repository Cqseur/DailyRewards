package cqseur.dailyrewards

import cqseur.dailyrewards.ui.RewardScreen
import cqseur.dailyrewards.ui.DailyRewardsConfigScreen
import cqseur.dailyrewards.config.DailyRewardsConfig
import cqseur.dailyrewards.RewardFetcher
import cqseur.dailyrewards.ModSoundEvents

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.minecraft.text.Text
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.network.message.SignedMessage
import com.mojang.authlib.GameProfile
import net.minecraft.network.message.MessageType
import java.time.Instant
import org.slf4j.LoggerFactory
import java.util.regex.Pattern
import net.minecraft.client.MinecraftClient
import kotlin.random.Random

class DailyRewardsClient : ClientModInitializer {
    private var pendingOffer: RewardOffer? = null
    private var openConfigNextTick = false
    private val logger = LoggerFactory.getLogger("[DailyRewards-Client]")

    private val linkPattern: Pattern = Pattern.compile("https://rewards\\.hypixel\\.net/claim-reward/([a-zA-Z0-9_-]+)")
 
    override fun onInitializeClient() {
        ModSoundEvents.init()
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("dailyrewards").executes {
                    val mc = MinecraftClient.getInstance()
                    mc.execute {
                        openConfigNextTick = true
                    }
                    1
                }
            )
            dispatcher.register(
                ClientCommandManager.literal("debugcards").executes { ctx ->
                    val mc = MinecraftClient.getInstance()
                    val devUuidRaw = "6d1c17283f5e4ea4ba64a2cebb6c6a3e"
                    val currentUuidRaw = mc.player?.uuid?.toString()?.replace("-", "") ?: ""
                    if (currentUuidRaw != devUuidRaw) {
                        mc.player?.sendMessage(Text.literal(DailyRewardsConfig.PREFIX + "§cYou are not allowed to use this command."), false)
                        return@executes 0
                    }
                    val commonOptions = listOf(
                        Pair("Murder Mystery Coins", "coins") to 1000..10000,
                        Pair("Mystery Dust", "dust") to 1..20,
                        Pair("Arcade Coins", "coins") to 250..5000
                    )
                    val rareOptions = listOf(
                        Pair("Souls", "souls") to 2..10,
                        Pair("BedWars XP", "experience") to 100..1000,
                        Pair("SkyWars Tokens", "coins") to 1..5
                    )
                    val epicOptions = listOf(
                        Pair("Hypixel XP", "experience") to 1000..5000,
                        Pair("Rare Debug Card", "chest_open") to 1..2
                    )
                    val legendaryOptions = listOf(
                        Pair("Reward Token", "adsense_token") to 1..1,
                        Pair("Mystery Box", "mystery_box") to 1..1
                    )
                    fun <T> pick(list: List<T>) = list[Random.nextInt(list.size)]

                    val cards = listOf(
                        run {
                            val (info, range) = pick(commonOptions)
                            val (name, icon) = info
                            RewardCard(name = name, amount = range.random().toString(), description = "Debug $name", rarity = "common", iconUrl = icon)
                        },
                        run {
                            val (info, range) = pick(commonOptions)
                            val (name, icon) = info
                            RewardCard(name = name, amount = range.random().toString(), description = "Debug $name", rarity = "common", iconUrl = icon)
                        },
                        run {
                            val (info, range) = pick(rareOptions)
                            val (name, icon) = info
                            RewardCard(name = name, amount = range.random().toString(), description = "Debug $name", rarity = "rare", iconUrl = icon)
                        },
                        run {
                            val (info, range) = pick(epicOptions)
                            val (name, icon) = info
                            RewardCard(name = name, amount = range.random().toString(), description = "Debug $name", rarity = "epic", iconUrl = icon)
                        },
                        run {
                            val (info, range) = pick(legendaryOptions)
                            val (name, icon) = info
                            RewardCard(name = name, amount = range.random().toString(), description = "Debug $name", rarity = "legendary", iconUrl = icon)
                        }
                    )
                    val offer = RewardOffer("debug", cards)
                    pendingOffer = offer
                    1
                }
            )
        }

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (openConfigNextTick && client.currentScreen == null) {
                client.setScreen(DailyRewardsConfigScreen.create(null))
                openConfigNextTick = false
            }
            
            pendingOffer?.let { offer ->
                if (client.currentScreen == null) {
                    client.setScreen(RewardScreen(offer))
                    pendingOffer = null
                }
            }
        }

        ClientReceiveMessageEvents.GAME.register { msg: Text, overlay: Boolean ->
            val matcher = linkPattern.matcher(msg.string)
            if (matcher.find()) {
                val id = matcher.group(1)
                RewardFetcher.fetch(id)
            }
        }
    }
}