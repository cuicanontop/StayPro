package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;

@ModuleInfo(name = "Timer", category = Category.PLAYER, description = "Changes your client tick speed")
public class Timer extends Module {
    private final Setting<Boolean> slow = setting("SlowMode",false);
    private final Setting<Float> tickSlow = setting("TickSlow",8f,1,10).whenTrue( slow);
    private final Setting<Float> tickNormal = setting("TickNormal",1.2f,1,10).whenFalse( slow);

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
    }

    @Override
    public void onTick() {
        if (!slow.getValue()) {
            mc.timer.tickLength = 50.0f / tickNormal.getValue();
        } else {
            mc.timer.tickLength = 50.0f / (tickSlow.getValue() / 10.0f);
        }
    }
}
