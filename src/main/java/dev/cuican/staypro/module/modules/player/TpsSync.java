package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
@Parallel
@ModuleInfo(name = "TpsSync", category = Category.PLAYER, description = "Syncs your client with the TPS.")
public class TpsSync extends Module {
    private static TpsSync INSTANCE = new TpsSync();
    public Setting<Boolean> attack = setting("Attack", Boolean.FALSE);
    public Setting<Boolean> mining =setting("Mine", Boolean.TRUE);

    public TpsSync() {
        setInstance();
    }

    public static TpsSync getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TpsSync();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

