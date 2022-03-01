

package dev.cuican.staypro.module;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.concurrent.decentralization.ListenableImpl;
import dev.cuican.staypro.concurrent.task.VoidTask;
import dev.cuican.staypro.event.events.client.InputUpdateEvent;
import dev.cuican.staypro.event.events.network.PacketEvent.Receive;
import dev.cuican.staypro.event.events.network.PacketEvent.Send;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import dev.cuican.staypro.notification.NotificationManager;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.setting.settings.*;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.KeyBind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;

public class Module extends ListenableImpl {
    public final String name = this.getAnnotation().name();
    public final Category category = this.getAnnotation().category();
    public final Parallel annotation = (Parallel)this.getClass().getAnnotation(Parallel.class);
    public final boolean parallelRunnable;
    public final String description;
    boolean enabled;
    private final List<Setting<?>> settings;
    public static Minecraft mc = Minecraft.getMinecraft();
    public final KeyBind keyBind;
    private final Setting<KeyBind> bindSetting;
    private final Setting<String> visibleSetting;
    private final Setting<VoidTask> reset;

    public Module() {
        this.parallelRunnable = this.annotation != null && this.annotation.runnable();
        this.description = this.getAnnotation().description();
        this.enabled = false;
        this.settings = new ArrayList();
        this.keyBind = new KeyBind(this.getAnnotation().keyCode(), this::toggle);
        this.bindSetting = this.setting("Bind", this.keyBind, "The key bind of this module");
        this.visibleSetting = this.setting("Visible", this.getAnnotation().visible() ? "True" : "False", this.listOf("True", "False"), "Show on active module list or not");
        this.reset = this.setting("Reset", () -> {
            this.disable();
            this.settings.forEach(Setting::reset);
        }, "Reset this module");
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    @SafeVarargs
    public final <T> List<T> listOf(T... elements) {
        return Arrays.asList(elements);
    }

    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }

    }

    public void reload() {
        if (this.enabled) {
            this.enabled = false;
            Stay.MODULE_BUS.unregister(this);
            this.onDisable();
            this.enabled = true;
            Stay.MODULE_BUS.register(this);
            this.onEnable();
        }

    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isDisabled() {
        return !this.enabled;
    }

    public void enable() {
        this.enabled = true;
        Stay.MODULE_BUS.register(this);
        this.subscribe();
        NotificationManager.moduleToggle(this, true);
        this.onEnable();
    }

    public static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }

    public static boolean nullCheck() {
        return mc.player == null;
    }

    public void disable() {
        this.enabled = false;
        Stay.MODULE_BUS.unregister(this);
        this.unsubscribe();
        NotificationManager.moduleToggle(this, false);
        this.onDisable();
    }

    public void onPacketReceive(Receive event) {
    }

    public void onPacketSend(Send event) {
    }

    public void onTick() {
    }

    public void onRenderTick() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onRender(RenderOverlayEvent event) {
    }

    public void onRenderWorld(RenderEvent event) {
    }

    public void onInputUpdate(InputUpdateEvent event) {
    }

    public void onSettingChange(Setting<?> setting) {
    }

    public Setting<VoidTask> setting(String name, VoidTask defaultValue) {
        ActionSetting setting = new ActionSetting(name, defaultValue);
        this.settings.add(setting);
        return setting;
    }

    public Setting<KeyBind> setting(String name, KeyBind defaultValue) {
        BindSetting setting = new BindSetting(name, defaultValue);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Boolean> setting(String name, boolean defaultValue) {
        BooleanSetting setting = new BooleanSetting(name, defaultValue);
        this.settings.add(setting);
        return setting;
    }
    public Setting<String> setting(String name, String String) {
        StringString setting = new StringString(name, String);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Integer> setting(String name, int defaultValue, int minValue, int maxValue) {
        IntSetting setting = new IntSetting(name, defaultValue, minValue, maxValue);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Float> setting(String name, float defaultValue, float minValue, float maxValue) {
        FloatSetting setting = new FloatSetting(name, defaultValue, minValue, maxValue);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Double> setting(String name, double defaultValue, double minValue, double maxValue) {
        DoubleSetting setting = new DoubleSetting(name, defaultValue, minValue, maxValue);
        this.settings.add(setting);
        return setting;
    }

    public Setting<String> setting(String name, String defaultMode, List<String> modes) {
        ModeSetting setting = new ModeSetting(name, defaultMode, modes);
        this.settings.add(setting);
        return setting;
    }

    public Setting<String> setting(String name, String defaultMode, String... modes) {
        ModeSetting setting = new ModeSetting(name, defaultMode, Arrays.asList(modes));
        this.settings.add(setting);
        return setting;
    }

    public Setting<VoidTask> setting(String name, VoidTask defaultValue, String description) {
        Setting<VoidTask> setting = (new ActionSetting(name, defaultValue)).des(description);
        this.settings.add(setting);
        return setting;
    }

    public Setting<KeyBind> setting(String name, KeyBind defaultValue, String description) {
        Setting<KeyBind> setting = (new BindSetting(name, defaultValue)).des(description);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Boolean> setting(String name, boolean defaultValue, String description) {
        Setting<Boolean> setting = (new BooleanSetting(name, defaultValue)).des(description);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Integer> setting(String name, int defaultValue, int minValue, int maxValue, String description) {
        Setting<Integer> setting = (new IntSetting(name, defaultValue, minValue, maxValue)).des(description);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Float> setting(String name, float defaultValue, float minValue, float maxValue, String description) {
        Setting<Float> setting = (new FloatSetting(name, defaultValue, minValue, maxValue)).des(description);
        this.settings.add(setting);
        return setting;
    }

    public Setting<Double> setting(String name, double defaultValue, double minValue, double maxValue, String description) {
        Setting<Double> setting = (new DoubleSetting(name, defaultValue, minValue, maxValue)).des(description);
        this.settings.add(setting);
        return setting;
    }

    public Setting<String> setting(String name, String defaultMode, List<String> modes, String description) {
        Setting<String> setting = (new ModeSetting(name, defaultMode, modes)).des(description);
        this.settings.add(setting);
        return setting;
    }

    public Setting<String> setting(String name, String defaultMode, String description, String... modes) {
        Setting<String> setting = (new ModeSetting(name, defaultMode, Arrays.asList(modes))).des(description);
        this.settings.add(setting);
        return setting;
    }

    private ModuleInfo getAnnotation() {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            return (ModuleInfo)this.getClass().getAnnotation(ModuleInfo.class);
        } else {
            throw new IllegalStateException("No Annotation on class " + this.getClass().getCanonicalName() + "!");
        }
    }

    public String getModuleInfo() {
        return "";
    }

    public String getHudSuffix() {
        return this.name + (!this.getModuleInfo().equals("") ? ChatUtil.colored("7") + "[" + ChatUtil.colored("f") + this.getModuleInfo() + ChatUtil.colored("7") + "]" : this.getModuleInfo());
    }
}
