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
import java.util.Map;

import simplenlg.exception.SimplenlgException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

// TODO: Auto-generated Javadoc
/**
 * ProtegeClass is an interface to a Protege Frame (ie, Class or Instance)
 * <P>
 * This class is a fairly simple "wrapper" which translates KBEntity methods to
 * appropriate Protege methods
 * <P>
 * If this class is used, protege.jar must be included as a library
 * 
 * @author ereiter
 */

public class ProtegeClass implements KBEntity {

	// class fields
	/** The frame. */
	private Frame frame; // The protege Frame

	/** The kb. */
	private ProtegeKB kb; // The ProtegeKB object which contains the frame

	/** The feature values. */
	private Map<String, Object> featureValues; // internal map, used to cache

	// feature values

	// note that value of null means feature does not have a value

	/**
	 * create a ProtegeClass from a Protege Frame object.
	 * 
	 * @param kb
	 *            the kb
	 * @param frame
	 *            the frame
	 */
	public ProtegeClass(ProtegeKB kb, Frame frame) {
		this.frame = frame;
		this.kb = kb;
		this.featureValues = new HashMap<String, Object>();
	}

	// get the type of a Frame
	// If Cls, this is the name of the class
	// If instance, get name of class which is direct type of instance
	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.kb.KBEntity#getType()
	 */
	public String getType() {
		// return name of self, or first ancestor with a name
		if (this.frame instanceof Cls) {
			return this.frame.getName();
		} else if (this.frame instanceof Instance) {
			return ((Instance) this.frame).getDirectType().getName();
		} else {
			throw new SimplenlgException("Impossible Protege object");
		}
	}

	// get the value of a feature
	// if its a Protege object, return a ProtegeClass
	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.kb.KBEntity#getValue(java.lang.String)
	 */
	public Object getValue(String featureName) {
		// check lookup table
		if (this.featureValues.containsKey(featureName)) {
			return this.featureValues.get(featureName);
		}

		// get Protege slot for this feature
		Slot slot = this.kb.getKB().getSlot(featureName);

		// if doesn't exist, record as null
		if (slot == null) {
			this.featureValues.put(featureName, null);
			return null;
		}

		// get value of slot
		// complicated because there are four things so check
		Collection value = null;
		if (this.frame instanceof Cls) { // for classes, look at template slots
			Cls cframe = (Cls) this.frame;
			// case 1 - template slot value (classes only)
			value = cframe.getTemplateSlotValues(slot);

			// case 2 - template slot default values (classes only)
			if (value == null || value.isEmpty()) {
				value = cframe.getTemplateSlotDefaultValues(slot);
			}
		}
		// case 3: own slot value
		if (value == null || value.isEmpty()) {
			value = this.frame.getOwnSlotValues(slot);
		}

		// case 4: own slot default value
		if (value == null || value.isEmpty()) {
			value = this.frame.getOwnSlotDefaultValues(slot);
		}

		// get result
		Object result;
		if (value == null || value.isEmpty()) {
			result = null;
		} else if (value.size() == 1) {
			result = convert(this.kb.getFirstInCollection(value));
		} else {
			result = new ArrayList();
			for (Object o : value) {
				((ArrayList) result).add(convert(o));
			}
		}

		// save value, and return
		this.featureValues.put(featureName, result);
		return result;
	}

	/**
	 * Convert.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the object
	 */
	private Object convert(Object value) {
		// convert to our values as needed
		if (value == null) {
			return null; // do nothing
		} else if (value instanceof Float) {
			return new Double((Float) value);
		} else if (value instanceof Cls) {
			// return wrapper
			return this.kb.getClass(((Cls) value).getName());
		} else if (value instanceof Frame) {
			return new ProtegeClass(this.kb, (Frame) value);
		} else {
			return value;
		}

	}

}
