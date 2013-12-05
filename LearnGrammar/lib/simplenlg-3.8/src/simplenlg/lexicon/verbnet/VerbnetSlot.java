package simplenlg.lexicon.verbnet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class VerbnetSlot {

	VerbnetSlotType type;
	Set<String> contents;
	Set<String> selectionalRestrictions;
	boolean optional;

	public VerbnetSlot(VerbnetSlotType type) {
		setType(type);
		this.contents = new HashSet<String>();
		this.selectionalRestrictions = new HashSet<String>();
	}

	public VerbnetSlot(VerbnetSlotType type, Collection<String> contents) {
		this(type);
		setContents(contents);
	}

	public Set<String> getSelectionalRestrictions() {
		return this.selectionalRestrictions;
	}

	public boolean hasSelectionalRestrictions() {
		return !this.selectionalRestrictions.isEmpty();
	}

	public void setSelectionalRestrictions(Collection<String> selRestr) {
		this.selectionalRestrictions.clear();
		this.selectionalRestrictions.addAll(selRestr);
	}

	public void addSelectionalRestriction(String selRestr) {
		this.selectionalRestrictions.add(selRestr);
	}

	public VerbnetSlotType getType() {
		return this.type;
	}

	public void setType(VerbnetSlotType type) {
		this.type = type;
	}

	public Set<String> getContents() {
		return this.contents;
	}

	public void setContents(Collection<String> contents) {
		this.contents.clear();
		this.contents.addAll(contents);
	}

	public void addContent(String content) {
		this.contents.add(content);
	}

	public boolean isOptional() {
		return this.optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

}
