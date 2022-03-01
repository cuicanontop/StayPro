package dev.cuican.staypro.notification;

import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.Wrapper;

import java.util.ArrayList;

public class NotificationManager {

    public static void raw(String message) {
        ChatUtil.printChatMessage(message);
    }

    public static void info(String message) {
        raw("[Info]" + message);
    }

    public static void warn(String message) {
        raw(color("6") + "[Warning]" + color("r") + message);
    }

    public static void error(String message) {
        ChatUtil.printErrorChatMessage(color("c") + "[Error]" + color("r") + message);
    }

    public static void fatal(String message) {
        ChatUtil.printErrorChatMessage(color("4") + "[Fatal]" + color("r") + message);
    }

    public static void debug(String message) {
        raw(color("a") + "[Debug]" + color("r") + message);
    }

    public static void moduleToggle(Module module, boolean toggled) {
       if(Wrapper.mc.world!=null) add(new Notification(module.name + " has been " + (toggled ? color("aEnabled") : color("cDisabled")) + color("r") + "!", Notification.Type.Info));

    }

    public static String color(String color) {
        return ChatUtil.SECTIONSIGN + color;
    }
    public static ArrayList<Notification> notifications = new ArrayList<Notification>();

    public static void add(Notification noti) {
        noti.y = notifications.size() * 25;
        notifications.add(noti);

    }

    public static void draw() {
        int i = 0;
        Notification remove = null;
        for (Notification notification : notifications) {
//            if (i == 0) {
            if (notification.x == 0) {
                notification.in = !notification.in;
            }
            if (Math.abs(notification.x - notification.width) < 0.1 && !notification.in) {
                remove = notification;
            }
            if (notification.in) {
                notification.x = (float) notification.animationUtils.animate(0, notification.x, 0.1f);
            } else {
                notification.x = (float) notification.animationUtils.animate(notification.width, notification.x, 0.1f);
            }
//            }
            notification.onRender();
            i++;
        }
        if (remove != null) {
            notifications.remove(remove);
        }
    }

}
