package tech.seanborg.projectparaphrase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.webkit.WebView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity
{
    public static String TAG = "projectparaphrase";
    TextToSpeech erm = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextToSpeech.OnInitListener test = new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                erm.speak("testing text to speech", TextToSpeech.QUEUE_ADD, null, null);
                
            }
        };
        
        erm = new TextToSpeech(getApplicationContext(), test);
        
        final WebView webView = findViewById(R.id.webView);
        // webView.loadData(
        //         "<html>\n" +
        //         "<header><title>This is title</title></header>\n" +
        //         "<body>\n" +
        //         "Hello world\n" +
        //         "</body>\n" +
        //         "</html>",null, null
        // );
        
        
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://buildstream.gitlab.io/buildstream/tutorial/first-project.html";
        
        webView.loadUrl(url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        // Display the first 500 characters of the response string.
                        // webView.loadData(response, null, null);
                        // Log.v(TAG, String.format("%s (MainActivity.java:45)", response));
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                webView.loadData("That didn't work!", null, null);
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
