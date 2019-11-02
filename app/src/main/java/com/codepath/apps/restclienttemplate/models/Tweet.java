package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    public String body;
    public String createdAt;
    public long id;
    public User user;
    public String retweetCount;
    public String favoriteCount;
    public String time;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.id = jsonObject.getLong("id");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

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
