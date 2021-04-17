package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements book_list.BookListFragmentInterface, ControlFragment.onDataPass{

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    book_list bookListFragment;
    static BookList bookList = new BookList();

    // Changing Views and such
    BookDetailsFragment bookDetailsFragment;
    boolean exists;
    static boolean flag;
    static int prevPos;
    String jsonString = "";

    // Service Variables
    private AudiobookService.MediaControlBinder AudioService;
    private MainActivityViewModel mViewModel;

    // Flags for Button Presses from Control Fragment
    boolean playPress;
    boolean pausePress;
    boolean stopPress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to Audiobook Service
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mViewModel.getBinder().observe(this, new Observer<AudiobookService.MediaControlBinder>() {
            @Override
            public void onChanged(AudiobookService.MediaControlBinder mediaControlBinder) {
                if (mediaControlBinder != null){
                    AudioService = mediaControlBinder;
                }
                else{
                    AudioService = null;
                }
            }
        });


        // Just inserts the Audio Controls to the audioControlLayout everytime
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.audioControlLayout, ControlFragment.class, null)
                .commit();


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

        //This is for populating the list with the books
        if (bookList != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.listLayout, bookListFragment.newInstance(bookList))
                    .commit();
        }

        //Checks to see if user had previously selected a book
        if (flag = true && bookList.size() != 0 && !exists){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.listLayout, BookDetailsFragment.newInstance(bookList.get(prevPos)))
                    .addToBackStack(null)
                    .commit();
        }

        //Creates second view if in landscape. Has bug with prevPos
        if (exists && bookList.size() != 0) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container2, BookDetailsFragment.newInstance(bookList.get(prevPos)))
                    .commit();
        }
    }

    //Get the JSON String Result from the JSON Request to URL
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                jsonString = data.getStringExtra("jsonArray");
                String userInput = data.getStringExtra("userInput").toLowerCase();

                //Add the JSON String to bookList
                try {
                    JSONArray resultArray = new JSONArray(jsonString);

                    //Empty bookList and repopulate with new search results
                    try {
                        bookList.emptyList();
                    }
                    catch (Exception e){};

                    //Checks if user input is blank. if so, returns all book objects
                    if (userInput == "") {
                        for (int i = 0; i < resultArray.length(); i++){
                            JSONObject object = (JSONObject) resultArray.get(i);
                            if (object.get("title").toString().toLowerCase().contains(userInput) || object.get("author").toString().toLowerCase().contains(userInput)){
                                bookList.add(new Book((String)object.get("title"), (String)object.get("author"), Integer.parseInt(object.get("id").toString()), (String)object.get("cover_url")));
                            }
                        }
                    } else{
                        for (int i = 0; i < resultArray.length(); i++){
                            JSONObject object = (JSONObject) resultArray.get(i);
                            if (object.get("title").toString().toLowerCase().contains(userInput) || object.get("author").toString().toLowerCase().contains(userInput)){
                                bookList.add(new Book((String)object.get("title"), (String)object.get("author"), Integer.parseInt(object.get("id").toString()), (String)object.get("cover_url")));
                            }
                        }
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.listLayout, bookListFragment.newInstance(bookList))
                            .commit();
                    flag = false;
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
                    .replace(R.id.listLayout, BookDetailsFragment.newInstance(bookList.get(position)))
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container2, BookDetailsFragment.newInstance(bookList.get(position)))
                    .commit();
        }
        prevPos = position;
        flag = true;
    }




    private void playBook(int id){
        if (AudioService!= null){
            if (AudioService.isPlaying() != true){
                AudioService.play(id);
            }

            String headerString = bookList.get(prevPos).getTitle() + " by " + bookList.get(prevPos).getAuthor();
            Intent intent = new Intent("PLAYING_AUDIO");
            intent.putExtra("Header",headerString);
            sendBroadcast(intent);
        }
    }


    //Service Connection Methods
    @Override
    protected void onPause(){
        super.onPause();
        if(mViewModel.getBinder() != null){
            unbindService((mViewModel.getServiceConnection()));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        startService();
    }

    private void startService(){
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        startService(serviceIntent);

        bindService();
    }

    private void bindService(){
        Intent serviceIntent = new Intent(this, AudiobookService.class);
        bindService(serviceIntent, mViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDataPass(boolean pause, boolean play, boolean stop) {
        pausePress = pause;
        playPress = play;
        stopPress = stop;

        if (play == true) {
            playBook(bookList.get(prevPos).getId());
        }
        if (stop == true){
            AudioService.stop();
        }
        if (pause == true){
            AudioService.pause();
        }
    }
}
