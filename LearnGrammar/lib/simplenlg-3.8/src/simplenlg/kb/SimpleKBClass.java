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

import java.util.HashMap;
import java.util.Map;

import simplenlg.exception.SimplenlgException;

// TODO: Auto-generated Javadoc
/**
 * SimpleKBClass represents a class in SimpleKB
 * <P>
 * Also can be used to represent a KBEntity
 * <P>
 * setFeatureValue is used to set the value of a feature.
 * 
 * @author ereiter
 */

public class SimpleKBClass implements KBEntity {

	/** The name. */
	String name; // class name (can be null)

	/** The parent. */
	SimpleKBClass parent; // parent class(null if none)

	/** The feature values. */
	Map<String, Object> featureValues;

	/**
	 * constructs a class with specified name.
	 * 
	 * @param name
	 *            the name
	 */
	public SimpleKBClass(String name) {
		// construct given name
		// use this when there is no parent
		this.name = name;
		this.featureValues = new HashMap<String, Object>();
	}

	/**
	 * constructs a class with specified name and parent.
	 * 
	 * @param name
	 *            the name
	 * @param parent
	 *            the parent
	 */
	public SimpleKBClass(String name, SimpleKBClass parent) {
		// construct given name and parent
		this(name);
		this.parent = parent;
	}

	/**
	 * constructs a class with specified parent.
	 * 
	 * @param parent
	 *            the parent
	 */
	public SimpleKBClass(SimpleKBClass parent) {
		// create an unnamed class with a parent
		this(null, parent);
	}

	/**
	 * sets the value of a feature.
	 * 
	 * @param featureName
	 *            the feature name
	 * @param value
	 *            the value
	 */
	public void setFeatureValue(String featureName, Object value) {
		// set the value of a feature
		this.featureValues.put(featureName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.kb.KBEntity#getType()
	 */
	public String getType() {
		// return name of self, or first ancestor with a name
		SimpleKBClass c = this;
		while (c != null) {
			if (c.getName() != null) {
				return c.getName();
			}
			c = c.getParent();
		}

		throw new SimplenlgException(
				"SimpleKBClass - class does not have a named parent");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.kb.KBEntity#getValue(java.lang.String)
	 */
	public Object getValue(String featureName) {
		// get feature value
		// return null if feature doesn't exist
		// return parent's value for this feature if class exists but doesn't
		// specify this feature

		SimpleKBClass c = this;

		// look for value, go up hierarchy
		while (c != null) {
			Object value = c.getFeatureValue(featureName);
			if (value != null) {
				return value;
			}
			c = c.getParent();
		}

		// no value here or in parents, return null
		return null;
	}

	/**
	 * Gets the feature value.
	 * 
	 * @param featureName
	 *            the feature name
	 * 
	 * @return the feature value
	 */
	public Object getFeatureValue(String featureName) {
		// get value if a feature from map
		return this.featureValues.get(featureName);
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		// return value of name
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// return name of class
		return "SimpleKBClass " + this.name;
	}

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	SimpleKBClass getParent() {
		// return parent of a class
		return this.parent;
	}

}
