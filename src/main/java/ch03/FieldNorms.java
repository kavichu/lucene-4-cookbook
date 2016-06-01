package ch03;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created by valdesl on 01/06/2016.
 */
public class FieldNorms {
    
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        Document doc = new Document();
        TextField textField = new TextField("name", "", Field.Store.YES);
        
        float boost = 1f;
        String[] names = {"John R Smith", "Mary Smith", "Peter Smith", "Adam Smith"};
        for(String name : names){
            boost *= 1.1;
            textField.setStringValue(name);
            textField.setBoost(boost);
            doc.removeField("name");
            doc.add(textField);
            indexWriter.addDocument(doc);
        } 
        indexWriter.commit();
        
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        Query query = new TermQuery(new Term("name", "smith"));
        TopDocs topDocs = indexSearcher.search(query, 100);
        System.out.println("Searching 'smith'");
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            doc = indexReader.document(scoreDoc.doc);
            System.out.println(doc.getField("name").stringValue());
        }
    }
    
}
