package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookList bookList = new BookList();

        String[]titles = getResources().getStringArray(R.array.titles);
        String[]authors = getResources().getStringArray(R.array.authors);

        for (int i = 0; i < 10; i++){
            bookList.add(new Book(titles[i], authors[i]));
        }










    }
}