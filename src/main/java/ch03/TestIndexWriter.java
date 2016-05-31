package ch03;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by valdesl on 30/05/2016.
 */
public class TestIndexWriter {
    
    public static void main(String[] args) throws IOException {
        // Creating and IndexWriter
        FSDirectory directory = FSDirectory.open(new File("C:/lucene-index"));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setRAMBufferSizeMB(64);
        config.setMaxBufferedDocs(4000);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        // Adding a StringField to a document and index
        {
            Document document = new Document();
            document.add(new StringField("telephone_number", "1 55 44 1258321", Field.Store.YES));
            document.add(new StringField("area_code", "0484", Field.Store.YES));
            indexWriter.addDocument(document);
            indexWriter.commit();
        }
        
        // Adding a TextField to a document and index
        {
            Document document = new Document();
            String text = "Lucene is an information Retrieval library written in Java";
            document.add(new TextField("text", text,  Field.Store.YES));
            indexWriter.addDocument(document);
            indexWriter.commit();
        }
        
        // Adding Numeric Fields
        {
            IntField intField = new IntField("int_value", 100, Field.Store.YES);
            LongField longField = new LongField("long_value", 100L, Field.Store.YES);
            FloatField floatField = new FloatField("float_value", 100.0F, Field.Store.YES);
            DoubleField doubleField = new DoubleField("double_value", 100.0D, Field.Store.YES);
            
            FieldType sortedIntField = new FieldType();
            sortedIntField.setNumericType(FieldType.NumericType.INT);
            sortedIntField.setNumericPrecisionStep(Integer.MAX_VALUE);
            sortedIntField.setStored(false);
            sortedIntField.setIndexed(true);
            IntField intFieldSorted = new IntField("int_value_sort", 100, sortedIntField);
            
            Document document = new Document();
            document.add(intField);
            document.add(longField);
            document.add(floatField);
            document.add(doubleField);
            document.add(intFieldSorted);
            indexWriter.addDocument(document);
            indexWriter.commit();
        }

    }
    
}
