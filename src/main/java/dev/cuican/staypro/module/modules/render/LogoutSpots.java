package dev.cuican.staypro.module.modules.render;


import com.google.common.base.Strings;
import dev.cuican.staypro.Stay;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.event.events.client.ClientDisconnectionFromServerEvent;
import dev.cuican.staypro.event.events.client.ConnectionEvent;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.ColorUtil;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.Wrapper;
import dev.cuican.staypro.utils.graphics.RenderUtils;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import dev.cuican.staypro.utils.graphics.StayTessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Rewrote by Py on 13/05/2021
 */

@ModuleInfo(name = "LogOutSpots", category = Category.RENDER, description = "Draw EntityPlayer LogOut Spots")
public class LogoutSpots extends Module {

    public final Setting<Boolean> rainbow = setting("Rainbow", true);
    private final Setting<Boolean> nameTag = setting("NameTag", true);
    private final Setting<Boolean> rect = setting("Rectangle", true).whenTrue(nameTag);
    private final Setting<Boolean> scaleing = setting("Scale", false).whenTrue(nameTag);
    private final Setting<Boolean> smartScale = setting("SmartScale", true).whenTrue( scaleing);
    private final Setting<Float> scaling = setting("Size", 4, 0.1f, 20f).whenTrue( scaleing);
    private final Setting<Float> factor = setting("Factor", 0.3f, 0.1f, 1f).whenTrue( scaleing);
    private final Setting<Boolean> colorSync = setting("ColorSync", false).whenTrue(nameTag);
    private final Setting<Integer> redFont = setting("Red_Font", 255, 0, 255).whenFalse( colorSync);
    private final Setting<Integer> greenFont = setting("Green_Font", 198, 0, 255).whenFalse( colorSync);
    private final Setting<Integer> blueFont = setting("Blue_Font", 203, 0, 255).whenFalse( colorSync);
    private final Setting<Integer> alphaFont = setting("Alpha_Font", 70, 0, 255).whenFalse( colorSync);
    private final Setting<Integer> red = setting("Red", 255, 0, 255).whenFalse( rainbow);
    private final Setting<Integer> green = setting("Green", 198, 0, 255).whenFalse( rainbow);
    private final Setting<Integer> blue = setting("Blue", 203, 0, 255).whenFalse( rainbow);
    private final Setting<Integer> alpha = setting("Alpha", 70, 0, 255);
    private final List<LogoutPos> spots = new CopyOnWriteArrayList<>();
    public Setting<Float> range = setting("Range", 300f, 1, 500);
    public Setting<Boolean> message = setting("Message", true);
    private final Setting<Boolean> coords = setting("Coords", true).whenTrue( message);
    public Setting<Integer> rainbowSpeed = setting("RainbowSpeed", 20, 0, 100).whenTrue( rainbow);
    public Setting<Integer> rainbowSaturation = setting("RainbowSaturation", 100, 0, 255).whenTrue( rainbow);
    public Setting<Integer> rainbowBrightness = setting("RainbowBrightness", 255, 0, 255).whenTrue( rainbow);

    @Listener
    public void onLogout(ClientDisconnectionFromServerEvent event) {
        this.spots.clear();
    }


    @Override
    public void onDisable() {
        this.spots.clear();
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (!this.spots.isEmpty()) {
            this.spots.forEach(spot -> {
                if (spot.getEntity() != null) {
                    AxisAlignedBB bb = RenderUtils3D.interpolateAxis(spot.getEntity().getEntityBoundingBox());
                    StayTessellator.drawBoxTest(bb, getCurrentColor(), 63);
                    double x = this.interpolate(spot.getEntity().lastTickPosX, spot.getEntity().posX, Wrapper.getMinecraft().getRenderPartialTicks()) - LogoutSpots.mc.getRenderManager().renderPosX;
                    double y = this.interpolate(spot.getEntity().lastTickPosY, spot.getEntity().posY, Wrapper.getMinecraft().getRenderPartialTicks()) - LogoutSpots.mc.getRenderManager().renderPosY;
                    double z = this.interpolate(spot.getEntity().lastTickPosZ, spot.getEntity().posZ, Wrapper.getMinecraft().getRenderPartialTicks()) - LogoutSpots.mc.getRenderManager().renderPosZ;
                    this.renderNameTag(spot.getEntity(), x, y, z, Wrapper.getMinecraft().getRenderPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
                }
            });
        }
    }

    public Color getCurrentColor() {
        return new Color(getCurrentColorHex(), true);
    }

    public int getCurrentColorHex() {
        if (this.rainbow.getValue()) {
            int colorSpeed = 101 - this.rainbowSpeed.getValue();
            float hue = System.currentTimeMillis() % (360 * colorSpeed) / (360.0f * colorSpeed);
            int rainbow = Color.HSBtoRGB(hue, this.rainbowSaturation.getValue() / 255.0f, this.rainbowBrightness.getValue() / 255.0f);
            return ColorUtil.changeAlpha(rainbow, alpha.getValue());
        }
        return ColorUtil.toARGB(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

    public int getCurrentFontColorHex() {
        return ColorUtil.toARGB(this.redFont.getValue(), this.greenFont.getValue(), this.blueFont.getValue(), this.alphaFont.getValue());
    }

    @Override
    public void onTick() {
        if (!fullNullCheck()) {
            this.spots.removeIf(spot -> mc.player.getDistanceSq(spot.getEntity()) >= MathUtil.square(this.range.getValue()) || isPlayerOnline(spot.getEntity().getName()));
        }
    }
    public Timer logoutTimer = new Timer();
    @Listener
    public void onPacketReceive(PacketEvents.Receive event) {

        if (event.getPacket() instanceof SPacketPlayerListItem && !Module.fullNullCheck() && logoutTimer.passedS(1.0)) {
            SPacketPlayerListItem packet = event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction())) {
                return;
            }
            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> !Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null).forEach(data -> {
                switch (packet.getAction()) {
                    case ADD_PLAYER: {
                        Stay.EVENT_BUS.post(new ConnectionEvent(ConnectionEvent.ADD_PLAYER, data.getProfile()));
                        break;
                    }
                    case REMOVE_PLAYER: {
                        Stay.EVENT_BUS.post(new ConnectionEvent(ConnectionEvent.REMOVE_PLAYER, data.getProfile()));
                        break;
                    }
                }
            });
        }
    }

