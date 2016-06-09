package ch06;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by valdesl on 09/06/2016.
 */
public class AdvancedQueryParser {

    public static void main(String[] args) throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc = new Document();
        StringField stringField = new StringField("name", "", Field.Store.YES);
        TextField textField = new TextField("content", "", Field.Store.YES);
        IntField intField = new IntField("num", 0, Field.Store.YES);

        String[] fields = {"First", "Second", "Third", "Fourth"};
        HashMap<String, String> textValues = new HashMap<String, String>();
        HashMap<String, Integer> intValues = new HashMap<String, Integer>();

        textValues.put(fields[0], "Humpty Dumpty sat on a wall,");
        textValues.put(fields[1], "Humpty Dumpty had a great fall.");
        textValues.put(fields[2], "All the king's horses and all the king's men");
        textValues.put(fields[3], "Couldn't put Humpty together again.'");
        intValues.put(fields[0], 100);
        intValues.put(fields[1], 200);
        intValues.put(fields[2], 300);
        intValues.put(fields[3], 400);
        

        for(String field : fields) {
            doc.removeField("name");
            doc.removeField("content");
            doc.removeField("num");

            stringField.setStringValue(field);
            textField.setStringValue(textValues.get(field));
            intField.setIntValue(intValues.get(field));
            
            doc.add(stringField);
            doc.add(textField);
            doc.add(intField);

            indexWriter.addDocument(doc);
        }

        indexWriter.commit();
        indexWriter.close();

        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser queryParser = new QueryParser("content", analyzer);
        // configure queryParser here
        Query query = queryParser.parse("humpty");
        TopDocs topDocs = indexSearcher.search(query, 100);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(String.format("%d - name: %s - content: %s - num: %s", scoreDoc.doc, doc.get("name"), doc.get("content"), doc.get("num")));
        }
    }

}
