/**
 * 
 */
package simplenlg.lexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import simplenlg.lexicon.lexicalitems.LexicalItem;

public class LexicalClass<T extends LexicalItem> {

	protected List<LexicalClass<T>> subclasses;
	protected LexicalClass<T> superclass;
	protected String id;
	protected Set<T> members;
	protected LexiconInterface parentLexicon;

	public LexicalClass(String id) {
		this.subclasses = new ArrayList<LexicalClass<T>>();
		this.superclass = null;
		this.members = new HashSet<T>();
		this.id = id;
		this.parentLexicon = null;
	}

	public LexicalClass(String id, Collection<T> classMembers) {
		this(id);
		addMembers(classMembers);
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return this.id;
	}

	public void setSuperclass(LexicalClass<T> c) {
		this.superclass = c;
	}

	public void addSubclass(LexicalClass<T> c) {
		this.subclasses.add(c);
		c.setSuperclass(this);
	}

	public boolean hasSuperclass() {
		return this.superclass != null;
	}

	public LexicalClass<T> getSuperclass() {
		return this.superclass;
	}

	public boolean hasSubclasses() {
		return !this.subclasses.isEmpty();
	}

	public List<LexicalClass<T>> getSubclasses() {
		return this.subclasses;
	}

	public void addMembers(Collection<T> newMembers) {

		for (T member : newMembers) {
			this.members.add(member);
		}
	}

	public boolean hasMember(LexicalItem member) {
		return this.members.contains(member);
	}

	public Set<T> getMembers() {
		return this.members;
	}

	public void setParentLexicon(LexiconInterface lex) {
		this.parentLexicon = lex;
	}

	public LexiconInterface getParentLexicon() {
		return this.parentLexicon;
	}

}