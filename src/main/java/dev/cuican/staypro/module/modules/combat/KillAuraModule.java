package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.event.events.render.RenderItemAnimationEvent;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import dev.cuican.staypro.mixin.accessor.AccessorMinecraft;
import dev.cuican.staypro.mixin.accessor.AccessorRenderManager;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.modules.player.Instant;
import dev.cuican.staypro.module.modules.render.Aimbot;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.inventory.ItemUtil;
import dev.cuican.staypro.utils.math.LagCompensator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Random;

@ModuleInfo(name = "KillAura", category = Category.COMBAT,description = "KillAura")
public class KillAuraModule extends Module {
    public static boolean isAiming;
    public static RenderManager manager;
    private void drawCircle(Entity entity, float partialTicks, double rad, double height) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0f);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        final float r = ((float) 1 / 255) * Color.WHITE.getRed();
        final float g = ((float) 1 / 255) * Color.WHITE.getGreen();
        final float b = ((float) 1 / 255) * Color.WHITE.getBlue();

        final double pix2 = Math.PI * 2.0D;

        for (int i = 0; i <= 90; ++i) {
            GL11.glVertex3d(x + rad * Math.cos(i * pix2 / 45), y + height, z + rad * Math.sin(i * pix2 / 45));
        }

        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11. glEnable(GL11.GL_TEXTURE_2D);
        GL11. glPopMatrix();
    }
    static {
        isAiming = false;
    }
    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }


    public Setting<String> Mode = setting("Mode","Closest",listOf(
            "Closest",
            "Priority",
            "Switch"));
    public Setting<String> render = setting("RenderMode", "Novo",listOf(  "Novo", "Off","Circle"));
    public Setting<Float> Distance = setting("Range", 5.5f, 0, 8);
    public Setting<Boolean> HitDelay = setting("HitDelay", true);
    public Setting<Boolean> TPSSync = setting("TpsSync", true);
    public Setting<Boolean> Players = setting("Players", true);
    public Setting<Boolean> Monsters = setting("Monsters", true);
    public Setting<Boolean> Neutrals = setting("Neutrals", true);
    public Setting<Boolean> Animals = setting("Animals", true);
    public Setting<Boolean> Tamed = setting("Tamed", false);
    public Setting<Boolean> Projectiles = setting("Projectiles", false);
    public Setting<Boolean> SwordOnly = setting("SwordOnly", false);
    public Setting<Boolean> PauseIfCrystal = setting("PauseIfCA", true);
    public Setting<Boolean> PauseIfEating = setting("PauseIfEating", false);
    public Setting<Boolean> AutoSwitch = setting("AutoSwitch", true);
    public Setting<Boolean> swap = setting("Swap", false);
    public Setting<Boolean> animation = setting("Animation", false);
    public Setting<Boolean> RenderTarget = setting("Render", false);
    public Setting<Boolean> NOswingArm = setting("swingArm", true);
    public Setting<Boolean> Only32k = setting("Only32K", false);

    public Setting<Boolean> rotate = setting("Rotate", true);

    public Setting<Integer> Ticks = setting("Ticks", 10, 0, 40);
    public Setting<Boolean> targetHUD = setting("ShowTarget", false);
    public Setting<String> hudMode = setting("HudMode", "Simple",listOf("Simple","Debug","Ice","Fancy","Skid","Latest")).whenTrue(targetHUD);
    public Setting<Boolean> debugs = setting("Light", true).whenTrue(targetHUD).whenAtMode(hudMode,"Debug");
    public Setting<Integer> Width = setting("Width", 0, 0, 3000).whenTrue(targetHUD);
    public Setting<Integer> Height = setting("Height", 0, 0, 3000).whenTrue(targetHUD);
    public Entity CurrentTarget;
    public int RemainingTicks = 0;
    public boolean canRender = false;
    public int f = -1;
    public int sword = -1;
    public static boolean lookAtPacket(final double n, final double n2, final double n3, final EntityPlayer entityPlayer, final boolean b) {
        final double[] calculateLook = EntityUtil.calculateLookAt(n, n2, n3, entityPlayer,1);
        if (ModuleManager.getModuleByName("Aimbot").isDisabled()) {
            ModuleManager.getModuleByName("Aimbot").enable();
        }
        return ((Aimbot) ModuleManager.getModuleByName("Aimbot")).setRotation((float) calculateLook[0], (float) calculateLook[1], b);
    }
    public static void resetRotation() {
        ((Aimbot) ModuleManager.getModuleByName("Aimbot")).resetRotation();
    }


    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glColor4f(red, green, blue, alpha);
        RenderUtils3D.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        RenderUtils3D.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    @Override
    public void onEnable() {
        if(fullNullCheck()){
            return;
        }
        f = mc.player.inventory.currentItem;
        sword = InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD);
        canRender = false;
        RemainingTicks = 0;
    }

    @Override
    public void onDisable() {
        if(fullNullCheck()){
            return;
        }
        canRender = false;
        isAiming = false;
        resetRotation();

    }
    public static Instant INSTANCE = new Instant();
    public static Instant getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Instant();
        }
        return INSTANCE;
    }
    @Override
    public String getModuleInfo() {
        return Mode.getValue();
    }

    public boolean IsValidTarget(Entity p_Entity) {
        if (p_Entity instanceof EntityArmorStand) {
            return false;
        }
        if (!(p_Entity instanceof EntityLivingBase)) {
            boolean l_IsProjectile = p_Entity instanceof EntityShulkerBullet || p_Entity instanceof EntityFireball;
            if (!l_IsProjectile)
                return false;

            if (!Projectiles.getValue())
                return false;
        }

        if (p_Entity instanceof EntityPlayer) {
            /// Ignore if it's us
            if (p_Entity == mc.player)
                return false;

            if (!Players.getValue())
                return false;

            /// They are a friend, ignore it.
            if (FriendManager.isFriend(p_Entity.getName()))
                return false;
        }

        if (EntityUtil.isHostileMob(p_Entity) && !Monsters.getValue())
            return false;

        if (EntityUtil.isPassive(p_Entity)) {
            if (p_Entity instanceof AbstractChestHorse) {
                AbstractChestHorse l_Horse = (AbstractChestHorse) p_Entity;

                if (l_Horse.isTame() && !Tamed.getValue())
                    return false;
            }

            if (!Animals.getValue())
                return false;
        }

        if (EntityUtil.isHostileMob(p_Entity) && !Monsters.getValue())
            return false;

        if (EntityUtil.isNeutralMob(p_Entity) && !Neutrals.getValue())
            return false;

        boolean l_HealthCheck = true;

        if (p_Entity instanceof EntityLivingBase) {
            EntityLivingBase l_Base = (EntityLivingBase) p_Entity;

            l_HealthCheck = !l_Base.isDead && l_Base.getHealth() > 0.0f;
        }

        return l_HealthCheck && p_Entity.getDistance(p_Entity) <= Distance.getValue();
    }
    int b;

    @Override
    public void onTick() {
        if(fullNullCheck()){
            return;
        }

        if (SwordOnly.getValue() ){
            if(!(mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() instanceof ItemSword)){
                CurrentTarget = null;
                canRender = false;
                resetRotation();
                return;
            }
        }
        f = mc.player.inventory.currentItem;
        sword = InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD);
        if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
            if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && PauseIfCrystal.getValue())
                return;
            if (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && PauseIfEating.getValue())
                return;
        }
        if (Only32k.getValue()) {
            if (!ItemUtil.Is32k(mc.player.getHeldItemMainhand()))
                return;
        }
        if (RemainingTicks > 0) {
            --RemainingTicks;
        }
        if (Mode.getValue().equals("Closest")) {
            CurrentTarget = mc.world.loadedEntityList.stream()
                    .filter(this::IsValidTarget)
                    .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                    .orElse(null);
        }
        if (Mode.getValue().equals("Priority")) {
            if (CurrentTarget == null) {
                CurrentTarget = mc.world.loadedEntityList.stream()
                        .filter(this::IsValidTarget)
                        .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                        .orElse(null);
            }
        }
        if (Mode.getValue().equals("Switch")) {
            CurrentTarget = mc.world.loadedEntityList.stream()
                    .filter(this::IsValidTarget)
                    .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                    .orElse(null);
        }
        if (CurrentTarget == null || CurrentTarget.getDistance(mc.player) > Distance.getValue()) {
            CurrentTarget = null;
            canRender = false;
            return;
        }
        if (CurrentTarget != null) {
            int l_Slot = -1;
            if (AutoSwitch.getValue()) {
                for (int l_I = 0; l_I < 9; ++l_I) {
                    if (mc.player.inventory.getStackInSlot(l_I).getItem() instanceof ItemSword) {
                        l_Slot = l_I;
                        mc.player.inventory.currentItem = l_Slot;
                        mc.playerController.updateController();
                        break;
                    }
                }
            }


            if (rotate.getValue()) {
                lookAtPacket(CurrentTarget.posX, CurrentTarget.posY + 1, CurrentTarget.posZ, mc.player, false);
            }

            final float l_Ticks = 20.0f - LagCompensator.INSTANCE.getTickRate();
            final boolean l_IsAttackReady = !HitDelay.getValue() || (mc.player.getCooledAttackStrength(TPSSync.getValue() ? -l_Ticks : 0.0f) >= 1);
            if (!l_IsAttackReady)
                return;
            if (!HitDelay.getValue() && RemainingTicks > 0)
                return;
            RemainingTicks = Ticks.getValue();
            if (swap.getValue()) {
                InventoryUtil.switchToHotbarSlot(sword , false);
            }
            if (mc.getConnection() != null) {
                canRender = true;
                mc.player.connection.sendPacket(new CPacketUseEntity(CurrentTarget));
            }
            if (swap.getValue()) {
                InventoryUtil.switchToHotbarSlot(f, false);
            }
            isAiming = true;
            for (int oao = 0; oao < 360; oao++) {
                oao = b++;
            }
            if(!NOswingArm.getValue()){
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            mc.player.resetCooldown();
        }
    }
    float[] lastRotations;
    @Listener
    public void onTransformItem(RenderItemAnimationEvent.Transform event) {
        if (fullNullCheck() || CurrentTarget == null || !canRender) return;
        if (event.getHand() == EnumHand.MAIN_HAND && CurrentTarget != null && animation.getValue()) {
            if (SwordOnly.getValue() ){
                if(!(mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() instanceof ItemSword)){
                    CurrentTarget = null;
                    canRender = false;
                    return;
                }
            }
            float i;
            if (mc.player.getPrimaryHand().equals(EnumHandSide.RIGHT)) {
                i = 1f;
            } else {
                i = -1f;
            }
            GlStateManager.translate(0.15f * i, 0.3f, 0.0f);
            GlStateManager.rotate(5f * i, 0.0f, 0.0f, 0.0f);
            if (i > 0F) GlStateManager.translate(0.56f, -0.52f, -0.72f * i);
            else GlStateManager.translate(
                    0.56f,
                    -0.52f,
                    0.5F
            );
            GlStateManager.translate(0.0f, 0.2f * 0.6f, 0.0f);
            GlStateManager.rotate(++b, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(1.625f, 1.625f, 1.625f);
        }
    }
    boolean step = false;
    double delay = 0;
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
      public String displayName;
        @Override
    public void onRenderWorld(RenderEvent event) {
        if (CurrentTarget != null && canRender && RenderTarget.getValue()) {
            manager = mc.getRenderManager();
            int tmer =  ((AccessorMinecraft)mc).getRightClickDelayTimer();
            double x = CurrentTarget.lastTickPosX
                    + (CurrentTarget.posX - CurrentTarget.lastTickPosX) * tmer
                    - ((AccessorRenderManager)manager).getRenderPosX();
            double y = CurrentTarget.lastTickPosY
                    + (CurrentTarget.posY - CurrentTarget.lastTickPosY) * tmer
                    - ((AccessorRenderManager)manager).getRenderPosY();
            double z = CurrentTarget.lastTickPosZ
                    + (CurrentTarget.posZ - CurrentTarget.lastTickPosZ) * tmer
                    - ((AccessorRenderManager)manager).getRenderPosZ();

            if(render.getValue().equals("Circle")){
                for (int i = 0; i < 5; i++) {
                    drawCircle(CurrentTarget, tmer, 0.8, delay / 100);
                }

            }
            if (render.getValue().equals("Novo")) {
                if (CurrentTarget instanceof EntityPlayer) {

                    final double width = CurrentTarget.getEntityBoundingBox().maxX
                            - CurrentTarget.getEntityBoundingBox().minX;
                    final double height = CurrentTarget.getEntityBoundingBox().maxY
                            - CurrentTarget.getEntityBoundingBox().minY + 0.25;
                    final float red = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 1.0f : 0.0f;
                    final float green = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.2f : 0.5f;
                    final float blue = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.0f : 1.0f;
                    final float alpha = 0.2f;
                    final float lineRed = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.0f : 9.0f;
                    final float lineGreen = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.2f : 0.5f;
                    final float lineBlue = ((EntityPlayer) CurrentTarget).hurtTime > 0 ? 0.0f : 1.0f;
                    final float lineAlpha = 1.0f;
                    final float lineWdith = 2.0f;
                    drawEntityESP(x, y, z, width, height, red, green, blue, alpha, lineRed, lineGreen, lineBlue, lineAlpha, lineWdith);
                } else {
                    final double width = CurrentTarget.getEntityBoundingBox().maxZ
                            - CurrentTarget.getEntityBoundingBox().minZ;
                    final double height = 0.1;
                    final float red = 0.0f;
                    final float green = 0.5f;
                    final float blue = 1.0f;
                    final float alpha = 0.5f;
                    final float lineRed = 0.0f;
                    final float lineGreen = 0.5f;
                    final float lineBlue = 1.0f;
                    final float lineAlpha = 1.0f;
                    final float lineWdith = 2.0f;
                    drawEntityESP(x, y + CurrentTarget.getEyeHeight() + 0.25, z, width, height, red, green,
                            blue, alpha, lineRed, lineGreen, lineBlue, lineAlpha, lineWdith);
                }
            }
        }
        if (delay > 200) {
            step = false;
        }
        if (delay < 0) {
            step = true;
        }
        if (step) {
            delay += 1;
        } else {
            delay -= 1;
        }


    }
    @Override
    public void onRender(RenderOverlayEvent event) {
        if (targetHUD.getValue()) {
            if(CurrentTarget==null){
                return;
            }
            EntityLivingBase target = (EntityLivingBase) CurrentTarget;
            if (hudMode.getValue().equals("Simple")) {
                if (target != null) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                    FontManager.fontRenderer.drawStringWithShadow(target.getName(),
                            Width.getValue() / 2f - FontManager.fontRenderer.getStringWidth(target.getName()) / 2f,
                            Height.getValue() / 2f - 33, 16777215);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getTextureManager().bindTexture(new ResourceLocation("logo.png"));

                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDepthMask(false);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);

                    int i = 0;
                    while (i < target.getMaxHealth() / 2.0f) {
                        mc.ingameGUI.drawTexturedModalRect(
                                (Width.getValue() / 2) - target.getMaxHealth() / 2.0f * 10.0f / 2.0f + (i * 10),
                                (Height.getValue() / 2 - 20), 16, 0, 9, 9);
                        ++i;
                    }
                    i = 0;
                    while (i < target.getHealth() / 2.0f) {
                        mc.ingameGUI.drawTexturedModalRect(
                                (Width.getValue() / 2) - target.getMaxHealth() / 2.0f * 10.0f / 2.0f + (i * 10),
                                (Height.getValue() / 2 - 20), 52, 0, 9, 9);
                        ++i;
                    }

                    GL11.glDepthMask(true);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                    GlStateManager.disableBlend();
                    GlStateManager.color(1, 1, 1);
                    RenderHelper.disableStandardItemLighting();
                }
            }
            if (hudMode.getValue().equals("Debug")) {


                if (target != null) {
                    int width = (Width.getValue() / 2) + 100;
                    int height = Height.getValue() / 2;

                    EntityLivingBase player = target;
                    if (debugs.getValue()) {
                        Gui.drawRect(width - 70, height + 30, width + 80, height + 105, new Color(255, 255, 255, 100).getRGB());

                    } else {
                        Gui.drawRect(width - 70, height + 30, width + 80, height + 105, new Color(0, 0, 0, 140).getRGB());
                    }
                    FontManager.fontRenderer.drawString(player.getName() + "             " + (ModuleManager.getModuleByName("Criticals").isEnabled() ? "Critical " : " "), width - 65, height + 35, 0xFFFFFF);
                    FontManager.fontRenderer.drawString(player.onGround ? "YES Ground" : "NO Ground", width - 65, height + 50, 0xFFFFFF);
                    FontManager.fontRenderer.drawString("Hp: " + player.getHealth(), width - 65 + FontManager.fontRenderer.getStringWidth("off Ground") + 13, height + 50, 0xFFFFFF);
                    FontManager.fontRenderer.drawString("Distance: " + mc.player.getDistance(player), width - 65, height + 60, -1);
                    FontManager.fontRenderer.drawString("FallDie: " + player.fallDistance, width - 65, height + 70, -1);
                    FontManager.fontRenderer.drawString("Time: " + player.hurtTime, width - 15, height + 70, -1);

                    FontManager.fontRenderer.drawString(player.getHealth() > mc.player.getHealth() ? "浣犲彲鑳戒細杈撳摝" : "浣犲彲鑳借兘璧㈠惂", width - 65, height + 80, player.getHealth() > mc.player.getHealth() ? Color.RED.getRGB() : Color.GREEN.brighter().getRGB());
                    GL11.glPushMatrix();
                    GL11.glColor4f(1, 1, 1, 1);
                    GlStateManager.scale(1.0f, 1.0f, 1.0f);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(player.getHeldItem(EnumHand.MAIN_HAND), width + 50, height + 80);
                    GL11.glPopMatrix();

                    float health = player.getHealth();
                    float healthPercentage = (health / player.getMaxHealth());
                    float targetHealthPercentage = 0;
                    if (healthPercentage != lastHealth) {
                        float diff = healthPercentage - this.lastHealth;
                        targetHealthPercentage = this.lastHealth;
                        this.lastHealth += diff / 8;
                    }
                    Color healthcolor = Color.WHITE;
                    if (healthPercentage * 100 > 75) {
                        healthcolor = Color.GREEN;
                    } else if (healthPercentage * 100 > 50 && healthPercentage * 100 < 75) {
                        healthcolor = Color.YELLOW;
                    } else if (healthPercentage * 100 < 50 && healthPercentage * 100 > 25) {
                        healthcolor = Color.ORANGE;
                    } else if (healthPercentage * 100 < 25) {
                        healthcolor = Color.RED;
                    }
                    Gui.drawRect(width - 70, height + 104, (int) (width - 70 + (149 * targetHealthPercentage)), height + 106, healthcolor.getRGB());
                    Gui.drawRect(width - 70, height + 104, (int) (width - 70 + (149 * healthPercentage)), height + 106, Color.GREEN.getRGB());
                    GL11.glColor4f(1, 1, 1, 1);
                    GuiInventory.drawEntityOnScreen(width + 60, height + 75, 20, Mouse.getX(), Mouse.getY(), player);
                }
            }
            if (hudMode.getValue().equals("Ice")) {
                if (target != null) {

                    DecimalFormat dec = new DecimalFormat("#");

                    healthBarTarget = Width.getValue() / 2 - 41 + (((140) / (target.getMaxHealth())) * (target.getHealth()));

                    // Lower is faster, higher is slower
                    double HealthBarSpeed = 5;

                    if (healthBar > healthBarTarget) {
                        healthBar = ((healthBar) - ((healthBar - healthBarTarget) / HealthBarSpeed));
                    } else if (healthBar < healthBarTarget) {
                        //healthBar = healthBarTarget;
                        healthBar = ((healthBar) + ((healthBarTarget - healthBar) / HealthBarSpeed));
                    }
                    //Command.sendPrivateChatMessage(healthBarTarget + " : " + healthBar);

                    int color = (target.getHealth() / target.getMaxHealth() > 0.66f) ? 0xff00ff00 : (target.getHealth() / target.getMaxHealth() > 0.33f) ? 0xffff9900 : 0xffff0000;

                    color = 0xff00ff00;
                    float[] hsb = Color.RGBtoHSB(((int) 255), ((int) 255), ((int) 255), null);
                    float hue = hsb[0];
                    float saturation = hsb[1];
                    color = Color.HSBtoRGB(hue, saturation, 1);

                    float hue1 = System.currentTimeMillis() % (int) ((100.5f - 50) * 1000) / (float) ((100.5f - 50) * 1000);
                    color = Color.HSBtoRGB(hue1, 0.65f, 1);

                    Gui.drawRect(Width.getValue() / 2 - 110, Height.getValue() / 2 + 100, Width.getValue() / 2 + 110, Height.getValue() / 2 + 170, 0xff36393f);
                    Gui.drawRect(Width.getValue() / 2 - 41, Height.getValue() / 2 + 100 + 54, Width.getValue() / 2 + 100, Height.getValue() / 2 + 96 + 45, 0xff202225);
                    Gui.drawRect(Width.getValue() / 2 - 41, Height.getValue() / 2 + 100 + 54, (int) healthBar, Height.getValue() / 2 + 96 + 45, color);
                    //Gui.drawRect(Width.getValue() / 2 - 41, Height.getValue() / 2 + 100 + 54, healthBarTarget, Height.getValue() / 2 + 96 + 45, color);

                    GlStateManager.color(1, 1, 1);
                    GuiInventory.drawEntityOnScreen(Width.getValue() / 2 - 75, Height.getValue() / 2 + 165, 25, 1f, 1f, target);
                    FontManager.fontRenderer.drawString(target.getName(), Width.getValue() / 2 - 40, Height.getValue() / 2 + 110, -1);
                    FontManager.fontRenderer.drawString("HP: ", Width.getValue() / 2 - 40, Height.getValue() / 2 + 125, -1);
                    FontManager.fontRenderer.drawString("搂c鉂�: 搂f" + dec.format(target.getHealth()), Width.getValue() / 2 - 40 + FontManager.fontRenderer.getStringWidth("HP: "), Height.getValue() / 2 + 125, color);
                }
            }
            if (hudMode.getValue().equals("Fancy")) {

                DecimalFormat dec = new DecimalFormat("#");
                if (target != null) {

                    int color = (target.getHealth() / target.getMaxHealth() > 0.66f) ? 0xff00ff00 : (target.getHealth() / target.getMaxHealth() > 0.33f) ? 0xffff9900 : 0xffff0000;

                    Gui.drawRect(Width.getValue() / 2 - 110, Height.getValue() / 2 + 100, Width.getValue() / 2 + 110, Height.getValue() / 2 + 170, 0x50000000);
                    Gui.drawRect(Width.getValue() / 2 - 110, Height.getValue() / 2 + 100, (int) (Width.getValue() / 2 - 110 + (((220) / (target.getMaxHealth())) * (target.getHealth()))), Height.getValue() / 2 + 96, color);
                    GlStateManager.color(1, 1, 1);
                    GuiInventory.drawEntityOnScreen(Width.getValue() / 2 - 75, Height.getValue() / 2 + 165, 25, 1f, 1f, target);
                    FontManager.fontRenderer.drawString(target.getName(), Width.getValue() / 2 - 40, Height.getValue() / 2 + 110, -1);
                    FontManager.fontRenderer.drawString("HP: ", Width.getValue() / 2 - 40, Height.getValue() / 2 + 125, -1);
                    FontManager.fontRenderer.drawString(dec.format(target.getHealth()) + " 搂f/ " + dec.format(target.getMaxHealth()), Width.getValue() / 2 - 40 + FontManager.fontRenderer.getStringWidth("HP: "), Height.getValue() / 2 + 125, color);
                    FontManager.fontRenderer.drawString(dec.format(target.getMaxHealth()) + "", Width.getValue() / 2 - 40 + FontManager.fontRenderer.getStringWidth("HP: ") + FontManager.fontRenderer.getStringWidth(dec.format(target.getHealth()) + " / "), Height.getValue() / 2 + 125, color);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(target.getHeldItem( EnumHand.MAIN_HAND), Width.getValue() / 2 - 40, Height.getValue() / 2 + 143);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,3), Width.getValue() / 2 - 10, Height.getValue() / 2 + 143);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,2), Width.getValue() / 2 + 20, Height.getValue() / 2 + 143);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,1), Width.getValue() / 2 + 50, Height.getValue() / 2 + 143);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,0), Width.getValue() / 2 + 80, Height.getValue() / 2 + 143);
                    target.getArmorInventoryList();
                }
            }
            if (hudMode.getValue().equals("Skid")) {
                if (target != null) {

                    DecimalFormat dec = new DecimalFormat("#");

                    healthBarTarget = Width.getValue() / 2 - 41 + (((140) / (target.getMaxHealth())) * (target.getHealth()));

                    // Lower is faster, higher is slower
                    double HealthBarSpeed = 5;

                    if (healthBar > healthBarTarget) {
                        healthBar = ((healthBar) - ((healthBar - healthBarTarget) / HealthBarSpeed));
                    } else if (healthBar < healthBarTarget) {
                        //healthBar = healthBarTarget;
                        healthBar = ((healthBar) + ((healthBarTarget - healthBar) / HealthBarSpeed));
                    }
                    //Command.sendPrivateChatMessage(healthBarTarget + " : " + healthBar);

                    int color = (target.getHealth() / target.getMaxHealth() > 0.66f) ? 0xff00ff00 : (target.getHealth() / target.getMaxHealth() > 0.33f) ? 0xffff9900 : 0xffff0000;

                    color = 0xff00ff00;
                    float[] hsb = Color.RGBtoHSB(((int) 255), ((int) 255), ((int) 255), null);
                    float hue = hsb[0];
                    float saturation = hsb[1];
                    color = Color.HSBtoRGB(hue, saturation, 1);

                    float hue1 = System.currentTimeMillis() % (int) ((100.5f - 50) * 1000) / (float) ((100.5f - 50) * 1000);
                    color = Color.HSBtoRGB(hue1, 0.65f, 1);

                    Gui.drawRect(Width.getValue() / 2 - 110, Height.getValue() / 2 + 100, Width.getValue() / 2 + 110, Height.getValue() / 2 + 170, 0xff36393f);
                    Gui.drawRect(Width.getValue() / 2 - 41, Height.getValue() / 2 + 100 + 54, Width.getValue() / 2 + 100, Height.getValue() / 2 + 96 + 45, 0xff202225);
                    Gui.drawRect(Width.getValue() / 2 - 41, Height.getValue() / 2 + 100 + 54, (int) healthBar, Height.getValue() / 2 + 96 + 45, color);
                    //Gui.drawRect(Width.getValue() / 2 - 41, Height.getValue() / 2 + 100 + 54, healthBarTarget, Height.getValue() / 2 + 96 + 45, color);

                    GlStateManager.color(1, 1, 1);
                    GuiInventory.drawEntityOnScreen(Width.getValue() / 2 - 75, Height.getValue() / 2 + 165, 25, 1f, 1f, target);
                    FontManager.fontRenderer.drawString(target.getName(), Width.getValue() / 2 - 40, Height.getValue() / 2 + 110, -1);
                    FontManager.fontRenderer.drawString("HP: ", Width.getValue() / 2 - 40, Height.getValue() / 2 + 125, -1);
                    FontManager.fontRenderer.drawString("搂c鉂�: 搂f" + dec.format(target.getHealth()), Width.getValue() / 2 - 40 + FontManager.fontRenderer.getStringWidth("HP: "), Height.getValue() / 2 + 125, color);
                }
            }
            if (hudMode.getValue().equals("Latest")) {
                String name = StringUtils.stripControlCodes(target.getName());
                float startX = 20;
                float renderX = (Width.getValue() / 2) + startX;
                float renderY = (Height.getValue() / 2) + 10;
                int maxX2 = 30;
                float healthPercentage = target.getHealth() / target.getMaxHealth();
                if (getCurrentArmor(target,3) != null) {
                    maxX2 += 15;
                }
                if (getCurrentArmor(target,2) != null) {
                    maxX2 += 15;
                }
                if (getCurrentArmor(target,1) != null) {
                    maxX2 += 15;
                }
                if (getCurrentArmor(target,0) != null) {
                    maxX2 += 15;
                }
                if (target.getHeldItem(EnumHand.MAIN_HAND) != null) {
                    maxX2 += 15;
                }

                float maxX = Math.max(maxX2, FontManager.fontRenderer.getStringWidth(name) + 30);
                Gui.drawRect((int) renderX, (int) renderY, (int) (renderX + maxX), (int) renderY + 40, new Color(0, 0, 0, 0.6f).getRGB());
                Gui.drawRect((int) renderX, (int) renderY + 38, (int) (renderX + (maxX * healthPercentage)), (int) renderY + 40, getHealthColor(target));
                FontManager.fontRenderer.drawStringWithShadow(name, renderX + 25, renderY + 7, -1);
                int xAdd = 0;
                double multiplier = 0.85;
                GlStateManager.pushMatrix();
                GlStateManager.scale(multiplier, multiplier, multiplier);
                if (getCurrentArmor(target,3) != null) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,3), (int) ((((Width.getValue() / 2) + startX + 23) + xAdd) / multiplier), (int) (((Height.getValue() / 2) + 28) / multiplier));
                    xAdd += 15;
                }
                if (getCurrentArmor(target,2) != null) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,2), (int) ((((Width.getValue() / 2) + startX + 23) + xAdd) / multiplier), (int) (((Height.getValue() / 2) + 28) / multiplier));
                    xAdd += 15;
                }
                if (getCurrentArmor(target,1) != null) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,1), (int) ((((Width.getValue() / 2) + startX + 23) + xAdd) / multiplier), (int) (((Height.getValue() / 2) + 28) / multiplier));
                    xAdd += 15;
                }
                if (getCurrentArmor(target,0) != null) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getCurrentArmor(target,0), (int) ((((Width.getValue() / 2) + startX + 23) + xAdd) / multiplier), (int) (((Height.getValue() / 2) + 28) / multiplier));
                    xAdd += 15;
                }
                if (target.getHeldItem(EnumHand.MAIN_HAND) != null) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(target.getHeldItem( EnumHand.MAIN_HAND), (int) ((((Width.getValue() / 2) + startX + 23) + xAdd) / multiplier), (int) (((Height.getValue() / 2) + 28) / multiplier));
                }
                GlStateManager.popMatrix();
                GuiInventory.drawEntityOnScreen((int) renderX + 12, (int) renderY + 33, 15, target.rotationYaw, target.rotationPitch, target);
            }


        }
    }

    public ItemStack getCurrentArmor(EntityLivingBase target,int iss){
        if(target==null){
            return ItemStack.EMPTY;
        }
        int i = 0;
        for (ItemStack is :  target.getArmorInventoryList()) {
            if(i==iss){
                return is;
            }
            i++;

        }
        return ItemStack.EMPTY;

    }
    public static int getHealthColor(final EntityLivingBase player) {
        final float f = player.getHealth();
        final float f2 = player.getMaxHealth();
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) | 0xFF000000;
    }
    static double healthBarTarget = 0, healthBar = 0;
    float lastHealth = 0;



}