package ch04;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created by valdesl on 06/06/2016.
 */
public class SortLogic {
    
    public static void main(String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        Document doc = new Document();
        StringField stringField = new StringField("name", "", Field.Store.YES);
        
        String[] contents = {"foxtrot", "echo", "delta", "charlie", "bravo", "alpha"};
        for(String content : contents) {
            stringField.setStringValue(content);
            doc.removeField("name");
            doc.add(stringField);
            indexWriter.addDocument(doc);
        }
        
        indexWriter.commit();
        
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        WildcardQuery query = new WildcardQuery(new Term("name", "*"));
        SortField sortField = new SortField("name", SortField.Type.STRING);
        Sort sort = new Sort(sortField);
        
        TopDocs topDocs = indexSearcher.search(query, null, 100, sort);
        for(ScoreDoc scoreDoc : topDocs.scoreDocs){
            doc = indexReader.document(scoreDoc.doc);
            // The sort does not specify scoring, no need to compute scores.
            System.out.println(String.format("%.02f: %s", scoreDoc.score, doc.getField("name").stringValue()));
        }
    }    
}
