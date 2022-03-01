package dev.cuican.staypro.mixin.ViaForge;

import java.io.File;
import java.util.List;

import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.gui.ViaForge.gui.GuiProtocolSlider;
import dev.cuican.staypro.mixin.accessor.AccessorGuiScreen;
import dev.cuican.staypro.module.pingbypass.PingBypass;
import dev.cuican.staypro.module.pingbypass.guis.GuiAddPingBypass;
import dev.cuican.staypro.module.pingbypass.guis.GuiButtonPingBypassOptions;
import dev.cuican.staypro.module.pingbypass.guis.GuiConnectingPingBypass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiMultiplayer.class})
public abstract class MixinGuiMultiplayer
extends GuiScreen {
    @Inject(method={"createButtons"}, at=@At(value="HEAD"), cancellable=true)
    public void Method279(CallbackInfo ci) {
        AccessorGuiScreen screen = (AccessorGuiScreen) this;
        List<GuiButton> buttonList = screen.getButtonList();
        if (!new File(Minecraft.getMinecraft().gameDir, "novia").exists()) {
            buttonList.add(new GuiProtocolSlider(1200, this.width / 2 + 4 + 76 + 76, this.height - 28, 105, 20));
            screen.setButtonList(buttonList);
        }
    }

    //ping

    private GuiButton pingBypassButton;

    @Inject(method = "createButtons", at = @At("HEAD"))
    public void createButtonsHook(CallbackInfo info)
    {
        this.buttonList.add(new GuiButtonPingBypassOptions(1339, width - 24, 5));
        pingBypassButton = addButton(new GuiButton(1337, width - 126, 5, 100, 20, getDisplayString()));
    }

    PingBypass PingBypasss = (PingBypass) ModuleManager.getModuleByName("PingBypass");
    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    protected void actionPerformed(GuiButton button, CallbackInfo info)
    {
        if (button.enabled)
        {
            if (button.id == 1337)
            {
                PingBypasss.toggle();
                pingBypassButton.displayString = getDisplayString();
                info.cancel();
            }
            else if (button.id == 1339)
            {
                mc.displayGuiScreen(new GuiAddPingBypass(this));
                info.cancel();
            }
        }
    }

    @Inject(method = "confirmClicked", at = @At("HEAD"), cancellable = true)
    public void confirmClickedHook(boolean result, int id, CallbackInfo info)
    {
        if (id == 1337)
        {
            mc.displayGuiScreen(this);
        }
    }

    @Inject(method = "connectToServer", at = @At("HEAD"), cancellable = true)
    public void connectToServerHook(ServerData data, CallbackInfo info)
    {
        if (PingBypasss.isEnabled())
        {
            mc.displayGuiScreen(new GuiConnectingPingBypass(this, mc, data));
            info.cancel();
        }
    }
    private static final String SECTIONSIGN  = "\u00A7";
    private String getDisplayString()
    {
        return "PingBypass: " + (PingBypasss.isEnabled() ? SECTIONSIGN + "aOn" : SECTIONSIGN + "cOff");
    }

}