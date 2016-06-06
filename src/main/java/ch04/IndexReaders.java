package ch04;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by valdesl on 06/06/2016.
 */
public class IndexReaders {
    
    public static void main(String[] args) throws IOException {
        // open a directory
        Directory directory = FSDirectory.open(new File("C:\\lucene-index\\confluence-index"));
        // set up DirectoryReader
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        // pull a list of underlying AtomicReaders
        List<AtomicReaderContext> atomicReaderContexts = directoryReader.leaves();
        //  retrieve the first AtomicReader from the list
        AtomicReader atomicReader = atomicReaderContexts.get(0).reader();
        // open another DirectoryReader by calling openIfChanged
        DirectoryReader newDirectoryReader = DirectoryReader.openIfChanged(directoryReader);
        
        // assign newDirectoryReader
        if(newDirectoryReader != null) {
            IndexSearcher indexSearcher = new IndexSearcher(newDirectoryReader);
            directoryReader.close();
        }
    }

}
