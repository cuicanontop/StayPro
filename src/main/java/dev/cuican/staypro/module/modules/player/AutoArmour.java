package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.modules.misc.XCarry;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.particles.DamageUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "AutoArmour", category = Category.PLAYER, description = "Automatically equips armour")
public class AutoArmour extends Module {
    public final Setting<Integer> delay = setting("Delay", 50, 0, 500);
    public final Setting<Boolean> mendingTakeOff = setting("TakeOffMend", true);
    public final Setting<Integer> closestEnemy = setting("EnemyRange", 8, 1, 20).whenFalse(mendingTakeOff);
    public final Setting<Integer> mend_percentage = setting("Mend%", 95, 1, 100).whenFalse(mendingTakeOff);
    public final Setting<Boolean> curse = setting("CurseOfBinding", false);
    public final Setting<Integer> actions = setting("Actions", 1, 1, 12);
    public final Setting<Boolean> updateController = setting("Update", true);
    public final Setting<Boolean> shiftClick = setting("ShiftClick", true);
    public final Timer timer = new Timer();
    public final Timer elytraTimer = new Timer();
    public final List<Integer> doneSlots = new ArrayList<>();
    private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
    public boolean mending = false;

    @Override
    public void onEnable() {
        timer.reset();
        taskList.clear();
        elytraTimer.reset();
    }


    @Override
    public void onDisable() {
        timer.reset();
        elytraTimer.reset();
        taskList.clear();
        doneSlots.clear();
        mending = false;
    }




    @Override
    public void onRenderTick() {
        if (fullNullCheck() || mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }

        if (this.taskList.isEmpty()) {
            int slot;
            int slot2;
            int slot3;
            ItemStack chest;
            int slot4;
            if (this.mendingTakeOff.getValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && mc.gameSettings.keyBindUseItem.isKeyDown() && (this.isSafe() || EntityUtil.isSafe(mc.player, 1, false, true))) {
                mending = true;
                ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
                if (!helm.isEmpty() && DamageUtil.getRoundedDamage(helm) >= this.mend_percentage.getValue()) {
                    this.takeOffSlot(5);
                    mending = true;
                }
                ItemStack chest2 = mc.player.inventoryContainer.getSlot(6).getStack();
                if (!chest2.isEmpty() && DamageUtil.getRoundedDamage(chest2) >= this.mend_percentage.getValue()) {
                    this.takeOffSlot(6);
                    mending = true;
                }
                ItemStack legging2 = mc.player.inventoryContainer.getSlot(7).getStack();
                if (!legging2.isEmpty() && DamageUtil.getRoundedDamage(legging2) >= this.mend_percentage.getValue()) {
                    this.takeOffSlot(7);
                    mending = true;
                }
                ItemStack feet2 = mc.player.inventoryContainer.getSlot(8).getStack();
                if (!feet2.isEmpty() && DamageUtil.getRoundedDamage(feet2) >= this.mend_percentage.getValue()) {
                    this.takeOffSlot(8);
                    mending = true;
                }
                return;
            }
            ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
            if (helm.getItem() == Items.AIR && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                this.getSlotOn(5, slot4);
            }
            if ((chest = mc.player.inventoryContainer.getSlot(6).getStack()).getItem() == Items.AIR) {
                if (this.taskList.isEmpty()) {
                    if ((slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                        this.getSlotOn(6, slot3);
                        mending = true;
                    }
                }
            }
            if (chest.getItem() == Items.ELYTRA && this.elytraTimer.passedMs(500L) && this.taskList.isEmpty()) {
                slot3 = InventoryUtil.findItemInventorySlot(Items.DIAMOND_CHESTPLATE, false, XCarry.getInstance().isEnabled());
                if (slot3 == -1 && (slot3 = InventoryUtil.findItemInventorySlot(Items.IRON_CHESTPLATE, false, XCarry.getInstance().isEnabled())) == -1 && (slot3 = InventoryUtil.findItemInventorySlot(Items.GOLDEN_CHESTPLATE, false, XCarry.getInstance().isEnabled())) == -1 && (slot3 = InventoryUtil.findItemInventorySlot(Items.CHAINMAIL_CHESTPLATE, false, XCarry.getInstance().isEnabled())) == -1) {
                    slot3 = InventoryUtil.findItemInventorySlot(Items.LEATHER_CHESTPLATE, false, XCarry.getInstance().isEnabled());
                }
                if (slot3 != -1) {
                    this.taskList.add(new InventoryUtil.Task(slot3));
                    this.taskList.add(new InventoryUtil.Task(6));
                    this.taskList.add(new InventoryUtil.Task(slot3));
                    if (this.updateController.getValue()) {
                        this.taskList.add(new InventoryUtil.Task());
                    }
                }
                this.elytraTimer.reset();
            }
            if (mc.player.inventoryContainer.getSlot(7).getStack().getItem() == Items.AIR && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                this.getSlotOn(7, slot2);
            }
            if ((mc.player.inventoryContainer.getSlot(8).getStack()).getItem() == Items.AIR && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, this.curse.getValue(), XCarry.getInstance().isEnabled())) != -1) {
                this.getSlotOn(8, slot);
            }
        }
        if (this.timer.passedMs(this.delay.getValue())) {
            if (!this.taskList.isEmpty()) {
                for (int i = 0; i < this.actions.getValue(); ++i) {
                    InventoryUtil.Task task = this.taskList.poll();
                    if (task == null) continue;
                    task.run();
                }
            }
            this.timer.reset();
        }
        mending = false;
    }

    @Override
    public String getModuleInfo() {
        return   "Mending"  ;
    }

    public boolean isSafe() {
        EntityPlayer closest = EntityUtil.getClosestEnemy(closestEnemy.getValue());
        if (closest == null) {
            return true;
        }
        return mc.player.getDistanceSq(closest) >= MathUtil.square(closestEnemy.getValue());
    }

    private void takeOffSlot(int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            for (int i : InventoryUtil.findEmptySlots(XCarry.getInstance().isEnabled())) {
                if (this.doneSlots.contains(target)) continue;
                target = i;
                this.doneSlots.add(i);
            }
            if (target != -1) {
                if (target < 5 && target > 0 || !this.shiftClick.getValue()) {
                    this.taskList.add(new InventoryUtil.Task(slot));
                    this.taskList.add(new InventoryUtil.Task(target));
                } else {
                    this.taskList.add(new InventoryUtil.Task(slot, true));
                }
                if (this.updateController.getValue()) {
                    this.taskList.add(new InventoryUtil.Task());
                }
            }
        }
    }

    private void getSlotOn(int slot, int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove((Object) target);
            if (target < 5 && target > 0 || !this.shiftClick.getValue()) {
                this.taskList.add(new InventoryUtil.Task(target));
                this.taskList.add(new InventoryUtil.Task(slot));
            } else {
                this.taskList.add(new InventoryUtil.Task(target, true));
            }
            if (this.updateController.getValue()) {
                this.taskList.add(new InventoryUtil.Task());
            }
        }
    }

}

