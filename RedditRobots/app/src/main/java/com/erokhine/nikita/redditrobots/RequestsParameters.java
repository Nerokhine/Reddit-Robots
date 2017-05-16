package com.erokhine.nikita.redditrobots;


/**
 * Created by Nikita on 2016-07-16.
 */
public class RequestsParameters {
    DisplayMessageActivity activity;
    String url;
    Header[] headers;
    String payload;
    String type;
    String method; //method to invoke after execution is done. If the empty string, no method is called

    RequestsParameters(DisplayMessageActivity activity, String url, Header[] headers, String payload, String type, String method) {
        this.activity = activity;
        this.url = url;
        this.headers = headers;
        this.payload = payload;
        this.type = type;
        this.method = method;
    }
}
