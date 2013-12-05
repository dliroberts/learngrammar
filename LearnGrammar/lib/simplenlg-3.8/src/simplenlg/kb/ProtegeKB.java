/* ==================================================
 * SimpleNLG: An API for Natural Language Generation
 * ==================================================
 *
 * Copyright (c) 2007, the University of Aberdeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted FOR RESEARCH PURPOSES ONLY, provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 * 		this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 * 3. Neither the name of the University of Aberdeen nor the names of its contributors 
 * 	  may be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 *    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 *    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 *    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 *    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *     LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *     ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *     EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *     
 *  Redistribution and use for purposes other than research requires special permission by the
 *  copyright holders and contributors. Please contact Ehud Reiter (ereiter@csd.abdn.ac.uk) for
 *  more information.
 *     
 *	   =================    
 *     Acknowledgements:
 *     =================
 *     This library contains a re-implementation of some rules derived from the MorphG package
 *     by Guido Minnen, John Carroll and Darren Pearce. You can find more information about MorphG
 *     in the following reference:
 *     	Minnen, G., Carroll, J., and Pearce, D. (2001). Applied Morphological Processing of English.
 *     		Natural Language Engineering 7(3): 207--223.
 *     Thanks to John Carroll (University of Sussex) for permission to re-use the MorphG rules. 
 */

package simplenlg.kb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import simplenlg.exception.SimplenlgException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

// TODO: Auto-generated Javadoc
/**
 * ProtegeKB is an interface to a Protege knowledge base
 * <P>
 * This class is a fairly simple "wrapper" which translates NLGKB methods to
 * appropriate Protege methods
 * <P>
 * ProtegeClass is similarly a wrapper for a Protege Class or Instance, which
 * implements KBEntity methods
 * <P>
 * If this class is used, protege.jar must be included as a library
 * 
 * @author ereiter
 */

public class ProtegeKB implements NLGKB {

	// Protege KB
	/** The kb. */
	KnowledgeBase kb;

	// we keep our own className->ProtegeClass map, for efficiency
	// if a className maps to null, this means the class does not exist
	/** The lookup class. */
	private Map<String, ProtegeClass> lookupClass;

	/**
	 * create a ProtegeKB from the specific Protege KB (.pprj) file
	 * 
	 * @param filename
	 *            the filename
	 */
	public ProtegeKB(String filename) {
		// open the Protege KB with the specified filename
		List errors = new ArrayList();
		Project project = new Project(filename, errors);
		this.lookupClass = new HashMap<String, ProtegeClass>();
		if (errors.size() == 0) {
			this.kb = project.getKnowledgeBase();
		} else {
			this.kb = null;
			throw new SimplenlgException("Protege KB: " + errors.toString());
		}
	}

	/**
	 * create a ProtegeKB from a specific Protege knowledge base.
	 * 
	 * @param kb
	 *            the kb
	 */
	public ProtegeKB(KnowledgeBase kb) {
		this.lookupClass = new HashMap<String, ProtegeClass>();
		this.kb = kb;
	}

	// return ProtegeClass object for a name
	// return null if doesn't exist
	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.kb.NLGKB#getClass(java.lang.String)
	 */
	public KBEntity getClass(String className) {

		// first check if name is already in lookup table
		if (this.lookupClass.containsKey(className)) {
			return this.lookupClass.get(className);
		}

		// else see if Protege knows about this class
		Frame c = this.kb.getFrame(className); // get Frame object (includes
		// classes and instances)
		if (c != null) { // frame exists
			ProtegeClass pc = new ProtegeClass(this, c);
			this.lookupClass.put(className, pc); // put in table
			return pc;
		} else { // frame doesn't exist
			this.lookupClass.put(className, null); // mark class as
			// null in table
			return null;
		}
	}

	// return T if ancestor is a superclass of class
	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.kb.NLGKB#isAncestor(java.lang.String, java.lang.String)
	 */
	public boolean isAncestor(String className, String ancestorName) {
		Cls c = this.kb.getCls(className);
		Cls ancestor = this.kb.getCls(ancestorName);
		return c == ancestor || c.hasSuperclass(ancestor);
	}

	// return the actual KB object
	/**
	 * Gets the kB.
	 * 
	 * @return the kB
	 */
	public KnowledgeBase getKB() {
		return this.kb;
	}

	// set the KB
	/**
	 * Sets the kB.
	 * 
	 * @param kb
	 *            the new kB
	 */
	public void setKB(KnowledgeBase kb) {
		this.lookupClass = new HashMap<String, ProtegeClass>();
		this.kb = kb;
	}

	// get an instance of a class; used for testing
	/**
	 * Gets the instance of class.
	 * 
	 * @param className
	 *            the class name
	 * 
	 * @return the instance of class
	 */
	public ProtegeClass getInstanceOfClass(String className) {
		Cls c = this.kb.getCls(className);
		Frame instance = (Frame) getFirstInCollection(c.getInstances());
		if (instance != null) {
			return new ProtegeClass(this, instance);
		} else {
			return null;
		}
	}

	/**
	 * Gets the first in collection.
	 * 
	 * @param coll
	 *            the coll
	 * 
	 * @return the first in collection
	 */
	Object getFirstInCollection(Collection coll) {
		// return first element of collection
		// return null if collection is empty
		Iterator it = coll.iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}

	/**
	 * Gets the subclass names.
	 * 
	 * @param className
	 *            the class name
	 * 
	 * @return the subclass names
	 */
	public Set<String> getSubclassNames(String className) {
		// return a set of the names of all subclasses of the specified class
		// don't include the class iteself
		Cls c = this.kb.getCls(className);
		Iterator it = c.getSubclasses().iterator();
		Set<String> result = new HashSet<String>();
		while (it.hasNext()) {
			Cls subclass = (Cls) it.next();
			String subclassName = subclass.getName();
			if (!subclassName.equalsIgnoreCase(className)) {
				result.add(subclassName);
			}
		}

		return result;
	}

}
