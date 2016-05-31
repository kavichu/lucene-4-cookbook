package ch03;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created by valdesl on 31/05/2016.
 */
public class ReusingDocuments {
    
   public static void main(String[] args) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc = new Document();
        StringField stringField = new StringField("name", "", Field.Store.YES);

        String[] names = {"John", "Mary", "Peter"};
        for(String name : names){
            stringField.setStringValue(name);
            doc.removeField("name");
            doc.add(stringField);
            indexWriter.addDocument(doc);
        }

        indexWriter.commit();
        IndexReader reader = DirectoryReader.open(directory);
        for(int i=0; i < 3; i++){
            doc = reader.document(i);
            System.out.println("DocId: " + i + ", name: " + doc.getField("name").stringValue());
        }
   }
      
}
