package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements book_list.BookListFragmentInterface{

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    book_list booklist;
    BookList bookList;

    BookDetailsFragment bookDetailsFragment;
    boolean exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exists = findViewById(R.id.container2) != null;

        bookList = new BookList();

        String[] titles = getResources().getStringArray(R.array.titles);
        String[] authors = getResources().getStringArray(R.array.authors);

        for (int i = 0; i < 10; i++) {
            bookList.add(new Book(titles[i], authors[i]));
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, booklist.newInstance(bookList))
                .commit();

        if (exists) {
            bookDetailsFragment = new BookDetailsFragment();


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container2, bookDetailsFragment)
                    .commit();
        }

    }

    @Override
    public void itemClicked(int position){
        if (!exists){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, BookDetailsFragment.newInstance(bookList.get(position)))
                    .addToBackStack(null)
                    .commit();
        } else {
            bookDetailsFragment.changeBook(bookList.get(position));
        }
    }
}
