package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements book_list.BookListFragmentInterface{

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    book_list bookListFragment;
    BookList bookList;

    BookDetailsFragment bookDetailsFragment;
    boolean exists;
    static int prevPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        String[] titles = getResources().getStringArray(R.array.titles);
        String[] authors = getResources().getStringArray(R.array.authors);

        for (int i = 0; i < 10; i++) {
            bookList.add(new Book(titles[i], authors[i]));
        }

        //This is for populating the list with the books
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, bookListFragment.newInstance(bookList))
                .commit();
         */

        //Implement Search Button
        Button searchbutton = findViewById(R.id.searchbutton);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookSearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        exists = findViewById(R.id.container2) != null;

        //bookList = new BookList();

        /*
        //Find out a method to make it so prevpos isnt defaulted to 0
        if (exists) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container2, BookDetailsFragment.newInstance(bookList.get(prevPos)))
                    .commit();
        }
         */
    }

    //Get the JSON String Result from the JSON Request to URL
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String jsonString = data.getStringExtra("jsonArray");
                System.out.println(jsonString);
            }
        }
    }





    @Override
    public void itemClicked(int position){
        System.out.println(exists);
        if (!exists){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, BookDetailsFragment.newInstance(bookList.get(position)))
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(bookList.get(position)))
                    .commit();
        }
        prevPos = position;
    }
}
