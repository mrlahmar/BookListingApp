package com.example.android.booklistingapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by m_s info on 24/02/2018.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    public BookAdapter(@NonNull Context context, @NonNull List<Book> objects) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent,false);
        }

        // find the current Book
        Book currentBook = getItem(position);
        Log.i(LOG_TAG, "Item Position" + position);

        // Setting the Cover(Image) of the Book
        ImageView currentBookImage = listItemView.findViewById(R.id.book_cover);
        Picasso.get().load(currentBook.getImgUrl()).into(currentBookImage);

        // Setting the Title of the Book
        TextView currentBookTitle = (TextView) listItemView.findViewById(R.id.book_title);
        currentBookTitle.setText(currentBook.getTitle());

        // Setting the Author of the Book
        TextView currentBookAuthor = (TextView) listItemView.findViewById(R.id.author);
        currentBookAuthor.setText(currentBook.getAuthor());

        // Setting the Language of the Book
        TextView currentBookLanguage = (TextView) listItemView.findViewById(R.id.language);
        currentBookLanguage.setText(currentBook.getLanguage());

        // We use Log to say that the ListView has been returned successfully
        Log.i(LOG_TAG, "ListView has been returned");

        return listItemView;
    }

}
