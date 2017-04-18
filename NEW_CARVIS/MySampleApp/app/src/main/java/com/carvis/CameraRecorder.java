package com.carvis;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.CARVISAPP.R;

public class CameraRecorder extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "Recorder";
    public static SurfaceView mSurfaceView;
    public static SurfaceHolder mSurfaceHolder;
    public static Camera mCamera ;
    public static boolean mPreviewRunning;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_recorder);

        mSurfaceView = (SurfaceView) findViewById(R.id.carvisSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button btnStart = (Button) findViewById(R.id.startService);
        btnStart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(CameraRecorder.this, RecorderService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
                //finish();
            }
        });

        Button btnStop = (Button) findViewById(R.id.stopService);
        btnStop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                stopService(new Intent(CameraRecorder.this, RecorderService.class));
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}