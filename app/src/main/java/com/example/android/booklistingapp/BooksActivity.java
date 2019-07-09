package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class BooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Tag for Log Messages
     */
    private static final String LOG_TAG = BooksActivity.class.getName();

    /** Constant value for the Book Loader ID , we can choose an integer
     *  This is really comes to play if you're using multiple loaders
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * URL for Book data from the Google Books API
     */
    private String GOOGLE_BOOKS_REQUEST_URL = "";

    /**
     * Adapter for the list of Books
     */
    private BookAdapter bookAdapter;

    /**
     * Search Field
     */
    private SearchView mSearchViewField;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Circle ProgressBar is displayed when the activity is loading data from the internet
     */
    private View circleProgressBar;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        final ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        /**
         * At the beginning check the connection with internet and save result to (boolean) variable isConnected
         * Checking if network is available
         * If TRUE - work with LoaderManager
         * If FALSE - hide loading spinner and show emptyStateTextView
         */
        checkConnection(connectivityManager);

        // Link the ListView to JAVA Code
        ListView booksListView = (ListView) findViewById(R.id.list);

        // create a new adapter that takes an empty list of Books as input
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        booksListView.setAdapter(bookAdapter);

        //Search Button
        Button mSearchButton = (Button) findViewById(R.id.search_button);

        //Search field
        mSearchViewField = (SearchView) findViewById(R.id.search_view_field);
        mSearchViewField.onActionViewExpanded();
        mSearchViewField.setIconified(true);
        mSearchViewField.setQueryHint("Enter a book title");

        /**
         * Setting the empty view
         */
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyStateTextView);

        /**
         * Circle Progress
         * */
        circleProgressBar = findViewById(R.id.loading_spinner);


        // if there is a network connection, fetch data
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with the loaders
            LoaderManager loaderManager = getLoaderManager();


            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID,null,this);
        }
        else {
            // Otherwise, display error
            // First, hide loading indicator so error messages will be visible
            circleProgressBar.setVisibility(View.GONE);

            // update empty state with no connection error message
            mEmptyStateTextView = findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Set an item click listener on the Search Button, which sends a request to
        // Google Books API based on value from Search View
        mSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Check if internet connection status
                checkConnection(connectivityManager);

                if (isConnected) {

                    // Update URL and restart loader to displaying new result of searching
                    updateQueryUrl(mSearchViewField.getQuery().toString());
                    restartLoader();
                    Log.i(LOG_TAG, "Search Box Value" + mSearchViewField.getQuery().toString());
                } else {
                    // Clear the adapter from previous book data
                    bookAdapter.clear();

                    // Set mEmptyStateTextView visible
                    mEmptyStateTextView.setVisibility(View.VISIBLE);

                    // and display the error message "No Internet Connection"
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });

        // Set an item click listener on the ListView, which sends an intent to an app
        // to open the Google Play Books app with more information about the selected book.
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = bookAdapter.getItem(position);

                // Convert the String Url to URI object (to pass into the internet constructor)
                Uri bookUri = Uri.parse(currentBook.getBookUrl());

                // Create a new intent to view buy the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.i("There is no instance",": Created new one at the beginning");

        // create a new loader for the given url
        SearchView mSearchViewField = findViewById(R.id.search_view_field);
        updateQueryUrl(mSearchViewField.getQuery().toString());
        return new BookLoader(this, GOOGLE_BOOKS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> book) {

        // set the visibility of the Progress Bar to GONE
        View circleProgressBar = findViewById(R.id.loading_spinner);
        circleProgressBar.setVisibility(View.GONE);

        // set empty state text to display "No books found"
        mEmptyStateTextView.setText(R.string.no_books);

        // clear the adapter from previous data
        bookAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (book != null && !book.isEmpty()) {
            bookAdapter.addAll(book);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        bookAdapter.clear();
    }

    public void updateQueryUrl(String searchValue) {
        if(searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("https://www.googleapis.com/books/v1/volumes?q=")
                .append(searchValue)
                .append("&filter=paid-ebooks&maxResults=30");
        this.GOOGLE_BOOKS_REQUEST_URL = sb.toString();
    }

    private void restartLoader() {
        mEmptyStateTextView.setVisibility(View.GONE);
        mSearchViewField.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER_ID,null,this);
    }
    private void checkConnection(ConnectivityManager connectivityManager) {
        // Status of Internet Connection

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = networkInfo != null && networkInfo.isConnected();
    }
}
