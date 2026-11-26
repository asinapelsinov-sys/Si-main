package com.example.simine;

import com.example.simine.network.BlizzardPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SiMine implements ModInitializer {
    public static final String MODID = "simine";

    public static final SimpleParticleType BLIZZARD_SNOW = FabricParticleTypes.simple();

    public static final SoundEvent BLIZZARD_WIND = SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(MODID, "blizzard_wind"));

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                ResourceLocation.fromNamespaceAndPath(MODID, "blizzard_snow"), BLIZZARD_SNOW);

        Registry.register(BuiltInRegistries.SOUND_EVENT, BLIZZARD_WIND.getLocation(), BLIZZARD_WIND);

        PayloadTypeRegistry.playS2C().register(BlizzardPayload.TYPE, BlizzardPayload.CODEC);

        BlizzardManager.init();
    }
}
