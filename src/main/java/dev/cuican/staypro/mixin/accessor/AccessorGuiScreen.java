package dev.cuican.staypro.mixin.accessor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

@Mixin(value={GuiScreen.class})
public interface AccessorGuiScreen {
    @Accessor(value="buttonList")
    void setButtonList(List<GuiButton> var1);

    @Accessor(value="buttonList")
    List<GuiButton> getButtonList();
}
