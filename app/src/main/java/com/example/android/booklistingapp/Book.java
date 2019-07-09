package com.example.android.booklistingapp;

/**
 * Created by m_s info on 24/02/2018.
 */

public class Book {

    /**
     * Title of the Book
     */
    private String title;
    /**
     * Author of the Book
     */
    private String author;
    /**
     * Language of the Book
     */
    private String language;
    /**
     * URL of the Book
     */
    private String bookUrl;
    /**
     * ImageURL of the Book
     */
    private String imgUrl;
    /**
     * Price of the Book
     */
    private double price;
    /**
     * Currency of the Price
     */
    private String currency;

    /**
     * @param title - (String) name of the book i.e.: "The Power of Habit"
     * @param author - (String) name of author i.e.: "Charles Duhigg"
     * @param imgUrl - (String) URL address of an image cover i.e.: "http://books.google.com/books/(...)"
     * @param language - (String) country code i.e.: "AR"
     * @param bookUrl  - (String) url for buying page on Google Play
     */

    public Book(String title, String author, String language, String bookUrl, String imgUrl) {
        this.title = title;
        this.author = author;
        this.language = language;
        this.bookUrl = bookUrl;
        this.imgUrl = imgUrl;
    }

    // Defining the Getters for attributes, so we can access them
    public String getBookUrl() {
        return bookUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getLanguage() {
        return language;
    }

}
