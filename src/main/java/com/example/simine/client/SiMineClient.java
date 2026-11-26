package com.example.simine.client;

import com.example.simine.SiMine;
import com.example.simine.network.BlizzardPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class SiMineClient implements ClientModInitializer {

    // ===== Настройки метели =====
    private static final int   PARTICLES_PER_TICK = 150;   // основной поток снега
    private static final int   NEAR_PARTICLES     = 30;    // густая завеса вокруг игрока
    private static final float RAMP_UP            = 0.02F; // скорость нарастания метели
    private static final float RAMP_DOWN          = 0.01F; // скорость затухания
    private static final float EXPOSURE_SPEED     = 0.05F; // как быстро эффекты меняются при входе под крышу
    private static final float DARKNESS_RAIN      = 1.0F;  // затемнение неба (0..1)
    private static final float DARKNESS_THUNDER   = 0.8F;  // доп. затемнение (0..1)

    private static boolean darknessApplied = false;
    private static BlizzardWindSound windSound = null;

    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance()
                .register(SiMine.BLIZZARD_SNOW, BlizzardSnowParticle.Provider::new);

        ClientPlayNetworking.registerGlobalReceiver(BlizzardPayload.TYPE,
                (payload, context) -> ClientBlizzardState.active = payload.active());

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientBlizzardState.active = false;
            ClientBlizzardState.intensity = 0.0F;
            ClientBlizzardState.exposure = 0.0F;
            darknessApplied = false;
            windSound = null;
        });

        ClientTickEvents.END_CLIENT_TICK.register(SiMineClient::onClientTick);
    }

    private static void onClientTick(Minecraft mc) {
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null || mc.isPaused()) return;

        // --- плавная сила метели ---
        if (ClientBlizzardState.active && level.dimension() == Level.OVERWORLD) {
            ClientBlizzardState.intensity = Math.min(1.0F, ClientBlizzardState.intensity + RAMP_UP);
        } else {
            ClientBlizzardState.intensity = Math.max(0.0F, ClientBlizzardState.intensity - RAMP_DOWN);
        }
        float k = ClientBlizzardState.intensity;

        // --- на улице игрок или под крышей ---
        boolean outdoors = level.canSeeSky(BlockPos.containing(player.getEyePosition()));
        float ex = ClientBlizzardState.exposure + (outdoors ? EXPOSURE_SPEED : -EXPOSURE_SPEED);
        ClientBlizzardState.exposure = Math.max(0.0F, Math.min(1.0F, ex));

        // --- звук вьюги ---
        if (k > 0.0F && (windSound == null || windSound.isStopped())) {
            windSound = new BlizzardWindSound();
            mc.getSoundManager().play(windSound);
        }

        // --- затемнение неба: уровень дождя/грозы темнит небо и свет на улице ---
        if (k > 0.0F) {
            level.setRainLevel(k * DARKNESS_RAIN);
            level.setThunderLevel(k * DARKNESS_THUNDER);
            darknessApplied = true;
        } else if (darknessApplied) {
            level.setRainLevel(0.0F);
            level.setThunderLevel(0.0F);
            darknessApplied = false;
        }

        if (k <= 0.0F || level.dimension() != Level.OVERWORLD) return;

        // --- частицы (видимость ограничена туманом до 6-10 блоков, поэтому спавним ближе) ---
        RandomSource r = level.random;
        int main = (int) (PARTICLES_PER_TICK * k);
        for (int i = 0; i < main; i++) {
            // ветер дует в сторону -X, поэтому спавним с запасом с +X
            double x = player.getX() + r.nextDouble() * 26.0 - 8.0;
            double z = player.getZ() + (r.nextDouble() - 0.5) * 28.0;
            double y = player.getY() - 3.0 + r.nextDouble() * 13.0;
            spawn(level, r, x, y, z);
        }

        // плотная завеса прямо вокруг игрока
        int near = (int) (NEAR_PARTICLES * k);
        for (int i = 0; i < near; i++) {
            double x = player.getX() + (r.nextDouble() - 0.5) * 12.0;
            double z = player.getZ() + (r.nextDouble() - 0.5) * 12.0;
            double y = player.getY() + r.nextDouble() * 5.0;
            spawn(level, r, x, y, z);
        }
    }

    private static void spawn(ClientLevel level, RandomSource r, double x, double y, double z) {
        // не спавним под крышами и внутри пещер
        int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING,
                (int) Math.floor(x), (int) Math.floor(z));
        if (y < top) return;

        level.addParticle(SiMine.BLIZZARD_SNOW, x, y, z,
                -0.55 - r.nextDouble() * 0.35,   // сильный ветер
                -0.10 - r.nextDouble() * 0.10,   // падение
                0.0);
    }
}
