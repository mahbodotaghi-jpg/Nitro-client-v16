package dev.nitro.util;

import dev.nitro.NitroClient;
import dev.nitro.module.modules.Hitbox;
import dev.nitro.module.modules.StaticHitboxes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;

public class HitboxUtil {
    /** Extra targeting margin for an entity (used by EntityMixin when the crosshair target is computed). */
    public static float margin(Entity e) {
        if (!(e instanceof PlayerEntity p) || NitroClient.modules == null) return 0f;
        if (p == MinecraftClient.getInstance().player) return 0f;

        float m = 0f;
        Hitbox h = NitroClient.modules.get(Hitbox.class);
        if (h != null && h.isEnabled()) m += h.expansion();

        StaticHitboxes s = NitroClient.modules.get(StaticHitboxes.class);
        if (s != null && s.isEnabled()) {
            float standing = p.getDimensions(EntityPose.STANDING).height();
            float current = p.getDimensions(p.getPose()).height();
            if (standing > current) m += (standing - current) / 2f;
        }
        return m;
    }
}