    @Listener
    public void onConnection(final ConnectionEvent event) {
        final UUID uuid = event.getProfile().getId();
        final EntityPlayer entity = mc.world.getPlayerEntityByUUID(uuid);
        String name = "";
        BlockPos pos = null;
        if (entity != null) {
            pos = entity.getPosition();
        }
        if (event.getProfile() != null) {
            if (event.getProfile().getName() != null && !event.getProfile().getName().equals("")) {
                name = event.getProfile().getName();
            }
        }
        if (event.getStage() == 0) {
            if (entity != null) {
                entity.getName();
                name = entity.getName();
                pos = entity.getPosition();
            }
            if (this.message.getValue()) {
                ChatUtil.printChatMessage("\u00a7a" + name + " just logged in" + (this.coords.getValue() && pos != null ? " at (" + pos.x + ", " + pos.y + ", " + pos.z + ")!" : "!"));
            }
        } else if (event.getStage() == 1) {
            if (entity != null) {
                entity.getName();
                name = entity.getName();
                pos = entity.getPosition();
            }
            if (this.message.getValue()) {
                if (name == null) {
                    return;
                }
                ChatUtil.printChatMessage("\u00a7c" + name + " just logged out" + (this.coords.getValue() && pos != null ? " at (" + pos.x + ", " + pos.y + ", " + pos.z + ")!" : "!"));
            }

            if (pos != null) {
                this.spots.add(new LogoutPos(name, event.getProfile().getId(), entity));
            }
        }
    }

    private void renderNameTag(final EntityPlayer player, final double x, final double y, final double z, final float delta, final double xPos, final double yPos, final double zPos) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5 : 0.7);
        final Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        final double originalPositionX = camera.posX;
        final double originalPositionY = camera.posY;
        final double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        final double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        double scale = (0.0018 + this.scaling.getValue() * (distance * this.factor.getValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScale.getValue()) {
            scale = 0.0195;
        }
        if (!this.scaleing.getValue()) {
            scale = this.scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        String playerName = player.getName();
        String coords = "XYZ: " + (int) xPos + ", " + (int) yPos + ", " + (int) zPos;
        int nameAJ = mc.fontRenderer.getStringWidth(player.getName()) / 2;
        int coordsAJ = mc.fontRenderer.getStringWidth(coords) / 2;
        double width = (Math.max(nameAJ, coordsAJ) * 2);
        double height = (mc.fontRenderer.FONT_HEIGHT * 2);
        if (this.rect.getValue()) {
            RenderUtils.drawRoundedRectangle(-1 - 2 - (width / 2.0), -2, width + 4, height + 4, 2, new Color(33, 33, 33, 110));
            RenderUtils.drawRoundedRectangleOutline(-1 - 2 - (width / 2.0), -2, width + 4, height + 4, 2, 1.0f, new Color(137, 137, 137, 171));
        }
        mc.fontRenderer.drawString(playerName, -1 - nameAJ, -1, this.colorSync.getValue() ? getCurrentColorHex() : getCurrentFontColorHex());
        mc.fontRenderer.drawString(coords, -1 - coordsAJ, -1 + mc.fontRenderer.FONT_HEIGHT + 2, this.colorSync.getValue() ? getCurrentColorHex() : getCurrentFontColorHex());
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private double interpolate(final double previous, final double current, final float delta) {
        return previous + (current - previous) * delta;
    }

    private boolean isPlayerOnline(String name) {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private static class LogoutPos {
        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;

        public LogoutPos(final String name, final UUID uuid, final EntityPlayer entity) {
            this.name = name;
            this.uuid = uuid;
            this.entity = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
        }

        public String getName() {
            return this.name;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public EntityPlayer getEntity() {
            return this.entity;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }

}
