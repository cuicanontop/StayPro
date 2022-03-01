package dev.cuican.staypro.client;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.concurrent.TaskManager;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.event.Priority;
import dev.cuican.staypro.event.events.client.KeyEvent;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import dev.cuican.staypro.gui.StayHUDEditor;
import dev.cuican.staypro.gui.renderers.HUDEditorRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.hud.huds.*;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.modules.client.*;
import dev.cuican.staypro.module.modules.combat.*;
import dev.cuican.staypro.module.modules.misc.*;
import dev.cuican.staypro.module.modules.movement.*;
import dev.cuican.staypro.module.modules.player.*;
import dev.cuican.staypro.module.modules.player.Timer;
import dev.cuican.staypro.module.modules.render.*;
import dev.cuican.staypro.module.pingbypass.PingBypass;
import net.minecraft.client.Minecraft;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleManager {

    public final Map<Class<? extends Module>, Module> moduleMap = new ConcurrentHashMap<>();
    public final List<Module> moduleList = new ArrayList<>();
    private final Set<Class<? extends Module>> classes = new HashSet<>();

    private static ModuleManager instance;

    public static List<Module> getModules() {
        return getInstance().moduleList;
    }

    public static void init() {
        if (instance == null) instance = new ModuleManager();
        instance.moduleMap.clear();

        //HIDDEN
        registerNewModule(Aimbot.class);
        //Client
        registerNewModule(AntiWeather.class);
        registerNewModule(ActiveModuleList.class);
        registerNewModule(ClickGUI.class);
        registerNewModule(GUISetting.class);
        registerNewModule(HUDEditor.class);
        registerNewModule(WaterMark.class);

        //Combat
        registerNewModule(AntiAutoCity.class);
        registerNewModule(AutoWeak.class);
        registerNewModule(AutoBurrow.class);
        registerNewModule(burrows.class);
        registerNewModule(HoleSnap.class);
        registerNewModule(InfiniteDive.class);
        registerNewModule(Burrow.class);
        registerNewModule(EntityControl.class);
        registerNewModule(EntitySpeed.class);
        registerNewModule(PistonAura.class);
        registerNewModule(AutoTrap.class);
        registerNewModule(KillAuraModule.class);
        registerNewModule(Auto32k.class);
        registerNewModule(AntiBurrow.class);
        registerNewModule(Anti32k.class);
        registerNewModule(AutoCity.class);
        registerNewModule(CevBreaker.class);
        registerNewModule(Criticals.class);
        registerNewModule(ZetaCrystal.class);
        registerNewModule(Anti32kTotem.class);
        registerNewModule(AutoTotem.class);
        registerNewModule(StayAura.class);
        registerNewModule(OffHandCrystal.class);
        registerNewModule(HoleFiller.class);
        registerNewModule(Surround.class);
        registerNewModule(BedAura.class);
        registerNewModule(AntiCevTrap.class);

        //Misc
        registerNewModule(PortalGodMode.class);
        registerNewModule(MultiTask.class);
        registerNewModule(Crasher.class);
        registerNewModule(PingBypass.class);
        registerNewModule(AntiEz.class);
        registerNewModule(BowMcBomb.class);
        registerNewModule(Timestamps.class);
        registerNewModule(HotbarRefill.class);
        registerNewModule(CustomChat.class);
        registerNewModule(SkinFlicker.class);
        registerNewModule(Spammer.class);
        registerNewModule(ExtraTab.class);
        registerNewModule(MCF.class);
        registerNewModule(XCarry.class);
        registerNewModule(AutoReconnect.class);
        registerNewModule(AutoWither.class);
        registerNewModule(MCP.class);
        registerNewModule(AutoQueue.class);
        //Movement
        registerNewModule(Phase.class);
        registerNewModule(AntiVoid.class);
        registerNewModule(ElytraPlus.class);
        registerNewModule(Jesus.class);
        registerNewModule(Step.class);
        registerNewModule(BoatFly.class);
        registerNewModule(ReverseStep.class);
        registerNewModule(NoSlowDown.class);
        registerNewModule(Flight.class);
        registerNewModule(Sprint.class);
        registerNewModule(Velocity.class);
        registerNewModule(Strafe.class);
        registerNewModule(Speed.class);
        registerNewModule(GuiMove.class);
        registerNewModule(NoFall.class);

        //Player

        registerNewModule(PacketXP.class);
        registerNewModule(Timer.class);
        registerNewModule(Fastuse.class);
        registerNewModule(AutoArmour.class);
        registerNewModule(ActiveModuleList.class);
        registerNewModule(FakePlayer.class);
        registerNewModule(PacketEat.class);
        registerNewModule(TpsSync.class);
        registerNewModule(Instant.class);
        registerNewModule(PingSpoof.class);
        registerNewModule(Scaffold.class);
        registerNewModule(Freecam.class);
        registerNewModule(Speedmine.class);
        //Render
        registerNewModule(FullBright.class);
        registerNewModule(ESP.class);
        registerNewModule(Burrowesp.class);
        registerNewModule(VoidESP.class);
        registerNewModule(AntiDeathScreen.class);
        registerNewModule(PortalESP.class);
        registerNewModule(PearlViewer.class);
        registerNewModule(BreakESP.class);
        registerNewModule(BlockHighlight.class);
        registerNewModule(HandColor.class);
        registerNewModule(LiquidInteract.class);
        registerNewModule(Tracer.class);
        registerNewModule(CameraClip.class);
        registerNewModule(SurroundRender.class);
        registerNewModule(ItemPhysics.class);
        registerNewModule(NoRender.class);
        registerNewModule(Nametags.class);
        registerNewModule(HoleESP.class);
        registerNewModule(ShulkerPreview.class);
        registerNewModule(ViewModel.class);
        registerNewModule(Wireframe.class);
        registerNewModule(AntiOverlay.class);
        registerNewModule(Brightness.class);
        registerNewModule(LogoutSpots.class);
        registerNewModule(ArmorHUD.class);
        registerNewModule(TabFriends.class);
        registerNewModule(PopChams.class);


        //HUD
        registerNewModule(StayPro.class);
        registerNewModule(HoleOverlayltem.class);
        registerNewModule(CrystalHUD.class);
        registerNewModule(TotHUD.class);
        registerNewModule(XpHUD.class);
        registerNewModule(IintfHUD.class);
        registerNewModule(Csgo.class);
        registerNewModule(CombatInfo.class);
        registerNewModule(Logs.class);
        registerNewModule(Welcomer.class);

        registerNewModule(FPS.class);
        registerNewModule(Ping.class);
        registerNewModule(Player.class);
        registerNewModule(TPS.class);
        registerNewModule(CoordsHUD.class);
        registerNewModule(SpeedsHud.class);

        registerNewModule(Friends.class);
        registerNewModule(TextRadar.class);
        registerNewModule(Ram.class);
        registerNewModule(Server.class);
        registerNewModule(Players.class);


        instance.loadModules();
        Stay.EVENT_BUS.register(instance);
    }



    public static void registerNewModule(Class<? extends Module> clazz) {
        instance.classes.add(clazz);
    }


    @Listener(priority = Priority.HIGHEST)
    public void onKey(KeyEvent event) {
        moduleList.forEach(it -> it.keyBind.test(event.getKey()));
    }

    @Listener(priority = Priority.HIGHEST)
    public void onRenderHUD(RenderOverlayEvent event) {
        for (int i = HUDEditorRenderer.instance.hudModules.size() - 1; i >= 0; i--) {
            HUDModule hudModule = HUDEditorRenderer.instance.hudModules.get(i);

            if (!(Minecraft.getMinecraft().currentScreen instanceof StayHUDEditor) && hudModule.isEnabled())
                hudModule.onHUDRender(event.getScaledResolution());
        }
    }

    public static ModuleManager getInstance() {
        if (instance == null) instance = new ModuleManager();
        return instance;
    }

    public static Module getModule(Class<? extends Module> clazz) {
        return getInstance().moduleMap.get(clazz);
    }

    public static Module getModuleByName(String targetName) {
        for (Module module : getModules()) {
            if (module.name.equalsIgnoreCase(targetName)) {
                return module;
            }
        }
        Stay.log.info("Module " + targetName + " is not exist.Please check twice!");
        return null;
    }

    private void loadModules() {
        Stay.log.info("[ModuleManager]Loading modules.");
        TaskManager.runBlocking(unit -> classes.stream().sorted(Comparator.comparing(Class::getSimpleName)).forEach(clazz -> {
            if (clazz != HUDModule.class) {
                try {
                    if (clazz.isAnnotationPresent(Parallel.class) && clazz.getAnnotation(Parallel.class).loadable()) {
                        TaskManager.launch(unit, () -> {
                            try {
                                add(clazz.newInstance(), clazz);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println("Couldn't initiate Module " + clazz.getSimpleName() + "! Error: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
                            }
                        });
                    } else {
                        add(clazz.newInstance(), clazz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Couldn't initiate Module " + clazz.getSimpleName() + "! Error: " + e.getClass().getSimpleName() + ", message: " + e.getMessage());
                }
            }
        }));
        sort();
        Stay.log.info("[ModuleManager]Loaded " + moduleList.size() + " modules");
    }

    private synchronized void add(Module module, Class<? extends Module> clazz) {
        moduleList.add(module);
        moduleMap.put(clazz, module);
    }

    private void sort() {
        moduleList.sort(Comparator.comparing(it -> it.name));
    }


}
