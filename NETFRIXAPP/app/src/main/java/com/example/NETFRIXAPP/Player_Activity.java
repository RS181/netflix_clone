package com.example.NETFRIXAPP;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class Player_Activity extends AppCompatActivity {

    private boolean isFullscreen = false;

    private ExoPlayer player;
    private String Link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);
        player = new SimpleExoPlayer.Builder(this).build();

        Intent intent = getIntent();
        Link = intent.getStringExtra("MovieLink");
        Toast.makeText(this, Link, Toast.LENGTH_SHORT ).show();

        StyledPlayerView playerview = findViewById(R.id.playerview);
       // Button playNow = (Button)  findViewById(R.id.playNow);


        playerview.setPlayer(player);
        Uri uri = Uri.parse(Link);
        MediaSource mediaSource = buildMediaSource(uri);
        // Prepare and start playback
        //player.setMediaItem(VideoUrl);
        //player.prepare(mediaSource);
        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(true);

    }
    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri)); //  m38u
        //return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);// mp4
    }

    @Override
    protected void onStop(){
        super.onStop();
        player.setPlayWhenReady(false);
        player.release();
        player = null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode
            setFullscreen(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait mode
            setFullscreen(false);
        }
    }
    private void setFullscreen(boolean fullscreen) {
        if (fullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // Adjust layout for fullscreen
            // For example, hide action bar, navigation bar, etc.
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // Restore layout to its original state
            // For example, show action bar, navigation bar, etc.
        }
        isFullscreen = fullscreen;
    }
}