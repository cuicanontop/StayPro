package dev.cuican.staypro.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.event.events.client.KeyEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.block.BlockInteractionHelper;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.block.WorldUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.math.LagCompensator;
import dev.cuican.staypro.utils.particles.DamageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@ModuleInfo(name = "AutoXin32k",description = "Automatically places 32ks", category = Category.COMBAT)
public class Auto32k extends Module {


    public Setting<Boolean> rotate = setting("Rotate", true);
    public Setting<Boolean> swordOnly = setting("SwordOnly", true);
    public Setting<Boolean> tot = setting("Totem switch", true);
    public Setting<Boolean> criticals = setting("criticals", true);
    public Setting<Boolean> NBT = setting("NBT", true);
    public Setting<Boolean> looks = setting("LookMode", true);
    private final Setting<KeyBind> bind2 = setting("LookPlacesBind",new KeyBind(Keyboard.KEY_NONE, this::looksmods)).whenTrue(looks);
    public Setting<Boolean> fuckmom = setting("Armor detection", true).whenTrue(criticals);
    public Setting<Boolean> Second = setting("Second knife", true).whenTrue(criticals);
    private final Setting<Double> fuck =setting("Execute full tool HP:", 24.0, 0.0, 36.0).whenTrue(Second);
    private final Setting<Integer> packets = setting("criticalsPackets", 3, 1, 4).whenTrue(criticals);
    public Setting<Boolean> no = setting("Close crystal", false);
    public Setting<Boolean> Killaura = setting("Killaura", true);
    public Setting<Boolean> tps = setting("Killaura.TpsSync", true).whenTrue(Killaura);
    public Setting<Boolean> packet = setting("Killaura.Packet", false).whenTrue(Killaura);
    public Setting<Boolean> delay = setting("Killaura.HitDelay", false).whenTrue(Killaura);
    public Setting<Boolean> auto = setting("Killaura.AutoDelay", false).whenFalse(delay);
    private final Setting<Double> fucks =setting("HP:", 13.0, 0.0, 36.0).whenTrue(auto);
    private Setting<Integer> delay2 = setting("KILLDelay", 100, 0, 1000).whenTrue(Killaura);
    public Setting<Boolean> friends = setting("No friends", true);
    private final Setting<KeyBind> bind = setting("PlacesBind", new KeyBind(Keyboard.KEY_NONE,this::place32k));
    private final Setting<Double> range =setting("Place Distance", 5.0, 0.0, 10.0);
    private final Setting<Double> range3 =setting("Detection distance", 10.0, 1.0, 20.0);





    private int fume=0;
    public int totems = 0;
    private String stagething;
    private int Hopperslot;
    private int ShulkerSlot;
    private int playerHotbarSlot;
    public static BlockPos placeTarget;
    public static BlockPos placeTarget2;
    private boolean active;
    private int beds;
    private int stage;
    private int shulkerSlot;
    private final Timer timer = new Timer();
    private final Timer timer2 = new Timer();
    private int hopperSlot;
    private boolean isSneaking;
    private boolean isAttacking = false;
    private boolean offHand = false;
    private float I;
    private Entity IS;

