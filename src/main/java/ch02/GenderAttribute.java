package ch02;

import org.apache.lucene.util.Attribute;

/**
 * Created by valdesl on 30/05/2016.
 */
public interface GenderAttribute extends Attribute {
    
    enum Gender {Male, Female, Undefined};
    
    void setGender(Gender gender);
    
    Gender getGender();
}