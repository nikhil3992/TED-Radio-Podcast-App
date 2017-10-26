package com.example.nikhil.group22_hw07;
/**
 * Assignment - Homework #07
 * File name - ImageAsyncTask.java
 * Full Name - Naga Manikanta Sri Venkata Jonnalagadda
 *             Karthik Gorijavolu
 * Group #22
 * **/

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageAsyncTask extends AsyncTask<Podcast,Void,Void> {

    private ImageInterface imageInterface;

    public ImageAsyncTask(ImageInterface imageInterface) {
        super();
        this.imageInterface = imageInterface;
    }

    @Override
    protected Void doInBackground(Podcast... params) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0].getImageURL());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream is = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if(bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap,60,60,false);
                params[0].setImageBitmap(bitmap);
            }

            return null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        imageInterface.sendImage();
    }

    public interface ImageInterface {
        void sendImage();
    }
}
