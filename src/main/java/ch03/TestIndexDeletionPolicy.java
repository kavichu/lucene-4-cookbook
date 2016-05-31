package ch03;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.List;

/**
 * Created by valdesl on 31/05/2016.
 */
public class TestIndexDeletionPolicy {
    
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

        SnapshotDeletionPolicy policy = new SnapshotDeletionPolicy(NoDeletionPolicy.INSTANCE);
        config.setIndexDeletionPolicy(policy);

        IndexWriter indexWriter = new IndexWriter(directory, config);
        IndexCommit lastSnapshot;

        Document document = new Document();
        indexWriter.addDocument(document);
        indexWriter.commit();

        lastSnapshot = policy.snapshot();

        document = new Document();
        indexWriter.addDocument(document);
        indexWriter.commit();    
        lastSnapshot = policy.snapshot();

        document = new Document();
        indexWriter.addDocument(document);
        indexWriter.rollback();
        indexWriter.close();


        List<IndexCommit> commits = DirectoryReader.listCommits(directory);

        System.out.println("Commits count: " + commits.size());
        for(IndexCommit commit : commits) {
            IndexReader reader = DirectoryReader.open(commit);
            System.out.println("Commit " + commit.getSegmentCount());
            System.out.println("Number of docs: " + reader.numDocs());
        }

        System.out.println("\nSnapshots count: " + policy.getSnapshotCount());
        List<IndexCommit> snapshots = policy.getSnapshots();
        for(IndexCommit snapshot: snapshots) {
            IndexReader reader = DirectoryReader.open(snapshot);
            System.out.println("Snapshot " + snapshot.getSegmentCount());
            System.out.println("Number of docs: " + reader.numDocs());
        }

        policy.release(lastSnapshot);
        System.out.println("\nSnapshots count: " + policy.getSnapshotCount());
    }    
}
