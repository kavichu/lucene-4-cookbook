package ch04;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created by valdesl on 07/06/2016.
 */
public class MyFieldComparatorSource extends FieldComparatorSource {
    public FieldComparator newComparator(String fieldName, int numHits, int sortPos, boolean reversed) {
        return new MyFieldComparator(fieldName, numHits);
    }


    public static void main(String[] args) throws IOException, ParseException {

        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc = new Document();
        TextField textField = new TextField("content", "", Field.Store.YES);


        String[] contents = {"Humpty Dumpty sat on a wall,",
                "Humpty Dumpty had a great fall.",
                "All the king's horses and all the king's men",
                "Couldn't put Humpty together again.",
                "Humpty is here."};
        for(String content : contents) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }

        indexWriter.commit();

        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);


        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse("humpty");

        SortField sortField = new SortField("content", new MyFieldComparatorSource());
        Sort sort = new Sort(sortField);

        TopDocs topDocs = indexSearcher.search(query, 4, sort);

        System.out.println("Using Custom FieldComparator");
        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            doc = indexReader.document(scoreDoc.doc);
            System.out.println(String.format("%d - %f: %s", scoreDoc.doc, scoreDoc.score, doc.get("content")));
        }

        System.out.println("Default search");
        topDocs = indexSearcher.search(query, 4);
        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            doc = indexReader.document(scoreDoc.doc);
            System.out.println(String.format("%d - %f: %s", scoreDoc.doc, scoreDoc.score, doc.get("content")));
        }


    }
}