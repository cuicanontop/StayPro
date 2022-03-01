/*
 * Decompiled with CFR 0.151.
 */
package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;

@Parallel
@ModuleInfo(name = "PacketEat", category = Category.PLAYER, description = "PacketEat")
public class PacketEat
extends Module {
    private static PacketEat INSTANCE = new PacketEat();

    public PacketEat() {
        this.setInstance();
    }

    public static PacketEat getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new PacketEat();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

