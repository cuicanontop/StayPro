package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.TransformSideFirstPersonEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @Author GL_DONT_CARE (Viewmodel Transformations)
 * @Author NekoPvP (Item FOV)
 */
@ModuleInfo(name = "ViewModel", category = Category.RENDER,description = "ViewModel")
public class ViewModel extends Module {
    private final Setting<String> type = setting("Type", "BOTH",listOf(
            "Value",
            "FOV",
            "BOTH"));
    private final Setting<Double> xRight = setting("RightX", 0.2, -2, 2);
    private final Setting<Double> yRight = setting("RightY", 0.2, -2, 2);
    private final Setting<Double> zRight = setting("RightZ", 0.2, -2, 2);
    private final Setting<Double> xLeft = setting("LeftX", 0.2, -2, 2);
    private final Setting<Double> yLeft = setting("LeftY", 0.2, -2, 2);
    private final Setting<Double> zLeft = setting("LeftZ", 0.2, -2, 2);
    private final Setting<Integer> fov = setting("ItemFov", 150, 70, 200);
    public Setting<Boolean> cancelEating = setting("CancelEating", false);

    @Listener
    public void transform(TransformSideFirstPersonEvent event) {
        if (type.getValue().equals("Value") || type.getValue().equals("BOTH")) {
            if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
                GlStateManager.translate(xRight.getValue(), yRight.getValue(), zRight.getValue());
            } else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
                GlStateManager.translate(xLeft.getValue(), yLeft.getValue(), zLeft.getValue());
            }
        }
    }

    @Listener
    public void onFov(EntityViewRenderEvent.FOVModifier event) {
        if (type.getValue().equals("FOV") || type.getValue().equals("BOTH")) {
            event.setFOV(fov.getValue());
        }
    }



}