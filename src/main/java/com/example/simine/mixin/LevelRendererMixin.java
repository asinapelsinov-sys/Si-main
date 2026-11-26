package com.example.simine.mixin;

import com.example.simine.client.ClientBlizzardState;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Во время метели скрываем ванильные капли дождя/снег и звук дождя
 * (затемнение неба идёт через уровень дождя), вместо них - наши частицы.
 * require = 0: если что-то не найдётся, мод не упадёт, просто эффект не применится.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true, require = 0)
    private void simine$hideVanillaPrecipitation(CallbackInfo ci) {
        if (ClientBlizzardState.intensity > 0.0F) ci.cancel();
    }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true, require = 0)
    private void simine$hideVanillaRainSound(CallbackInfo ci) {
        if (ClientBlizzardState.intensity > 0.0F) ci.cancel();
    }
}
