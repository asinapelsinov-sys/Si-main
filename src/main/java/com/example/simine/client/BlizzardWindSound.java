package com.example.simine.client;

import com.example.simine.SiMine;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

/** Зацикленный вой вьюги. Громкость зависит от силы метели и от того, на улице ли игрок. */
public class BlizzardWindSound extends AbstractTickableSoundInstance {

    private int age = 0;

    public BlizzardWindSound() {
        super(SiMine.BLIZZARD_WIND, SoundSource.WEATHER, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
        this.pitch = 1.0F;
        this.relative = true;                      // звучит "в голове", а не из точки мира
        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    @Override
    public void tick() {
        age++;
        float k = ClientBlizzardState.intensity;
        if (k <= 0.0F) {
            this.stop();
            return;
        }
        // на улице громко, под крышей приглушённо
        float indoors = 0.25F;
        float mix = indoors + (1.0F - indoors) * ClientBlizzardState.exposure;
        this.volume = Math.min(1.0F, k * mix * 1.1F);
        // лёгкие порывы по высоте тона
        this.pitch = 0.95F + 0.07F * (float) Math.sin(age * 0.03);
    }
}
