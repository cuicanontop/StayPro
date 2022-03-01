package dev.cuican.staypro.module.modules.misc;

import dev.cuican.staypro.concurrent.TaskManager;
import dev.cuican.staypro.concurrent.repeat.RepeatUnit;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static dev.cuican.staypro.concurrent.TaskManager.runRepeat;
import static dev.cuican.staypro.utils.FileUtil.readTextFileAllLines;

@Parallel
@ModuleInfo(name = "Spammer", category = Category.MISC, description = "Automatically spam")
public class Spammer extends Module {

    Setting<Integer> delay = setting("DelayS", 10, 1, 100);
    Setting<Boolean> randomSuffix = setting("Random", false);
    Setting<Boolean> greenText = setting("GreenText", false);
    public Setting<String> threshold = setting("Threshold", "cuican");
    private static final String fileName = "Stay/spammer/Spammer.txt";
    private static final String defaultMessage = "hello world";
    private static final List<String> spamMessages = new ArrayList<>();
    private static final Random rnd = new Random();

    RepeatUnit fileChangeListener = new RepeatUnit(1000, this::readSpamFile);

    RepeatUnit runner = new RepeatUnit(() -> delay.getValue() * 1000, () -> {
        if (mc.player == null) {
            disable();
        } else if (spamMessages.size() > 0) {
            String messageOut;
            if (randomSuffix.getValue()) {
                int index = rnd.nextInt(spamMessages.size());
                messageOut = spamMessages.get(index);
                spamMessages.remove(index);
            } else {
                messageOut = spamMessages.get(0);
                spamMessages.remove(0);
            }
            spamMessages.add(messageOut);
            if (this.greenText.getValue()) {
                messageOut = "> " + messageOut;
            }
            mc.player.connection.sendPacket(new CPacketChatMessage(messageOut.replaceAll("\u00a7", "")));
        }
    });

    public Spammer() {
        runner.suspend();
        TaskManager.runRepeat(runner);
        TaskManager.runRepeat(fileChangeListener);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            this.disable();
            return;
        }
        runner.resume();
        readSpamFile();
    }

    @Override
    public void onDisable() {
        spamMessages.clear();
        runner.suspend();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void readSpamFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception ignored) {
            }
        }
        List<String> fileInput = readTextFileAllLines(fileName);
        Iterator<String> i = fileInput.iterator();
        spamMessages.clear();
        while (i.hasNext()) {
            String s = i.next();
            if (s.replaceAll("\\s", "").isEmpty()) continue;
            spamMessages.add(s);
        }

        if (spamMessages.size() == 0) {
            spamMessages.add(defaultMessage);
        }
    }

}

