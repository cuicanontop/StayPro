package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.network.EntityJoinWorldEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.EventRenderTooltip;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;


import org.lwjgl.input.Mouse;

import java.util.*;

@ModuleInfo(name = "ShulkerPreview", category = Category.RENDER, description = "Previews shulkers in the game GUI")
public class ShulkerPreview extends Module {

    public static ShulkerPreview INSTACE;

    public ShulkerPreview() {
        INSTACE = this;
    }

}