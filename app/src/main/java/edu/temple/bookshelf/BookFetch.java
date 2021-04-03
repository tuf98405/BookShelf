package edu.temple.bookshelf;

import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BookFetch extends AsyncTask<String, Void, String> {


    public AsyncResponse delegate = null;

    String data = "";

    public BookFetch (AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected String doInBackground(String... strings){

        try {
            URL url = new URL("https://kamorris.com/lab/cis3515/search.php?");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }


            /* MIGHT NEED IN MAIN
            JSONArray resultArray = null;
            resultArray = new JSONArray(data);

            for (int i = 0; i < resultArray.length(); i++){
                JSONObject object = (JSONObject) resultArray.get(i);
                singleParsed =  "ID: " + object.get("id") + "\n" +
                                "Title: " + object.get("title") + "\n" +
                                "Author: " + object.get("author") + "\n" +
                                "CoverURL: " + object.get("cover_url") + "\n";
                dataParsed = dataParsed + singleParsed + "\n";
            }
            */


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    protected void onPostExecute(String result) {
        delegate.processFinish(result);

    }

    public interface AsyncResponse{
        void processFinish(String jsonString);
    }

}
