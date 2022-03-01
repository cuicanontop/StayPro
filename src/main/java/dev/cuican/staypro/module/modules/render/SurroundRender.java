/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.RenderUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Iterator;


@ModuleInfo(name = "CityESP", category = Category.RENDER, description = "CityESP")
public class SurroundRender
extends Module {
    public EntityPlayer target;
    private final Setting<Integer> range = setting("Range", 5, 1, 10);

    @Listener
    public void onRenderWorld(RenderEvent event) {
        if (SurroundRender.fullNullCheck()) {
            return;
        }
        this.target = this.getTarget(this.range.getValue());
        this.surroundRender();
    }

    private void surroundRender() {
        if (this.target == null) {
            return;
        }
        Vec3d a = this.target.getPositionVector();
        if (SurroundRender.mc.world.getBlockState(new BlockPos(a)).getBlock() == Blocks.OBSIDIAN || SurroundRender.mc.world.getBlockState(new BlockPos(a)).getBlock() == Blocks.ENDER_CHEST) {
            RenderUtils3D.drawBoxESP(new BlockPos(a), new Color(255, 255, 0), false, new Color(255, 255, 0), 1.0f, false, true, 42, true);
        }
        if (EntityUtil.getSurroundWeakness(a, -1, 1)) {
            this.surroundRender(a, -1.0, 0.0, 0.0, true);
        }
        if (EntityUtil.getSurroundWeakness(a, -1, 2)) {
            this.surroundRender(a, 1.0, 0.0, 0.0, true);
        }
        if (EntityUtil.getSurroundWeakness(a, -1, 3)) {
            this.surroundRender(a, 0.0, 0.0, -1.0, true);
        }
        if (EntityUtil.getSurroundWeakness(a, -1, 4)) {
            this.surroundRender(a, 0.0, 0.0, 1.0, true);
        }
        if (EntityUtil.getSurroundWeakness(a, -1, 5)) {
            this.surroundRender(a, -1.0, 0.0, 0.0, false);
        }
        if (EntityUtil.getSurroundWeakness(a, -1, 6)) {
            this.surroundRender(a, 1.0, 0.0, 0.0, false);
        }
        if (EntityUtil.getSurroundWeakness(a, -1, 7)) {
            this.surroundRender(a, 0.0, 0.0, -1.0, false);
        }
        if (!EntityUtil.getSurroundWeakness(a, -1, 8)) {
            return;
        }
        this.surroundRender(a, 0.0, 0.0, 1.0, false);
    }

    private void surroundRender(Vec3d pos, double x, double y, double z, boolean red) {
        BlockPos position = new BlockPos(pos).add(x, y, z);
        if (SurroundRender.mc.world.getBlockState(position).getBlock() == Blocks.AIR) {
            return;
        }
        if (SurroundRender.mc.world.getBlockState(position).getBlock() == Blocks.FIRE) {
            return;
        }
        if (red) {
            RenderUtils3D.drawBoxESP(position, new Color(255, 0, 0), false, new Color(255, 0, 0), 1.0f, false, true, 42, true);
            return;
        }
        RenderUtils3D.drawBoxESP(position, new Color(0, 0, 255), false, new Color(0, 0, 255), 1.0f, false, true, 42, true);
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = range;
        Iterator iterator = SurroundRender.mc.world.playerEntities.iterator();
        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer)iterator.next();
            if (EntityUtil.isntValid((Entity)player, range) || !EntityUtil.isInHole((Entity)player)) {
                continue;
            }
            if (target == null) {
                target = player;
                distance = SurroundRender.mc.player.getDistanceSq((Entity)player);
                continue;
            }
            if (!(SurroundRender.mc.player.getDistanceSq((Entity)player) < distance)) {
                continue;
            }
            target = player;
            distance = SurroundRender.mc.player.getDistanceSq((Entity)player);
        }
        return target;
    }
}

