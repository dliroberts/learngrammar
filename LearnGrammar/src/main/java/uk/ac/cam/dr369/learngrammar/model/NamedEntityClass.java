package uk.ac.cam.dr369.learngrammar.model;

public enum NamedEntityClass {
	ORGANISATION("I-ORG"), PERSON("I-PER"), LOCATION("I-LOC"),
	TIME("I-TIM"), DATE("I-DAT"), CURRENCY("I-MON"),
	MISCELLANEOUS("O");
	
	private final String tag;
	
	private NamedEntityClass(String tag) {
		this.tag = tag;
	}
	
	public String toString() {
		return tag;
	}
	public static NamedEntityClass valueOfByLabel(String label) {
		if (label == null)
			return null;
		for (NamedEntityClass nec : values()) {
			if (nec.tag.equals(label))
				return nec;
		}
		throw new IllegalArgumentException("No enum const class "+NamedEntityClass.class+" instance with label="+label);
	}
}