/**
 * 
 */
package uk.ac.cam.dr369.learngrammar.commonality;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.dr369.learngrammar.util.Utils;

public class FeatureProfile {
	private final boolean lemmas;
	private final boolean posTags;
	private final boolean supertags;
	private final boolean grTypes;
	private final boolean recurseHierarchy;
	private final int baseScore;
	private final int depthBonus;
	private final int maxHeight;
	private final String desc;
	public FeatureProfile(boolean lemmas, boolean posTags, boolean supertags, boolean grTypes,
			boolean recurseHierarchy, int baseScore, int depthBonus, int maxHeight) {
		this.lemmas = lemmas;
		this.posTags = posTags;
		this.supertags = supertags;
		this.grTypes = grTypes;
		this.recurseHierarchy = recurseHierarchy;
		this.baseScore = baseScore;
		this.depthBonus = depthBonus;
		this.maxHeight = maxHeight;
		
		List<String> feats = new ArrayList<String>();
		if (lemmas)
			feats.add("lemmas");
		if (posTags)
			feats.add("POS");
		if (supertags)
			feats.add("supertags");
		if (grTypes)
			feats.add("GR types");
		desc = Utils.formatForPrinting(feats);
	}
	public double score(Twig struct) {
		double score = baseScore;
		int i = struct.getGrs().size() + struct.getTokens().size();
		i = Commonality.lowerSalientStructureHeight(this, i);
		while (i > 0) {
			score += depthBonus;
			i = Commonality.lowerSalientStructureHeight(this, i);
		}
		score *= struct.weight();
		return score;
	}
	public int maxHeight() {
		return maxHeight;
	}
	public int baseScore() {
		return baseScore;
	}
	public int depthBonus() {
		return depthBonus;
	}
	public boolean lemmas() {
		return lemmas;
	}
	public boolean recurseHierarchy() {
		return recurseHierarchy;
	}
	public boolean pos() {
		return posTags;
	}
	public boolean supertags() {
		return supertags;
	}
	public boolean grTypes() {
		return grTypes;
	}
	public boolean initialGrValues() {
		return false; // FIXME
	}
	public boolean grs() {
		return grTypes() || initialGrValues();
	}
	public boolean tokens() {
		return lemmas() || pos() || supertags();
	}
	public String featureDescription() {
		return desc;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + baseScore;
		result = prime * result + depthBonus;
		result = prime * result + (grTypes ? 1231 : 1237);
		result = prime * result + (lemmas ? 1231 : 1237);
		result = prime * result + maxHeight;
		result = prime * result + (posTags ? 1231 : 1237);
		result = prime * result + (recurseHierarchy ? 1231 : 1237);
		result = prime * result + (supertags ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureProfile other = (FeatureProfile) obj;
		if (baseScore != other.baseScore)
			return false;
		if (depthBonus != other.depthBonus)
			return false;
		if (grTypes != other.grTypes)
			return false;
		if (lemmas != other.lemmas)
			return false;
		if (maxHeight != other.maxHeight)
			return false;
		if (posTags != other.posTags)
			return false;
		if (recurseHierarchy != other.recurseHierarchy)
			return false;
		if (supertags != other.supertags)
			return false;
		return true;
	}
	public String toString() {
		return desc;
	}
//	public boolean subsumes(FeatureProfile o) {
//		boolean thisSubsumes = false;
//		boolean otherSubsumes = false;
//		
//		if (lemmas && !o.lemmas)
//			thisSubsumes = true;
//		else if (!lemmas && o.lemmas)
//			otherSubsumes = true;
//		
//		if (grTypes && !o.grTypes)
//			thisSubsumes = true;
//		else if (!grTypes && o.grTypes)
//			otherSubsumes = true;
//		
//		if (supertags && !o.supertags)
//			thisSubsumes = true;
//		else if (!supertags && o.supertags)
//			otherSubsumes = true;
//		
//		if (posTags && !o.posTags)
//			thisSubsumes = true;
//		else if (!posTags && o.posTags)
//			otherSubsumes = true;
//
//		if (recurseHierarchy && !o.recurseHierarchy)
//			thisSubsumes = true;
//		else if (!recurseHierarchy && o.recurseHierarchy)
//			otherSubsumes = true;
//		
//		if (maxHeight > o.maxHeight)
//			thisSubsumes = true;
//		else if (maxHeight < o.maxHeight)
//			otherSubsumes = true;
//		
//		return thisSubsumes && !otherSubsumes;
//	}
}