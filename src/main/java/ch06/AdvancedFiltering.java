package ch06;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by valdesl on 09/06/2016.
 */
public class AdvancedFiltering {
    
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc = new Document();
        StringField stringField = new StringField("name", "", Field.Store.YES);
        TextField textField = new TextField("content", "", Field.Store.YES);
        IntField intField = new IntField("num", 0, Field.Store.YES);

        String[] names = {"First", "Second", "Third", "Fourth"};
        HashMap<String, String> contents = new HashMap<String, String>();
        HashMap<String, Integer> nums = new HashMap<String, Integer>();
        contents.put("First",  "Humpty Dumpty sat on a wall,");
        nums.put("First", 100);
        contents.put("Second",  "Humpty Dumpty had a great fall.");
        nums.put("Second", 200);
        contents.put("Third",  "All the king's horses and all the king's men");
        nums.put("Third", 300);
        contents.put("Fourth",  "Couldn't put Humpty together again.'");
        nums.put("Fourth", 400);

        for (String name : names) {
            doc.removeField("name");
            doc.removeField("content");
            doc.removeField("num");
            
            stringField.setStringValue(name);
            textField.setStringValue(contents.get(name));
            intField.setIntValue(nums.get(name));

            doc.add(stringField);
            doc.add(textField);
            doc.add(intField);

            indexWriter.addDocument(doc);
        }
        indexWriter.commit();
        indexWriter.close();

        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        List<Filter> filters = new ArrayList<Filter>();
        TermRangeFilter termRangeFilter = TermRangeFilter.newStringRange("name", "A", "G", true, true);
        NumericRangeFilter numericRangeFilter = NumericRangeFilter.newIntRange("num", 200, 400, true, true);
        FieldCacheRangeFilter fieldCacheRangeFilter = FieldCacheRangeFilter.newStringRange("name", "A", "G", true, true);
        QueryWrapperFilter queryWrapperFilter = new QueryWrapperFilter(new TermQuery(new Term("content", "together")));
        PrefixFilter prefixFilter = new PrefixFilter(new Term("name", "F"));
        FieldCacheTermsFilter fieldCacheTermsFilter = new FieldCacheTermsFilter("name", "First");
        FieldValueFilter fieldValueFilter = new FieldValueFilter("name1");
        CachingWrapperFilter cachingWrapperFilter = new CachingWrapperFilter(termRangeFilter);

        filters.add(termRangeFilter);
        filters.add(numericRangeFilter);
        filters.add(fieldCacheRangeFilter);
        filters.add(queryWrapperFilter);
        filters.add(prefixFilter);
        filters.add(fieldCacheTermsFilter);
        filters.add(fieldValueFilter);
        filters.add(cachingWrapperFilter);         


        for(Filter filter : filters){
            Query query = new TermQuery(new Term("content", "humpty"));
            TopDocs topDocs = indexSearcher.search(query, filter, 100);
            System.out.println(String.format("%s -> Searching 'humpty' - %d hits", filter.getClass().getSimpleName(), topDocs.totalHits));
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                doc = indexReader.document(scoreDoc.doc);
                System.out.println(String.format("name: %s - content: %s - num: %s",
                                                doc.getField("name").stringValue(),
                                                doc.getField("content").stringValue(),
                                                doc.getField("num").stringValue()));
            }
        }

    }
}
