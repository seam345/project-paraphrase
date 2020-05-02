package tech.seanborg.projectparaphrase;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static tech.seanborg.projectparaphrase.MainActivity.TAG;

public class HtmlConverterToSpeech
{
    HtmlConverter htmlConverter = null;
    TextToSpeech t2s = null;
    int t2sStatus = TextToSpeech.ERROR;
    boolean pause = false;
    boolean ready = false;
    LinkedList<File> readyFiles = new LinkedList<>();
    ArrayList<ArrayList<File>> files = null;
    String url = "";
    File cacheDir = null;
    MediaPlayer player = null;
    UtteranceProgressListener listener = null;
    
    public HtmlConverterToSpeech(final HtmlConverter htmlConverter, final Context context, String url)
    {
        this.htmlConverter = htmlConverter;
        final TextToSpeech.OnInitListener test = new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                t2sStatus = status;
                if (t2sStatus == TextToSpeech.SUCCESS)
                {
                    if (htmlConverter.paragraphs != null)
                    {
                        prepare();
                    }
                }
            }
        };
        t2s = new TextToSpeech(context, test);
        setUrl(url);
        cacheDir = context.getCacheDir();
        player = new MediaPlayer();
        listener = new UtteranceProgressListener()
        {
            @Override
            public void onStart(String utteranceId)
            {
            
            }
            
            @Override
            public void onDone(String utteranceId)
            {
                Log.v(TAG, String.format("utterence id = %s (HtmlConverterToSpeech.java:62)", utteranceId));
            }
            
            @Override
            public void onError(String utteranceId)
            {
            
            }
        };
        t2s.setOnUtteranceProgressListener(listener);
        
    }
    
    public void setUrl(String url)
    {
        url = url.replace('/', '_');
        url = url.replace(':', '_');
        this.url = url;
    }
    
    public void prepare()
    {
        Log.v(TAG, String.format("called prepare (HtmlConverterToSpeech.java:58)"));
        if (!ready)
        {
            File topDir = new File(cacheDir.getPath() + "/" + url);
            files = new ArrayList(htmlConverter.paragraphs.length);
            Log.v(TAG, String.format("paragraphs size %d, files array initialize %d (HtmlConverterToSpeech.java:63)", htmlConverter.paragraphs.length, files.size()));
            for (int i = 0; i < htmlConverter.paragraphs.length; i++)
            {
                ArrayList<File> tempParagraph = new ArrayList<>(htmlConverter.paragraphs[i].sentences.length);
                File paragraphDir = new File(topDir.getPath() + "/" + Integer.toString(i));
                if (!paragraphDir.exists())
                {
                    paragraphDir.mkdirs();
                }
                for (int j = 0; j < htmlConverter.paragraphs[i].sentences.length; j++)
                {
                    File sentenceFile = new File(topDir.getPath() + "/" + Integer.toString(i) + "/" + Integer.toString(j) + ".wav");
                    if (!sentenceFile.exists())
                    {
                        Log.v(TAG, String.format("File didnt exist %s (HtmlConverterToSpeech.java:71)", sentenceFile.getAbsolutePath()));
                        try
                        {
                            sentenceFile.createNewFile();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    String urrerenceID = String.format("%03d_%03d", i, j);
                    t2s.synthesizeToFile(htmlConverter.paragraphs[i].sentences[j], null, sentenceFile, urrerenceID);
                    readyFiles.add(sentenceFile);
                    tempParagraph.add(sentenceFile);
                }
                files.add(tempParagraph);
                // t2s.speak(psaragraph, TextToSpeech.QUEUE_ADD, null, null);
                // Log.v(TAG, String.format("%s (HtmlConverterToSpeech.java:41)", psaragraph));
            }
            cacheDir.getPath();
        }
        ready = true;
    }
    
    public boolean playPause(Context context)
    {
        if (player.isPlaying())
        {
            player.pause();
            pause = true;
        }
        else if (pause)
        {
            player.start();
        }else
        {
            play();
        }
        
       
        return player.isPlaying();
    }
    
    void play()
    {
        if (readyFiles.size() > 0)
        {
            try
            {
                player.reset();
                player.setDataSource(readyFiles.poll().getPath());
                player.prepare();
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        play();
                    }
                });
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
