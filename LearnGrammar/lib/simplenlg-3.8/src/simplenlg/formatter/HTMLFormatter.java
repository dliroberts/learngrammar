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
package simplenlg.formatter;

import java.util.Arrays;
import java.util.List;

/**
 * Class to represent HTML markups
 * 
 */
public class HTMLFormatter extends Formatter {

	// start markers that can absorb or be absorbed, strongest first
	// <BODY> and <UL> are not absorbable
	private final static List<String> ABSORBABLE_START_MARKERS = Arrays.asList(
			"<H1>", "<H2>", "<H3>", "<LI>", "<P>");

	// end markers that can absorb or be absorbed, strongest first
	// </BODY> and </UL> are not absorbable
	private final static List<String> ABSORBABLE_END_MARKERS = Arrays.asList(
			"</H1>" + Formatter.NEW_LINE, "</H2>" + Formatter.NEW_LINE, "</H3>"
					+ Formatter.NEW_LINE, "</LI>" + Formatter.NEW_LINE, "</P>"
					+ Formatter.NEW_LINE);

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getAbsorbableEndMarkups()
	 */
	@Override
	protected List<String> getAbsorbableEndMarkups() {
		// TODO Auto-generated method stub
		return HTMLFormatter.ABSORBABLE_END_MARKERS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getAbsorbableStartMarkups()
	 */
	@Override
	protected List<String> getAbsorbableStartMarkups() {
		// TODO Auto-generated method stub
		return HTMLFormatter.ABSORBABLE_START_MARKERS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getDocEnd()
	 */
	@Override
	protected String getDocEnd() {
		// TODO Auto-generated method stub
		return "</BODY>" + Formatter.NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getDocHeaderEnd()
	 */
	@Override
	protected String getDocHeaderEnd() {
		// TODO Auto-generated method stub
		return "</H1>" + Formatter.NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getDocHeaderStart()
	 */
	@Override
	protected String getDocHeaderStart() {
		// TODO Auto-generated method stub
		return "<H1>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getDocStart()
	 */
	@Override
	protected String getDocStart() {
		// TODO Auto-generated method stub
		return "<BODY>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getIndentedListEnd()
	 */
	@Override
	protected String getIndentedListEnd() {
		// TODO Auto-generated method stub
		return "</UL>" + Formatter.NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getIndentedListStart()
	 */
	@Override
	protected String getIndentedListStart() {
		// TODO Auto-generated method stub
		return "<UL>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getListElementEnd()
	 */
	@Override
	protected String getListElementEnd() {
		// TODO Auto-generated method stub
		return "</LI>" + Formatter.NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getListElementStart()
	 */
	@Override
	protected String getListElementStart() {
		// TODO Auto-generated method stub
		return "<LI>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getParaEnd()
	 */
	@Override
	protected String getParaEnd() {
		// TODO Auto-generated method stub
		return "</P>" + Formatter.NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getParaStart()
	 */
	@Override
	protected String getParaStart() {
		// TODO Auto-generated method stub
		return "<P>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getSectionHeaderEnd()
	 */
	@Override
	protected String getSectionHeaderEnd() {
		// TODO Auto-generated method stub
		return "</H2>" + Formatter.NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getSectionHeaderStart()
	 */
	@Override
	protected String getSectionHeaderStart() {
		// TODO Auto-generated method stub
		return "<H2>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getSubSectionHeaderEnd()
	 */
	@Override
	protected String getSubSectionHeaderEnd() {
		// TODO Auto-generated method stub
		return "</H3>" + Formatter.NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Markups#getSubSectionHeaderStart()
	 */
	@Override
	protected String getSubSectionHeaderStart() {
		// TODO Auto-generated method stub
		return "<H3>";
	}

}
