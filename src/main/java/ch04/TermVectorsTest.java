package ch04;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Created by valdesl on 06/06/2016.
 */
public class TermVectorsTest {

    public static void main(String[] args) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        FieldType textFieldType = new FieldType();
        textFieldType.setIndexed(true);
        textFieldType.setTokenized(true);
        textFieldType.setStored(true);
        textFieldType.setStoreTermVectors(true);
        textFieldType.setStoreTermVectorPositions(true);
        textFieldType.setStoreTermVectorOffsets(true);
        
        Document doc = new Document();
        Field textField = new Field("content", "", textFieldType);
        
        String[] contents = {"Humpty Dumpty sat on a wall,",
                            "Humpty Dumpty had a great fall.",
                            "All the king's horses and all the king's men",
                            "Couldn't put Humpty together again."};
        
        for(String content : contents) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }
        
        indexWriter.commit();
        
        IndexReader indexReader = DirectoryReader.open(directory);
        DocsAndPositionsEnum docsAndPositionsEnum = null;
        Terms termsVector = null;
        TermsEnum termsEnum = null;
        BytesRef term = null;
        String val = null;
        
        for (int i=0; i < indexReader.maxDoc(); i++) {
            termsVector = indexReader.getTermVector(i, "content");
            termsEnum = termsVector.iterator(termsEnum);
            
            while( (term = termsEnum.next()) != null) {
                val = term.utf8ToString();
                System.out.println("DocId: " + i);
                System.out.println("  term: " + val);
                System.out.println("  length: " + term.length);
                docsAndPositionsEnum = termsEnum.docsAndPositions(null, docsAndPositionsEnum);
                
                if(docsAndPositionsEnum.nextDoc() >= 0) {
                    int freq = docsAndPositionsEnum.freq();
                    System.out.println("  freq: " + freq);
                    for(int j=0; j < freq; j++) {
                        System.out.println("    [");
                        System.out.println("    position: " + docsAndPositionsEnum.nextPosition());
                        System.out.println("    offset start: " + docsAndPositionsEnum.startOffset());
                        System.out.println("    offset end: " + docsAndPositionsEnum.endOffset());
                        System.out.println("    ]");
                    }
                }
            }
        }
    }
    
}