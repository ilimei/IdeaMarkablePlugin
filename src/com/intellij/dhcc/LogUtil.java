package com.intellij.dhcc;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * Created by Administrator on 2016/8/29.
 */
public class LogUtil {

    private static String groupDisplayId = "com.dhcc";
    private static String title="MarkablePlugin";
    public static boolean canLog = false;

    private LogUtil() {
    }

    public static void logSuccess(String message) {
        if(!canLog) {
            return;
        }

        Notification notification = new Notification(groupDisplayId, title, message, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }
    public static void logError(String message) {
        if(!canLog) {
            return;
        }

        Notification notification = new Notification(groupDisplayId, title, message, NotificationType.ERROR);
        Notifications.Bus.notify(notification);
    }
    public static void logWarn(String message) {
        if(!canLog) {
            return;
        }

        Notification notification = new Notification(groupDisplayId,title, message, NotificationType.WARNING);
        Notifications.Bus.notify(notification);
    }

    public static void printException(Exception e){
        StringBuilder stringBuilder=new StringBuilder();
        readErrorLog(e,stringBuilder);
        String info=stringBuilder.toString();
        logError(info);
    }

    private static void readErrorLog(Throwable throwable, StringBuilder stringBuilder){
        StackTraceElement[] stack = throwable.getStackTrace();

        String message=throwable.getMessage();
        stringBuilder.append(throwable.getClass().getName());
        if(message!=null)
            stringBuilder.append(":")
                    .append(throwable.getMessage());
        stringBuilder.append("\n");
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement element = stack[i];
            stringBuilder.append("at ").append(element.getClassName())
                    .append(".").append(element.getMethodName())
                    .append("(").append(element.getFileName())
                    .append(":").append(element.getLineNumber())
                    .append(")\n");
        }
        Throwable cause=throwable.getCause();
        if(cause!=null){
            stringBuilder.append("Caused by: ");
            readErrorLog(cause,stringBuilder);
        }
    }
}
