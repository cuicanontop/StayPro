package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.BlockEvent;
import dev.cuican.staypro.event.events.client.PlayerDamageBlockEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.modules.combat.Surround;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.RenderUtil;
import dev.cuican.staypro.utils.RenderUtilss;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.math.LagCompensator;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Iterator;

@ModuleInfo(name = "SpeedMine", category = Category.PLAYER, description = "Speeds up mining.")
public class Speedmine extends Module {
    private static Speedmine INSTANCE = new Speedmine();
    public final Timer timer = new Timer();
    public Setting<Boolean> tweaks = setting("Tweaks", true);
    public Setting<Boolean> reset = setting("Reset", true);
    public Setting<Boolean> noBreakAnim = setting("NoBreakAnim", false);
    public Setting<Boolean> noDelay = setting("NoDelay", false);
    public Setting<Boolean> noSwing = setting("NoSwing", false);
    public Setting<Boolean> allow = setting("AllowMultiTask", false);
    public Setting<Boolean> doubleBreak = setting("DoubleBreak", false);
    public Setting<Boolean> webSwitch = setting("WebSwitch", false);
    public Setting<Boolean> silentSwitch = setting("SilentSwitch", false);
    public Setting<Integer> range = setting("range", 10, 1, 50);

    public Setting<Boolean> render = setting("Render", true);
    public Setting<Integer> red = setting("Red", 255, 0, 255).whenTrue(render);
    public Setting<Integer> green = setting("Green", 10, 0, 255).whenTrue(render);
    public Setting<Integer> blue = setting("Blue", 60, 0, 255).whenTrue(render);
    public Setting<Boolean> box = setting("Box", true).whenTrue(render);
    public Setting<Integer> boxAlpha = setting("BoxAlpha", 85, 0, 255).whenTrue(render).whenTrue(box);
    public Setting<Boolean> outline = setting("Outline", true).whenTrue(render);
    public Setting<Float> lineWidth = setting("LineWidth",  1.0F, 0.1F, 5.0F).whenTrue(render).whenTrue(outline);

    public BlockPos currentPos;
    public IBlockState currentBlockState;
    public float breakTime;
    private boolean isMining;
    private BlockPos lastPos;
    private EnumFacing lastFacing;

    public Speedmine() {
        setInstance();
    }

    public static Speedmine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Speedmine();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        if (this.currentPos != null) {
            if ( mc.player.getDistanceSq(this.currentPos) > MathUtil.square((double)this.range.getValue())) {
                this.currentPos = null;
                this.currentBlockState = null;
                return;
            }

            if ( this.silentSwitch.getValue() && this.timer.passedMs(1234L) && this.getPickSlot() != -1) {
                int is = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem =this.getPickSlot();
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos,EnumFacing.UP));
                mc.player.inventory.currentItem =is;
                mc.playerController.updateController();
            }




            if (this.noDelay.getValue()) {
                mc.playerController.blockHitDelay = 0;
            }

            if (this.isMining && this.lastPos != null && this.lastFacing != null && this.noBreakAnim.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
            }

            if (this.reset.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && !(Boolean)this.allow.getValue()) {
                mc.playerController.isHittingBlock = false;
            }
            if (mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) && mc.world.getBlockState(this.currentPos).getBlock() != Blocks.AIR) {
                if (this.webSwitch.getValue() && this.currentBlockState.getBlock() == Blocks.WEB && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                    InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
                }
            } else {
                this.currentPos = null;
                this.currentBlockState = null;
            }
        }

    }



    @Override
    public void onRenderWorld(RenderEvent event) {
        if (this.render.getValue() && this.currentPos != null) {
            Color color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.boxAlpha.getValue());
            RenderUtil.boxESP(this.currentPos, color, this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true);
        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (!fullNullCheck()) {

                if (this.noSwing.getValue() && event.getPacket() instanceof CPacketAnimation) {
                    event.isCancelled();
                }

                CPacketPlayerDigging packet;
                if (this.noBreakAnim.getValue() && event.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)event.getPacket()) != null) {
                    packet.getPosition();

                    try {
                        Iterator var3 = mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(packet.getPosition())).iterator();

                        while(var3.hasNext()) {
                            Entity entity = (Entity)var3.next();
                            if (entity instanceof EntityEnderCrystal) {
                                this.showAnimation();
                                return;
                            }
                        }
                    } catch (Exception var5) {
                    }

                    if (packet.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                        this.showAnimation(true, packet.getPosition(), packet.getFacing());
                    }

                    if (packet.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                        this.showAnimation();
                    }
                }
            }


    }


    @Listener
    public void onBlockEvent(BlockEvent event) {
        if (!fullNullCheck()) {
            if ( mc.world.getBlockState(event.pos).getBlock() instanceof BlockEndPortalFrame) {
                mc.world.getBlockState(event.pos).getBlock().setHardness(50.0F);
            }

            if (this.reset.getValue() && mc.playerController.curBlockDamageMP > 0.1F) {
                mc.playerController.isHittingBlock = true;
            }

            if (tweaks.getValue()) {
                if (BlockUtil.canBreak(event.pos)) {
                    if (reset.getValue()) {
                        mc.playerController.isHittingBlock = false;
                    }

                           if (this.currentPos == null) {
                                this.currentPos = event.pos;
                                this.currentBlockState = mc.world.getBlockState(this.currentPos);
                                ItemStack object = new ItemStack(Items.DIAMOND_PICKAXE);
                                this.breakTime = object.getDestroySpeed(this.currentBlockState) / 3.71F;
                                this.timer.reset();
                            }

                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                            event.isCancelled();

                }

                BlockPos above;
                if (this.doubleBreak.getValue() && BlockUtil.canBreak(above = event.pos.add(0, 1, 0)) && mc.player.getDistance((double)above.getX(), (double)above.getY(), (double)above.getZ()) <= 5.0D) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
                    mc.playerController.onPlayerDestroyBlock(above);
                    mc.world.setBlockToAir(above);
                }
            }

        }
    }

    private int getPickSlot() {
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_PICKAXE) {
                return i;
            }
        }

        return -1;
    }

    private void showAnimation(boolean isMining, BlockPos lastPos, EnumFacing lastFacing) {
        this.isMining = isMining;
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public void showAnimation() {
        this.showAnimation(false, null, null);
    }







}

