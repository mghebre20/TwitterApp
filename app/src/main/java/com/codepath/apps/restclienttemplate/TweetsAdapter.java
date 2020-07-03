package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

//this TweetsAdapter class extends super and parametized by viewholder created below
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>  {

    //defining member variables
    Context context;
    List<Tweet> tweets;

    //this constructor passes in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    //for each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // pass in context to get layout inflator
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        // wrap our view inside view holder we defined
        return new ViewHolder(view);
    }

    //bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the data at position
        Tweet tweet = tweets.get(position);
        //bind the tweet with the view holder
        holder.bind(tweet);

    }

    //take the list of tweets and make that the size of our data
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //swipe to refresh feature
    //we want to fire off an api call to fetch new data
    //use helper methods in our RecyclerView.Adapter class to clear or add items to a dataset
    // we want to modify the existing reference to tweets rather than making a new one
    //clean all elements of the recycler, the underlying datatset--tweets
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    //add a list of items
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }


   // define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        // introducing variables for our elements in xml file
       ImageView ivProfileImage;
       TextView tvUserName;
       TextView tvBody;
       TextView tvTimeStamp;
       TextView tvName;
       ImageView ivTweetImage;

       //itemView is a representation of one row in Recycler view (a tweet)
       public ViewHolder(@NonNull View itemView) {
           super(itemView);

           // defining my variables here
           ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
           tvUserName = itemView.findViewById(R.id.tvUserName);
           tvBody = itemView.findViewById(R.id.tvBody);
           tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
           tvName = itemView.findViewById(R.id.tvName);
           ivTweetImage = itemView.findViewById(R.id.ivTweetImage);

           itemView.setOnClickListener(this);

       }
        //method in ViewHolder class for onBindViewHolder
       // here we take out the different attributes of the tweet and fill in our layout view
       public void bind(Tweet tweet) {

           //bind variables to tweet
           tvUserName.setText("@" +tweet.user.userName);
            tvBody.setText(tweet.body);
//           tvTimeStamp.setText(" · " +tweet.tvTimeStamp);
           tvName.setText(tweet.user.name);

           tvTimeStamp.setText(" · " + getRelativeTimeAgo(tweet.createdAt));


                   //If there's an image 
                   if(tweet.tweetImageUrls.size() > 0){
                       //Set media 
                       Glide.with(context).load(tweet.tweetImageUrls.get(0)).transform(new CenterCrop(), new RoundedCorners(30)).into(ivTweetImage);
                   //Recovers visibility on a recycled item after it had been toggled off 
                       ivTweetImage.setVisibility(View.VISIBLE);
                   } else{
                       //No image? Hide the view. 
                       ivTweetImage.setVisibility(View.GONE);
                   }


            //using glide to load in the image into our variable based on image url for user
           Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
       }

       public String getRelativeTimeAgo(String rawJsonDate) {
           String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
           SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
           sf.setLenient(true);
           String relativeDate = "";
           try {
               long dateMillis = sf.parse(rawJsonDate).getTime();
               relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
           } catch (ParseException e) {
               e.printStackTrace();
           }
           return relativeDate;
       }

       @Override
       public void onClick(View v) {
           //we want the items position
           int position = getAdapterPosition();
           //if position is viewed
           if(position != RecyclerView.NO_POSITION) {
               Tweet tweet = tweets.get(position);
               Intent intent = new Intent(context, DetailsActivity.class);
               intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(tweet));
               context.startActivity(intent);
           }

       }
   }
}
