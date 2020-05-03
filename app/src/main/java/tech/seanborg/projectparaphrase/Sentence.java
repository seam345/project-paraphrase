package tech.seanborg.projectparaphrase;

import androidx.annotation.Nullable;

import java.io.File;

public class Sentence
{
    String originalText = "";
    String synthesizedText = ""; // this may not be the same as the original text as i may replace stuff like . with dot or / with slash to synthesize it more naturally
    private File synthesizedSpeech = null;
    boolean speechReady = false;
    
    public void setSynthesizedSpeech(File synthesizedSpeech)
    {
        speechReady = false;
        this.synthesizedSpeech = synthesizedSpeech;
    }
    
    @Nullable
    public File getSynthesizedSpeech()
    {
        if (speechReady)
        {
            return synthesizedSpeech;
        } else
        {
            return null;
        }
    }
}
