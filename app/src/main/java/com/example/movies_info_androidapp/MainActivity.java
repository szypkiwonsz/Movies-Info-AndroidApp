package com.example.movies_info_androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText titleMovie;
    TextView yearMovie, runtimeMovie, directorMovie, genreMovie;

    // Button onClock method.
    public void getMovieData(View view) {
        DownloadTask task = new DownloadTask();

        // Replacing spaces in title to underscores.
        String titleWithoutSpaces = titleMovie.getText().toString();
        titleWithoutSpaces = titleWithoutSpaces.trim();
        titleWithoutSpaces = titleWithoutSpaces.replaceAll("\\s", "\u005F");

        task.execute("http://www.omdbapi.com/?t=" + titleWithoutSpaces + "&apikey=39f41e43");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting typed title of movie by user.
        titleMovie = findViewById(R.id.titleMovie);

        // Variables of data that program is looking for.
        yearMovie = findViewById(R.id.yearMovie);
        runtimeMovie = findViewById(R.id.runtimeMovie);
        directorMovie = findViewById(R.id.directorMovie);
        genreMovie = findViewById(R.id.genreMovie);

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String json = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    json += current;
                    data = reader.read();
                }
                return json;

            } catch (IOException e) {
                e.printStackTrace();
                return "Error.";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String response = jsonObject.getString("Response");

                if (response.equals("False")) {
                    yearMovie.setText("MOVIE NOT FOUND!");
                    runtimeMovie.setVisibility(View.GONE);
                    genreMovie.setVisibility(View.GONE);
                    directorMovie.setVisibility(View.GONE);
                }
                else {
                    runtimeMovie.setVisibility(View.VISIBLE);
                    genreMovie.setVisibility(View.VISIBLE);
                    directorMovie.setVisibility(View.VISIBLE);

                    String year = jsonObject.getString("Year");
                    String runtime = jsonObject.getString("Runtime");
                    String genre = jsonObject.getString("Genre");
                    String director = jsonObject.getString("Director");

                    yearMovie.setText(year);
                    runtimeMovie.setText(runtime);
                    genreMovie.setText(genre);
                    directorMovie.setText(director);
                }
                } catch (JSONException e) {
                    e.printStackTrace();
            }
        }

    }

}
