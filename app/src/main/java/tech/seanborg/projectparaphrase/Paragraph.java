package tech.seanborg.projectparaphrase;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class Paragraph
{
    Sentence[] sentences = null;
    boolean code = false;
    String originalHTML = "";
    Tag originalTag = null;
    Attributes originalAttributes = null;
    
    
    
    public Paragraph(Sentence[] sentences, boolean code)
    {
        this.sentences = sentences;
        this.code = code;
    }
    
    public Paragraph(String paragraph, boolean code)
    {
        // this whole method will need replacing as I want the html as well as the strings
        String[] sentenceText = paragraph.split("\\.");
        sentences = new Sentence[sentenceText.length];
        for (int i = 0; i < sentenceText.length; i++)
        {
            Sentence sentence = new Sentence();
            sentence.synthesizedText = sentenceText[i];
            sentences[i] = sentence;
        }
        this.code = code;
    }
    
    public Paragraph(Element originalElement)
    {
        originalHTML = originalElement.html();
        originalTag = originalElement.tag();
        originalAttributes = originalElement.attributes();
        if (originalTag.preserveWhitespace()) // most likely a block containing code
        {
            code = true;
        }
        String paragraph = originalElement.text();
        String[] sentenceText = paragraph.split("\\.");
        sentences = new Sentence[sentenceText.length];
        for (int i = 0; i < sentenceText.length; i++)
        {
            Sentence sentence = new Sentence();
            sentence.synthesizedText = sentenceText[i];
            sentence.originalText = sentenceText[i]; // todo want to make this html not text will need to work on this
            sentences[i] = sentence;
        }
        
        
        
        
    }
    
    
    String toOriginalHTML()
    {
        return originalHTML;
    }
    
    String toHtmlHighlightSentence(int highlightNum)
    {
        Element temp = new Element(originalTag, "", originalAttributes);
        // String html = "";
        for (int i = 0; i < sentences.length; i++)
        {
            if (i == highlightNum)
            {
                temp.append("<span style=\"background-color: #f7ff0070;\">" + sentences[i].originalText + "<\\span>");
                // html += "<span style=\"background-color: #f7ff0070;\">" + sentences[i].originalText + "<\\span>"; // this assumes i'm not breaking a sentence in the middle of a tag which I would find very unlikly
            }else
            {
                temp.append(sentences[i].originalText);
                // html += sentences[i].originalText;
            }
        }
        return temp.html();
    }
    
}
