package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    book_list booklist;

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

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,booklist.newInstance(bookList))
                .commit();












    }
}