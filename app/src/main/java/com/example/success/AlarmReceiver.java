package com.example.success;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //创建notification的builder
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context,"0*123");
            NotificationChannel firstchannel = new NotificationChannel("111", "firstchannel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(firstchannel);
            builder.setChannelId("111");
        }
        else {
            builder=new Notification.Builder(context);
        }
        builder.setContentTitle("该背单词了！");
        builder.setContentText("点击回到主页面");
        Intent goHomeIntent = new Intent(Intent.ACTION_MAIN);
        builder.setContentIntent(PendingIntent.getActivity(context,0,goHomeIntent,FLAG_MUTABLE));
        //TODO 设置两个Icon
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification build = builder.build();
        build.flags|=Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(002,build);
        System.out.println("here");
    }
}

