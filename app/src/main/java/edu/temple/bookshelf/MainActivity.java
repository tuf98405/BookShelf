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
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements book_list.BookListFragmentInterface{

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    book_list bookListFragment;
    BookList bookList = new BookList();

    BookDetailsFragment bookDetailsFragment;
    boolean exists;
    static int prevPos;
    String jsonString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //This is for populating the list with the books
        if (bookList != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container1, bookListFragment.newInstance(bookList))
                    .commit();
        }


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
                jsonString = data.getStringExtra("jsonArray");
                String userInput = data.getStringExtra("userInput");
                System.out.println(jsonString);
                System.out.println(userInput);

                //Add the JSON String to bookList
                try {
                    JSONArray resultArray = new JSONArray(jsonString);

                    //Empty bookList and repopulate with new search results
                    try {
                        bookList.emptyList();
                    }
                    catch (Exception e){};

                    for (int i = 0; i < resultArray.length(); i++){
                        JSONObject object = (JSONObject) resultArray.get(i);
                        if (object.get("title").toString().contains(userInput) || object.get("author").toString().contains(userInput)){
                            System.out.println((String)object.get("title"));
                            System.out.println((String)object.get("author"));
                            System.out.println((String)object.get("id"));
                            System.out.println((String)object.get("cover_url"));
                            bookList.add(new Book((String)object.get("title"), (String)object.get("author"), Integer.parseInt(object.get("id").toString()), (String)object.get("cover_url")));
                        }
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container1, bookListFragment.newInstance(bookList))
                            .commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("JSON String was Null");
                }

            }
        }
    }





    @Override
    public void itemClicked(int position){
        System.out.println(exists);
        if (!exists){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(bookList.get(position)))
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container2, BookDetailsFragment.newInstance(bookList.get(position)))
                    .commit();
        }
        prevPos = position;
    }
}
