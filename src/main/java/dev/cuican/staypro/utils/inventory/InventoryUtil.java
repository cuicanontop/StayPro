package dev.cuican.staypro.utils.inventory;


import dev.cuican.staypro.mixin.client.IPlayerControllerMP;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class InventoryUtil {
    public static EnumHand getHand(int slot)
    {
        return slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public static int findSkullSlot( ) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;



        for (int i = 0; i < 9; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemSkull) {
                return i;
            }
        }
        return slot;
    }
    public static Minecraft mc = Minecraft.getMinecraft();
    public static int findSkullSlot2( ) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;



        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemSkull) {
                return i;
            }
        }
        return slot;
    }
    public static int findMaterials(Block block) {
        for(int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).getBlock() == block) {
                return i;
            }
        }

        return -1;
    }
    public static int findItem(Item item) {

            for(int i = 0; i < 9; ++i) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                    return i;
                }
            }

            return -1;

    }
    public static int pickItem(int item, boolean allowInventory) {

        ArrayList<ItemStack> filter = new ArrayList<ItemStack>();
        for (int i1 = 0; i1 < (allowInventory?mc.player.inventory.mainInventory.size():9); i1++) {
            if (Item.getIdFromItem(mc.player.inventory.mainInventory.get(i1).getItem()) == item) {
                filter.add(mc.player.inventory.mainInventory.get(i1));
            }
        }

        //filter.sort((a, b) -> b.func_190916_E() - a.func_190916_E());
        if (!(filter.size() < 1))
            return mc.player.inventory.mainInventory.indexOf(filter.get(0));
        return -1;
    }
    private static int currentItem;
    public static void push() {
        currentItem = mc.player.inventory.currentItem;
    }
    public static void setSlot(int slot) {
        if (slot > 8 || slot < 0) return;
        mc.player.inventory.currentItem = slot;
    }

    public static void pop() {
        mc.player.inventory.currentItem = currentItem;
    }

    public static List<Integer> findAllItemSlots(Class<? extends Item> itemToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }

            slots.add(i);
        }
        return slots;
    }

    public static List<Integer> findAllBlockSlots(Class<? extends Block> blockToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slots.add(i);
            }
        }
        return slots;
    }
    public static int getItemHotbars(Item input) {
        for (int i = 0; i < 36; ++i) {
            Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) != Item.getIdFromItem(input)) {
                continue;
            }
            return i;
        }
        return -1;
    }
    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }

            if (itemToFind.isInstance(stack.getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = mc.player.getHeldItemMainhand();
        result = isInstanceOf(stack, clazz);
        if (!result) {
            ItemStack offhand = mc.player.getHeldItemOffhand();
            result = isInstanceOf(stack, clazz);
        }
        return result;
    }

    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (clazz.isInstance(item)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            return clazz.isInstance(block);
        }
        return false;
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
        int slot = -1;
        float damage = 0.0f;
        for (int i = 9; i < 45; ++i) {
            boolean cursed;
            ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (s.getItem() == Items.AIR || !(s.getItem() instanceof ItemArmor)) continue;
            ItemArmor armor = (ItemArmor) s.getItem();
            if (armor.armorType != type) continue;
            float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s);
            boolean bl = cursed = binding && EnchantmentHelper.hasBindingCurse(s);
            if (!(currentDamage > damage) || cursed) continue;
            damage = currentDamage;
            slot = i;
        }
        return slot;
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
        int slot = findArmorSlot(type, binding);
        if (slot == -1 && withXCarry) {
            float damage = 0.0f;
            for (int i = 1; i < 5; ++i) {
                boolean cursed;
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR || !(craftingStack.getItem() instanceof ItemArmor)) continue;
                ItemArmor armor = (ItemArmor) craftingStack.getItem();
                if (armor.armorType != type) continue;
                float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, craftingStack);
                boolean bl = cursed = binding && EnchantmentHelper.hasBindingCurse(craftingStack);
                if (!(currentDamage > damage) || cursed) continue;
                damage = currentDamage;
                slot = i;
            }
        }
        return slot;
    }

    public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
        int slot = findItemInventorySlot(item, offHand);
        if (slot == -1 && withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Item craftingStackItem;
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR || (craftingStackItem = craftingStack.getItem()) != item)
                    continue;
                slot = i;
            }
        }
        return slot;
    }

    public static int findItemInventorySlot(Item item, boolean offHand) {
        AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getValue().getItem() != item || entry.getKey() == 45 && !offHand) continue;
            slot.set(entry.getKey());
            return slot.get();
        }
        return slot.get();
    }

    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        if (mc.currentScreen instanceof GuiCrafting) {
            return fuckYou3arthqu4kev2(10, 45);
        }
        return getInventorySlots(9, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }

    private static Map<Integer, ItemStack> fuckYou3arthqu4kev2(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, mc.player.openContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }

    public static List<Integer> findEmptySlots(boolean withXCarry) {
        ArrayList<Integer> outPut = new ArrayList<Integer>();
        for (Map.Entry<Integer, ItemStack> entry : getInventoryAndHotbarSlots().entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getValue().getItem() != Items.AIR) continue;
            outPut.add(entry.getKey());
        }
        if (withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (!craftingStack.isEmpty() && craftingStack.getItem() != Items.AIR) continue;
                outPut.add(i);
            }
        }
        return outPut;
    }


    public static boolean isBlock(final Item item, final Class clazz) {
        if (item instanceof ItemBlock) {
            final Block block = ((ItemBlock) item).getBlock();
            return clazz.isInstance(block);
        }
        return false;
    }

    public static List<Integer> getItemInventory(final Item item) {
        final List<Integer> ints = new ArrayList<>();
        for (int i = 9; i < 36; ++i) {
            final Item target = mc.player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(item)) {
                ints.add(i);
            }
        }
        if (ints.size() == 0) {
            ints.add(-1);
        }
        return ints;
    }

    public static void switchToHotbarSlot(final int slot, final boolean silent) {
        if (mc.player == null || mc.world == null || mc.player.inventory == null) {
            return;
        }
        try {
            if (mc.player.inventory.currentItem == slot || slot < 0) {
                return;
            }
        } catch (Exception ignored) {
        }
        if (silent) {
            try {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.playerController.updateController();
            } catch (Exception ignored) {
            }
        } else {
            try {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
                mc.playerController.updateController();
            } catch (Exception ignored) {
            }
        }
    }

    public static void switchToHotbarSlot(final Class<?> clazz, final boolean silent) {
        final int slot = findHotbarBlock(clazz);
        if (slot > -1) {
            switchToHotbarSlot(slot, silent);
        }
    }

    public static int findHotbarItem(final Class<?> clazz) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }
            }
        }
        return -1;
    }


    public static void switchToSlot(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }
    public static int getItemHotbar(final Item input) {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory != null) {
                Item item = mc.player.inventory.getStackInSlot(i).getItem();
                if (Item.getIdFromItem(item) == Item.getIdFromItem(input)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getItemInv(final Item input) {
        for (int i = 0; i < 45; ++i) {
            if (mc.player.inventory != null) {
                Item item = mc.player.inventory.getStackInSlot(i).getItem();
                if (Item.getIdFromItem(item) == Item.getIdFromItem(input)) {
                    return i;
                }
            }
        }
        return -1;
    }
    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) {
                continue;
            }
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock) stack.getItem()).getBlock())) {
                continue;
            }
            return i;
        }
        return -1;
    }
    public static int findHotbarBlocks(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) {
                continue;
            }
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock) stack.getItem()).getBlock())) {
                continue;
            }
            return i;
        }
        return -1;
    }
    public static int findHotbarBlock(Block block) {
        if (ItemUtil.areSame(mc.player.getHeldItemOffhand(), block)) {
            return -2;
        }

        int result = -1;
        for (int i = 9; i >= 0; i--) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (ItemUtil.areSame(stack, block)) {
                result = i;
                if (mc.player.inventory.currentItem == i) {
                    break;
                }
            }
        }

        return result;
    }


    public static int findHotbarItem(Item item) {
        if (ItemUtil.areSame(mc.player.getHeldItemOffhand(), item)) {
            return -2;
        }

        int result = -1;
        for (int i = 9; i > 0; i--) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (ItemUtil.areSame(stack, item)) {
                result = i;
                if (mc.player.inventory.currentItem == i) {
                    break;
                }
            }
        }

        return result;
    }

    public static int findItem(Item item, boolean xCarry) {
        if (mc.player.inventory.getItemStack().getItem() == item) {
            return -2;
        }

        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
            if (stack.getItem() == item) {
                return i;
            }
        }

        if (xCarry) {
            for (int i = 1; i < 5; i++) {
                ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
                if (stack.getItem() == item) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int getCount(Item item) {
        int result = 0;
        for (int i = 0; i < 46; i++) {
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
            if (stack.getItem() == item) {
                result += stack.getCount();
            }
        }

        if (mc.player.inventory.getItemStack().getItem() == item) {
            result += mc.player.inventory.getItemStack().getCount();
        }

        return result;
    }

    public static boolean isHoldingServer(Item item) {
        ItemStack offHand = mc.player.getHeldItemOffhand();
        if (ItemUtil.areSame(offHand, item)) {
            return true;
        }

        ItemStack mainHand = mc.player.getHeldItemMainhand();
        if (ItemUtil.areSame(mainHand, item)) {
            int current = mc.player.inventory.currentItem;
            int server = getServerItem();
            return server == current;
        }

        return false;
    }

    public static boolean isHolding(Item item) {
        return isHolding(mc.player, item);
    }

    public static boolean isHolding(Block block) {
        return isHolding(mc.player, block);
    }

    public static boolean isHolding(EntityLivingBase entity, Item item) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();

        return ItemUtil.areSame(mainHand, item) || ItemUtil.areSame(offHand, item);
    }

    public static boolean isHolding(EntityLivingBase entity, Block block) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();

        return ItemUtil.areSame(mainHand, block) || ItemUtil.areSame(offHand, block);
    }

    public static int getServerItem() {
        return ((IPlayerControllerMP) mc.playerController).getItem();
    }



    public static int findHotbarBlock(Block block, Block optional)
    {
     int o =   findHotbarBlock(block);
     if(o==-1)findHotbarBlock(optional);
     return o;

    }

    public static int findInHotbar(Predicate<ItemStack> condition)
    {
        return findInHotbar(condition, true);
    }

    public static int findInHotbar(Predicate<ItemStack> condition,
                                   boolean offhand)
    {
        if (offhand && condition.test(mc.player.getHeldItemOffhand()))
        {
            return -2;
        }

        int result = -1;
        for (int i = 8; i > -1; i--)
        {
            if (condition.test(mc.player.inventory.getStackInSlot(i)))
            {
                result = i;
                if (mc.player.inventory.currentItem == i)
                {
                    break;
                }
            }
        }

        return result;
    }
    public enum Switch {
        NORMAL,
        SILENT,
        NONE
    }

    public static class Task {
        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public Task() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public Task(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
            this.update = false;
        }

        public void run() {
            if (this.update) {
                mc.playerController.updateController();
            }
            if (this.slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, mc.player);
            }
        }

        public boolean isSwitching() {
            return !this.update;
        }
    }

}
