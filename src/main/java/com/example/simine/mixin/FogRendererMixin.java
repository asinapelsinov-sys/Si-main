package com.example.simine.mixin;

import com.example.simine.client.ClientBlizzardState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogShape;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Во время метели на улице ограничиваем видимость до 6-10 блоков
 * (дальность плавно "дышит" порывами ветра).
 * require = 0: при несовпадении версии мод не упадёт, просто тумана не будет.
 */
@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("TAIL"), require = 0)
    private static void simine$blizzardFog(CallbackInfo ci) {
        float k = ClientBlizzardState.intensity * ClientBlizzardState.exposure;
        if (k <= 0.0F) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        // не трогаем туман в воде, лаве, порошковом снеге
        if (mc.gameRenderer.getMainCamera().getFluidInCamera() != FogType.NONE) return;

        float t = mc.level.getGameTime();
        float end = 8.0F + 2.0F * (float) Math.sin(t * 0.045F);   // 6..10 блоков
        float start = end * 0.25F;

        RenderSystem.setShaderFogStart(Mth.lerp(k, RenderSystem.getShaderFogStart(), start));
        RenderSystem.setShaderFogEnd(Mth.lerp(k, RenderSystem.getShaderFogEnd(), end));
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }
}
