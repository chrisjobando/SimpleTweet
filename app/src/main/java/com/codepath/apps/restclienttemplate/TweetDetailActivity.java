package com.codepath.apps.restclienttemplate;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {
    ImageView ivProfileImage;
    TextView tvTime;
    TextView tvScreenName;
    ImageView ivVerify;
    TextView tvBody;
    ImageView ivMedia;

    ImageView ivRetweet;
    ImageView ivFavorite;
    TextView tvRetweets;
    TextView tvFavorites;

    boolean favorited;
    boolean retweeted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweetdetail);

        final Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        favorited = tweet.favorited;
        retweeted = tweet.retweeted;

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvTime = findViewById(R.id.tvTime);
        tvBody = findViewById(R.id.tvBody);
        ivMedia = findViewById(R.id.ivMedia);
        tvScreenName = findViewById(R.id.tvScreenName);
        ivVerify = findViewById(R.id.ivVerify);
        ivRetweet = findViewById(R.id.ivRetweet);
        tvRetweets = findViewById(R.id.tvRetweets);
        ivFavorite = findViewById(R.id.ivFavorite);
        tvFavorites = findViewById(R.id.tvFavorites);

        tvScreenName.setText(tweet.user.name);
        tvTime.setText(tweet.time);
        tvRetweets.setText(tweet.retweetCount);
        tvFavorites.setText(tweet.favoriteCount);

        if (tweet.user.verified) {
            ivVerify.setVisibility(View.VISIBLE);
        } else {
            ivVerify.setVisibility(View.INVISIBLE);
        }

        SpannableString body = new SpannableString(tweet.body);

        if (tweet.hasLinks || tweet.hasMedia) {
            Matcher matcher = Pattern.compile("((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(body);
            while(matcher.find()) {
                body.setSpan(new ForegroundColorSpan(
                                Color.parseColor("#13b0ee")), matcher.start(),
                        matcher.end(), 0);
            }
        }

        if (tweet.hasMedia) {
            ivMedia.setVisibility(View.VISIBLE);
            DrawableImageViewTarget ivTarget = new DrawableImageViewTarget(ivMedia);
            Glide.with(this)
                .load(tweet.mediaUrl)
                .override(tweet.mediaW, tweet.mediaH)
                .centerCrop()
                .transform(new RoundedCornersTransformation(30, 30))
                .into(ivTarget);
        }

        if (tweet.hasMentions) {
            Matcher matcher = Pattern.compile("@([A-Za-z0-9_-]+)").matcher(body);
            while (matcher.find()) {
                body.setSpan(new ForegroundColorSpan(
                                Color.parseColor("#13b0ee")), matcher.start(),
                        matcher.end(), 0);
            }
        }

        if (tweet.hasHashTags) {
            Matcher matcher = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(body);
            while (matcher.find()) {
                body.setSpan(new ForegroundColorSpan(
                                Color.parseColor("#13b0ee")), matcher.start(),
                        matcher.end(), 0);
            }
        }

        tvBody.setText(body);

        if (retweeted) {
            DrawableCompat.setTint(ivRetweet.getDrawable(), Color.WHITE);
        }
        if (favorited) {
            DrawableCompat.setTint(ivFavorite.getDrawable(), Color.WHITE);
        }

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterClient client = TwitterApp.getRestClient(getApplicationContext());

                if (!retweeted) {
                    DrawableCompat.setTint(ivRetweet.getDrawable(), Color.WHITE);
                    client.retweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess for retweeting");
                            try {
                                int num = Integer.parseInt(tvRetweets.getText().toString());
                                if (num < 1000) {
                                    tvRetweets.setText(String.valueOf(++num));
                                } else {
                                    tvRetweets.setText(getApplicationContext().getResources()
                                            .getString(R.string.retweets));
                                }
                            } catch (NumberFormatException e) {
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response,
                                              Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure for retweeting");
                        }
                    }, tweet.id);
                } else {
                    DrawableCompat.setTint(ivRetweet.getDrawable(),
                            Color.parseColor("#9eb3c7"));
                    client.unretweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess for unretweeting");
                            try {
                                int num = Integer.parseInt(tvRetweets.getText().toString());
                                if (num < 1000) {
                                    tvRetweets.setText(String.valueOf(--num));
                                } else {
                                    tvRetweets.setText(getApplicationContext().getResources()
                                            .getString(R.string.retweets));
                                }
                            } catch (NumberFormatException e) {
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response,
                                              Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure for unretweeting");
                        }
                    }, tweet.id);
                }
                retweeted = !retweeted;
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterClient client = TwitterApp.getRestClient(getApplicationContext());

                if (!favorited) {
                    DrawableCompat.setTint(ivFavorite.getDrawable(), Color.WHITE);
                    client.favoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess for favoriting");
                            try {
                                int num = Integer.parseInt(tvFavorites.getText().toString());
                                if (num < 1000) {
                                    tvFavorites.setText(String.valueOf(++num));
                                } else {
                                    tvFavorites.setText(getApplicationContext().getResources()
                                            .getString(R.string.favorites));
                                }
                            } catch (NumberFormatException e) {
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response,
                                              Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure for favoriting");
                        }
                    }, tweet.id);
                } else {
                    DrawableCompat.setTint(ivFavorite.getDrawable(),
                            Color.parseColor("#9eb3c7"));
                    client.unfavoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("TweetsAdapter", "onSuccess for unfavoriting");
                            try {
                                int num = Integer.parseInt(tvFavorites.getText().toString());
                                if (num < 1000) {
                                    tvFavorites.setText(String.valueOf(--num));
                                } else {
                                    tvFavorites.setText(getApplicationContext().getResources()
                                            .getString(R.string.favorites));
                                }
                            } catch (NumberFormatException e) {
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response,
                                              Throwable throwable) {
                            Log.e("TweetsAdapter", "onFailure for unfavoriting");
                        }
                    }, tweet.id);
                }
                favorited = !favorited;
            }
        });

        Glide.with(getApplicationContext()).load(tweet.user.profileImageUrl)
            .transform(new RoundedCornersTransformation(100, 0))
            .into(ivProfileImage);
    }
}
