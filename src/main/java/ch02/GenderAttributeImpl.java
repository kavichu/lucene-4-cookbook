package ch02;

import org.apache.lucene.util.AttributeImpl;

/**
 * Created by valdesl on 30/05/2016.
 */
public class GenderAttributeImpl extends AttributeImpl
    implements GenderAttribute {
        
        private Gender gender = Gender.Undefined;
        
        public void setGender(Gender gender) {
            this.gender = gender;
        }
        
        public Gender getGender() {
            return gender;
        }
        
        @Override
        public void clear() {
            gender = Gender.Undefined;
        }
        
        @Override
        public void copyTo(AttributeImpl target) {
            ((GenderAttribute) target).setGender(gender);
        }
            
}