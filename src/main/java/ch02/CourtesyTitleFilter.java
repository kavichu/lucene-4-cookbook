package ch02;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by valdesl on 30/05/2016.
 */
public class CourtesyTitleFilter extends TokenFilter {
    Map<String, String> courtesyTitlemap = new HashMap<String, String>();
    
    private CharTermAttribute termAttr;
    
    public CourtesyTitleFilter(TokenStream input) {
        super(input);
        termAttr = addAttribute(CharTermAttribute.class);
        courtesyTitlemap.put("Dr", "doctor");
        courtesyTitlemap.put("Mr", "mister");
        courtesyTitlemap.put("Mrs", "miss");
    }
    
    public boolean incrementToken() throws IOException {
        if(!input.incrementToken()){
            return false;
        }
        String small = termAttr.toString();
        if(courtesyTitlemap.containsKey(small)) {
            termAttr.setEmpty().append(courtesyTitlemap.get(small));
        }  
        return true;
    }
}
