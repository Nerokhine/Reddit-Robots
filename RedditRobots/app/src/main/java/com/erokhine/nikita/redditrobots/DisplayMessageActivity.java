package com.erokhine.nikita.redditrobots;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayMessageActivity extends AppCompatActivity {
    //figure out a better way of doing this later
    private String auth;
    private String userAgent = "Oh My!, How do you do!";
    private String web = "https://oauth.reddit.com";
    public static String subreddit = "";
    private String sort = "top";
    private String userName = "";
    private String password = "";
    private String app_id = "3P96ILUVDWLv-Q";
    private String secret = "rAHXPWXkNe8UX6zC4X68Op9qw60";
    private ArrayList<Comment> comments;
    public String linkID = "";
    public int numActiveThreads;
    public boolean finished;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] mobileArray = {};
    private boolean active;
    private String match;
    private String reply;
    private boolean replyOn;
    public boolean stop;
    public int stopTime;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.numActiveThreads = 0;
        this.finished = false;
        this.active = true;

        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 & getIntent().getExtras() == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_display_message);


        Intent intent = getIntent();
        this.match = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        this.reply = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
        this.userName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE4);
        this.password = intent.getStringExtra(MainActivity.EXTRA_MESSAGE5);
        if (intent.getStringExtra(MainActivity.EXTRA_MESSAGE3).equals("true")) {
            this.replyOn = true;
        } else {
            this.replyOn = false;
        }

        if (this.match.equals("")) {
            this.match = ".*";
        }

        TextView textView = (TextView) findViewById(R.id.textDisplay);
        textView.setText(this.match);


        this.comments = new ArrayList<Comment>();

        //encode secret
        String pass = app_id + ":" + secret;
        byte[] data = new byte[0];
        try {
            data = pass.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encoding = Base64.encodeToString(data, Base64.DEFAULT);


        Header[] headers = new Header[2];
        headers[0] = new Header("User-Agent", this.userAgent);
        headers[1] = new Header("Authorization", "Basic " + encoding);

        RequestsParameters parameters = new RequestsParameters(
                DisplayMessageActivity.this,
                "https://www.reddit.com/api/v1/access_token",
                headers,
                "grant_type=password&username=" + this.userName + "&password=" + this.password,
                "post",
                "updateText");
        this.numActiveThreads += 1;
        new RedditRequests().execute(parameters);
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(numActiveThreads);
                                if ((numActiveThreads == 0 && finished == true) || !active) {
                                    signalEnd();
                                    interrupt();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    protected void updateText(String message) throws JSONException {
        TextView textView = (TextView) findViewById(R.id.textDisplay);
        JSONObject obj = new JSONObject(message);
        //JSONArray arr = obj.getJSONArray("number");
        this.auth = "bearer " + obj.getString("access_token");
        textView.setText(this.auth);


        Header[] headers = new Header[2];
        headers[0] = new Header("User-Agent", this.userAgent);
        headers[1] = new Header("Authorization", this.auth);

        RequestsParameters parameters = new RequestsParameters(
                DisplayMessageActivity.this,
                this.web + "/r/" + this.subreddit + '/' + this.sort,
                headers,
                "",
                "get",
                "updateText2");
        this.numActiveThreads += 1;
        new RedditRequests().execute(parameters);


    }


    protected void updateText2(String message) throws JSONException {
        TextView textView = (TextView) findViewById(R.id.textDisplay);
        JSONObject obj = new JSONObject(message);
        //JSONArray arr = obj.getJSONArray("data");
        //this.auth = "bearer " + obj.getString("access_token");

        //threadID of the first thread of a subreddit

        String threadID = obj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("id");

        Header[] headers = new Header[2];
        headers[0] = new Header("User-Agent", this.userAgent);
        headers[1] = new Header("Authorization", this.auth);

        RequestsParameters parameters = new RequestsParameters(
                DisplayMessageActivity.this,
                web + "/r/" + subreddit + "/comments/article?&showmore=true&article=" + threadID,
                headers,
                "",
                "get",
                "parseCommentThreadMain");

        textView.setText(threadID);
        this.numActiveThreads += 1;
        new RedditRequests().execute(parameters);


    }

    protected void parseCommentThread(String message) throws JSONException {
        //TextView textView = (TextView) findViewById(R.id.textDisplay);
        //textView.setText(message);

        JSONArray arr = new JSONArray(message);

        for (int x = 0; x < arr.length(); x++) {
            if (arr.getJSONObject(x).getString("kind").equals("t1")) {
                Comment comment = new Comment
                        (arr.getJSONObject(x).getJSONObject("data").getString("id"),
                                arr.getJSONObject(x).getJSONObject("data").getString("body"));
                this.comments.add(comment);
                //comments[children['data']['id']] = (children['data']['body']).encode('utf-8')
                //System.out.println(arr.getJSONObject(x).getJSONObject("data").getString("body"));
                //System.out.println(arr.getJSONObject(x).getJSONObject("data").getJSONObject("replies").getJSONObject("data").getString("children"));
                String replies = arr.getJSONObject(x).getJSONObject("data").getString("replies");
                if (!replies.equals("")) {
                    JSONObject obj = new JSONObject(replies);
                    parseCommentThread(obj.getJSONObject("data").getString("children"));
                }
            } else if (arr.getJSONObject(x).getString("kind").equals("more")) {
                //ArrayList<String> idArray = new ArrayList<String>();
                String idArray = "";
                JSONArray arr2 = arr.getJSONObject(x).getJSONObject("data").getJSONArray("children");
                for (int y = 0; y < arr2.length(); y++) {
                    idArray = idArray + arr2.getString(y) + ",";
                    if (y % 100 == 0 && y != 0) {
                        idArray = idArray.substring(0, idArray.length() - 1);
                        System.out.println(idArray);

                        Header[] headers = new Header[2];
                        headers[0] = new Header("User-Agent", this.userAgent);
                        headers[1] = new Header("Authorization", this.auth);

                        RequestsParameters parameters = new RequestsParameters(
                                DisplayMessageActivity.this,
                                this.web + "/api/morechildren?api_type=json&link_id=" + this.linkID + "&children=" + idArray,
                                headers,
                                "",
                                "post",
                                "parseCommentThreadMore");

                        this.numActiveThreads += 1;
                        new RedditRequests().execute(parameters);
                        //res = request(web + '/api/morechildren?api_type=json&link_id=' + link_id + '&children=' + superArray, '', headers, 'post')
                        idArray = "";

                    }
                }

                if (idArray != "") {
                    idArray = idArray.substring(0, idArray.length() - 1);
                    System.out.println(idArray);
                    Header[] headers = new Header[2];
                    headers[0] = new Header("User-Agent", this.userAgent);
                    headers[1] = new Header("Authorization", this.auth);

                    RequestsParameters parameters = new RequestsParameters(
                            DisplayMessageActivity.this,
                            this.web + "/api/morechildren?api_type=json&link_id=" + this.linkID + "&children=" + idArray,
                            headers,
                            "",
                            "post",
                            "parseCommentThreadMore");
                    this.numActiveThreads += 1;
                    new RedditRequests().execute(parameters);
                }


                //Joiner.on(",").join(idArray);
                //for ID in children['data']['children']:
                //empty = False
                //idArray.append(str(ID))
            }

        }
    }

    protected void updateTextView() {
        /*TextView textView = (TextView) findViewById(R.id.textDisplay);
        String result = "";
        for(int x = 0; x < this.comments.size(); x++) {
            result = result + "Comment ID: " + this.comments.get(x).id + "\n"
                            + "Comment Body: " + this.comments.get(x).body + "\n\n";
        }

        textView.setText(result);
        System.out.println("Number of comments: " + this.comments.size());*/
    }

    protected void signalEnd() {
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTextColor(getResources().getColor(R.color.primary_dark_material_light));
        ArrayList<String> mobileArrayTemp = new ArrayList<String>();
        int size = 0;
        for (int x = 0; x < comments.size(); x++) {
            //regular expression
            Pattern p = Pattern.compile(this.match);
            Matcher m = p.matcher(comments.get(x).body);
            if (m.matches()) {
                mobileArrayTemp.add(comments.get(x).body);

                //make post requests to matched comments
                if (this.replyOn) {
                    System.out.println("hi");
                    Header[] headers = new Header[2];
                    headers[0] = new Header("User-Agent", this.userAgent);
                    headers[1] = new Header("Authorization", this.auth);

                    RequestsParameters parameters = new RequestsParameters(
                            DisplayMessageActivity.this,
                            this.web + "/api/comment?parent=" + "t1_" + comments.get(x).id,
                            headers,
                            "&text=" + this.reply,
                            "post",
                            "");
                    this.numActiveThreads += 1;
                    new RedditRequests().execute(parameters);
                }
            }
        }
        String[] mobileArray = new String[mobileArrayTemp.size()];
        for (int x = 0; x < mobileArrayTemp.size(); x++) {
            mobileArray[x] = mobileArrayTemp.get(x);
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, mobileArray);

        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);
        System.out.println("Number of comments: " + this.comments.size());

        /*NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder)
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.shrek)
                        .setContentTitle("Reddit Robots")
                        .setContentText("Thread Parsing Complete");

        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        // Creates an explicit intent for an Activity in your app
        //Intent resultIntent = new Intent(this, DisplayMessageActivity.class);
        Intent resultIntent = this.getIntent();
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DisplayMessageActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_IMMUTABLE
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(123, mBuilder.build());*/
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, DisplayMessageActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder)
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.shrek)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentIntent(intent)
                                .setPriority(5) //private static final PRIORITY_HIGH = 5;
                                .setContentText("Thread Parsing Complete")
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }

    public void onBackPressed() {
        this.active = false;
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DisplayMessage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.erokhine.nikita.redditrobots/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DisplayMessage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.erokhine.nikita.redditrobots/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
