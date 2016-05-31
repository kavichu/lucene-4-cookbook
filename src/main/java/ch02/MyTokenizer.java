package ch02;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeFactory;

import java.io.Reader;

/**
 * Created by valdesl on 30/05/2016.
 */
public class MyTokenizer extends CharTokenizer {
    
    public MyTokenizer(Reader input) {
        super(input);
    }
    
    public MyTokenizer(AttributeFactory factory, Reader input) {
        super(factory, input);
    }
    
    @Override
    protected boolean isTokenChar(int c) {
        return !Character.isSpaceChar(c);
    }
}
