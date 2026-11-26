package com.example.simine;

import com.example.simine.network.BlizzardPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class SiMine implements ModInitializer {
    public static final String MODID = "simine";

    public static final SimpleParticleType BLIZZARD_SNOW = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                ResourceLocation.fromNamespaceAndPath(MODID, "blizzard_snow"), BLIZZARD_SNOW);

        PayloadTypeRegistry.playS2C().register(BlizzardPayload.TYPE, BlizzardPayload.CODEC);

        BlizzardManager.init();
    }
}
