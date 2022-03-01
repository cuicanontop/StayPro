package dev.cuican.staypro.client;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.concurrent.TaskManager;
import dev.cuican.staypro.concurrent.decentralization.ListenableImpl;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.event.Priority;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.notification.NotificationManager;
import dev.cuican.staypro.event.decentraliized.DecentralizedClientTickEvent;
import dev.cuican.staypro.event.decentraliized.DecentralizedPacketEvent;
import dev.cuican.staypro.event.decentraliized.DecentralizedRenderTickEvent;
import dev.cuican.staypro.event.decentraliized.DecentralizedRenderWorldEvent;
import dev.cuican.staypro.event.events.client.InputUpdateEvent;
import dev.cuican.staypro.event.events.client.SettingUpdateEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import dev.cuican.staypro.event.events.render.RenderWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleBus extends ListenableImpl {

    public ModuleBus() {
        Stay.EVENT_BUS.register(this);
        listener(DecentralizedClientTickEvent.class, it -> onTick());
        listener(DecentralizedRenderTickEvent.class, this::onRenderTick);
        listener(DecentralizedRenderWorldEvent.class, this::onRenderWorld);
        listener(DecentralizedPacketEvent.Send.class, this::onPacketSend);
        listener(DecentralizedPacketEvent.Receive.class, this::onPacketReceive);
        subscribe();
    }

    private final List<Module> modules = new CopyOnWriteArrayList<>();

    public synchronized void register(Module module) {
        modules.add(module);
        Stay.EVENT_BUS.register(module);
    }

    public void unregister(Module module) {
        modules.remove(module);
        Stay.EVENT_BUS.unregister(module);
    }

    public boolean isRegistered(Module module) {
        return modules.contains(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    @Listener(priority = Priority.HIGHEST)
    public void onKey(InputUpdateEvent event) {
        modules.forEach(mod -> mod.onInputUpdate(event));
    }

    public void onTick() {
        TaskManager.runBlocking(it -> modules.forEach(module -> {
            if (module.parallelRunnable) {
                it.launch(() -> {
                    try {
                        module.onTick();
                    } catch (Exception exception) {
                        NotificationManager.fatal("Error while running Parallel Tick!");
                        exception.printStackTrace();
                    }
                });
            } else {
                try {
                    module.onTick();
                } catch (Exception exception) {
                    NotificationManager.fatal("Error while running Tick!");
                    exception.printStackTrace();
                }
            }
        }));
    }





    public void onRenderTick(RenderOverlayEvent event) {
        TaskManager.runBlocking(it -> modules.forEach(module -> {
            try {
                module.onRender(event);
            } catch (Exception exception) {
                NotificationManager.fatal("Error while running onRender!");
                exception.printStackTrace();
            }
            if (module.parallelRunnable) {
                it.launch(() -> {
                    try {
                        module.onRenderTick();
                    } catch (Exception exception) {
                        NotificationManager.fatal("Error while running Parallel Render Tick!");
                        exception.printStackTrace();
                    }
                });
            } else {
                try {
                    module.onRenderTick();
                } catch (Exception exception) {
                    NotificationManager.fatal("Error while running Render Tick!");
                    exception.printStackTrace();
                }
            }
        }));
    }

    public void onRenderWorld(RenderWorldEvent event) {
        WorldRenderPatcher.INSTANCE.patch(event);
    }

    public void onPacketSend(PacketEvent.Send event) {
        modules.forEach(module -> {
            try {
                module.onPacketSend(event);
            } catch (Exception exception) {
                NotificationManager.fatal("Error while running PacketSend!");
                exception.printStackTrace();
            }
        });
    }

    public void onPacketReceive(PacketEvent.Receive event) {
        modules.forEach(module -> {
            try {
                module.onPacketReceive(event);
            } catch (Exception exception) {
                NotificationManager.fatal("Error while running PacketReceive!");
                exception.printStackTrace();
            }
        });
    }


    @Listener(priority = Priority.HIGHEST)
    public void onSettingChange(SettingUpdateEvent event) {
        modules.forEach(it -> {
            try {
                it.onSettingChange(event.getSetting());
            } catch (Exception exception) {
                NotificationManager.fatal("Error while running onSettingChange!");
                exception.printStackTrace();
            }
        });
    }

}
