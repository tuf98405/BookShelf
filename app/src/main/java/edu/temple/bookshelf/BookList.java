package edu.temple.bookshelf;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BookList implements Parcelable {
    ArrayList<Book> bookList;
    public BookList(){
        bookList = new ArrayList<Book>();
    }

    protected BookList(Parcel in) {
        bookList = in.createTypedArrayList(Book.CREATOR);
    }

    public static final Creator<BookList> CREATOR = new Creator<BookList>() {
        @Override
        public BookList createFromParcel(Parcel in) {
            return new BookList(in);
        }

        @Override
        public BookList[] newArray(int size) {
            return new BookList[size];
        }
    };

    public void add(Book book){
        bookList.add(book);
    }

    public void remove(Book book){
        bookList.remove(book);
    }

    public Book get(int index){
        return bookList.get(index);
    }

    public int size(){
        return bookList.size();
    }

    public ArrayList<Book> getList(){
        return bookList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(bookList);
    }
}
