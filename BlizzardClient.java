package com.example.blizzardmod.client;

import com.example.blizzardmod.BlizzardMod;
import com.example.blizzardmod.network.BlizzardPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class BlizzardClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance()
                .register(BlizzardMod.BLIZZARD_SNOW, BlizzardSnowParticle.Provider::new);

        ClientPlayNetworking.registerGlobalReceiver(BlizzardPayload.TYPE,
                (payload, context) -> ClientBlizzardState.active = payload.active());

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                ClientBlizzardState.active = false);

        ClientTickEvents.END_CLIENT_TICK.register(BlizzardClient::onClientTick);
    }

    private static void onClientTick(Minecraft mc) {
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;

        if (!ClientBlizzardState.active || level == null || player == null || mc.isPaused()) return;
        if (level.dimension() != Level.OVERWORLD) return;

        RandomSource r = level.random;
        for (int i = 0; i < 40; i++) {
            double x = player.getX() + (r.nextDouble() - 0.5) * 40.0;
            double z = player.getZ() + (r.nextDouble() - 0.5) * 40.0;
            double y = player.getY() + 2.0 + r.nextDouble() * 14.0;

            // не спавним частицы под крышами / внутри пещер
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING,
                    (int) Math.floor(x), (int) Math.floor(z));
            if (y < top) continue;

            level.addParticle(BlizzardMod.BLIZZARD_SNOW, x, y, z,
                    -0.15 - r.nextDouble() * 0.12,   // ветер
                    -0.06 - r.nextDouble() * 0.05,   // падение
                    0.0);
        }
    }
}
