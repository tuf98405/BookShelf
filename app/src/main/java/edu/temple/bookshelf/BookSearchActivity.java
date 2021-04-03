package edu.temple.bookshelf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;

import java.util.concurrent.ExecutionException;

public class BookSearchActivity extends AppCompatActivity {

    String returned = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_book_search);



        //Cancel button
        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Search button
        Button search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BookFetch asyncTask = (BookFetch) new BookFetch(new BookFetch.AsyncResponse(){
                    @Override
                    public void processFinish(String array) {
                        returned = array;
                    }
                }).execute();

                try {
                    asyncTask.get();
                    Intent resulted = new Intent();
                    resulted.putExtra("jsonArray", returned);
                    setResult(RESULT_OK, resulted);
                    finish();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}