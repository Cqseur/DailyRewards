package cqseur.dailyrewards

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent

/**
 * Custom Sounds
 **/
object ModSoundEvents {
    val HOVER: SoundEvent = register("hover")
    val PICK: SoundEvent = register("pick")
    val COMMON: SoundEvent = register("common")
    val RARE: SoundEvent = register("rare")
    val EPIC: SoundEvent = register("epic")
    val LEGENDARY: SoundEvent = register("legendary")

    private fun register(name: String): SoundEvent {
        val id = Identifier.fromNamespaceAndPath("dailyrewards", name)
        val event = SoundEvent.createVariableRangeEvent(id)
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, event)
    }

    fun init() {}
}
