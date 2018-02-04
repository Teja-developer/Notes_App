package com.notes.kt.kt;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by Teja on 1/30/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTileService extends TileService {

    private final  String LOG_TAG = "MyTileService";
    private final int STATE_ON=1;
    private final int STATE_OFF=0;
    private int toggleState = STATE_ON;

    @Override
    public void onTileAdded() {
       Log.v("LOG_TAG","onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        Log.v("LOG_TAG","onTileRemoved");
    }

    @Override
    public void onStartListening() {
        Log.v("LOG_TAG","onStartListening");
    }

    @Override
    public void onStopListening() {
        Log.v("LOG_TAG","onStopListening");
    }

    @Override
    public void onClick() {
        Log.v("LOG_TAG","onClick"+Integer.toString(getQsTile().getState()));
        Icon icon;
        startActivity(new Intent(this,NewNote.class));
        if(toggleState == STATE_ON)
        {
            toggleState = STATE_OFF;
            icon = Icon.createWithResource(getApplicationContext(),R.drawable.ic_mode_edit);
        }
        else
        {
            toggleState = STATE_ON;
            icon = Icon.createWithResource(getApplicationContext(),R.drawable.ic_mode_edit);
        }

        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }
}
