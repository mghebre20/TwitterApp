package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.wafflecopter.charcounttextview.CharCountTextView;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    //constant TAG for log statement
    //TAG same as activity name
    public static final String TAG = "ComposeActivity";
    //constant for max character length
    public static final int MAX_TWEET_LENGTH = 280;

    //introduce member variables from activity_compose.xml file
    EditText etCompose;
    Button btnTweet;


    //introducing TwitterClient
    TwitterClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // we want a reference to the client
        //pass in this, a reference to the ComposeActivity
        client = TwitterApplication.getRestClient(this);

        //we are dragging out the corresponding layout elements by id and assigning to our member variable
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        //tells users when they passed limit
        CharCountTextView charCountTextView = (CharCountTextView)findViewById(R.id.tvCharacterCount);
        charCountTextView.setEditText(etCompose);
        charCountTextView.setCharCountChangedListener(new CharCountTextView.CharCountChangedListener() {
            @Override
            public void onCountChanged(int i, boolean b) {
            }
        });

        // Set a click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //error handling - if user's text is too short or too long
                final String tweetContent = etCompose.getText().toString();
                //tweetContent refers to user input or data
                if(tweetContent.isEmpty()) {
                    //Android Snackbar may be a more convenient air handler  message
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty.", Toast.LENGTH_LONG).show();

                // if this happens, we don't want to make the api call to twitter
                    return;
                }
                if(tweetContent.length()> MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long.", Toast.LENGTH_LONG).show();

                    // if this happens, we don't want to make the api call to twitter
                    return;
                }
//                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();

                //make an API call to Twitter to publish to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet ");
                        //the json we get back is a json object, which is a tweet model
                        // try-catch for Json exception
                        try {
                        Tweet tweet = Tweet.fromJSON(json.jsonObject);
                            Log.i(TAG, "published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            //make tweet model into a parceable object
                            //handled by intent.putExtra
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            //set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            //closes the activity, pass data to parent
                            finish();
                    } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet ", throwable);
                    }
                });
            }
        });

    }
}