package ch04;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by valdesl on 07/06/2016.
 */
public class MyCollector extends Collector{

    private int totalHits = 0;
    private int docBase;
    private Scorer scorer;
    private List<ScoreDoc> topDocs = new ArrayList<ScoreDoc>();
    private ScoreDoc[] scoreDocs;

    public MyCollector() {

    }

    public void setScorer(Scorer scorer) {
        this.scorer = scorer;
    }

    public boolean acceptsDocsOutOfOrder() {
        return false;
    }

    public void collect(int doc) throws IOException {
        float score = scorer.score();
        if(score > 0) {
            score += (1 / (doc + 1));
        }
        ScoreDoc scoreDoc = new ScoreDoc(doc + docBase, score);
        topDocs.add(scoreDoc);
        totalHits++;
    }

    public void setNextReader(AtomicReaderContext context) {
        this.docBase = context.docBase;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public ScoreDoc[] getScoreDocs() {
        if(scoreDocs != null){
            return scoreDocs;
        }
        Collections.sort(topDocs, new Comparator<ScoreDoc>() {
            public int compare(ScoreDoc d1, ScoreDoc d2) {
                if (d1.score > d2.score) {
                    return -1;
                } else if (d1.score == d2.score) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        scoreDocs = topDocs.toArray(new ScoreDoc[topDocs.size()]);
        return scoreDocs;
    }


    public static void main(String[] args) throws IOException, ParseException {
        MyCollector collector = new MyCollector();

        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory directory = FSDirectory.open(new File("C:\\lucene-index\\confluence-index"));

        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser queryParser = new QueryParser("contentBody", analyzer);
        Query query = queryParser.parse("itaipu binacional");

        indexSearcher.search(query, collector);

        for(ScoreDoc scoreDoc : collector.getScoreDocs()) {
            Document doc = indexReader.document(scoreDoc.doc);
            System.out.println(String.format("%f: %s", scoreDoc.score, doc.get("urlPath")));
        }
    }
}
