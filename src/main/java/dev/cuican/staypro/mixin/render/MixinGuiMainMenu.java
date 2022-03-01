package dev.cuican.staypro.mixin.render;


import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.gui.StayMainMenu;
import dev.cuican.staypro.module.modules.client.ClickGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiMainMenu.class}, priority = 3500)
public abstract class MixinGuiMainMenu extends GuiScreen {
    @Shadow
    private String splashText;

    private static boolean first = true;

    @Inject(method = {"initGui"}, at = @At(value = "HEAD"))
    public void init(CallbackInfo info) {
        ClickGUI clickGUI = (ClickGUI)ModuleManager.getModuleByName("ClickGUI");
        assert clickGUI != null;
        if(clickGUI.GuiMainMenu.getValue()){
            Minecraft.getMinecraft().displayGuiScreen(new StayMainMenu());
        }

    }
//
//    @Inject(method = {"initGui"}, at = @At(value = "RETURN"))
//    public void initGui(CallbackInfo ci) {
//        if (first) {
//            Minecraft.getMinecraft().displayGuiScreen(new StayGuiStart());
//            first = false;
//        }
//    }



    @Inject(method = "<init>", at = @At("RETURN"))
    public void postConstructor(final CallbackInfo ci) {
        this.splashText = "cuican is so fucking handsome";
    }






}

