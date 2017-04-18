package com.carvis;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import com.CARVISAPP.R;

import java.util.ArrayList;

/**
 * Created by Seamus on 16/04/2017.
 */

public class CarvisMediaPlayer {
    MediaPlayer mediaPlayer;
    ArrayList<Integer> queue;
    Context context;
    boolean isPlaying, justPlayedSpeedLimit, justPlayedCamera;

    public CarvisMediaPlayer(MediaPlayer mediaPlayer, ArrayList<Integer> queue, Context context) {
        this.mediaPlayer = mediaPlayer;
        this.queue = queue;
        this.context = context;
        justPlayedSpeedLimit = false;
        justPlayedCamera = false;
    }

    public void play() {
        if (!isPlaying) {
            Log.wtf("play", "222222");
            stopMediaPlayer();
            mediaPlayer = MediaPlayer.create(context, queue.get(0));
            mediaPlayer.start();
            isPlaying = true;

            if (queue.get(0) == R.raw.speedlimitpolly) {
                setJustPlayedSpeedLimit();
            } else if (queue.get(0) == R.raw.speedcamerapolly) {
                setJustPlayedCamera();
            }
//            if(queue.contains(song)) {
//                queue.remove(song);
//            }
//      //  }
//        else{
////            Log.wtf("play","333333");
////            if (!queue.contains(song))
////                queue.add(song);
////        }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (queue.size() != 0)
                        queue.remove(0);
                    if (!queue.isEmpty()) {
                        isPlaying = false;
                        play();
                    } else {
                        Log.wtf("oncompleterion", "else ");
                        stopMediaPlayer();
                        isPlaying = false;
                    }
                }
            });
        }

    }

    public void addSongToQueue(int song) {
//        Log.wtf("QUEUE SIZE:::: ", String.valueOf(queue.size()));
        if (!queue.contains(song)) {

            if (song == R.raw.speedcamerapolly && !justPlayedCamera) {
                queue.add(song);
            } else if (song == R.raw.speedlimitpolly && !justPlayedSpeedLimit)
                queue.add(song);
            else if (song == R.raw.badtraffic)
                queue.add(song);
        }

        if (queue.size() != 0)
            play();
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            // mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void setJustPlayedSpeedLimit() {
        justPlayedSpeedLimit = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 10000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {
                            Log.i("", e.getMessage());
                        }
                    }
                }
                Log.wtf("just played spe", "");
                justPlayedSpeedLimit = false;
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void setJustPlayedCamera() {
        justPlayedCamera = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 20000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {
                            Log.i("", e.getMessage());
                        }
                    }
                }
                Log.wtf("just played c", "");
                justPlayedCamera = false;
            }

        };
        Thread t = new Thread(r);
        t.start();
    }


}