package simplenlg.lexicon.verbnet;

import java.util.HashSet;
import java.util.Set;

import simplenlg.lexicon.LexicalClass;
import simplenlg.lexicon.lexicalitems.Verb;

public class VerbnetClass extends LexicalClass<Verb> {

	Set<VerbnetFrame> frames;

	public VerbnetClass(String id) {
		super(id);
		this.frames = new HashSet<VerbnetFrame>();
	}

	public void addFrame(VerbnetFrame frame) {
		this.frames.add(frame);
	}

	public Set<VerbnetFrame> getFrames() {
		Set<VerbnetFrame> allFrames = new HashSet<VerbnetFrame>();

		if (hasSuperclass()) {
			allFrames.addAll(((VerbnetClass) this.superclass).getFrames());
		}

		return allFrames;
	}

	public Set<VerbnetFrame> getDirectFrames() {
		return this.frames;
	}

	public boolean hasFrames() {
		return !this.frames.isEmpty();
	}

	public void addMember(Verb newMember) {
		newMember.addVerbnetClass(this);
		this.members.add(newMember);
	}

	public boolean hasMember(Verb member) {
		return this.members.contains(member);
	}
}