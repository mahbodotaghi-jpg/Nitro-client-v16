package dev.nitro.module.modules;

import dev.nitro.mixin.MinecraftClientAccessor;
import dev.nitro.module.Category;
import dev.nitro.module.Module;
import dev.nitro.setting.BooleanSetting;

/** Clears the client's click cooldowns every tick, so you can attack (and optionally place) with no built-in pause. */
public class NoHitDelay extends Module {
    private final BooleanSetting useDelay = add(new BooleanSetting("Also use delay", true));

    public NoHitDelay() { super("NoHitDelay", "Removes the client-side attack/use click delay.", Category.COMBAT); }

    @Override
    public void onPreTick() {
        MinecraftClientAccessor acc = (MinecraftClientAccessor) mc;
        acc.nitro$setAttackCooldown(0);
        if (useDelay.get()) acc.nitro$setItemUseCooldown(0);
    }
}
