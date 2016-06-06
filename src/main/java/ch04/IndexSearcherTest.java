package ch04;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import java.io.File;
import java.io.IOException;

/**
 * Created by valdesl on 06/06/2016.
 */
public class IndexSearcherTest {

    public static void printResults(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
        for(int i=0; i < topDocs.scoreDocs.length; i++){
            int docId = topDocs.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println(String.format("%d: %s", i, doc.get("urlPath")));
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Directory directory = FSDirectory.open(new File("C:\\lucene-index\\confluence-index"));
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        
        // Using a TermQuery
        Query query = new TermQuery(new Term("contentBody", "luis"));
        TopDocs topDocs = indexSearcher.search(query, 100);
        printResults(topDocs, indexSearcher);

        // Using a QueryParser
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser("contentBody", analyzer);
        query = queryParser.parse("itaipu binacional");
        topDocs = indexSearcher.search(query, 100);
        printResults(topDocs, indexSearcher);
    }
}
