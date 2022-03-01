package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.event.events.client.PlayerDamageBlockEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.graphics.BlockRenderSmooth;
import dev.cuican.staypro.utils.graphics.StayTessellator;
import dev.cuican.staypro.utils.tool.MineUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "InstantBreak", category = Category.PLAYER, description = "Better Mining")
public class Instant extends Module {
    public static BlockRenderSmooth blockRenderSmooth = new BlockRenderSmooth(new BlockPos(0, 0, 0), 500);
    public static FadeUtils fadeBlockSize = new FadeUtils(2000L);
    public static Instant INSTANCE = new Instant();
    public Timer timer = new Timer();
    public Setting<Boolean> haste = setting("Haste", false);
    public Setting<Boolean> ghostHand = setting("GhostHand", false);


    public Setting<Boolean> render = setting("Render", true);
    public Setting<Integer> red = setting("Red", 255, 0, 255).whenTrue(render);
    public Setting<Integer> green = setting("Green", 255, 0, 255).whenTrue(render);
    public Setting<Integer> blue = setting("Blue", 255, 0, 255).whenTrue(render);
    public Setting<Integer> alpha = setting("Alpha", 60, 0, 255).whenTrue(render);
    public Setting<Boolean> rainbow = setting("Rainbow", false).whenTrue(render);
    public Setting<Integer> RGBSpeed = setting("RGBSpeed", 1, 0, 255).whenTrue(rainbow);
    public Setting<Float> Saturation = setting("Saturation", 0.3f, 0, 1).whenTrue(rainbow);
    public Setting<Float> Brightness = setting("Brightness", 1f, 0, 1).whenTrue(rainbow);

    private final List<Block> godBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.BEDROCK);
    private static boolean cancelStart = false;
    private static boolean empty = false;
    private static EnumFacing facing;
    public static BlockPos breakPos;

    private static final Timer breakSuccess = new Timer();

    public static Instant getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Instant();
        }
        return INSTANCE;
    }


    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (fullNullCheck()) {
            rend();
            return;
        }
        if(!isEnabled()){
            rend();
            return;
        }
        if (!(event.getPacket() instanceof CPacketPlayerDigging)) {
            return;
        }
        CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
        if (packet.getAction() != CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            return;
        }
        event.setCancelled(this.cancelStart);
    }


    public static void ondeve(BlockPos pos){
        if (fullNullCheck()) {
            return;
        }

        if (!BlockUtil.canBreak(pos)) {
            return;
        }
        if(breakPos!=null){
            if(breakPos.equals(pos)){
                return;
            }
        }
        rend();
        blockRenderSmooth.setNewPos(pos);
        fadeBlockSize.reset();
        empty = false;
        cancelStart = false;
        breakPos = pos;
        breakSuccess.reset();
        facing = EnumFacing.UP;
        if (breakPos == null) {
            return;
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos,facing));
        cancelStart = true;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, facing));

    }

    @Listener
    public void onBlockEvent(PlayerDamageBlockEvent event) {
        if (fullNullCheck()) {
            rend();
            return;
        }
        if(!isEnabled()){
            return;
        }
        if (!BlockUtil.canBreak(event.pos)) {
            return;
        }
        if(breakPos!=null){
            if(breakPos.equals(event.pos)){
                return;
            }
        }
        blockRenderSmooth.setNewPos(event.pos);
        fadeBlockSize.reset();
        this.empty = false;
        this.cancelStart = false;

        breakPos = event.pos;
        this.breakSuccess.reset();
        this.facing = event.facing;
        if (breakPos == null) {
            return;
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, this.facing));
        this.cancelStart = true;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
        event.setCancelled(true);
    }

    @Override
    public void onRenderTick() {
        if (fullNullCheck()) {
            rend();
            return;
        }
        if (!cancelStart)return;
        if(Wrapper.getPlayer().getDistance(breakPos.getX(), breakPos.getY(), breakPos.getZ())>=10)return;
        if( ModuleManager.getModuleByName("CevBreaker").isEnabled())return;
        int toolslot = MineUtil.findBestTool(breakPos);
        if(toolslot == -1)return;

        if (this.godBlocks.contains(mc.world.getBlockState(breakPos).getBlock()))return;
        if (this.ghostHand.getValue()) {
            int slotMain = mc.player.inventory.currentItem;
            if (mc.world.getBlockState(breakPos).getBlock() == Blocks.OBSIDIAN&&!this.breakSuccess.passedMs(1234L))  return;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(toolslot));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slotMain));
            mc.playerController.updateController();
            return;
        }
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));

    }
    public void insup(){
        for(int i = 9; i < 35; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_PICKAXE) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));

                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                mc.playerController.updateController();
                return;
            }
        }
    }


    private static void rend(){
        empty = false;
        cancelStart = false;
        breakPos = null;
    }
    @Listener
    public void onRenderWorld(RenderEvent event) {

        if(fullNullCheck()){
            rend();
            return;
        }
        if(!isEnabled()){
            rend();
            return;
        }
        if (render.getValue() && breakPos != null && this.cancelStart) {
            Vec3d interpolateEntity = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            AxisAlignedBB pos = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(blockRenderSmooth.getRenderPos());
//            pos = pos.grow(0.0020000000949949026).offset(-interpolateEntity.x, -interpolateEntity.y, -interpolateEntity.z);
//
            renderESP(pos, (float) fadeBlockSize.easeOutQuad());
        }
    }

    public void renderESP(AxisAlignedBB axisAlignedBB, float size) {
        double centerX = axisAlignedBB.minX + ((axisAlignedBB.maxX - axisAlignedBB.minX) / 2);
        double centerY = axisAlignedBB.minY + ((axisAlignedBB.maxY - axisAlignedBB.minY) / 2);
        double centerZ = axisAlignedBB.minZ + ((axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2);
        double full = (axisAlignedBB.maxX - centerX);
        double progressValX = full * size;
        double progressValY = full * size;
        double progressValZ = full * size;
        AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
        int hsBtoRGB = Color.HSBtoRGB((new float[]{
                System.currentTimeMillis() % 11520L / 11520.0f * RGBSpeed.getValue()
        })[0], Saturation.getValue(), Brightness.getValue());
        int r = (hsBtoRGB >> 16 & 0xFF);
        int g = (hsBtoRGB >> 8 & 0xFF);
        int b = (hsBtoRGB & 0xFF);
        StayTessellator.drawBoxTests(axisAlignedBB1, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : (blue.getValue()), alpha.getValue(), 63);
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        rend();
        fadeBlockSize.reset();

        if (haste.getValue()) {
            mc.player.removePotionEffect(MobEffects.HASTE);
        }
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            rend();
            return;
        }

        fadeBlockSize.reset();
    }

    @Override
    public String getModuleInfo() {
        return "Instant";
    }
}
