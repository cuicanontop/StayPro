package dev.cuican.staypro.module.modules.render;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.mixin.accessor.AccessorRenderManager;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ColorValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "EPS", description = "EPS", category = Category.RENDER)
public class ESP extends Module {
    public Setting<Boolean> csgo = setting("csgo", false);
    public Setting<Boolean> show_targets = setting("Show Targets", true);
    public ColorValue playerColor = new ColorValue(-52237);
    public ColorValue animalColor = new ColorValue(-12779725);
    public static ColorValue Field1314;
    public static ColorValue colorValueSetting;
    @Listener
    public void onRenderWorld(RenderEvent event) {
        if (ESP.mc.world == null || ESP.mc.player == null) {
            return;
        }
        if (csgo.getValue()) {
            if (mc.getRenderManager() == null) {
                return;
            }
            mc.world.loadedEntityList.stream().filter(ESP::Method395).forEach(this::Method1322);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }

    }
    public static boolean Method395(Entity entity) {
        return mc.player != entity && entity != mc.getRenderViewEntity();
    }
    public void Method1322(Entity entity) {
        block8: {
            if (entity == ESP.mc.player || !this.Method386(entity)) break block8;
            float f = mc.getRenderViewEntity().getDistance(entity);
            if (f < 3.0f) {
                f = 3.0f;
            }
            float f2 = 1.0f / (f / 3.0f);
            GL11.glBlendFunc(770, 771);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            GlStateManager.pushMatrix();
            Vec3d vec3d = new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(new Vec3d((entity.posX - entity.lastTickPosX) * (double)mc.getRenderPartialTicks(), (entity.posY - entity.lastTickPosY) * (double)mc.getRenderPartialTicks(), (entity.posZ - entity.lastTickPosZ) * (double)mc.getRenderPartialTicks()));
            GlStateManager.translate(vec3d.x - ((AccessorRenderManager)mc.getRenderManager()).getRenderPosX(), vec3d.y - ((AccessorRenderManager)mc.getRenderManager()).getRenderPosY(), vec3d.z - ((AccessorRenderManager)mc.getRenderManager()).getRenderPosZ());
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-ESP.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            Minecraft minecraft = mc;
            RenderManager renderManager = minecraft.getRenderManager();
            float f3 = renderManager.options.thirdPersonView == 2 ? -1 : 1;
            float f4 = 1.0f;
            float f5 = 0.0f;
            float f6 = 0.0f;
            try {
                GlStateManager.rotate(f3, f4, f5, f6);
            }
            catch (NullPointerException nullPointerException) {
                GlStateManager.rotate(1.0f, 1.0f, 0.0f, 0.0f);
            }
            int n = this.Method1315(entity);
            float f7 = (float)(n >> 16 & 0xFF) / 255.0f;
            float f8 = (float)(n >> 8 & 0xFF) / 255.0f;
            float f9 = (float)(n & 0xFF) / 255.0f;
            GL11.glColor4f(f7, f8, f9, 1.0f);
            GL11.glLineWidth(3.0f * f2);
            GL11.glEnable(2848);
            GL11.glBegin(2);
            GL11.glVertex2d((double)(-entity.width) * 1.2, -((double)entity.height * 0.2));
            GL11.glVertex2d((double)(-entity.width) * 1.2, (double)entity.height * 1.2);
            GL11.glVertex2d((double)entity.width * 1.2, (double)entity.height * 1.2);
            GL11.glVertex2d((double)entity.width * 1.2, -((double)entity.height * 0.2));
            GL11.glEnd();
            if (entity instanceof EntityLivingBase) {
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
                GL11.glLineWidth(5.0f * f2);
                GL11.glBegin(1);
                GL11.glVertex2d((double)entity.width * 1.4, (double)entity.height * 1.2);
                GL11.glVertex2d((double)entity.width * 1.4, -((double)entity.height * 0.2));
                GL11.glEnd();
                GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                float f10 = ((EntityLivingBase)entity).getHealth() / ((EntityLivingBase)entity).getMaxHealth();
                GL11.glBegin(1);
                GL11.glVertex2d((double)entity.width * 1.4, (double)entity.height * 1.2 * (double)f10);
                GL11.glVertex2d((double)entity.width * 1.4, -((double)entity.height * 0.2));
                GL11.glEnd();
                float f11 = ((EntityLivingBase)entity).getAbsorptionAmount() / 16.0f;
                if (f11 > 0.0f) {
                    GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
                    GL11.glBegin(1);
                    GL11.glVertex2d((double)entity.width * 1.6, (double)entity.height * 0.92);
                    GL11.glVertex2d((double)entity.width * 1.6, -((double)entity.height * 0.2));
                    GL11.glEnd();
                    GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                    GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
                    GL11.glBegin(1);
                    GL11.glVertex2d((double)entity.width * 1.6, (double)entity.height * 0.92 * (double)f11);
                    GL11.glVertex2d((double)entity.width * 1.6, -((double)entity.height * 0.2));
                    GL11.glEnd();
                }
            }
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
            GlStateManager.resetColor();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }

    public Integer Method1315(Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (show_targets.getValue().booleanValue() && Method423(entity)) {
                int n = getRender(entity);
                return new Color(255, n / 5, (int)((double)n / 1.0493)).hashCode();
            }
            return playerColor.Method774();
        }
        if (entity instanceof IMob) {
            return colorValueSetting.Method774();
        }
        if (entity instanceof IAnimals || entity instanceof INpc) {
            return animalColor.Method774();
        }
        return Field1314.Method774();
    }
    public Map<Entity, Long> Field261 = new ConcurrentHashMap<Entity, Long>();
    public boolean Method423(Entity entity) {
        return this.Field261.containsKey(entity);
    }

    public int getRender(Entity entity) {
        int n;
        block11: {
            Entity entity2;
            Map<Entity, Long> map;
            try {
                map = this.Field261;
                entity2 = entity;
            }
            catch (NullPointerException nullPointerException) {
                return 255;
            }
            boolean bl = map.containsKey(entity2);
            if (bl) break block11;
            return 255;
        }
        long l = System.currentTimeMillis();
        Map<Entity, Long> map = this.Field261;
        Entity entity3 = entity;
        Long l2 = map.get(entity3);
        Long l3 = l2;
        long l4 = l3;
        int n2 = n = (int)(l - l4) / 118;
        int n3 = 255;
        return Math.min(n2, n3);
    }
    public boolean Method386(Entity entity) {

        if (entity instanceof EntityPlayer && entity != mc.player && entity != mc.getRenderViewEntity() ) {
            return true;
        }
        return false;

    }
}
