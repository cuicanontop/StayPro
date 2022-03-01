// 
// Decompiled by Procyon v0.5.36
// 

package dev.cuican.staypro.module.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.*;

@ModuleInfo(name = "PearlViewer", category = Category.RENDER)
public class PearlViewer extends Module {
    private final HashMap<UUID, List<Vec3d>> poses = new HashMap<>();
    private final HashMap<UUID, Double> time = new HashMap<>();
    private final Setting<Boolean> chat = setting("Chat", true);
    private final Setting<Boolean> render = setting("Render", true);
    private final Setting<Double> renderTime = setting("RenderTime", 5d, 0, 30);
    private final Setting<Integer> Thick = setting("Thick", 3, 0, 10);

    @Override
    public void onTick() {
        Iterator iter = (new HashMap(this.time)).entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<UUID, Double> e = (Map.Entry) iter.next();

            if (e.getValue() <= 0.0D) {
                this.poses.remove(e.getKey());
                this.time.remove(e.getKey());
            } else {
                this.time.replace(e.getKey(), e.getValue() - 0.05D);
            }
        }

        iter = mc.world.loadedEntityList.iterator();

        while (true) {
            Entity e;
            do {
                if (!iter.hasNext()) {
                    return;
                }

                e = (Entity) iter.next();
            } while (!(e instanceof EntityEnderPearl));

            if (!this.poses.containsKey(e.getUniqueID())) {
                if (chat.getValue()) {
                    for (net.minecraft.entity.player.EntityPlayer entityPlayer : mc.world.playerEntities) {
                        if (entityPlayer.getDistance(e) < 4.0F && !((Entity) entityPlayer).getName().equals(mc.player.getName())) {
                            ChatUtil.sendMessage(ChatFormatting.RED + entityPlayer.getName() + ChatFormatting.AQUA + " Threw a Pearl !");
                            break;
                        }
                    }
                }

                this.poses.put(e.getUniqueID(), new ArrayList<>(Collections.singletonList(e.getPositionVector())));
                this.time.put(e.getUniqueID(), this.renderTime.getValue());
            } else {
                this.time.replace(e.getUniqueID(), this.renderTime.getValue());
                List<Vec3d> v = this.poses.get(e.getUniqueID());
                v.add(e.getPositionVector());
            }
        }
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if(fullNullCheck()){
            return;
        }
        //RenderUtils.glSetup();
        GL11.glPushMatrix();
        if (this.render.getValue()) {
            GL11.glLineWidth((float) Thick.getValue());
            Iterator<Map.Entry<UUID, List<Vec3d>>> posIter = this.poses.entrySet().iterator();

            while (true) {
                Map.Entry<?,?> e;
                do {
                    if (!posIter.hasNext()) {
                        RenderUtils.glCleanup();
                        return;
                    }
                    e = posIter.next();
                } while (((List) e.getValue()).size() <= 2);
                GL11.glBegin(1);
                Random rand = new Random(e.getKey().hashCode());
                double r = 0.5D + rand.nextDouble() / 2.0D;
                double g = 0.5D + rand.nextDouble() / 2.0D;
                double b = 0.5D + rand.nextDouble() / 2.0D;
                GL11.glColor3d(r, g, b);
                double[] rPos = RenderUtils.rPos();
                for (int i = 1; i < ((List) e.getValue()).size(); ++i) {
                    GL11.glVertex3d(((Vec3d) ((List) e.getValue()).get(i)).x - rPos[0], ((Vec3d) ((List) e.getValue()).get(i)).y - rPos[1], ((Vec3d) ((List) e.getValue()).get(i)).z - rPos[2]);
                    GL11.glVertex3d(((Vec3d) ((List) e.getValue()).get(i - 1)).x - rPos[0], ((Vec3d) ((List) e.getValue()).get(i - 1)).y - rPos[1], ((Vec3d) ((List) e.getValue()).get(i - 1)).z - rPos[2]);
                }
                GL11.glPopMatrix();
                //GL11.glEnd();
            }
        }
    }

}
