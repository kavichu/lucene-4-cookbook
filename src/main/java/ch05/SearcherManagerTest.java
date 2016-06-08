package ch05;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by valdesl on 08/06/2016.
 */
public class SearcherManagerTest {

    public static void search(IndexSearcher indexSearcher, String queryTerm) throws ParseException, IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse(queryTerm);
        TopDocs topDocs = indexSearcher.search(query, 100);
        System.out.println(String.format("Searching for: %s", queryTerm));
        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(String.format("%d -> %f: %s", scoreDoc.doc, scoreDoc.score, doc.get("content")));
        }
    }

    public static void main(String[] args) throws IOException, ParseException {

        Directory directory = FSDirectory.open(new File("data/index"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc = new Document();
        TextField textField = new TextField("content", "", Field.Store.YES);
        String[] contents = {"Humpty Dumpty sat on a wall,",
                             "Humpty Dumpty had a great fall.",
                             "All the king's horses and all the king's men",
                             "Couldn't put Humpty together again."};
        
        for(String content : contents) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }

        // Searcher Manager
        SearcherManager searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());
        IndexSearcher indexSearcher = null;

        // Refresh, acquire and search
        searcherManager.maybeRefresh();
        indexSearcher = searcherManager.acquire();
        SearcherManagerTest.search(indexSearcher, "humpty");

        // Release indexSearcher and commit indexWriter
        searcherManager.release(indexSearcher);
        indexWriter.commit();

        contents = new String[]{"In this example, we intentionally strip out the usual test setup so that we can highlight",
                                "the important statements. Make a note of how we set up SearcherManager by passing",
                                "in an IndexWriter, a true value (so we can read all the uncommitted deletes), and a",
                                "default SearcherFactory. Before we perform a search, we call maybeRefresh to check"};
        for(String content : contents) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }
        // Refresh, acquire and search
        searcherManager.maybeRefresh();
        indexSearcher = searcherManager.acquire();
        SearcherManagerTest.search(indexSearcher, "intentionally");
    
        // Release indexSearcher and commit indexWriter
        searcherManager.release(indexSearcher);
        indexWriter.commit();
        
        contents = new String[]{ "A generation is analogous to versioning in a revision control system. In TrackingIndexWriter,",
                                 "when an index changes, a new generation is created and can be used to open the index in",
                                 "that particular point in time. TrackingIndexWriter is a wrapper class to IndexWriter. It provides",
                                 "the corresponding addDocument, updateDocument, and deleteDocument methods",
                                 "to keep a track of index changes. On each update, a long value is returned, reflecting the",
                                 "current index generation. This value can be used to acquire an IndexSearcher that includes",
                                 "all the updates up to this specific point (generation). This class is intended to run alongside" };
        for(String content : contents) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }
        // Refresh, acquire and search
        searcherManager.maybeRefresh();
        indexSearcher = searcherManager.acquire();
        SearcherManagerTest.search(indexSearcher, "wrapper");

        // Release indexSearcher and commit indexWriter
        searcherManager.release(indexSearcher);
        indexWriter.commit();

        indexSearcher = searcherManager.acquire();
        SearcherManagerTest.search(indexSearcher, "reflecting");
        searcherManager.release(indexSearcher);
    }

}
