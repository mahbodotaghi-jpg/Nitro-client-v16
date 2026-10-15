package dev.nitro.module.modules;

import dev.nitro.module.Category;
import dev.nitro.module.Module;

/** Keeps the standing hitbox height as your target when a player crouches, crawls or swims (see EntityMixin). */
public class StaticHitboxes extends Module {
    public StaticHitboxes() {
        super("StaticHitboxes", "Crouching/crawling players keep their full standing hitbox for your crosshair.", Category.COMBAT);
    }
}
