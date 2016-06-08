package ch05;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ControlledRealTimeReopenThread;

import java.io.IOException;

/**
 * Created by valdesl on 08/06/2016.
 */
public class TrackingIndexWriterTest extends AbstractIndexWriterSearcher {

    private TrackingIndexWriter trackingIndexWriter;
    private final ControlledRealTimeReopenThread controlledRealTimeReopenThread;
    
    public TrackingIndexWriterTest() throws IOException {
        trackingIndexWriter = new TrackingIndexWriter(indexWriter);
        controlledRealTimeReopenThread = new ControlledRealTimeReopenThread(trackingIndexWriter, searcherManager, 5, 0.001f);
        controlledRealTimeReopenThread.start();
    }

    @Override
    public void commit() throws IOException, InterruptedException {
        System.out.println("Closing -> ControlledRealTimeReopenThread -> indexWriter.commit()");
//        synchronized (controlledRealTimeReopenThread) {
//            controlledRealTimeReopenThread.wait(10000);
//        }
        super.commit();
        controlledRealTimeReopenThread.close();
//        controlledRealTimeReopenThread.start();
    }
    
    public long addDocument(Document doc) throws IOException {
        return trackingIndexWriter.addDocument(doc);
    }

    public void search(String queryTerm, long generation) throws InterruptedException, IOException, ParseException {
        // wait milliseconds
        synchronized (controlledRealTimeReopenThread) {
            controlledRealTimeReopenThread.waitForGeneration(generation);
        }
        super.search(queryTerm);
    }

    @Override
    public void search(String queryTerm) throws InterruptedException, IOException, ParseException {
        // wait milliseconds
        synchronized (controlledRealTimeReopenThread) {
            controlledRealTimeReopenThread.wait(100);
        }
        super.search(queryTerm);
    }


    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        TrackingIndexWriterTest trackingIndexWriter = new TrackingIndexWriterTest();

        long indexGeneration = 0;
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
            indexGeneration = trackingIndexWriter.addDocument(doc);
            System.out.println(String.format("IndexGeneration: %d", indexGeneration));
        }

        trackingIndexWriter.search("humpty", indexGeneration);


        contents = new String[]{"In this example, we intentionally strip out the usual test setup so that we can highlight",
                                "the important statements. Make a note of how we set up SearcherManager by passing",
                                "in an IndexWriter, a true value (so we can read all the uncommitted deletes), and a",
                                "default SearcherFactory. Before we perform a search, we call maybeRefresh to check"};
        for(String content : contents) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexGeneration = trackingIndexWriter.addDocument(doc);
            System.out.println(String.format("IndexGeneration: %d", indexGeneration));
        }
        trackingIndexWriter.search("intentionally", indexGeneration);

        
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
            indexGeneration = trackingIndexWriter.addDocument(doc);
            System.out.println(String.format("IndexGeneration: %d", indexGeneration));
        }
        trackingIndexWriter.search("wrapper", indexGeneration);

        trackingIndexWriter.commit();

        trackingIndexWriter.search("reflecting", indexGeneration);

    }

}
