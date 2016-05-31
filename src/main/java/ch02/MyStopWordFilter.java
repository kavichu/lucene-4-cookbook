package ch02;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

/**
 * Created by valdesl on 25/05/2016.
 */
public class MyStopWordFilter extends TokenFilter {

    private CharTermAttribute charTermAtt;
    private PositionIncrementAttribute posIncrAtt;

    public MyStopWordFilter(TokenStream stream){
        super(stream);
        charTermAtt = addAttribute(CharTermAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        int extraIncremenent = 0;
        boolean returnValue = false;
        while(input.incrementToken()){
            if(StopAnalyzer.ENGLISH_STOP_WORDS_SET.contains(charTermAtt.toString())){
                extraIncremenent++; // filter this word
                continue;
            }

            returnValue = true;
            break;
        }

        if(extraIncremenent > 0){
            posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + extraIncremenent);
        }
        return returnValue;
    }
}
