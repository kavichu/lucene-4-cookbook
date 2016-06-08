package ch05;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
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
public class SearcherLifetimeManagerTest {


    public static void search(IndexSearcher indexSearcher, String queryTerm) throws IOException, ParseException {
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
    
    public static void main(String[] args) throws IOException {
        Directory directory = FSDirectory.open(new File("data/index"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        DirectoryReader directoryReader = DirectoryReader.open(indexWriter, true);

        SearcherLifetimeManager searcherLifetimeManager = new SearcherLifetimeManager();

        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        long searcherToken = searcherLifetimeManager.record(indexSearcher);

        indexSearcher = searcherLifetimeManager.acquire(searcherToken);

        if(indexSearcher != null ){
            try {
                SearcherLifetimeManagerTest.search(indexSearcher, "humpty");
            } catch (ParseException e) {
                e.printStackTrace();
            } finally{
                searcherLifetimeManager.release(indexSearcher);
                indexSearcher = null;
            }
        }else{ 
            // searcher was pruned, nofity user that the serach session has timeout
            System.out.println("searcher was pruned, nofity user that the serach session has timeout");
        }

        searcherLifetimeManager.prune(new SearcherLifetimeManager.PruneByAge(600.0));
    }

}
