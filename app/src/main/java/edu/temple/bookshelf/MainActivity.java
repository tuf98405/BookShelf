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
    book_list bookListFragment;
    BookList bookList;

    BookDetailsFragment bookDetailsFragment;
    boolean exists;
    boolean selected;
    int prevpos;

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


        if (selected){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, bookDetailsFragment.newInstance(bookList.get(prevpos)))
                    .commit();
        }else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, bookListFragment.newInstance(bookList))
                    .commit();
            selected = false;
        }


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
        selected = true;
        prevpos = position;
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
