package tech.seanborg.projectparaphrase;

public class Paragraph
{
    String[] sentences = null;
    boolean code = false;
    
    public Paragraph(String[] sentences, boolean code)
    {
        this.sentences = sentences;
        this.code = code;
    }
    
    public Paragraph(String paragraph, boolean code)
    {
        this.sentences = paragraph.split("\\.");
        this.code = code;
    }
    
    
}
