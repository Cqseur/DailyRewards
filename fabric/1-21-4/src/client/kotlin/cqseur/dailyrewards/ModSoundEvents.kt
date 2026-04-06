package cqseur.dailyrewards

import net.minecraft.sound.SoundEvent
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

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
        val id = Identifier.of("dailyrewards", name)
        val event = SoundEvent.of(id)
        return Registry.register(Registries.SOUND_EVENT, id, event)
    }

    fun init() {}
}
