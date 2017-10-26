package com.example.nikhil.group22_hw07;


/**
 * Assignment - Homework #07
 * File name - GetPodcastsAsyncTask.java
 * Full Name - Naga Manikanta Sri Venkata Jonnalagadda
 *             Karthik Gorijavolu
 * Group #22
 * **/

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class GetPodcastsAsyncTask extends AsyncTask<String,Void,ArrayList<Podcast>> {

    private PodcastsListInterface podcastsListInterface;

    public GetPodcastsAsyncTask(PodcastsListInterface PodcastsListInterface) {
        super();
        this.podcastsListInterface = PodcastsListInterface;
    }

    @Override
    protected ArrayList<Podcast> doInBackground(String... params) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream is = connection.getInputStream();
            return PodcastUtil.PodcastParser.parsePodcast(is);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Podcast> Podcasts) {
        super.onPostExecute(Podcasts);
        podcastsListInterface.sendPodcastsList(Podcasts);
    }

    public interface PodcastsListInterface {
        void sendPodcastsList(ArrayList<Podcast> PodcastArrayList);
    }
}
