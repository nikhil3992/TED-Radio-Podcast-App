package com.example.nikhil.group22_hw07;
/**
 *
 * File name - MainActivity.java
 * Full Name - Nikhil Jonnalagadda
 *
 * **/
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetPodcastsAsyncTask.PodcastsListInterface,
        ImageAsyncTask.ImageInterface,PodcastAdapter.ItemClickInterface {

    RecyclerView recyclerView;
    PodcastAdapter adapter;
    List<Podcast> podcastList;
    ProgressDialog progressDialog;
    MediaPlayer mediaPlayer;
    Button playPauseButton;
    ProgressBar progressBar;
    Handler handler;
    int progress = 0;
    int totalProgress;
    int globalPosition = -1;
    static boolean viewIsLinear = true;
    BroadcastReceiver mMessageReceiver;
    boolean play = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Drawable drawable = resize(getResources().getDrawable(R.drawable.ted_actionbar_image));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(drawable);
            actionBar.setIcon(drawable);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        playPauseButton = (Button) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer!=null && mediaPlayer.isPlaying()) {
                    play = false;
                    playPauseButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_18dp);
                    mediaPlayer.pause();
                    podcastList.get(globalPosition).setPlaying(false);
                } else if(mediaPlayer!=null) {
                    play = true;
                    handler.postDelayed(new RunnableClass(),1000);
                    playPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_18dp);
                    mediaPlayer.start();
                    podcastList.get(globalPosition).setPlaying(true);

                }
                adapter.notifyDataSetChanged();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == 0) {
                    progressBar.setProgress((int)msg.obj);
                } else if(msg.what == 1) {
                    progressBar.setProgress(0);
                }
                return false;
            }
        });

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onPlayItemClick(intent.getIntExtra("pos",0));
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(getString(R.string.change_radio)));

        new GetPodcastsAsyncTask(this).execute(getString(R.string.api));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    class RunnableClass implements Runnable {

        @Override
        public void run() {
            if(play)
                updateUI();
        }
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 100, 100, false);
        return new BitmapDrawable(getResources(), bitmapResized);

    }

    public void updateUI() {

        Message msg = new Message();
        if(progress < totalProgress) {
            msg.what = 0;
            msg.obj = ++progress;
            handler.sendMessage(msg);
            handler.postDelayed(new RunnableClass(),1000);
            Intent intent = new Intent(getString(R.string.update_ui));
            intent.putExtra(getString(R.string.progress),progress);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            msg.what = 1;
            handler.sendMessage(msg);
            handler.postDelayed(new RunnableClass(),1000);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.switchView:
                if(viewIsLinear) {
                    viewIsLinear = false;
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(adapter);

                } else {
                    viewIsLinear = true;
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);

                }

        }
        return true;
    }

    @Override
    public void sendPodcastsList(ArrayList<Podcast> podcastArrayList) {

        progressDialog.dismiss();
        podcastList = podcastArrayList;
        for(Podcast p : podcastList) {
            new ImageAsyncTask(MainActivity.this).execute(p);
        }

        adapter = new PodcastAdapter(this,podcastList,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void sendImage() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayItemClick(int position) {

        final Podcast podcast = podcastList.get(position);

        if(globalPosition == position && mediaPlayer!=null) {
            if(mediaPlayer.isPlaying()) {
                play = false;
                playPauseButton.setBackgroundResource(R.drawable.ic_play_circle_outline_black_18dp);
                mediaPlayer.pause();

            } else {
                play = true;
                handler.postDelayed(new RunnableClass(),1000);
                playPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_18dp);
                mediaPlayer.start();
            }
            return;
        }
        if(globalPosition != -1) {
            podcastList.get(globalPosition).setPlaying(false);
            adapter.notifyDataSetChanged();
        }
        play = true;
        globalPosition = position;
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(podcast.getMp3URL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mediaPlayer!=null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                handler.removeCallbacks(new RunnableClass(),null);
                Message message = new Message();
                message.what = 1;
                playPauseButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                progressBar.setVisibility(View.VISIBLE);
                playPauseButton.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                progressDialog.setMessage(getString(R.string.loading_episodes));
                progressBar.setMax(podcast.getDuration());
                totalProgress = podcast.getDuration();
                progressBar.setProgress(0);
                progress = 0;
                playPauseButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_18dp);
                handler.postDelayed(new RunnableClass(),1000);
                mediaPlayer.start();

            }
        });
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(MainActivity.this,PlayActivity.class);
        intent.putExtra(getString(R.string.pod),podcastList.get(position));
        intent.putExtra(getString(R.string.position),position);
        startActivity(intent);
    }


}
