package com.movintracks.cordovamovintracks.remoteNotifications;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import io.movintracks.library.Movintracks;

/**
 * Created by jsingla on 06/08/14.
 */
public class GcmIntentService extends IntentService{

	private static final String TAG = "GcmIntentService";

	public GcmIntentService() {
		super("GcmIntentService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
			Log.w(TAG, "MessageType: "+messageType+" Extras: "+extras.toString());
			Movintracks.getInstance().receivedGoogleCloudMessage(extras);
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}
