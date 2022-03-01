package dev.cuican.staypro.module.modules.movement;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.InputUpdateEvent;
import dev.cuican.staypro.event.events.client.PlayerInteractEvent;
import dev.cuican.staypro.event.events.network.MotionUpdateMultiplierEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "GuiMove", description = "null", category = Category.MOVEMENT)
public class GuiMove extends Module {
    private final Setting<Boolean> chat = setting("Chat", false);
    private final Setting<Boolean> sneak = setting("Sneak", false);
    private final Setting<Integer> yawSpeed = setting("YawSpeed", 6, 0, 20);
    private final Setting<Integer> pitchSpeed = setting("PitchSpeed", 6, 0, 20);



    @Override
    public void onRenderTick() {

        if (isEnabled() && mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat) || (mc.currentScreen instanceof GuiChat && chat.getValue()) || mc.currentScreen instanceof GuiScreen) {
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                for (int i = 0; i < pitchSpeed.getValue(); ++i) {
                    mc.player.rotationPitch--;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                for (int i = 0; i < pitchSpeed.getValue(); ++i) {
                    mc.player.rotationPitch++;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                for (int i = 0; i < yawSpeed.getValue(); ++i) {
                    mc.player.rotationYaw++;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                for (int i = 0; i < yawSpeed.getValue(); ++i) {
                    mc.player.rotationYaw--;
                }
            }
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
                mc.player.setSprinting(true);
            }
            try {
                if (mc.player.rotationPitch > 90) mc.player.rotationPitch = 90;
                if (mc.player.rotationPitch < -90) mc.player.rotationPitch = -90;
            } catch (Exception ignored) {
            }
        }
    }

    @Listener
    public void awa(PlayerInteractEvent event) {
        if (isEnabled() && mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat) || (mc.currentScreen instanceof GuiChat && chat.getValue())) {
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                event.getMovementInput().moveForward = getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                event.getMovementInput().moveForward = -getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
                event.getMovementInput().moveStrafe = getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
                event.getMovementInput().moveStrafe = -getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                event.getMovementInput().jump = true;
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && sneak.getValue()) {
                event.getMovementInput().sneak = true;
            }
        }
    }

    private float getSpeed() {
        float x = 1;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && sneak.getValue()) {
            x = 0.30232558139f;
        }
        return x;
    }
}
