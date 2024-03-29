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

    public static void printAll(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException {
        System.out.println(String.format("Total hits: %d", topDocs.totalHits));
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(String.format("%d - name: %s - content: %s - num: %s", scoreDoc.doc, doc.get("name"), doc.get("content"), doc.get("num")));
        }
    }

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
        AdvancedQueryParser.printAll(topDocs, indexSearcher);

        System.out.println("Wildcard search");
        query = queryParser.parse("toge*");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);

        System.out.println("Wildcard search - allow leading wildcard");
        queryParser.setAllowLeadingWildcard(true);
        query = queryParser.parse("*orses");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);
        queryParser.setAllowLeadingWildcard(false);


        System.out.println("Term range search");
        query = queryParser.parse("[aa TO c]");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);


        System.out.println("Autogenerated phrase query");
        queryParser.setAutoGeneratePhraseQueries(true);
        query = queryParser.parse("humpty+dumpty+sat");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);
        queryParser.setAutoGeneratePhraseQueries(false);


        System.out.println("Date resolution");
        System.out.println("queryParser.setDateResolution(\"date\", DateTools.Resolution.DAY);");
        System.out.println("queryParser.setLocale(Locale.US);");
        System.out.println("queryParser.setTimeZone(TimeZone.getTimeZone(\"America/New_York\"));");
//        queryParser.setDateResolution("date", DateTools.Resolution.DAY);
//        queryParser.setLocale(Locale.US);
//        queryParser.setTimeZone(TimeZone.getTimeZone("America/New_York"));



        System.out.println("Default operator");
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        query = queryParser.parse("humpty dumpty");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);
        queryParser.setDefaultOperator(QueryParser.Operator.OR);



        System.out.println("Enable position increments");
        queryParser.setEnablePositionIncrements(true);
        query = queryParser.parse("\"humpty dumpty\"");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);
        queryParser.setEnablePositionIncrements(false);


        System.out.println("Fuzzy query");
        float minSim = queryParser.getFuzzyMinSim();
        int prefixLen = queryParser.getFuzzyPrefixLength();
        queryParser.setFuzzyMinSim(2f);
        queryParser.setFuzzyPrefixLength(3);
        query = queryParser.parse("hump~");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);
        queryParser.setFuzzyMinSim(minSim);
        queryParser.setFuzzyPrefixLength(prefixLen);



        System.out.println("Lowercase expanded term");
        queryParser.setLowercaseExpandedTerms(true);
        query = queryParser.parse("\"Humpty Dumpty\"");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);
        queryParser.setLowercaseExpandedTerms(false);



        System.out.println("Phrase slop");
        int slop = queryParser.getPhraseSlop();
        queryParser.setPhraseSlop(3);
        query = queryParser.parse("\"Humpty Dumpty wall\"");
        topDocs = indexSearcher.search(query, 100);
        AdvancedQueryParser.printAll(topDocs, indexSearcher);
        queryParser.setPhraseSlop(slop);
    }

}
