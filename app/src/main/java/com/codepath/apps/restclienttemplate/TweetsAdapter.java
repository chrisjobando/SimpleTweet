package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.parceler.Parcels;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get data at position
        Tweet tweet = tweets.get(position);

        // Bind tweet with the ViewHolder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements in the Recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll((tweetList));
        notifyDataSetChanged();
    }



    // Define a ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        ImageView ivProfileImage;
        TextView tvTime;
        TextView tvScreenName;
        ImageView ivVerify;
        TextView tvBody;

        ImageView ivRetweet;
        ImageView ivFavorite;
        TextView tvRetweets;
        TextView tvFavorites;

        boolean favorited;
        boolean retweeted;

        public ViewHolder(@Nonnull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.tweetContainer);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivVerify = itemView.findViewById(R.id.ivVerify);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            tvRetweets = itemView.findViewById(R.id.tvRetweets);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvFavorites = itemView.findViewById(R.id.tvFavorites);
        }

        public void bind(final Tweet tweet) {
            favorited = tweet.favorited;
            retweeted = tweet.retweeted;

            tvScreenName.setText(tweet.user.name);
            tvTime.setText(tweet.time);
            tvRetweets.setText(tweet.retweetCount);
            tvFavorites.setText(tweet.favoriteCount);

            SpannableString body = new SpannableString(tweet.body);

            if (tweet.user.verified) {
                ivVerify.setVisibility(View.VISIBLE);
            } else {
                ivVerify.setVisibility(View.INVISIBLE);
            }

            if (tweet.hasLinks || tweet.hasMedia) {
                Matcher matcher = Pattern.compile("((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE).matcher(body);
                while(matcher.find()) {
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

            if (tweet.hasMentions) {
                Matcher matcher = Pattern.compile("@([A-Za-z0-9_-]+)").matcher(body);
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
                    TwitterClient client = TwitterApp.getRestClient(context);

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
                                        tvRetweets.setText(context.getResources()
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
                                        tvRetweets.setText(context.getResources()
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
                    TwitterClient client = TwitterApp.getRestClient(context);

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
                                        tvFavorites.setText(context.getResources()
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
                                        tvFavorites.setText(context.getResources()
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

            Glide.with(context).load(tweet.user.profileImageUrl)
            .transform(new RoundedCornersTransformation(100, 0))
            .into(ivProfileImage);


            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, TweetDetailActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);

                }
            });
        }
    }
}
