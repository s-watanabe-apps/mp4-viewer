package com.swapps.mp4viewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class Mp4Activity extends AppCompatActivity {
    VideoView videoView;
    List<String> paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4);

        init();
    }

    public void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);;
        if(preferences.getInt("background", 0) == 0) {
            findViewById(R.id.mp4Layout).setBackgroundColor(Color.parseColor(getString(R.string.list_color_white_background)));
        } else{
            findViewById(R.id.mp4Layout).setBackgroundColor(Color.parseColor(getString(R.string.list_color_black_background)));
        }

        // ナビゲーションバーを隠す
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        videoView = findViewById(R.id.videoView);
        paths = getIntent().getStringArrayListExtra("paths");
        setVideoView(0);
    }

    private void setVideoView(final int index) {
        videoView.suspend();
        videoView.setVideoPath(paths.get(index));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (mediaPlayer.getVideoWidth() > mediaPlayer.getVideoHeight()) {
                    // 横表示にする
                    Mp4Activity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    // 縦表示にする
                    Mp4Activity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                int next = index + 1;
                if (paths.size() <= next) {
                    next = 0;
                }
                setVideoView(next);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            videoView = null;
        }
    }
}
