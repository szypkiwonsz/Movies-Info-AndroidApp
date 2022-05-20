package com.example.movies_info_androidapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText titleMovie;
    TextView yearMovie, runtimeMovie, directorMovie, genreMovie;
    ////////////////Bartek/////////////////////
    Button btnSpeech;
    ///////////////////////////////////////////
    // Button onClock method.
    public void getMovieData(View view) {
        DownloadTask task = new DownloadTask();

        // Replacing spaces in title to underscores.
        String titleWithoutSpaces = titleMovie.getText().toString();
        titleWithoutSpaces = titleWithoutSpaces.trim();
        titleWithoutSpaces = titleWithoutSpaces.replaceAll("\\s", "\u005F");

        task.execute("https://www.omdbapi.com/?t=" + titleWithoutSpaces + "&apikey=39f41e43");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK){
            titleMovie.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
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

        // Barek Wilmowicz edit :)///////////////////////////////////////////
        btnSpeech = findViewById(R.id.btnSpeech);
        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeakNow(view);
            }
        });
    }
       private void SpeakNow(View view) {
           Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
           intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
           intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "You can talk...");
           startActivityForResult(intent,111);
        }


        ///////////////////////////////////////////


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
