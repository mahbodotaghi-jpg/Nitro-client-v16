package dev.nitro.module.modules;

import dev.nitro.NitroClient;
import dev.nitro.module.Category;
import dev.nitro.module.Module;
import dev.nitro.setting.BooleanSetting;
import dev.nitro.setting.NumberSetting;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Detaches the camera from your player. Your character stays where it is: movement, attack, use,
 * drop and hand-swap input are blocked, and mouse look is redirected to the camera (EntityMixin).
 */
public class Freecam extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1));
    private final BooleanSetting followPitch = add(new BooleanSetting("Follow pitch", true));
    private OtherClientPlayerEntity cam;

    public Freecam() { super("Freecam", "Fly the camera around while your player stays put.", Category.RENDER); }

    /** Called from EntityMixin. Returns true when the look delta was handled by the camera. */
    public static boolean redirectLook(Entity e, double dx, double dy) {
        if (NitroClient.modules == null) return false;
        Freecam f = NitroClient.modules.get(Freecam.class);
        if (f == null || !f.isEnabled() || f.cam == null) return false;
        if (e != MinecraftClient.getInstance().player) return false;
        f.cam.changeLookDirection(dx, dy);
        return true;
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) { toggle(); return; }
        cam = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
        cam.copyPositionAndRotation(mc.player);
        syncPrev();
        mc.setCameraEntity(cam);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.setCameraEntity(mc.player);
        cam = null;
    }

    private void syncPrev() {
        cam.prevX = cam.lastRenderX = cam.getX();
        cam.prevY = cam.lastRenderY = cam.getY();
        cam.prevZ = cam.lastRenderZ = cam.getZ();
        cam.prevYaw = cam.getYaw();
        cam.prevPitch = cam.getPitch();
    }

    private static void block(KeyBinding k) {
        k.setPressed(false);
        while (k.wasPressed()) { /* drain queued presses so they can't fire */ }
    }

    @Override
    public void onPreTick() {
        block(mc.options.forwardKey);
        block(mc.options.backKey);
        block(mc.options.leftKey);
        block(mc.options.rightKey);
        block(mc.options.jumpKey);
        block(mc.options.sneakKey);
        block(mc.options.sprintKey);
        block(mc.options.attackKey);
        block(mc.options.useKey);
        block(mc.options.pickItemKey);
        block(mc.options.dropKey);
        block(mc.options.swapHandsKey);
    }

    private boolean down(KeyBinding k) {
        InputUtil.Key key = KeyBindingHelper.getBoundKeyOf(k);
        if (key.getCategory() != InputUtil.Type.KEYSYM) return false;
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), key.getCode());
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (cam == null || cam.getWorld() != mc.world) { toggle(); return; }
        if (mc.getCameraEntity() != cam) mc.setCameraEntity(cam);

        syncPrev();
        if (mc.currentScreen != null) return;

        int fw = (down(mc.options.forwardKey) ? 1 : 0) - (down(mc.options.backKey) ? 1 : 0);
        int strafe = (down(mc.options.rightKey) ? 1 : 0) - (down(mc.options.leftKey) ? 1 : 0);
        int up = (down(mc.options.jumpKey) ? 1 : 0) - (down(mc.options.sneakKey) ? 1 : 0);

        float yaw = cam.getYaw();
        Vec3d forward = Vec3d.fromPolar(followPitch.get() ? cam.getPitch() : 0f, yaw);
        Vec3d right = Vec3d.fromPolar(0f, yaw + 90f);

        Vec3d move = forward.multiply(fw).add(right.multiply(strafe)).add(0, up, 0);
        if (move.lengthSquared() < 1.0E-6) return;
        move = move.normalize().multiply(speed.get());

        cam.setPosition(cam.getX() + move.x, cam.getY() + move.y, cam.getZ() + move.z);
    }
}
