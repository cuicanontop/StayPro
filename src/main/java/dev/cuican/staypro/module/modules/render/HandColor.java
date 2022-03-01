package dev.cuican.staypro.module.modules.render;



import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;

import java.awt.*;

@ModuleInfo(name = "HandColor", description = "HandColor", category = Category.RENDER)
public class HandColor extends Module {

    public static HandColor INSTANCE;
    public Setting<Boolean> colorSync = setting("ColorSync",false);
    public Setting<Boolean> rainbow = setting("Rainbow",true);
    public Setting<Integer> saturation = setting("Saturation",50,0,100);
    public Setting<Integer> brightness = setting("Brightness",50,0,100);
    public Setting<Integer> speed = setting("Speed",50,0,100);
    public Setting<Integer> red = setting("Red",255,1,255);
    public Setting<Integer> green = setting("Green",192,1,255);
    public Setting<Integer> blue = setting("Blue",203,1,255);
    public Setting<Integer> alpha = setting("Alpha",90,1,255);
    public float hue;
    public HandColor(){
        HandColor.INSTANCE = this;
    }

    public void setInstance() {
        HandColor.INSTANCE = this;
    }

    public static HandColor getInstance() {
        if (HandColor.INSTANCE == null) {
            HandColor.INSTANCE = new HandColor();
        }
        return HandColor.INSTANCE;
    }

    public static HandColor getINSTANCE() {
        if (HandColor.INSTANCE == null) {
            HandColor.INSTANCE = new HandColor();
        }
        return HandColor.INSTANCE;
    }

    static {
        HandColor.INSTANCE = new HandColor();
    }

    public Color getCurrentColor() {
        final int colorSpeed = 101 - this.speed.getValue();
        this.hue = System.currentTimeMillis() % (360 * colorSpeed) / (360.0f * colorSpeed);
        if (this.rainbow.getValue()) {
            return Color.getHSBColor(this.hue, this.saturation.getValue() / 255.0f, this.brightness.getValue() / 255.0f);
        }
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }
}
