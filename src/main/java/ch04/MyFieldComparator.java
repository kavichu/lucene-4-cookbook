package ch04;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;

import java.io.IOException;

/**
 * Created by valdesl on 07/06/2016.
 * FieldComparator to sort results on a single-valued field by length of the field value
 * in and then by alphabetical order, both in ascending order:
 */
public class MyFieldComparator extends FieldComparator<String> {

    private String field;
    private String bottom;
    private String topValue;
    private BinaryDocValues cache;
    private String[] values;

    public MyFieldComparator(String field, int numHits) {
        this.field = field;
        this.values = new String[numHits];
    }

    public int compare(int slot1, int slot2) {
        return compareValues(values[slot1], values[slot2]);
    }

    public int compareBottom(int doc) {
        return compareValues(bottom, cache.get(doc).utf8ToString());
    }

    public int compareTop(int doc) {
        return compareValues(topValue, cache.get(doc).utf8ToString());
    }

    public int compareValues(String first, String second) {
        int val = first.length() - second.length();
        return val == 0  ? first.compareTo(second) : val;
    }

    public void copy(int slot, int doc) {
        values[slot] = cache.get(doc).utf8ToString();
    }

    public void setBottom(int slot) {
        this.bottom = values[slot];
    }

    public void setTopValue(String value) {
        this.topValue = value;
    }

    public String value(int slot) {
        return values[slot];
    }

    @Override
    public FieldComparator<String> setNextReader(AtomicReaderContext context) throws IOException {
        this.cache = FieldCache.DEFAULT.getTerms(context.reader(), field, true);
        return this;
    }
    
}


