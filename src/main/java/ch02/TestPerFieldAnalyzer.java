package ch02;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by valdesl on 25/05/2016.
 */
public class TestPerFieldAnalyzer {

    public static void main(String[] args) throws IOException {

        Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        analyzerPerField.put("myfield", new WhitespaceAnalyzer());

        PerFieldAnalyzerWrapper defAnalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);

        TokenStream ts = null;
        OffsetAttribute offsetAtt = null;
        CharTermAttribute charAtt = null;
        
        try {
            ts = defAnalyzer.tokenStream("myfield", new StringReader("lucene.apache.org AB-978"));
            offsetAtt = ts.addAttribute(OffsetAttribute.class);
            charAtt = ts.addAttribute(CharTermAttribute.class);
            
            ts.reset();
            
            System.out.println("== Processing field 'myfield' using WhitespaceAnalyzer (per field) ==");
            while(ts.incrementToken()){
                System.out.println(charAtt.toString());
                System.out.println("token start offset: " + offsetAtt.startOffset());
                System.out.println("token end offset: " + offsetAtt.endOffset());
            }
            ts.end();
            
            ts = defAnalyzer.tokenStream("content", new StringReader("lucene.apache.org AB-978"));
            offsetAtt = ts.addAttribute(OffsetAttribute.class);
            charAtt = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            
            System.out.println("== Processing field 'content' using StandardAnalyzer ==");
            
            while(ts.incrementToken()){
                System.out.println(charAtt.toString());
                System.out.println("token start offset: " + offsetAtt.startOffset());
                System.out.println("token ende offset: " + offsetAtt.endOffset());
            }
            ts.end();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            ts.close();
        }
    }
}
