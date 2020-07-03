 package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
  public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";

    // arbitruary REQUEST_CODE value for result
    private final int REQUEST_CODE = 25;

    //defining member variables
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;



      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client =  TwitterApplication.getRestClient(this);



       //initializing and
        swipeContainer = findViewById(R.id.swipeContainer);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            // we know user is trying to do a pull to refresh
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                //when user is trying to obtain new data we already do that by populatingHomeTimeline
                //call the populateHomeTimeline method
                populateHomeTimeline();

            }
        });

        //Finding and initializing the recycler view from our xml layout
        rvTweets = findViewById(R.id.rvTweets);

        //Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this , tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //recycler view setup and configuration: layout manager and the adapter
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

          //instantiate scrollListener
          scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
              @Override
              public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                  Log.i(TAG, "onLoadMore: " + page);

                  //make an api to get the next page of tweets and add them to our object of current tweets
                  loadMoreData();
              }
          };
          //adds the scroll listener to recycler view
          rvTweets.addOnScrollListener(scrollListener);

        populateHomeTimeline();
    }

      private void loadMoreData() {
          //1. Send an API request to retrieve appropriate paginated data
          client.getNextPageOfTweets(new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Headers headers, JSON json) {
                  Log.i(TAG, "onSuccess for loadMoreData" + json.toString());
                  //2. Deserialize and contruct new model objects from the Api response
                  JSONArray jsonArray = json.jsonArray;
                  try {
                      List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                      //3. Append the new data objects to the existing set of items inside the array of items
                      //4. Notify the adapter of the new items made with 'notifyItemRangeInserted()'
                      adapter.addAll(tweets);
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }

              @Override
              public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                  Log.e(TAG, "onFailure for loadMoreData", throwable);
              }
          }, tweets.get(tweets.size() -1).id);
      }

      // Inflate the menu; this adds items to the action bar if it is present.
      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.menu_main,menu);
          return true;
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          if(item.getItemId() == R.id.compose) {
//              //compose icon has been selected
//              Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();
              //navigate to the compose activity
              //trasmit between the TimelineActivity and ComposeActivity
              Intent intent = new Intent(this,ComposeActivity.class);

              // pass in intent and REQUEST_CODE and launch on child activity (compose activity)
              startActivityForResult(intent, REQUEST_CODE );
              return true;
          }
          return super.onOptionsItemSelected(item);
      }

      //requestCode defined above
      //resultCode is defined by Android making sure the child activity has finished successfully
      //intent data is the data that the user has communicated back to us
      @Override
      protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         //check to make sure REQUEST_CODE we passed in and resultCode
          if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Get data from the intent(tweet object)
               //returns a tweet object
              Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
              // update the Recycler view with this new tweet
              //modify data source of tweets and add at the first position of data source
              tweets.add(0, tweet);
              //update the adapter
              adapter.notifyItemInserted(0 );
              rvTweets.smoothScrollToPosition(0);
          }
          super.onActivityResult(requestCode, resultCode, data);
      }

      private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());
               JSONArray jsonArray =  json.jsonArray;
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
//                    //modify and use existing tweets member variable instead of creating a new tweets object
//                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
//
//                    //notify the adapter that the data has changed
//                    adapter.notifyDataSetChanged();

                    //now we call setResfreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    //logging for potential errors
                    Log.e(TAG, "Json exception: ", e);
//                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure" + response, throwable);
            }
        });
    }
}