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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity
{
    public static String TAG = "projectparaphrase";
    TextToSpeech erm = null;
    String textOnPage = "";
    static int status = TextToSpeech.ERROR;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final TextToSpeech.OnInitListener test = new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                MainActivity.status = status;
                // erm.speak(textOnPage, TextToSpeech.QUEUE_ADD, null, null);
                
            }
        };
        
        erm = new TextToSpeech(getApplicationContext(), test);
        
        final WebView webView = findViewById(R.id.webView);
        
        
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://buildstream.gitlab.io/buildstream/tutorial/first-project.html";
        
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
                        Document doc = Jsoup.parse(response);
                        webView.loadDataWithBaseURL(url, doc.toString(), null, null, null);
                        Element el = doc.select("div[itemprop=articleBody]").first();
                        Log.v(TAG, String.format("%d (MainActivity.java:67)", MainActivity.status));
                        
                        if (MainActivity.status == TextToSpeech.SUCCESS)
                        {
                            // erm.speak("arrrrrrrrrrr", TextToSpeech.QUEUE_ADD, null, null);
                            String blah = el.text().substring(0,erm.getMaxSpeechInputLength()-1);
                            erm.speak(blah, TextToSpeech.QUEUE_ADD, null, null);
                        }else{
                            Log.e(TAG, "onResponse: opps");
                        }
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
