/**
 * 
 */
package uk.ac.cam.dr369.learngrammar.commonality;

import java.util.List;
import java.util.SortedSet;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.parsing.DependencyStructure;

import com.google.common.collect.ImmutableSortedSet;

public class ScoredDependencyStructure extends DependencyStructure implements Comparable<DependencyStructure> {
	private static final long serialVersionUID = 1399484231232570153L;
	private final SortedSet<Score> scores;
	private final double verbosityPenalty;
	private final double score;
	
	public ScoredDependencyStructure(List<GrammaticalRelation> grs, List<Token> tokens, List<Score> scores, double verbosityPenalty) {
		super(grs, tokens, false);
		this.scores = ImmutableSortedSet.copyOf(scores);
		this.verbosityPenalty = verbosityPenalty;
		
		int i = 0;
		for (Score score : scores) {
			i += score.value();
		}
		this.score = i;
	}
	public double score() {
		return score;
//		return (1/verbosityPenalty) * ((double) i); // no longer needed as sentences limited to 13 words max
	}
	public SortedSet<Score> scores() {
		return scores;
	}
	public double getVerbosityPenalty() {
		return verbosityPenalty;
	}
	public int compareTo(DependencyStructure o) {
		if (o instanceof ScoredDependencyStructure) {
			ScoredDependencyStructure sds = (ScoredDependencyStructure) o;
			return (int) (score() - sds.score());
		}
		else
			return super.compareTo(o);
	}
	@Override
	public String toString() {
		return "Score : "+score()+"; "+super.toString();
	}
}