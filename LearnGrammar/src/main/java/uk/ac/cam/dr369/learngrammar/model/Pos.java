package uk.ac.cam.dr369.learngrammar.model;

import java.util.Set;

/**
 * A part of speech. Parser-independent representation.
 * @author duncan.roberts
 *
 */
public interface Pos {
	public String description();
	public String getLabel();
	Set<Pos> parents();
	boolean ancestorOf(Pos child);
	boolean descendentOf(Pos parent);
	public Set<Pos> ancestors();
	public double weight();
}