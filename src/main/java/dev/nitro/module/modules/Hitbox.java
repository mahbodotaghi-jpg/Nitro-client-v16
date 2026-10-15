package dev.nitro.module.modules;

import dev.nitro.module.Category;
import dev.nitro.module.Module;
import dev.nitro.setting.NumberSetting;

/** Makes other players' hitboxes bigger for your crosshair, so you hit them from slightly off-target (see EntityMixin). */
public class Hitbox extends Module {
    private final NumberSetting expand = add(new NumberSetting("Expand", 0.3, 0.0, 1.0, 0.05));

    public Hitbox() { super("Hitbox", "Expands player hitboxes for easier targeting.", Category.COMBAT); }

    public float expansion() { return expand.get().floatValue(); }
}
