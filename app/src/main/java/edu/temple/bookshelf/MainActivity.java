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
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    // Variables for Persistent Data
    String fileDataName = "BookData.json";
    boolean dataFileFlag = false;
    MediaPlayer mp = new MediaPlayer();
    boolean playing = false;
    int playingBookId;
    static int prog;


    // JSON Readers Writers
    FileReader fileReader = null;
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    BufferedReader bufferedReader = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        //JSONObject jsonFile = new JSONObject(data);

        // Checks if Book Data exists
        System.out.println("Files in directory");
        String[] files = getApplicationContext().fileList();
        File file = new File(context.getFilesDir(), fileDataName);
        //file.delete();
        for(int i = 0; i < files.length; i++){
            System.out.println(files[i] + " and " + fileDataName);
            if(files[i].equals(fileDataName)){
                System.out.println("Book Data has been found");
                dataFileFlag = true;
            }
        }

        // If Book data exists, then use it. If not, then create it
        if (dataFileFlag != true){
            try {
                file.createNewFile();
                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("{}");
                bufferedWriter.close();

                System.out.println("Book Data file has been created");
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        else{
            System.out.println("Book Data already exists. Using File for App");

            JSONObject jsonFile = null;
            try {
                jsonFile = getBookJson(file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(jsonFile == null){
                System.out.println("Data file is null");
            }
            else{
                System.out.println("Data file has been populated");



                StringBuffer output = new StringBuffer();
                String response = "Asdf";

                try {
                    fileReader = new FileReader(file.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                bufferedReader = new BufferedReader(fileReader);

                String line = "";
                while (true){
                    try {
                        if (!((line = bufferedReader.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    output.append(line + "\n");
                }

                response = output.toString();
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(output);

                if (mp.isPlaying()){

                }

            }
        }







        if (null == savedInstanceState) {
            // Just inserts the Audio Controls to the audioControlLayout everytime
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.audioControlLayout, ControlFragment.class, null)
                    .commit();
        }

        // Connect to Audiobook Service
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mViewModel.getBinder().observe(this, new Observer<AudiobookService.MediaControlBinder>() {
            @Override
            public void onChanged(AudiobookService.MediaControlBinder mediaControlBinder) {
                if (mediaControlBinder != null){
                    AudioService = mediaControlBinder;
                    AudioService.setProgressHandler(handler);
                }
                else{
                    AudioService = null;
                }
            }
        });



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
                                bookList.add(new Book((String)object.get("title"), (String)object.get("author"), Integer.parseInt(object.get("id").toString()), (String)object.get("cover_url"), Integer.parseInt(object.get("duration").toString())));
                            }
                        }
                    } else{
                        for (int i = 0; i < resultArray.length(); i++){
                            JSONObject object = (JSONObject) resultArray.get(i);
                            if (object.get("title").toString().toLowerCase().contains(userInput) || object.get("author").toString().toLowerCase().contains(userInput)){
                                bookList.add(new Book((String)object.get("title"), (String)object.get("author"), Integer.parseInt(object.get("id").toString()), (String)object.get("cover_url"), Integer.parseInt(object.get("duration").toString())));
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

    // Sets bookdetails for selected book from searched list
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
        File file = new File(getApplicationContext().getFilesDir() + "/" + fileDataName);
        try {
            writeToBookJSON(file, "prevPos", String.valueOf(position));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // Plays Audiobook if nothing is playing
    private void playBook(int id){



        boolean foundAudioBook = false;
        String[] files = getApplicationContext().fileList();
        for(int i = 0; i < files.length; i++){
            if(files[i].equals(id + ".mp3")){
                System.out.println("AudioBook has been found");
                foundAudioBook = true;
            }
        }

        if (foundAudioBook != true) {
            System.out.println("AudioBook has not been found");
            System.out.println("Attempting to Download...");
            String path = getApplicationContext().getFilesDir().toString();
            new AudioBookDownload().execute(String.valueOf(id), path);
        }

        if (AudioService!= null && foundAudioBook!= true){
            AudioService.stop();
            AudioService.play(id);
            playingBookId = id;

            String headerString = bookList.get(prevPos).getTitle() + " by " + bookList.get(prevPos).getAuthor();
            Intent intent = new Intent("PLAYING_AUDIO");
            intent.putExtra("Header",headerString);
            sendBroadcast(intent);
        }
        else{
            try{
                File file = new File(getApplicationContext().getFilesDir(), fileDataName);
                writeToBookJSON(file, String.valueOf(playingBookId), String.valueOf(mp.getCurrentPosition()));
                mp.reset();
                mp.setDataSource(getApplicationContext().getFilesDir() + "/" + id + ".mp3");
                mp.prepare();
                int seek = Integer.valueOf(getJsonValue(file, String.valueOf(id)));
                if (seek != 0){
                    mp.seekTo(seek);
                }

                mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

                mp.start();
                playingBookId = id;

                String headerString = bookList.get(prevPos).getTitle() + " by " + bookList.get(prevPos).getAuthor();
                Intent intent = new Intent("PLAYING_AUDIO");
                intent.putExtra("Header",headerString);
                sendBroadcast(intent);

            } catch(Exception e){
                System.out.println(e);
            }

        }
    }

    // Updates Audiobook to seekbar Position set by user
    private void updateProgress(int progress){
        if (AudioService != null){
            AudioService.seekTo(progress);
        }
        mp.seekTo(progress);
    }

    // Sets Seekbar depending on book progress only
    private void setSeekbar(int progress, int duration){
        Intent intent = new Intent("SET_SEEKBAR");
        intent.putExtra("progress", progress);
        intent.putExtra("duration", duration);
        sendBroadcast(intent);
    }

    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            setSeekbar(mp.getCurrentPosition(), mp.getDuration());
            mSeekbarUpdateHandler.postDelayed(this, 50);

            File file = new File(getApplicationContext().getFilesDir(), fileDataName);
            try {
                writeToBookJSON(file, String.valueOf(playingBookId), String.valueOf(mp.getCurrentPosition()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    // Loops and makes sure Seekbar is updated with Audiobook Progress
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg){
            AudiobookService.BookProgress bookProgress = (AudiobookService.BookProgress) msg.obj;
            if (bookProgress != null){
                setSeekbar(bookProgress.getProgress(), bookList.get(prevPos).getDuration());
            }
        }
    };


    //Service Connection Methods for maintaining Audio Playback
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

    // Passing Data between Audio Controls Fragment and services
    // For the Button Presses in Audio Controls
    @Override
    public void onButtonPass(boolean pause, boolean play, boolean stop) {
        pausePress = pause;
        playPress = play;
        stopPress = stop;

        if (play == true) {
            if (bookList.size() != 0){
                if (playingBookId != bookList.get(prevPos).getId()){
                    playBook(bookList.get(prevPos).getId());
                }
            }
        }
        if (stop == true){
            File file = new File(getApplicationContext().getFilesDir(), fileDataName);
            try {
                writeToBookJSON(file, String.valueOf(playingBookId), "0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AudioService.isPlaying()){
                AudioService.stop();
            }
            if (mp.isPlaying()){
                mp.pause();
            }
            playing = false;
            prog = 0;
            playingBookId = -1;
        }
        if (pause == true){
            File file = new File(getApplicationContext().getFilesDir(), fileDataName);
            try {
                writeToBookJSON(file, String.valueOf(playingBookId), String.valueOf(mp.getCurrentPosition()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AudioService.isPlaying() || mp.isPlaying())
                playing = false;
            else
                playing = true;
            AudioService.pause();
            if (mp.isPlaying() == false){
                mp.start();
                mSeekbarUpdateHandler.post(mUpdateSeekbar);
            }else {
                mp.pause();
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            }
        }
    }

    // For the Seekbar User Updates
    @Override
    public void onSeekbarPass(int progress) {
        updateProgress(progress);
        prog = progress;
    }


    public JSONObject getBookJson(File file) throws IOException, JSONException {

        StringBuffer output = new StringBuffer();
        String response = "Asdf";

        try {
            fileReader = new FileReader(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bufferedReader = new BufferedReader(fileReader);

        String line = "";
        while ((line = bufferedReader.readLine()) != null){
            output.append(line + "\n");
        }

        response = output.toString();
        bufferedReader.close();

        JSONObject jsonFile = new JSONObject(response);

        return jsonFile;
    }

    public void writeToBookJSON(File file, String id, String detail) throws IOException, JSONException {

        JSONObject existingJson = getBookJson(file);

        try {
            existingJson.put(id, detail);
        } catch(Exception e){
        }

        fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        bw.write(existingJson.toString());
        bw.close();

        System.out.println(existingJson.toString());

    }

    public String getJsonValue(File file, String id) throws IOException, JSONException {

        JSONObject existingJson = getBookJson(file);
        System.out.println(existingJson.toString());
        String identifier = "";

        try {
            identifier = (String) existingJson.getString(id);
        } catch(Exception e){
        }

        System.out.println(identifier);
        return identifier;

    }


}
