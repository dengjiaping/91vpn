package com.dmtec.a91jiasu.logic;

import android.content.Context;
import android.util.Log;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;


/**
 * Created by dmtec on 2017/11/12.
 *
 */

public class PMR extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        Log.e("MSG-R",pushNotificationMessage.getPushContent());
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        Log.e("MSG-R",pushNotificationMessage.getPushContent());
        return false;
    }
}
