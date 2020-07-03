package com.codepath.apps.restclienttemplate;

import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvBody;
    TextView tvTimeStamp;
    TextView tvName;
    ImageView ivTweetImage;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        ivProfileImage = findViewById(R.id. ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvBody =  findViewById(R.id.tvBody);
        tvTimeStamp = (TextView) findViewById(R.id.tvTimeStamp);
        tvName = (TextView) findViewById(R.id.tvName);
       ivTweetImage = findViewById(R.id.ivTweetImage);


        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));;


        tvBody.setText(tweet.body);
        tvName.setText(tweet.user.name);
        tvUserName.setText("@" + tweet.user.userName);
        tvTimeStamp.setText(tweet.createdAt.substring(0,19)+ "  · ");

        //Profile picture 
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);

        //If there's an image 
        if(tweet.tweetImageUrls.size() > 0){
            //Set media 
            Glide.with(this).load(tweet.tweetImageUrls.get(0)).transform(new CenterCrop(), new RoundedCorners(30)).into(ivTweetImage);
         //Recovers visibility on a recycled item after it had been toggled off 
            ivTweetImage.setVisibility(View.VISIBLE);
        } else {
            //No image? Hide the view. 
            ivTweetImage.setVisibility(View.GONE);
        }



    }
}

