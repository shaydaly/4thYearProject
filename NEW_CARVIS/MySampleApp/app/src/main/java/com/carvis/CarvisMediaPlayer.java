package com.carvis;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Seamus on 16/04/2017.
 */

public class CarvisMediaPlayer {
    MediaPlayer mediaPlayer;
    ArrayList<Integer> queue;
    Context context;
    boolean isPlaying;

    public CarvisMediaPlayer(MediaPlayer mediaPlayer, ArrayList<Integer> queue, Context context) {
        this.mediaPlayer = mediaPlayer;
        this.queue = queue;
        this.context = context;
    }

    public void play(int song){
        Log.wtf("play","11111111");
        if(!isPlaying) {
            Log.wtf("play","222222");
           stopMediaPlayer();
            mediaPlayer = MediaPlayer.create(context, song);
            mediaPlayer.start();
            isPlaying = true;
            if(queue.contains(song)) {
                queue.remove(song);
            }
        }
        else{
            Log.wtf("play","333333");
            if (!queue.contains(song))
                queue.add(song);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(!queue.isEmpty()){
                    Log.wtf("oncompleterion", "is empty");
                    play(queue.get(0));
                    isPlaying=false;
                }
                else{
                    Log.wtf("oncompleterion", "else ");
                    stopMediaPlayer();
                    isPlaying=false;
                }
            }
        });


    }

    public void addSongToQueue(int song){
        if(!queue.contains(song)){
            queue.add(0, song);
        }
        play(song);
    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            // mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
