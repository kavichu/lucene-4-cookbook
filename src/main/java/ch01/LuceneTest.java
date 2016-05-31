package ch01;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
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
import java.util.ArrayList;
import java.util.List;


/**
 * Created by valdesl on 24/05/2016.
 */
public class LuceneTest {

    Analyzer analyzer;
    Directory directory;
    IndexWriterConfig config;
    IndexWriter indexWriter;

    IndexReader indexReader;
    IndexSearcher indexSearcher;

    public LuceneTest() throws IOException, ParseException {
        analyzer = new WhitespaceAnalyzer();
        directory = new RAMDirectory();
        config = new IndexWriterConfig(Version.LATEST, analyzer);
    }

    public void addDocument(String text) throws IOException {
        indexWriter = new IndexWriter(directory, config);
        Document doc = new Document();
        doc.add(new TextField("Content", text, Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.close();
    }

    public void search(String text) throws IOException, ParseException {
        indexReader = DirectoryReader.open(directory);
        indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser("Content", analyzer);
        Query query = parser.parse(text);

        int hitsPerPage = 10;
        TopDocs docs = indexSearcher.search(query, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        int end = Math.min(docs.totalHits, hitsPerPage);
        System.out.println("Total Hits: " + docs.totalHits);
        System.out.print("Results: ");
        for(int i=0; i < end; i++){
            Document d = indexSearcher.doc(hits[i].doc);
            System.out.println("Content: " + d.get("Content"));
        }
    }

    public List<Document> getPage(int from, int size) throws ParseException, IOException {
        List<Document> documents = new ArrayList<Document>();
        QueryParser parser = new QueryParser("Content", analyzer);
        Query query = parser.parse("SearchTerm");
        TopDocs hits = indexSearcher.search(query, size);
        int end = Math.min(hits.totalHits, size);
        for(int i = 0; i < end; i++){
            int docId = hits.scoreDocs[i].doc;
            // Load the document
            Document d = indexSearcher.doc(docId);
            documents.add(d);
        }
        return documents;
    }


    public static void main(String[] args) throws IOException, ParseException {

        LuceneTest test = new LuceneTest();

        test.addDocument("Lucene is an Information Retrieval Library written in Java");
        test.search("Lucene");

    }




}
