package dev.cuican.staypro.event.events.render;

import com.google.common.collect.Lists;
import dev.cuican.staypro.event.StayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

public class GuiNewChatEvent extends StayEvent {
    public Minecraft mc;
    public List<ChatLine> drawnChatLines = Lists.newArrayList();
    public int scrollPos;

    public boolean isScrolled;

    public  float ChatScale;

    public  boolean ChatOpen;

    public  int LineCount;

    public  int ChatWidth;

    public int updateCounter;

    public GuiNewChatEvent(int updateCounter,int chatWidth,int lineCount,boolean chatOpen,float chatScale,boolean isScrolled,int scrollPos , List<ChatLine> drawnChatLines) {
        super();
        this.updateCounter = updateCounter;
        this.mc = Minecraft.getMinecraft();
        this.ChatWidth = chatWidth;
        this.LineCount = lineCount;
        this.ChatOpen = chatOpen;
        this.ChatScale = chatScale;
        this.isScrolled = isScrolled;
        this.scrollPos = scrollPos;
        this.drawnChatLines = drawnChatLines;
    }
}