    public void looksmods() {
        placeTarget=null;
        placeTarget2=null;
        this.Hopperslot = -1;
        this.ShulkerSlot = -1;
        RayTraceResult ray = mc.objectMouseOver;
        if (ray == null) {
            return;
        }
        BlockPos placePos = ray.getBlockPos();
        if (mc.world.getBlockState(new BlockPos(placePos.getX(), placePos.getY() + 1, placePos.getZ())).getBlock() != Blocks.AIR || mc.world.getBlockState(new BlockPos(placePos.getX(), placePos.getY() + 2, placePos.getZ())).getBlock() != Blocks.AIR) {
            return;
        }
        if (mc.world.getBlockState(new BlockPos(placePos.getX(), placePos.getY(), placePos.getZ())).getBlock() == Blocks.AIR && mc.world.getBlockState(new BlockPos(placePos.getX() + 1, placePos.getY(), placePos.getZ())).getBlock() == Blocks.AIR && mc.world.getBlockState(new BlockPos(placePos.getX() - 1, placePos.getY(), placePos.getZ())).getBlock() == Blocks.AIR && mc.world.getBlockState(new BlockPos(placePos.getX(), placePos.getY(), placePos.getZ() + 1)).getBlock() == Blocks.AIR && mc.world.getBlockState(new BlockPos(placePos.getX(), placePos.getY(), placePos.getZ() - 1)).getBlock() == Blocks.AIR) {
            return;
        }

        if (placePos == null) {
            return;
        }
        placeTarget = placePos.add(0, 1, 0);
        Anti32k.min=placeTarget;
        this.isSneaking = false;


        for (int x = 0; x <= 8; ++x) {
            Item item = Auto32k.mc.player.inventory.getStackInSlot(x).getItem();
            if (item == Item.getItemFromBlock(Blocks.HOPPER)) {
                this.Hopperslot = x;
                continue;
            }
            if (item instanceof ItemShulkerBox) {
                if (NBT.getValue()) {
                    if (mc.player.inventory.getStackInSlot(x).serializeNBT().copy().toString().indexOf("AttributeModifiers:[{UUIDMost:2345838571545327294L,UUIDLeast:-1985342459327194118L,Amount:32767,AttributeName") != -1) {
                        this.ShulkerSlot = x;
                    }
                } else {
                    this.ShulkerSlot = x;
                }

            }
        }

        if (ShulkerSlot == -1 || Hopperslot == -1) {
            ChatUtil.printChatMessage(this.getHudSuffix() +"Hopper/Shulker No Found!");
            return;
        }
        this.placeBlock(placePos.add(0, 1, 0), Hopperslot);
        this.placeBlock(placePos.add(0, 2, 0), ShulkerSlot);
        Anti32k.min=placeTarget;
        fume=0;
        WorldUtil.openBlock(placePos.add(0, 1, 0));
        ChatUtil.printChatMessage("[Auto32kHopper] " + ChatFormatting.GREEN + "Succesfully" + ChatFormatting.WHITE + " placed 32k");

    }




    private void placeBlock(BlockPos pos, int slot) {
        if (!TestUtil.emptyBlocks.contains(Auto32k.mc.world.getBlockState(pos).getBlock())) {
            return;
        }
        if (slot != Auto32k.mc.player.inventory.currentItem) {
            Auto32k.mc.player.inventory.currentItem = slot;
        }
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing f = enumFacingArray[n2];
            Block neighborBlock = Auto32k.mc.world.getBlockState(pos.offset(f)).getBlock();
            if (!TestUtil.emptyBlocks.contains(neighborBlock)) {
                Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                Auto32k.mc.playerController.processRightClickBlock(Auto32k.mc.player, Auto32k.mc.world, pos.offset(f), f.getOpposite(), new Vec3d((Vec3i)pos), EnumHand.MAIN_HAND);
                Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            ++n2;
        }
    }

