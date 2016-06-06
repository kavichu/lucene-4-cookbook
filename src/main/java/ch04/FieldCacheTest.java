package ch04;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created by valdesl on 06/06/2016.
 */
public class FieldCacheTest {
    
    public static void main (String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        Document doc = new Document();
        StringField stringField = new StringField("name", "", Field.Store.YES);
        
        String[] contents = {"alpha", "bravo", "charlie", "delta", "echo", "foxtrot"};
        
        for(String content : contents) {
            stringField.setStringValue(content);
            doc.removeField("name");
            doc.add(stringField);
            indexWriter.addDocument(doc);
        }
        indexWriter.commit();
        
        IndexReader indexReader = DirectoryReader.open(directory);
        BinaryDocValues cache = FieldCache.DEFAULT.getTerms(SlowCompositeReaderWrapper.wrap(indexReader), "name", true);
        
        for(int i=0; i < indexReader.maxDoc(); i++) {
            BytesRef bytesRef = cache.get(i);
            System.out.println(i + ": " + bytesRef.utf8ToString());
        }
    } 
    
}
