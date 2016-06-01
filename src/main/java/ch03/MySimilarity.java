package ch03;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * Created by valdesl on 01/06/2016.
 */
public class MySimilarity extends Similarity {
    
    private Similarity sim = null;
    
    public MySimilarity(Similarity sim){
        this.sim = sim;
    }
    
    @Override
    public long computeNorm(FieldInvertState fieldInvertState) {
        return sim.computeNorm(fieldInvertState);
    }

    @Override
    public SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStatistics, TermStatistics... termStatisticses) {
        return sim.computeWeight(queryBoost, collectionStatistics, termStatisticses);
    }

    @Override
    public SimScorer simScorer(SimWeight simWeight, AtomicReaderContext atomicReaderContext) throws IOException {
        final SimScorer scorer = sim.simScorer(simWeight, atomicReaderContext);
        final NumericDocValues values = atomicReaderContext.reader().getNumericDocValues("ranking");
        return new SimScorer(){
            @Override 
            public float score(int i, float v) {
                return values.get(i) * scorer.score(i, v);
            }
            @Override
            public float computeSlopFactor(int i){
                return scorer.computeSlopFactor(i);
            }
            @Override 
            public float computePayloadFactor(int i, int i1, int i2, BytesRef bytesRef) {
                return scorer.computePayloadFactor(i, i1, i2, bytesRef);
            }
        };
    }
}
