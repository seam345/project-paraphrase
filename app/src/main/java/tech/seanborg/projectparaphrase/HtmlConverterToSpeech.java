package tech.seanborg.projectparaphrase;

import android.content.Context;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    double lastConvertTag = 0;
    int paragraph = 0;
    int sentence = 0;
    
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
                // Log.v(TAG, String.format("utterence id = %s (HtmlConverterToSpeech.java:62)", utteranceId));
                String[] positionString = utteranceId.split("_");
                int paragraph = Integer.parseInt(positionString[0]);
                int sentence = Integer.parseInt(positionString[1]);
                htmlConverter.paragraphs[paragraph].sentences[sentence].speechReady = true;
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
        Log.v(TAG, String.format("lastCovertTag %f, currentTag %f (HtmlConverterToSpeech.java:93)", lastConvertTag, htmlConverter.currentConvertTag()));
        if (!ready || lastConvertTag != htmlConverter.currentConvertTag())
        {
            readyFiles.clear();
            lastConvertTag = htmlConverter.currentConvertTag();
            File topDir = new File(cacheDir.getPath() + "/" + url);
            files = new ArrayList(htmlConverter.paragraphs.length);
            // Log.v(TAG, String.format("paragraphs size %d, files array initialize %d (HtmlConverterToSpeech.java:63)", htmlConverter.paragraphs.length, files.size()));
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
                    htmlConverter.paragraphs[i].sentences[j].setSynthesizedSpeech(sentenceFile);
                    if (!sentenceFile.exists())
                    {
                        // Log.v(TAG, String.format("File didnt exist %s (HtmlConverterToSpeech.java:71)", sentenceFile.getAbsolutePath()));
                        try
                        {
                            sentenceFile.createNewFile();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    String urrerenceID = String.format("%03d_%03d", i, j);
                    t2s.synthesizeToFile(htmlConverter.paragraphs[i].sentences[j].synthesizedText, null, sentenceFile, urrerenceID);
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
        pause = false;
        player.stop();
    }
    
    public boolean playPause()
    {
        if (player.isPlaying())
        {
            player.pause();
            pause = true;
        } else if (pause)
        {
            player.start();
        } else
        {
            playAbsolute(0, 0);
        }
        
        
        return player.isPlaying();
    }
    
    void playAbsolute(int absoluteParagraph, int absoluteSentence)
    {
        try
        {
            player.reset();
            if (htmlConverter.paragraphs.length < absoluteParagraph)
            {
                Log.e(TAG, "playAbsolute: paragraph was higher than number of paragraphs");
                return;
            }
            if (htmlConverter.paragraphs[absoluteParagraph].sentences.length < absoluteSentence)
            {
                Log.e(TAG, "playAbsolute: sentence was higher than number of sentences ");
                return;
            }
            if (htmlConverter.paragraphs[absoluteParagraph].sentences[absoluteSentence].speechReady)
            {
                
                player.setDataSource(htmlConverter.paragraphs[absoluteParagraph].sentences[absoluteSentence].getSynthesizedSpeech().getPath());
                player.prepare();
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        playRelative(0,1);
                    }
                });
            }
            
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        this.sentence = absoluteSentence;
        this.paragraph = absoluteParagraph;
    }
    
    void playRelative(int relativeParagraph, int relativeSentence)
    {
        paragraph = this.paragraph + relativeParagraph;
        if (relativeParagraph != 0)
        {
            sentence=0; // go to beginning of paragraph
        }
        sentence = this.sentence + relativeSentence;
        try
        {
            player.reset();
            if (paragraph > htmlConverter.paragraphs.length)
            {
                Log.w(TAG, "playRelative: paragraph was higher than number of paragraphs");
                return;
            }
            if (paragraph < 0)
            {
                Log.w(TAG, "playRelative: paragraph was lower than 0");
                return;
            }
            // move forwards
            while (sentence >= htmlConverter.paragraphs[paragraph].sentences.length || htmlConverter.paragraphs[paragraph].code)
            {
                if (htmlConverter.paragraphs[paragraph].code)
                {
                    paragraph++;
                }else{
                    
                    // Log.v(TAG, String.format("in while loop paragraph: %d, sentence %d, sentence.length %d  (HtmlConverterToSpeech.java:223)", paragraph, sentence, htmlConverter.paragraphs[paragraph].sentences.length));
                    sentence -= htmlConverter.paragraphs[paragraph].sentences.length;
                    paragraph++;
                }
                if (paragraph > htmlConverter.paragraphs.length)
                {
                    Log.d(TAG, "playRelative: end of page");
                    return;
                }
            }
            while (sentence < 0 || htmlConverter.paragraphs[paragraph].code)
            {
                if (htmlConverter.paragraphs[paragraph].code)
                {
                    paragraph--;
                     if (paragraph < 0)
                    {
                        Log.w(TAG, "playRelative: beginning of page");
                        return;
                    }
                }else{
            
                    // Log.v(TAG, String.format("in while loop paragraph: %d, sentence %d, sentence.length %d  (HtmlConverterToSpeech.java:223)", paragraph, sentence, htmlConverter.paragraphs[paragraph].sentences.length));
                    paragraph--;
                    if (paragraph < 0)
                    {
                        Log.w(TAG, "playRelative: beginning of page");
                        return;
                    }
                    sentence += htmlConverter.paragraphs[paragraph].sentences.length;
                }
            }
            if (htmlConverter.paragraphs[paragraph].sentences[sentence].speechReady)
            {
            
                player.setDataSource(htmlConverter.paragraphs[paragraph].sentences[sentence].getSynthesizedSpeech().getPath());
                player.prepare();
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        playRelative(0,1);
                    }
                });
            }
        
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
