package edu.temple.bookshelf;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AudioBookDownload extends AsyncTask<String, Void, Void> {

    public AsyncResponse delegate = null;

    public AudioBookDownload(){
        this.delegate = delegate;
    }


    @Override
    protected Void doInBackground(String... params){
        int count;
        String BookID = params[0];
        String path = params[1];

        try {
            URL url = new URL("https://kamorris.com/lab/audlib/download.php?id=" + BookID);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            int lenghtOfFile = urlConnection.getContentLength();


            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream("" + path + "/" + BookID + ".mp3");

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // System.out.println((int)(total*100/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            System.out.println(output.toString());
            System.out.println("Download Successful");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

    public interface AsyncResponse{
        void processFinish(String jsonString);
    }

}