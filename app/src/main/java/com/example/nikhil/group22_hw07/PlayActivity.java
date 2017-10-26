package com.example.nikhil.group22_hw07;
/**
 *
 * File name - PlayActivity.java
 * Full Name - Nikhil Jonnalagadda
 *
 *
 * **/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayActivity extends AppCompatActivity {

    Podcast podcast;
    Button playPauseButton;
    ProgressBar progressBar;
    BroadcastReceiver broadcastReceiver;
    boolean play = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        TextView episodeTitle = (TextView) findViewById(R.id.episodeTitle);
        TextView episodeDescription = (TextView) findViewById(R.id.episodeDescription);
        TextView episodePubDate = (TextView) findViewById(R.id.episodePubDate);
        TextView episodeDuration = (TextView) findViewById(R.id.episodeDuration);
        ImageView episodeImage = (ImageView) findViewById(R.id.episodeImage);
        playPauseButton = (Button) findViewById(R.id.playPauseButton1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        Drawable drawable = resize(getResources().getDrawable(R.drawable.ted_actionbar_image));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(drawable);
            actionBar.setIcon(drawable);
        }


        Intent intent = getIntent();
        podcast = intent.getParcelableExtra(getString(R.string.pod));
        episodeTitle.setText(podcast.getTitle());
        episodeDescription.setText(getString(R.string.desc)+podcast.getDescription());
        try {
            String dateFormat = podcast.getPubDate();
            dateFormat = dateFormat.substring(5,7)+"-"+dateFormat.substring(8,11)+"-"+dateFormat.substring(12,16);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = sdf.parse(dateFormat);
            sdf.applyPattern("MM/dd/yyyy");
            episodePubDate.setText(getString(R.string.pub_date)+sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        episodeDuration.setText(getString(R.string.duration)+podcast.getDuration()+getString(R.string.seconds));
        if(podcast.getImageBitmap()!=null) {
            episodeImage.setImageBitmap(podcast.getImageBitmap());
        }
        progressBar.setMax(podcast.getDuration());


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressBar.setProgress(intent.getIntExtra(getString(R.string.progress),0));
            }
        };


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play) {
                    playPauseButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_18dp);
                    LocalBroadcastManager.getInstance(PlayActivity.this).unregisterReceiver(broadcastReceiver);
                    play = false;
                } else {
                    playPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_18dp);
                    LocalBroadcastManager.getInstance(PlayActivity.this).registerReceiver(broadcastReceiver,new IntentFilter(getString(R.string.update_ui)));
                    play = true;
                }
                Intent intent = new Intent(getString(R.string.change_radio));
                intent.putExtra("pos",getIntent().getIntExtra(getString(R.string.position),0));
                LocalBroadcastManager.getInstance(PlayActivity.this).sendBroadcast(intent);
                LocalBroadcastManager.getInstance(PlayActivity.this).registerReceiver(broadcastReceiver,
                        new IntentFilter(getString(R.string.update_ui)));

            }
        });

    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 100, 100, false);
        return new BitmapDrawable(getResources(), bitmapResized);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(PlayActivity.this).unregisterReceiver(broadcastReceiver);
    }
}
