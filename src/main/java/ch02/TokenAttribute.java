package ch02;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by valdesl on 25/05/2016.
 */
public class TokenAttribute {

    public static void main(String[] args) throws IOException {
        StringReader reader = new StringReader("Lucene is mainly used for " +
                "information retrieval and you can read more about it at lucene.apache.org.");
        StandardAnalyzer wa = new StandardAnalyzer();
        TokenStream ts = null;

        try {
            ts = wa.tokenStream("field", reader);

            OffsetAttribute offsetAt = ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);

            ts.reset();

            while(ts.incrementToken()){
                String token = termAtt.toString();
                System.out.println("[" + token + "]");
                System.out.println("Token starting offset: " + offsetAt.startOffset());
                System.out.println(" Token ending offset: " + offsetAt.endOffset());
                System.out.println();
            }


        }catch (IOException e){
            e.printStackTrace();
        }finally {
            ts.close();
            wa.close();
        }
    }

}
