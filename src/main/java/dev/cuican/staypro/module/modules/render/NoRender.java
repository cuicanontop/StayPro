package dev.cuican.staypro.module.modules.render;



import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.*;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEnderChest;



@ModuleInfo(name = "NoRender", category = Category.RENDER, description = "Ignore entity spawn packets")
public class NoRender extends Module {
    public static NoRender INSTANCE = new NoRender();
    public final Setting<Boolean> BlockLayer = setting("BlockLayer", true);
    public final Setting<Boolean> mob = setting("Mob", false);
    public final Setting<Boolean> sand = setting("Sand", false);
    public final Setting<Boolean> gentity = setting("GEntity", false);
    public final Setting<Boolean> object = setting("Object", false);
    public final Setting<Boolean> xp = setting("XP", false);
    public final Setting<Boolean> paint = setting("Paintings", false);
    public final Setting<Boolean> fire = setting("Fire", true);
    public final Setting<Boolean> explosion = setting("Explosions", true);
    public Setting<Boolean> skylightupdate = setting("SkylightUpdate", true);
    public Setting<Boolean> totemPops = setting("Totem", false);
    public Setting<Boolean> table = setting("EnchantmentTable", false);
    public Setting<Boolean> enderChest = setting("EnderChest", false);
    public Setting<Boolean> banner = setting("Banner", false);
    public Setting<Boolean> hurtCam = setting("NoHurtCam",true);
    public Setting<Boolean> armor = setting("Armor",false);
    public static NoRender getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NoRender();
        return INSTANCE;
    }

    @Listener
    public void NoRenderEventListener(NoRenderEvent event) {
        if ( this.armor.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void oao(RenderBlockOverlayEvent event) {
        if (fire.getValue()&&event.getPartialTicks()!=0)
            event.setCanceled(true);
    }

    @Listener
    public void onRenderOverlay(RenderOverlayEvent2 event) {
        event.setCanceled(true);
    }

    @Listener
    public void RenderLight(RenderLightEvent event) {
        if (skylightupdate.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void totemPop(RenderTotemPopEvent event) {
        if (totemPops.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void banner(RenderBannerEvent event) {
        if (banner.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void enderChest(RenderEnderChestEvent event) {
        if (enderChest.getValue()) {
            event.setCanceled(true);
        }
    }
    @Listener
    public void enchantmentTable(RenderEnchantmentTableEvent event) {
        if (table.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void BlockLayer(RenderLiquidVisionEvent event) {
        if (BlockLayer.getValue()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        Packet<?> packet = event.getPacket();
        if ((packet instanceof SPacketSpawnMob && mob.getValue()) ||
                (packet instanceof SPacketSpawnGlobalEntity && gentity.getValue()) ||
                (packet instanceof SPacketSpawnObject && object.getValue()) ||
                (packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) ||
                (packet instanceof SPacketSpawnObject && sand.getValue()) ||
                (packet instanceof SPacketExplosion && explosion.getValue()) ||
                (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.isCancelled();
    }

    public boolean tryReplaceEnchantingTable(TileEntity tileEntity) {
        if (table.getValue() && tileEntity instanceof TileEntityEnchantmentTable) {
            IBlockState blockState = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, 7);
            mc.world.setBlockState(tileEntity.getPos(), blockState);
            mc.world.markTileEntityForRemoval(tileEntity);
            return true;
        }
        return false;
    }

    public boolean tryReplaceEnderChest(TileEntity tileEntity) {
        if (enderChest.getValue() && tileEntity instanceof TileEntityEnderChest) {
            IBlockState blockState = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, 7);
            mc.world.setBlockState(tileEntity.getPos(), blockState);
            mc.world.markTileEntityForRemoval(tileEntity);
            return true;
        }
        return false;
    }

    public void setInstance() {
        INSTANCE = this;
    }



}
