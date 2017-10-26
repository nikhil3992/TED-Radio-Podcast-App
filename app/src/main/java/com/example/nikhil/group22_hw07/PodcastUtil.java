package com.example.nikhil.group22_hw07;

/**
 * Assignment - Homework #07
 * File name - PodcastUtil.java
 * Full Name - Naga Manikanta Sri Venkata Jonnalagadda
 *             Karthik Gorijavolu
 * Group #22
 * **/

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PodcastUtil {

    public static class PodcastParser {

        public static ArrayList<Podcast> parsePodcast(InputStream inputStream) throws XmlPullParserException, IOException {

            Podcast podcast = null;
            ArrayList<Podcast> podcastArrayList = new ArrayList<>();
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(inputStream,"UTF-8");

            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equals("item")) {
                            podcast = new Podcast();
                            podcast.setImageURL("");

                        } else if (parser.getName().equals("title")) {
                            if(podcast != null) {
                                podcast.setTitle(parser.nextText().trim());
                            }

                        } else if (parser.getName().equals("description")) {
                            if(podcast != null) {
                                podcast.setDescription(parser.nextText().trim());
                            }

                        } else if (parser.getName().equals("pubDate")) {
                            if(podcast != null) {
                                podcast.setPubDate(parser.nextText().trim());
                            }

                        } else if (parser.getName().equals("itunes:duration")) {
                            if(podcast != null) {
                                podcast.setDuration(Integer.parseInt(parser.nextText().trim()));
                            }

                        } else if (parser.getName().equals("itunes:image")) {
                            if(podcast != null) {
                                podcast.setImageURL(parser.getAttributeValue("","href"));
                            }

                        }  else if (parser.getName().equals("enclosure")) {
                            if(podcast != null) {
                                podcast.setMp3URL(parser.getAttributeValue("","url"));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item")) {
                            if(podcast != null) {
                                podcastArrayList.add(podcast);
                                podcast = null;
                            }
                        }
                }
                event = parser.next();
            }
            return podcastArrayList;
        }
    }
}
