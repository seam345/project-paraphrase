package tech.seanborg.projectparaphrase;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.List;

import static tech.seanborg.projectparaphrase.MainActivity.TAG;

public class HtmlConverter
{
    private String rawHTML = "";
    Paragraph[] paragraphs = null;
    
    
    static String codeFilter(Element element)
    {
        String codeText = element.text();
        
        codeText.replaceAll("\\.", " dot ");
        return codeText;
    }
    
    public void convert(String rawHTML)
    {
        Log.v(TAG, String.format("startdConvert (HtmlConverter.java:32)"));
        Document doc = Jsoup.parse(rawHTML);
        Element el = doc.select("div[itemprop=articleBody]").first();
        Elements code = doc.select("div.highlight>pre");
        // el.select()
        Elements noBlockCode = el.select("p,h1,h2,h3,h4,h5,h6");
        StringBuilder stringBuilder = new StringBuilder("");
        paragraphs = new Paragraph[noBlockCode.size()];
        for (int i = 0; i < noBlockCode.size(); i++)
        {
            Element e = noBlockCode.get(i);
            Elements inlineCode = e.select("span.pre");
            // if (inlineCode.size() == 0)
            {
                
                stringBuilder.append(e.text());
                paragraphs[i] = new Paragraph(e.text(),false);
                
            }
           /* else
            {
                List<Node> eChrildren = e.childNodes();
                // Elements blasdafa = e.textNodes();
                // e.textNodes()
                for (int j = 0; j < eChrildren.size(); j++)
                {
                    Node child = eChrildren.get(j);
                    Log.v(TAG, String.format("%s (MainActivity.java:88)", child.attributes().toString()));
                    if (child.attributes().hasKey("class"))
                    {
                        // Element eChild = new Element().;
                        // if (eChild.hasClass("pre"))
                        // {
                        //     stringBuilder.append(codeFilter(eChild));
                        // }else{
                        //     stringBuilder.append(eChild.text());
                        //
                        // }
                    } else
                    {
                        Log.v(TAG, String.format("emr should split this %s (MainActivity.java:87)", child.toString()));
                        stringBuilder.append(child.toString());
                    }
                }
            }*/
        }
    
    
        String blah = stringBuilder.toString();
        Log.v(TAG, String.format("%s (MainActivity.java:76)", blah));
        if (MainActivity.status == TextToSpeech.SUCCESS)
        {
            // String blah2 = el.text().substring(erm.getMaxSpeechInputLength());
            // erm.speak(blah, TextToSpeech.QUEUE_FLUSH, null, null);
            // erm.speak(blah2, TextToSpeech.QUEUE_ADD, null, null);
        } else
        {
            Log.e(TAG, "onResponse: opps");
        }
    }
}
