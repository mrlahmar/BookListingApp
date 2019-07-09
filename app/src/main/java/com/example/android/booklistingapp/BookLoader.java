package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /** Tag for LOG messages */
    private static final String LOG_TAG = BookLoader.class.getName();

    /** Query URL */
    private String bookUrl;

    /**
     * constructs a new {@link BookLoader}
     *
     * @param context of the activity
     * @param url a URL to load data from
     */
    public BookLoader(Context context, String url) {
        super(context);
        bookUrl = url;

        Log.i(LOG_TAG, "Loader !");
    }

    @Override
    protected void onStartLoading() {
        forceLoad();

        Log.i("On start loading ", ": Force Loaded !");
    }

    /**
     * This is on a background thread
     */

    @Override
    public List<Book> loadInBackground() {

        // return early if the URL is null
        if (bookUrl == null) {
            return null;
        }

        // perform the network request, parse the response and extract a list of books
        List<Book> result = BookUtils.fetchBookData(bookUrl);
        Log.i(LOG_TAG, ": Loaded in background");
        return result;
    }
}