    //criticals on
    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if(Second.getValue()){

            if(I>=fuck.getValue()){
                return;
            }

        }
            if(fuckmom.getValue()){
                if(IS!=null){
                    for (ItemStack armourStack : IS.getArmorInventoryList()) {

                        if(armourStack.getItem()!=Items.DIAMOND_CHESTPLATE||armourStack.getItem()!=Items.DIAMOND_HELMET||armourStack.getItem()!=Items.DIAMOND_LEGGINGS||armourStack.getItem()!=Items.DIAMOND_BOOTS){
                            CPacketUseEntity packet;
                            if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
                                if (!criticals.getValue()) {
                                    return;
                                }

                                if (!timer.passedMs(0L)) {
                                    return;
                                }
                                if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase && !mc.player.isInWater() && !mc.player.isInLava()) {
                                    switch (packets.getValue()) {
                                        case 1: {
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (double) 0.1f, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            break;
                                        }
                                        case 2: {
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            break;
                                        }
                                        case 3: {
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0125, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            break;
                                        }
                                        case 4: {
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1625, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 4.0E-6, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-6, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                            mc.player.connection.sendPacket(new CPacketPlayer());
                                            mc.player.onCriticalHit(Objects.requireNonNull(packet.getEntityFromWorld(mc.world)));
                                        }
                                    }
                                    timer.reset();
                                }
                            }
                            return;
                        }

                    }

                }


            }



        CPacketUseEntity packet;
        if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
            if (!criticals.getValue()) {
                return;
            }

            if (!timer.passedMs(0L)) {
                return;
            }
            if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase && !mc.player.isInWater() && !mc.player.isInLava()) {
                switch (packets.getValue()) {
                    case 1: {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (double) 0.1f, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    }
                    case 2: {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    }
                    case 3: {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0125, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    }
                    case 4: {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1625, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 4.0E-6, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-6, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer());
                        mc.player.onCriticalHit(Objects.requireNonNull(packet.getEntityFromWorld(mc.world)));
                    }
                }
                timer.reset();
            }
        }
    }

    public void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!this.isSneaking) {
            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (this.rotate.getValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        Auto32k.mc.playerController.processRightClickBlock(Auto32k.mc.player, Auto32k.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
    }


    private BlockPos getNearestHopper2() {
        Double maxDist = this.range.getValue();

        List<BlockPos> Blocksss = new ArrayList<>();
        Double x;
        for (x = maxDist; x >= -maxDist; x--) {
            Double y;
            for (y = maxDist; y >= -maxDist; y--) {

                Double z;
                for (z = maxDist; z >= -maxDist; z--) {
                    BlockPos pos = new BlockPos(Wrapper.getPlayer().posX + x, Wrapper.getPlayer().posY + y, Wrapper.getPlayer().posZ + z);
                    double dist = Wrapper.getPlayer().getDistance(pos.getX(), pos.getY(), pos.getZ());
                    BlockPos pos2 = new BlockPos(pos.getX(),pos.getY()+1,pos.getZ());
                    BlockPos pos3 = new BlockPos(pos.getX(),pos.getY()-1,pos.getZ());

                    if (dist >= maxDist && range3.getValue() >= dist    &&Wrapper.getWorld().getBlockState(pos3).getBlock() != Blocks.HOPPER && !(Wrapper.getWorld().getBlockState(pos3).getBlock() instanceof BlockShulkerBox)&&Wrapper.getWorld().getBlockState(pos3).getBlock() != Blocks.AIR) {
                        if(Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.WATER&&Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.LAVA&&Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.AIR){
                            continue;
                        }
                        if(Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.WATER&&Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.LAVA&&Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.AIR){
                            continue;
                        }
//
                        if(pos.getY()<1||pos.getY()>255){
                            continue;

                        }
                        EntityPlayer   target = getTarget(range3.getValue(), true);
                            double dists = Wrapper.getPlayer().getDistance(pos.getX() , pos.getY(), pos.getZ() );
                            if(dists>range3.getValue()){ continue;}
                            if(dists<range.getValue()){continue;}
                            if(target!=null){
                                if (dist<=Math.sqrt((target.posX - pos.getX()) * (target.posX - pos.getX()) + (target.posY - pos.getY()) * (target.posY - pos.getY()) + (target.posZ - pos.getZ()) * (target.posZ - pos.getZ()))){
                                    continue;
                                }

                            }

                            Blocksss.add(pos);




                    }


                }
            }
        }


        int a = 0;

        for (BlockPos renderBlock : Blocksss) {
            a++;
        }
        if (a == 0) {
            return null;
        }
        double ant =  -1;
        int fomen = -1;
        EntityPlayer   target = getTarget(range3.getValue(), true);
        for (int i = 0; i < a; i++) {
            BlockPos pos =Blocksss.get(i);
            double dist = Math.sqrt((target.posX - pos.getX()) * (target.posX - pos.getX()) + (target.posY - pos.getY()) * (target.posY - pos.getY()) + (target.posZ - pos.getZ()) * (target.posZ - pos.getZ()));
            if(dist>ant){
                ant=dist;
                fomen=i;
            }
        }
       if(fomen==-1){
           return null;
       }
        return Blocksss.get(fomen);

//        int a = 0;
//
//        for (BlockPos renderBlock : Blocksss) {
//            a++;
//        }
//        if (a == 0) {
//            return null;
//        }
//        Random random = new Random();
//
//        return Blocksss.get(random.nextInt(a));
    }
//    private BlockPos getNearestHopper() {
//        Double maxDist = this.range.getValue();
//        BlockPos ret = null;
//        List<BlockPos> Blocksss = new ArrayList<>();
//        Double x;
//        for (x = maxDist; x >= -maxDist; x--) {
//            Double y;
//            for (y = maxDist; y >= -maxDist; y--) {
//
//                Double z;
//                for (z = maxDist; z >= -maxDist; z--) {
//                    BlockPos pos = new BlockPos(Wrapper.getPlayer().posX + x+0.5D, Wrapper.getPlayer().posY + y+0.5D, Wrapper.getPlayer().posZ + z+0.5D);
//                    double dist = Wrapper.getPlayer().getDistance(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D);
//                    BlockPos pos2 = new BlockPos(pos.getX()+0.5D,pos.getY()+1,pos.getZ()+0.5D);
//                    BlockPos pos3 = new BlockPos(pos.getX()+0.5D,pos.getY()-1,pos.getZ()+0.5D);
//
//                        if (dist >= maxDist && range3.getValue() >= dist    &&Wrapper.getWorld().getBlockState(pos3).getBlock() != Blocks.HOPPER && !(Wrapper.getWorld().getBlockState(pos3).getBlock() instanceof BlockShulkerBox)&&Wrapper.getWorld().getBlockState(pos3).getBlock() != Blocks.AIR) {
//                            if(Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.WATER&&Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.LAVA&&Wrapper.getWorld().getBlockState(pos).getBlock() != Blocks.AIR){
//                                continue;
//                            }
//                            if(Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.WATER&&Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.LAVA&&Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.AIR){
//                                continue;
//                            }
////
//                            if(pos.getY()<1||pos.getY()>255){
//                                continue;
//
//                            }
//                            if (anti.getValue()) {
//
//                                EntityPlayer   target = getTarget(range4.getValue(), true);
//                                if (target != null) {
//
//                                        if(range2.getValue()>range3.getValue()&&Wrapper.getPlayer().getDistance(pos.getX() , pos.getY(), pos.getZ())<Wrapper.getPlayer().getDistance(target.posX , target.posY, target.posZ )){
//                                            double dists = Wrapper.getPlayer().getDistance(target.posX , target.posY, target.posZ );
//                                            if(Wrapper.getPlayer().getDistance(pos.getX() , pos.getY(), pos.getZ() )>4.5||Wrapper.getPlayer().getDistance(pos.getX() , pos.getY()+1, pos.getZ() )>4.5){
//                                                continue;
//                                            }
//                                            if (range2.getValue()-range3.getValue()>=dists){
//                                                continue;
//                                            }
//                                        }
//
//
//
//                                    double distance = Math.sqrt((target.posX - pos.getX()) * (target.posX - pos.getX()) + (target.posY - pos.getY()) * (target.posY - pos.getY()) + (target.posZ - pos.getZ()) * (target.posZ - pos.getZ()));
//                                    if (distance >= range2.getValue()) {
//                                        double dists = Wrapper.getPlayer().getDistance(pos.getX() , pos.getY(), pos.getZ() );
//                                        if(dists>6){
//                                            continue;
//                                        }
//                                        maxDist = dist;
//                                        ret = pos;
//                                        if (dists>=Math.sqrt((target.posX - pos.getX()) * (target.posX - pos.getX()) + (target.posY - pos.getY()) * (target.posY - pos.getY()) + (target.posZ - pos.getZ()) * (target.posZ - pos.getZ()))){
//                                            continue;
//                                        }
//                                        if(BlockUtil.isPositionPlaceable(pos, true)!=3&&dists>3){
//                                            continue;
//                                        }
//                                        if(Auxiliary.getValue()){
//                                            int bet =   BlockUtil.getenum(new BlockPos(target.posX,target.posY,target.posZ),new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ));
//                                            if(BlockUtil.getenum(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ),pos)==bet||bet==5){
//                                                Blocksss.add(pos);
//                                            }
//                                        }else {
//                                            Blocksss.add(pos);
//
//                                        }
//
//
//
//                                    }
//                                } else {
//                                    double dists = Wrapper.getPlayer().getDistance(pos.getX() , pos.getY(), pos.getZ() );
//                                    if(dists>6){
//                                        continue;
//                                    }
//                                    maxDist = dist;
//                                    ret = pos;
//                                    Blocksss.add(pos);
//                                }
//
//                            }else {
//                                double dists = Wrapper.getPlayer().getDistance(pos.getX() , pos.getY(), pos.getZ() );
//                                if(dists>6){
//                                    continue;
//                                }
//                                maxDist = dist;
//                                ret = pos;
//                                Blocksss.add(pos);
//                            }
//
//                        }
//
//
//                }
//            }
//        }
//
//
//        int a = 0;
//
//        for (BlockPos renderBlock : Blocksss) {
//            a++;
//        }
//        if (a == 0) {
//            return null;
//        }
//        Random random = new Random();
//
//        return Blocksss.get(random.nextInt(a));
//    }
    private BlockPos getNearestHopper3() {
        Double maxDist = this.range.getValue();
        BlockPos ret = null;
        List<BlockPos> Blocksss = new ArrayList<>();
        Double x;
        for (x = maxDist; x >= -maxDist; x--) {
            Double y;
            for (y = maxDist; y >= -maxDist; y--) {

                Double z;
                for (z = maxDist; z >= -maxDist; z--) {
                    BlockPos pos = new BlockPos(Wrapper.getPlayer().posX + x, Wrapper.getPlayer().posY + y, Wrapper.getPlayer().posZ + z);
                    double dist = Wrapper.getPlayer().getDistance(pos.getX(), pos.getY(), pos.getZ());
                    BlockPos pos2 = new BlockPos(pos.getX(),pos.getY()+1,pos.getZ());
                    BlockPos pos3 = new BlockPos(pos.getX(),pos.getY()-1,pos.getZ());

                    if (range.getValue() >= dist&&Wrapper.getWorld().getBlockState(pos3).getBlock() != Blocks.AIR&&(Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.AIR||Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.LAVA||Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.WATER)) {

                        if(Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.WATER&&Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.LAVA&&Wrapper.getWorld().getBlockState(pos2).getBlock() != Blocks.AIR){
                            continue;
                        }
                        Blocksss.add(pos);

                    }


                }
            }
        }

        EntityPlayer   target = getTarget(range3.getValue(), true);
        int a = 0;

        for (BlockPos renderBlock : Blocksss) {
            a++;
        }
        if (a == 0) {
            return null;
        }
        BlockPos  pp= null;
        if (target!=null){

            for (int i = 1; i < a; i++) {
            BlockPos pos2 =     Blocksss.get(i);

           if (pp==null){
               pp = pos2;
               continue;
           }
                double distance = Math.sqrt((target.posX - pp.getX()) * (target.posX - pp.getX()) + (target.posY - pp.getY()) * (target.posY - pp.getY()) + (target.posZ - pp.getZ()) * (target.posZ - pp.getZ()));
                double distance2 = Math.sqrt((target.posX - pos2.getX()) * (target.posX - pos2.getX()) + (target.posY - pos2.getY()) * (target.posY - pos2.getY()) + (target.posZ - pos2.getZ()) * (target.posZ - pos2.getZ()));

                if(distance2>distance){
                    pp = pos2;
                    continue;
                }

            }
        }
        if(pp==null){
            Random random = new Random();
       return Blocksss.get(random.nextInt(a));
        }

        return pp;
    }
    public void fck() {

        int i;
        int t;
        this.totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            ++this.totems;
        } else {


            if (mc.player.inventory.getItemStack().isEmpty()) {
                if (this.totems == 0) {
                    return;
                }

                t = -1;
                for (i = 0; i < 45; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() != Items.TOTEM_OF_UNDYING) {
                        continue;
                    }
                    t = i;
                    break;
                }
                if (t == -1) {
                    return;
                }
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
                this.moving = true;
                if (this.moving) {
                    mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                    this.moving = false;
                    if (!mc.player.inventory.getItemStack().isEmpty()) {
                        this.returnI = true;
                    }
                    return;
                }
            }
        }





        if (this.returnI) {
            t = -1;
            for (i = 0; i < 45; ++i) {
                if (!mc.player.inventory.getStackInSlot((int) i).isEmpty()) {
                    continue;
                }
                t = i;
                break;
            }
            if (t == -1) {
                return;
            }
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            this.returnI = false;
        }
    }

    private EntityPlayer getTarget(double range, boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range) ) {
                continue;
            }
            if (target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }
            if (!(mc.player.getDistanceSq(player) < distance)) {
                continue;
            }
            target = player;
            distance = mc.player.getDistanceSq(player);
        }
        return target;
    }
    public  void place32k() {
        this.Hopperslot = -1;
        this.ShulkerSlot = -1;
        this.isSneaking = false;
        placeTarget = null;
        placeTarget2 = null;

        for (int x = 0; x <= 8; ++x) {
            Item item = Auto32k.mc.player.inventory.getStackInSlot(x).getItem();
            if (item == Item.getItemFromBlock(Blocks.HOPPER)) {
                this.Hopperslot = x;
                continue;
            }
            if (item instanceof ItemShulkerBox) {
                if(NBT.getValue()){
                    if(mc.player.inventory.getStackInSlot(x).serializeNBT().copy().toString().indexOf("AttributeModifiers:[{UUIDMost:2345838571545327294L,UUIDLeast:-1985342459327194118L,Amount:32767,AttributeName")!=-1){
                        this.ShulkerSlot = x;
                    }
                }else {
                    this.ShulkerSlot = x;
                }

            }
        }

        if (ShulkerSlot == -1 || Hopperslot == -1) {
            ChatUtil.printChatMessage("Hopper/Shulker No Found!");
            return;
        }
        placeTarget = getNearestHopper3();
//        if(this.placeTarget==null){
//            this.placeTarget = getNearestHopper2();
//        }



        if (placeTarget != null) {


//            if(Wrapper.getPlayer().getDistance(placeTarget.getX() , placeTarget.getY(), placeTarget.getZ() )>4.5){
//                placeTarget = null;
//                int swordIndex;
//                int i;
//                for(swordIndex = -6; swordIndex <= 6; ++swordIndex) {
//                    for(i = -2; i <= 2; ++i) {
//                        for(int z = -6; z <= 6; ++z) {
//                            BlockPos autoPos = EntityUtil.getPlayerPos(Auto32k.mc.player).add(swordIndex, i, z);
//
//                            if (!TestUtil.emptyBlocks.contains(Auto32k.mc.world.getBlockState(autoPos).getBlock()) && TestUtil.emptyBlocks.contains(Auto32k.mc.world.getBlockState(autoPos.add(0, 1, 0)).getBlock()) && TestUtil.emptyBlocks.contains(Auto32k.mc.world.getBlockState(autoPos.add(0, 2, 0)).getBlock()) && Auto32k.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(autoPos.add(0, 1, 0))).isEmpty() && Auto32k.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(autoPos.add(0, 2, 0))).isEmpty() && (BlockUtil.getDistance32k(target, autoPos.add(0, 1, 0)) <= 6.0 ) && (BlockUtil.getDistance32k(target, autoPos.add(0, 2, 0)) <= 6.0) && (BlockUtil.getDistance32k((Entity)Auto32k.mc.player, autoPos) > 4.5) && (BlockUtil.getDistance32k((Entity)Auto32k.mc.player, autoPos.add(0, 1, 0)) > 4.5) && (BlockUtil.getDistance32k((Entity)Auto32k.mc.player, autoPos.add(0, 2, 0)) > 4.5)) {
//                                this.placeTarget = new BlockPos(autoPos.getX(),autoPos.getY()+1,autoPos.getZ());
//                            }
//                        }
//                    }
//                }
//            }
//            if(placeTarget==null){return;}



            Auto32k.mc.player.inventory.currentItem = this.Hopperslot;
            this.stagething = "HOPPER";
            placeBlock(new BlockPos(placeTarget), EnumFacing.DOWN);
            Auto32k.mc.player.inventory.currentItem = this.ShulkerSlot;
            this.stagething = "SHULKER";
            placeBlock(new BlockPos(placeTarget.getX(), placeTarget.getY()+1, placeTarget.getZ()), EnumFacing.DOWN);
            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            this.stagething = "OPENING";
            if(mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING&&tot.getValue()){
                tis.reset();
                fck();
            }
            mc.player.connection.sendPacket(new CPacketHeldItemChange(ShulkerSlot));
            fume=0;
            WorldUtil.openBlock(placeTarget);




            ChatUtil.printChatMessage("[Auto32kHopper] " + ChatFormatting.GREEN + "Succesfully" + ChatFormatting.WHITE + " placed 32k");

        } else {
            ChatUtil.printChatMessage("[Auto32kHopper] " + ChatFormatting.RED + "FAILED" + ChatFormatting.WHITE + " because your dumbass thought you could place there");


        }
        return;

    }

    boolean moving = false;
    boolean returnI = false;

    private Timer tis = new Timer();

    @Listener
    public void onkey(KeyEvent event){
        if (Keyboard.getEventKeyState() &&  bind2.getValue().getKeyCode() > -1&&bind2.getValue().getKeyCode() > -1&&looks.getValue()) {
            if (Keyboard.isKeyDown(bind2.getValue().getKeyCode()) && mc.currentScreen == null&&Keyboard.isKeyDown(bind.getValue().getKeyCode())) {
                Anti32k.min=null;
                looksmods();
                return;

            }

        }
        if (Keyboard.getEventKeyState()  && bind.getValue().getKeyCode() > -1) {
            if (Keyboard.isKeyDown(bind.getValue().getKeyCode())&&event.getKey()==bind.getValue().getKeyCode()&& mc.currentScreen == null) {
                place32k();
            }
        }
    }

    @Override
    public void onTick() {

        if (mc.player == null || mc.player.isDead) {
            return;
        }
        List<Entity> targets = mc.world.loadedEntityList.stream()
                .filter(entity -> entity != mc.player)
                .filter(entity -> mc.player.getDistance(entity) <= range.getValue())
                .filter(entity -> !entity.isDead)
                .filter(entity -> entity instanceof EntityPlayer)
                .filter(entity -> ((EntityPlayer) entity).getHealth() > 0)
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e)))
                .collect(Collectors.toList());

        targets.forEach(target -> {

            IS=target;
            if (mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && Auto32k.mc.currentScreen instanceof GuiHopper && Killaura.getValue()) {
                if (friends.getValue()) {
                    if (!FriendManager.isFriend(target.getName())) {


                        I = EntityUtil.getHealth(target);
                        attack( target);
                        timer2.reset();
                    }
                } else {

                    I = EntityUtil.getHealth(target);
                    attack( target);


                }
            }

        });
        int w = 0;

        if (Auto32k.mc.currentScreen instanceof GuiHopper) {
            Anti32k.min=placeTarget;
            GuiHopper gui = (GuiHopper) Auto32k.mc.currentScreen;
            this.active = true;
            for (int i = 0; i <= 4; i++) {
                if (gui.inventorySlots.inventorySlots.get(i).getStack().getItem() == Items.DIAMOND_SWORD) {

                    if (swordOnly.getValue()) {
                        if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemAir)) {
                            int ss = InventoryUtil.findHotbarBlock(Blocks.AIR);
                            if (ss != -1) {
                                mc.player.inventory.currentItem = ss;
                            } else {

                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ITEM,new BlockPos(1, 0, 0), mc.player.getHorizontalFacing()));

                            }


                        }
                    }

                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemAir) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, i, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                        break;
                    }


                }
            }

            Anti32k.min=placeTarget2;
            placeTarget2 = placeTarget;

        }else {

//            if(mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING&&placeTarget!=null){
//                WorldUtil.openBlock(this.placeTarget);
//            }
            fume++;
            if(fume>=10){
                placeTarget2 = null;
                Anti32k.min=placeTarget2;
                fume=0;
            }

        }



    }




    private void attack(Entity target) {
        if(NBT.getValue()&&mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem()==Items.DIAMOND_SWORD){
            if(mc.player.getHeldItem(EnumHand.MAIN_HAND).serializeNBT().copy().toString().indexOf("AttributeModifiers:[{UUIDMost:2345838571545327294L,UUIDLeast:-1985342459327194118L,Amount:32767,AttributeName")==-1){
                timer.reset();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ITEM,new BlockPos(1, 0, 0), mc.player.getHorizontalFacing()));
            return;
            }
        }
        if(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem()!=Items.DIAMOND_SWORD){
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ITEM,new BlockPos(1, 0, 0), mc.player.getHorizontalFacing()));
            return;
        }

        int wait = delay.getValue().booleanValue() ? delay2.getValue() : (int) (DamageUtil.getCooldownByWeapon(mc.player) * (tps.getValue().booleanValue() ? LagCompensator.INSTANCE.getTickRate() : 1.0F));
        if(auto.getValue()&&!delay.getValue()){
            EntityPlayer   targets = getTarget(range3.getValue(), true);
            if(targets!=null){
                if(targets.getHeldItem(EnumHand.OFF_HAND).getItem()==Items.TOTEM_OF_UNDYING&&targets.getHealth()<=fucks.getValue()){
                    wait = delay2.getValue();
                }
            }

        }

        if (!timer.passedMs(wait)) {
            return;
        }

        if (target == null) {
            return;
        }
        look.lookAt(new Vec3d(target.posX,target.posY,target.posZ));
        EntityUtil.attackEntity(target, packet.getValue().booleanValue(), true);
        timer.reset();
    }


}

