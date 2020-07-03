package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public long id;
    public User user;
    public  String tvTimeStamp;

    @Transient
    JSONObject entities;
    public List <String> tweetImageUrls = new ArrayList<>();



    // empty constructor needed by the Parceler library
    public Tweet() {}

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.tvTimeStamp = jsonObject.getString("created_at");

        //parsing through the entities for embedded image view
        tweet.entities = jsonObject.getJSONObject("entities");
        if(tweet.entities.has("media")){
            JSONArray mediaEntities = tweet.entities.getJSONArray("media");
            for (int i = 0; i < mediaEntities.length(); i++) {
                tweet.tweetImageUrls.add(mediaEntities.getJSONObject(i).getString("media_url_https"));
            }
            Log.i("TweetMedia", "Media! Found: " + tweet.tweetImageUrls.toString());
        } else
            Log.i("TweetMedia", "No media on tweet");
        return  tweet;
    }
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJSON(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

}
