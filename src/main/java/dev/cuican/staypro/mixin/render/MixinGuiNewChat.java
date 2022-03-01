package dev.cuican.staypro.mixin.render;

import com.google.common.collect.Lists;
import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.event.events.render.GuiNewChatEvent;
import dev.cuican.staypro.module.modules.client.ClickGUI;
import dev.cuican.staypro.utils.Wrapper;
import dev.cuican.staypro.utils.graphics.AnimationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat extends Gui {


    @Shadow
    public Minecraft mc;
    @Shadow
    private final List<ChatLine> drawnChatLines = Lists.<ChatLine>newArrayList();
    @Shadow
    private int scrollPos;
    @Shadow
    private boolean isScrolled;
    @Shadow
    public abstract float getChatScale();
    @Shadow
    public abstract boolean getChatOpen();
    @Shadow
    public abstract int getLineCount();
    @Shadow
    public abstract int getChatWidth();
    public AnimationUtil animationUtils = new AnimationUtil();

    /**
     * @author
     */

    @Inject(method = {"drawChat"}, at = @At(value = "HEAD"))
    public void drawChat(int updateCounter, CallbackInfo info) {
        ClickGUI clickGUI = (ClickGUI) ModuleManager.getModuleByName("ClickGUI");
        assert clickGUI != null;
        if (clickGUI.GuiMainMenu.getValue()) {
            if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
                int i = this.getLineCount();
                int j = this.drawnChatLines.size();
                float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

                if (j > 0) {
                    boolean flag = false;

                    if (this.getChatOpen()) {
                        flag = true;
                    }

                    float f1 = this.getChatScale();
                    int k = MathHelper.ceil((float) this.getChatWidth() / f1);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(2.0F, 8.0F, 0.0F);
                    GlStateManager.scale(f1, f1, 1.0F);
                    int l = 0;

                    for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                        ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);

                        if (chatline != null) {
                            int j1 = updateCounter - chatline.getUpdatedCounter();

                            if (j1 < 200 || flag) {
                                double d0 = (double) j1 / 200.0D;
                                d0 = 1.0D - d0;
                                d0 = d0 * 10.0D;
                                d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
                                d0 = d0 * d0;
                                int l1 = (int) (255.0D * d0);

                                if (flag) {
                                    l1 = 255;
                                }

                                l1 = (int) ((float) l1 * f);
                                ++l;

                                if (l1 > 3) {
                                    int i2 = 0;
                                    int j2 = -i1 * 9;
                                    drawRect(-2, j2 - 9, 0 + k + 4, j2, l1 / 2 << 24);
                                    String s = chatline.getChatComponent().getFormattedText();
                                    GlStateManager.enableBlend();
                                    float width = mc.fontRenderer.getStringWidth(s);
                                    mc.fontRenderer.drawStringWithShadow(s, 0, (float) (j2 - 8), 16777215 + (l1 << 24));
                                    GlStateManager.disableAlpha();
                                    GlStateManager.disableBlend();
                                }
                            }
                        }
                    }

                    if (flag) {
                        int k2 = this.mc.fontRenderer.FONT_HEIGHT;
                        GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                        int l2 = j * k2 + j;
                        int i3 = l * k2 + l;
                        int j3 = this.scrollPos * i3 / j;
                        int k1 = i3 * i3 / l2;

                        if (l2 != i3) {
                            int k3 = j3 > 0 ? 170 : 96;
                            int l3 = this.isScrolled ? 13382451 : 3355562;
                            drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                            drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
                        }
                    }

                    GlStateManager.popMatrix();
                }
            }
            GuiNewChatEvent event = new GuiNewChatEvent(updateCounter, this.getChatWidth(), this.getLineCount(), this.getChatOpen(), this.getChatScale(), this.isScrolled, this.scrollPos, this.drawnChatLines);
            Stay.EVENT_BUS.post(event);
        }
    }

    public float y;

}
