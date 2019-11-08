package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    String createdAt;
    public String body;
    public long id;
    public User user;
    public String retweetCount;
    public String favoriteCount;
    public String time;
    public String mediaUrl;
    public int mediaH;
    public int mediaW;
    public String mediaType;
    public boolean hasLinks;
    public boolean hasHashTags;
    public boolean retweeted;
    public boolean favorited;
    public boolean hasMedia;
    public boolean hasMentions;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.id = jsonObject.getLong("id");
        try {
            tweet.body = jsonObject.getString("full_text");
        } catch(JSONException e) {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");


        int retweets = jsonObject.getInt("retweet_count");
        int favorites = jsonObject.getInt("favorite_count");

        String createdAt = jsonObject.getString("created_at");
        tweet.time = TimeFormatter.getTimeDifference(createdAt);


        if (retweets < 1000) {
            tweet.retweetCount = String.valueOf(retweets);
        } else {
            retweets /= 1000;
            tweet.retweetCount = retweets + "K";
        }

        if (favorites < 1000) {
            tweet.favoriteCount = String.valueOf(favorites);

        } else {
            favorites /= 1000;
            tweet.favoriteCount = favorites + "K";
        }

        JSONObject entities = jsonObject.getJSONObject("entities");
        tweet.hasHashTags = entities.getJSONArray("hashtags").length() != 0;
        tweet.hasLinks = entities.getJSONArray("urls").length() != 0;
        tweet.hasMentions = entities.getJSONArray("user_mentions").length() != 0;

        try {
            tweet.mediaType = entities.getJSONArray("media").getJSONObject(0)
                    .getString("type");
            tweet.mediaUrl = entities.getJSONArray("media").getJSONObject(0)
                    .getString("media_url_https");
            tweet.mediaH = entities.getJSONArray("media").getJSONObject(0)
                    .getJSONObject("sizes").getJSONObject("small").getInt("h");
            tweet.mediaW = entities.getJSONArray("media").getJSONObject(0)
                    .getJSONObject("sizes").getJSONObject("small").getInt("w");
            tweet.hasMedia = true;
        } catch (JSONException e) {
            tweet.hasMedia = false;
        }



        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonArray.getJSONObject(i).getJSONObject("retweeted_status");
            } catch(JSONException ignored) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)));
            }

        }
        return tweets;
    }
}
