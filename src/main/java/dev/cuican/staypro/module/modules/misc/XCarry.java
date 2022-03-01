// 
// Decompiled by Procyon v0.5.36
// 

package dev.cuican.staypro.module.modules.misc;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.mixin.accessor.AccessorClient;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.network.play.client.CPacketCloseWindow;

@ModuleInfo(name = "XCarry", category = Category.MISC,description = "Xcarry in by cuican on tup")
public class XCarry extends Module {
    private static XCarry INSTANCE = new XCarry();
    private final Setting<Boolean> ForceCancel = setting("ForceCancel", false);

    public XCarry() {
        this.setInstance();
    }

    public static XCarry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new XCarry();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        if (mc.world != null) {
            mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
        }

    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = (CPacketCloseWindow) event.getPacket();
            if (((AccessorClient)packet).aqGetWindowId() == mc.player.inventoryContainer.windowId || ForceCancel.getValue()) {
                event.isCancelled();
            }
        }
    }
}
