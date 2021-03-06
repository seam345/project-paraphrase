package tech.seanborg.projectparaphrase;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity
{
    public static String TAG = "projectparaphrase";
    TextToSpeech erm = null;
    String textOnPage = "";
    static int status = TextToSpeech.ERROR;
    String html = "";
    String url = "https://buildstream.gitlab.io/buildstream/tutorial/first-project.html";
    HtmlConverter htmlConverter = null;
    HtmlConverterToSpeech htmlConverterToSpeech = null;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        htmlConverter = new HtmlConverter();
        
        htmlConverterToSpeech = new HtmlConverterToSpeech(htmlConverter, getApplicationContext(), url);
        
        final WebView webView = findViewById(R.id.webView);
        WebViewClient webViewClient = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                loadWebView(request.getUrl().toString(), webView, null);
                startLoadWebpage(getApplicationContext(), request.getUrl().toString(), webView, htmlConverter, htmlConverterToSpeech);
                return true;
            }
            
        };
        webView.setWebViewClient(webViewClient);
        
        
        EditText urlEditText = (EditText) findViewById(R.id.editTextUrl);
        urlEditText.setText(url);
        urlEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                url = v.getText().toString();
                if (!url.startsWith("http"))
                {
                    url = "https://" + url;
                }
                loadWebView(url, webView, null);
                startLoadWebpage(getApplicationContext(), url, webView, htmlConverter, htmlConverterToSpeech);
                return false;
            }
        });
        
        
        Button playPauseButton = (Button) findViewById(R.id.buttonPlayPause);
        playPauseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                htmlConverterToSpeech.playPause();
            }
        });
        
        Button fastForwardButton = (Button) findViewById(R.id.buttonFastForwards);
        fastForwardButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                htmlConverterToSpeech.playRelative(0,1);
            }
        });
    
        Button fastReverseButton = (Button) findViewById(R.id.buttonFastReverse);
        fastReverseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                htmlConverterToSpeech.playRelative(0,-1);
            }
        });
    
        Button paragraphForwardsButton = (Button) findViewById(R.id.buttonForwardsParagraph);
        paragraphForwardsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                htmlConverterToSpeech.playRelative(1, 0);
            }
        });
    
        Button paragraphReverseButton = (Button) findViewById(R.id.buttonBackParagraph);
        paragraphReverseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                htmlConverterToSpeech.playRelative(-1,0);
            }
        });
        
        
        // loadWebView(url, webView, null);
        Log.d(TAG, String.format("started load weboage (MainActivity.java:87)"));
        startLoadWebpage(getApplicationContext(), url, webView, htmlConverter, htmlConverterToSpeech);
        
        
    }
    
    
    static void startLoadWebpage(Context context, final String urlToLoad, final WebView webView, final HtmlConverter htmlConverter, final HtmlConverterToSpeech htmlConverterToSpeech)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlToLoad,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        htmlConverter.convert(response);
                        Log.v(TAG, String.format("finished convert (MainActivity.java:103)"));
                        loadWebView(urlToLoad, webView, null);
                        htmlConverterToSpeech.prepare();
                        Log.v(TAG, String.format("Called prepare! (MainActivity.java:105)"));
                        
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
    
    static void loadWebView(String baseUrl, WebView window, Document htmlJsoup)
    {
        if (htmlJsoup == null)
        {
            window.loadUrl(baseUrl);
        } else
        {
            window.loadDataWithBaseURL(baseUrl, htmlJsoup.toString(), null, null, null);
        }
    }
    
    static String codeFilter(Element element)
    {
        String codeText = element.text();
        
        codeText.replaceAll("\\.", " dot ");
        return codeText;
    }
}
