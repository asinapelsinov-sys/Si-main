package com.example.blizzardmod;

import com.example.blizzardmod.network.BlizzardPayload;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LightLayer;

public class BlizzardManager {

    private static boolean active = false;
    private static int ticksLeft = 0;
    private static int cooldown = 12000; // до следующей метели (тики)

    public static boolean isActive() {
        return active;
    }

    public static void start(MinecraftServer server, int duration) {
        active = true;
        ticksLeft = duration;
        broadcast(server, true);
    }

    public static void stop(MinecraftServer server) {
        active = false;
        ticksLeft = 0;
        broadcast(server, false);
    }

    private static void broadcast(MinecraftServer server, boolean state) {
        BlizzardPayload payload = new BlizzardPayload(state);
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(p, payload);
        }
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(BlizzardManager::onServerTick);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ServerPlayNetworking.send(handler.getPlayer(), new BlizzardPayload(active)));

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            active = false;
            ticksLeft = 0;
            cooldown = 12000;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        Commands.literal("blizzard")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.literal("start").executes(ctx -> {
                                    start(ctx.getSource().getServer(), 6000);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Метель началась"), true);
                                    return 1;
                                }))
                                .then(Commands.literal("stop").executes(ctx -> {
                                    stop(ctx.getSource().getServer());
                                    ctx.getSource().sendSuccess(() -> Component.literal("Метель закончилась"), true);
                                    return 1;
                                }))
                ));
    }

    private static void onServerTick(MinecraftServer server) {
        ServerLevel level = server.overworld();
        if (level == null) return;

        // --- Смена погоды ---
        if (active) {
            if (--ticksLeft <= 0) {
                stop(server);
                cooldown = 12000 + level.random.nextInt(24000);
            }
        } else if (--cooldown <= 0) {
            start(server, 6000 + level.random.nextInt(6000)); // 5-10 минут
        }

        if (!active) return;

        // --- Заморозка игроков ---
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.isCreative() || !player.isAlive()) continue;
            if (!player.canFreeze()) continue; // кожаная броня защищает, как от порошкового снега

            BlockPos eye = BlockPos.containing(player.getEyePosition());
            boolean exposed = level.canSeeSky(eye);
            boolean warm = level.getBrightness(LightLayer.BLOCK, player.blockPosition()) >= 12; // факел/костёр рядом

            if (exposed && !warm && !player.isOnFire()) {
                int max = player.getTicksRequiredToFreeze();
                int target = Math.min(max - 1, player.getTicksFrozen() + 3); // лёд на экране
                player.setTicksFrozen(target);

                if (target >= max - 1 && player.tickCount % 40 == 0) {
                    player.hurt(level.damageSources().freeze(), 1.0F);
                }
            }
        }
    }
}
