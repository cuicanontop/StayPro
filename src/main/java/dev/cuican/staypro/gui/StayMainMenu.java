package dev.cuican.staypro.gui;


import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.gui.alt.GuiAlt;
import dev.cuican.staypro.gui.bcomponent.ButtonComponent;
import dev.cuican.staypro.gui.bcomponent.NormalButton;
import dev.cuican.staypro.utils.ColorUtil;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class StayMainMenu
extends GuiScreen {
    private DynamicTexture texturebg;
    private DynamicTexture texturelogo;
    private final Timer timer = new Timer();
    List<ButtonComponent> buttonlist = new ArrayList<>();
    private DynamicTexture[] BG = new DynamicTexture[0];
    private int indexBG = 0;
    private final Timer changeBGTimer = new Timer();

    public void forwardLoopBG() {
        this.indexBG = this.indexBG < this.BG.length - 1 ? ++this.indexBG : 0;
    }

    public void loadBG() {
        try {
            File BGPath = new File("Stay/background/");
            if (!BGPath.exists()) {
                BGPath.mkdirs();
            }
            ArrayList<DynamicTexture> findBG = new ArrayList<>();
            for (File picture : Objects.requireNonNull(BGPath.listFiles())) {
                if (!picture.getName().endsWith(".png") && !picture.getName().endsWith(".jpg")) continue;
                findBG.add(new DynamicTexture(ImageIO.read(picture)));
            }
            this.BG = findBG.toArray(this.BG);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public StayMainMenu() {
        this.loadBG();
        try {
            Random random = new Random();
            String logo = "/assets/Stay/logo/logo"+random.nextInt(2)+".png";
            this.texturebg = new DynamicTexture(ImageIO.read(Stay.class.getResourceAsStream("/assets/Stay/Gui/gui3.jpg")));
            this.texturelogo = new DynamicTexture(ImageIO.read(Stay.class.getResourceAsStream(logo)));
        }
        catch (IOException ignored) { }
        this.init();
        this.timer.reset();
        this.changeBGTimer.reset();
    }

    private void init() {

        this.buttonlist.add(new NormalButton("Single", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiWorldSelection(this))));

        this.buttonlist.add(new NormalButton("Multi", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiMultiplayer(this))));

        this.buttonlist.add(new NormalButton("Alt", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiAlt(this))));

        this.buttonlist.add(new NormalButton("Settings", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings))));

        this.buttonlist.add(new NormalButton("Exit", 20.0f, 0, 70.0f, 13.0f).setOnClickListener(() -> this.mc.shutdown()));
    }

    @Override
    public void initGui() {

        AtomicReference<Float> startY = new AtomicReference<>((height / 6F) * 2);
        buttonlist.forEach(buttonComponent -> {
            buttonComponent.y = startY.get();
            startY.updateAndGet(v -> v + 20f);
        });
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.BG.length != 0 && this.changeBGTimer.passed(6000)) {
            this.forwardLoopBG();
            this.changeBGTimer.reset();
        }
        RenderUtils.drawRect(0.0, 0.0, this.width, this.height, Color.BLACK);
        int alpha = !this.timer.passed(950) ? ColorUtil.calculateAlphaChangeColor(0, 255, 1000, (int)this.timer.getPassedTimeMs()) : 255;
        double scale = !this.timer.passed(4000) ? MathUtil.calculateDoubleChange(3.0, 1.1, 4000, (int)this.timer.getPassedTimeMs()) : 1.1;
        double move = !this.timer.passed(3000) ? MathUtil.calculateDoubleChange(200.0, 0.0, 3000, (int)this.timer.getPassedTimeMs()) : 0.0;
        GL11.glPushMatrix();
        GL11.glTranslated(move, move, 0.0);
        GL11.glTranslated((double)this.width / 2.0, (double)this.height / 2.0, 0.0);
        GL11.glScaled(scale, scale, 0.0);
        float xOffset = -1.0f * (((float)mouseX - (float)this.width / 2.0f) / ((float)this.width / 16.0f));
        float yOffset = -1.0f * (((float)mouseY - (float)this.height / 2.0f) / ((float)this.height / 9.0f));
        float width = this.width + 94;
        float height = this.height + 66;
        float x = -47.0f + xOffset - width / 2.0f;
        float y = -33.0f + yOffset - height / 2.0f;
        if (this.BG.length != 0) {
            RenderUtils.bindTexture(this.BG[this.indexBG].getGlTextureId());
            RenderUtils.drawTexture(x, y, width, height, alpha);
        } else if (this.texturebg != null) {
            RenderUtils.bindTexture(this.texturebg.getGlTextureId());
            RenderUtils.drawTexture(x, y, width, height, alpha);
        }
        GL11.glPopMatrix();
        RenderUtils.bindTexture(this.texturelogo.getGlTextureId());
        RenderUtils.drawTexture(20.0, 10.0, 30.0, 30.0, alpha);
        FontManager.fontRenderer.drawCenteredString(Stay.MOD_NAME, 35.0f, 45.0f, -1);
        for (ButtonComponent button : this.buttonlist) {
            button.mouseMove(mouseX, mouseY);
        }
        for (ButtonComponent button : this.buttonlist) {
            button.render();
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int buttonID) throws IOException {
        for (ButtonComponent button : this.buttonlist) {
            button.mouseclick(mouseX, mouseY, buttonID);
        }
    }
}

