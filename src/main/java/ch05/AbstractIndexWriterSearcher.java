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
public class AbstractIndexWriterSearcher {

    protected Directory directory;
    protected StandardAnalyzer analyzer;
    protected IndexWriterConfig config;
    protected IndexWriter indexWriter;
    protected SearcherManager searcherManager;

    public AbstractIndexWriterSearcher() throws IOException {
        directory = FSDirectory.open(new File("data/index"));
        analyzer = new StandardAnalyzer();

        config = new IndexWriterConfig(Version.LATEST, analyzer);
        indexWriter = new IndexWriter(directory, config);

        searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());
    }

    public void addData(String[] data) throws IOException {
        Document doc = new Document();
        TextField textField = new TextField("content", "", Field.Store.YES);
        for(String content : data) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }
    }

    public void commit() throws IOException, InterruptedException {
        indexWriter.commit();
    }

    public void rollback() throws IOException {
        indexWriter.rollback();
    }

    public void search(String queryTerm) throws ParseException, IOException, InterruptedException {
        IndexSearcher indexSearcher = searcherManager.acquire();

        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse(queryTerm);
        TopDocs topDocs = indexSearcher.search(query, 100);
        System.out.println(String.format("Searching for: %s", queryTerm));
        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(String.format("%d -> %f: %s", scoreDoc.doc, scoreDoc.score, doc.get("content")));
        }

        searcherManager.release(indexSearcher);
    }

}
