package com.example.nikhil.group22_hw07;
/**
 * Assignment - Homework #07
 * File name - Podcast.java
 * Full Name - Naga Manikanta Sri Venkata Jonnalagadda
 *             Karthik Gorijavolu
 * Group #22
 * **/

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Podcast implements Parcelable {

    private String title,description,pubDate,imageURL,mp3URL;
    private int duration;
    private Bitmap imageBitmap;
    private boolean isPlaying;

    public Podcast() {
        this.isPlaying = false;
    }

    protected Podcast(Parcel in) {
        title = in.readString();
        description = in.readString();
        pubDate = in.readString();
        imageURL = in.readString();
        mp3URL = in.readString();
        duration = in.readInt();
        imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Podcast> CREATOR = new Creator<Podcast>() {
        @Override
        public Podcast createFromParcel(Parcel in) {
            return new Podcast(in);
        }

        @Override
        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getMp3URL() {
        return mp3URL;
    }

    public void setMp3URL(String mp3URL) {
        this.mp3URL = mp3URL;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", mp3URL='" + mp3URL + '\'' +
                ", duration=" + duration +
                ", imageBitmap=" + imageBitmap +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(pubDate);
        dest.writeString(imageURL);
        dest.writeString(mp3URL);
        dest.writeInt(duration);
        dest.writeParcelable(imageBitmap, flags);
    }

}
