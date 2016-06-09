package ch06;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by valdesl on 09/06/2016.
 */
public class UserSecurityFilter  extends Filter {
    
    private String userIdField;
    private String groupIdField;
    private String userId;
    private String groupId;

    public UserSecurityFilter(String userIdfield, 
                String groupIdField, String userId, String groupId) {
        this.userIdField = userIdfield;
        this.groupIdField = groupIdField;
        this.userId = userId;
        this.groupId = groupId;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptdocs) throws IOException {
        final SortedDocValues userIdDocValues = FieldCache.DEFAULT.getTermsIndex(context.reader(), userIdField);
        final SortedDocValues groupIdDocValues = FieldCache.DEFAULT.getTermsIndex(context.reader(), groupIdField);

        final int userIdOrd = userIdDocValues.lookupTerm(new BytesRef(userId));
        final int groupIdOrd = groupIdDocValues.lookupTerm(new BytesRef(groupId));

        return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptdocs){
            @Override
            protected final boolean matchDoc(int doc) {
                final int userIdDocOrd = userIdDocValues.getOrd(doc);
                final int groupIdDocOrd = groupIdDocValues.getOrd(doc);
                return userIdDocOrd == userIdOrd || groupIdDocOrd >= groupIdOrd;
            }
        };
    }

    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc = new Document();
        StringField stringFieldFile = new StringField("file", "", Field.Store.YES);
        StringField stringFieldUserId = new StringField("userId", "", Field.Store.YES);
        StringField stringFieldGroupId = new StringField("groupId", "", Field.Store.YES);

        String[] files = {"C:\\shared\\finance\\2014", "C:\\shared\\finance\\2015", "C:\\shared\\finance\\2016"};
        HashMap<String, String> userIds = new HashMap<String, String>();
        HashMap<String, String> groupIds = new HashMap<String, String>();

        userIds.put(files[0], "1001");
        groupIds.put(files[0], "20");
        userIds.put(files[1], "1101");
        groupIds.put(files[1], "30");
        userIds.put(files[2], "1205");
        groupIds.put(files[2], "40");

        for(String file : files) {
            doc.removeField("file");
            doc.removeField("userId");
            doc.removeField("groupId");
            stringFieldFile.setStringValue(file);
            stringFieldUserId.setStringValue(userIds.get(file));
            stringFieldGroupId.setStringValue(groupIds.get(file));
            doc.add(stringFieldFile);
            doc.add(stringFieldUserId);
            doc.add(stringFieldGroupId);
            indexWriter.addDocument(doc);
        }        
        indexWriter.commit();
    	indexWriter.close();

        // UserSecurityFilter
        UserSecurityFilter userSecurityFilter = new UserSecurityFilter("userId", "groupId", "1001", "40");
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = new MatchAllDocsQuery();
        TopDocs topDocs = indexSearcher.search(query, userSecurityFilter, 100);

        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            doc = indexReader.document(scoreDoc.doc);
            System.out.println(String.format("file: %s - userId: %s - groupId: %s", doc.get("file"), doc.get("userId"), doc.get("groupId")));
        }

        indexReader.close();

    }

}
