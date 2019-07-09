package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m_s info on 27/02/2018.
 */

public class BookUtils {

    private static final String LOG_TAG = BookUtils.class.getSimpleName();

    // create a private constructor because no one should create a BookUtils Object
    private BookUtils() {

    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first book from the input bookJSON string.
     */
    private static List<Book> extractBookInfoFromJSON (String bookJSON) {
        // if the string is empty return early
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();


        // Try to parse the JSON Response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {
            // create a JSON object from the JSON Response string
            JSONObject baseJsonObject = new JSONObject(bookJSON);

            /* This What we need to extract from the JSON Response
            Title, Author, Image URL, Book URL, Price ,Currency , Location/Language */

            // Extracting the JSONArray associated with the key called "items"
            // which represents a list of items (or Books)
            JSONArray bookJSONarray = baseJsonObject.getJSONArray("items");

            for(int i = 0; i < bookJSONarray.length(); i++) {

                // Extract an item (a book) from the JSON Array
                JSONObject aBook = bookJSONarray.getJSONObject(i);

                // Extract the Volume Info
                JSONObject volumeInfo = aBook.getJSONObject("volumeInfo");

                // Extract the Title from the Volume Info
                String bookTitle = volumeInfo.getString("title");
                // Extract the Authors from the Volume Info
                String author;

                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    // Check JSONArray Returns true if this object has no mapping for name or if it has a mapping whose value is NULL

                    if(!volumeInfo.isNull("authors")) {
                        // Get 1st Element of the JSON Array
                        author = (String) authors.get(0);
                    } else {
                        author = "Unknown Author";
                    }
                } else {
                    author = "Missing Information of Authors";
                }

                // For a Given Book, Extract the imageLinks JSON Object
                // So we can extract from it the smallThumbnail String
                // Which contains the image url
                JSONObject imgLinks = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imgLinks.getString("smallThumbnail");

                // Extract the Book URL(or Buy URL)
                JSONObject salesInfo = aBook.getJSONObject("saleInfo");
                String buyLink = salesInfo.getString("buyLink");

                // Extract the Book Language
                String bookLang = volumeInfo.getString("language");

                // Create a new Book Object with Title, Author, Image URL, Book URL
                // , Price ,Currency , Location/Language using the JSON Response;
                Book bookItem = new Book(bookTitle,author,bookLang,buyLink,imageUrl);

                // Add the Book Item to the list
                books.add(bookItem);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem Parsing the book JSON Results", e);
        }

        // return the list of Books
        return books;
    }

    // Query the Google Books dataset and return a list of Books
    public static List<Book> fetchBookData(String requestURL) {

        try{
            Thread.sleep(10000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        // create a URL object
        URL url = createURL(requestURL);

        // Perform a HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPrequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP Request");
        }

        // Extract the needed fields from the JSON Response and create a list of Books
        List<Book> result = extractBookInfoFromJSON(jsonResponse);

        return result;
    }

    // make an HTTP request from the given URL and return a String as a response
    private static String makeHTTPrequest(URL url) throws IOException {
        String jsonResponse = "";

        // if the url is null return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000 /*Milliseconds*/);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            // if the request was successful (response code = 200)
            // then read the input stream and parse the response
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readResponseFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error response code" + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Books data from the server" , e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if(inputStream != null) {

                //  Closing the inputStream may throw an IOException
                // that why the makeURLrequest throw an IOException

                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // convert the InputStream response to a String which contains the json response
    private static String readResponseFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }

        return output.toString();
    }

    // format the given URL string to URL Object
    private static URL createURL(String url) {
        URL finalUrl = null;
        try {
            finalUrl = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error when forming the URL", e);
        }
        return finalUrl;
    }


}
