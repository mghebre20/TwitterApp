package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

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
    public class ViewHolder extends RecyclerView.ViewHolder {

        // introducing variables for our elements in xml file
       ImageView ivProfileImage;
       TextView tvScreenName;
       TextView tvBody;

       //itemView is a representation of one row in Recycler view (a tweet)
       public ViewHolder(@NonNull View itemView) {
           super(itemView);

           // defining my variables here
           ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
           tvScreenName = itemView.findViewById(R.id.tvScreenName);
           tvBody = itemView.findViewById(R.id.tvBody);

       }
        //method in ViewHolder class for onBindViewHolder
       // here we take out the different attributes of the tweet and fill in our layout view
       public void bind(Tweet tweet) {

           //bind variables to tweet
           tvScreenName.setText(tweet.user.screenName);
            tvBody.setText(tweet.body);

            //using glide to load in the image into our variable based on image url for user
           Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
       }
   }
}
