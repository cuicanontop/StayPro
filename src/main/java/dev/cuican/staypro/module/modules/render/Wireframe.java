package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;

@ModuleInfo(name = "WireFrame", category = Category.RENDER)
public class Wireframe extends Module {
    private static Wireframe INSTANCE;

    static {
        Wireframe.INSTANCE = new Wireframe();
    }

    private final Setting<String> p = setting("Page", "ONE",listOf("ONE", "TWO", "THREE"));
    public final Setting<Float> alpha = setting("Alpha", 87, 0.1f, 255).whenAtMode(p, "TWO");
    public final Setting<Float> cAlpha = setting("CAlpha", 87, 0.1f, 255).whenAtMode(p, "TWO");
    public final Setting<Float> lineWidth = setting("Width", 1, 0.1f, 5).whenAtMode(p, "TWO");
    public final Setting<Float> crystalLineWidth = setting("CWidth", 1, 0.1f, 5).whenAtMode(p, "TWO");
    public Setting<String> mode = setting("Mode","WIREFRAME",listOf("SOLID", "WIREFRAME","GalaxyShader")).whenAtMode(p, "ONE");
    public Setting<String> cMode = setting("CMode", "SOLID",listOf("SOLID", "WIREFRAME","GalaxyShader")).whenAtMode(p, "ONE");
    public Setting<Boolean> players = setting("Players", true).whenAtMode(p, "ONE");
    public Setting<Boolean> playerModel = setting("PlayerModels", true).whenAtMode(p, "ONE");
    public Setting<Boolean> crystals = setting("Crystals", true).whenAtMode(p, "ONE");
    public Setting<Boolean> crystalModel = setting("CrystalModels", true).whenAtMode(p, "ONE");
    public Setting<Boolean> rainbow = setting("Rainbow", true).whenAtMode(p, "THREE");
    public Setting<Integer> rainbowHue = setting("RainbowDelay", 240, 0, 600).whenAtMode(p, "THREE");
    public Setting<Float> rainbowSaturation = setting("Saturation", 150F, 1, 255).whenAtMode(p, "THREE");
    public Setting<Float> rainbowBrightness = setting("Brightness", 150F, 1, 255).whenAtMode(p, "THREE");
    public Setting<Integer> red = setting("Red", 255, 1, 255).whenAtMode(p, "THREE");
    public Setting<Integer> green = setting("Green", 192, 1, 255).whenAtMode(p, "THREE");
    public Setting<Integer> blue = setting("Blue", 203, 1, 255).whenAtMode(p, "THREE");

    public Wireframe() {
        this.setInstance();
    }

    public static Wireframe getInstance() {
        if (Wireframe.INSTANCE == null) {
            Wireframe.INSTANCE = new Wireframe();
        }
        return Wireframe.INSTANCE;
    }

    public static Wireframe getINSTANCE() {
        if (Wireframe.INSTANCE == null) {
            Wireframe.INSTANCE = new Wireframe();
        }
        return Wireframe.INSTANCE;
    }


    private void setInstance() {
        Wireframe.INSTANCE = this;
    }


}
