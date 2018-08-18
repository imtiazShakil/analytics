/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aai.analytics.ai.keywordExtraction;


/**
 *
 * @author imtiaz
 */
public class Filters {
//	private static final Log LOG = LogFactory.getLog(Filters.class.getName());
	//private static KStemmer kstemmer = new KStemmer();

	public static String filter(String term) {
	    // TODO  change this
//		term = StopwordFilter.filter(term);
//		if (term == null)
//			return null;
//
//		if (LanguageCheck.checkWords(term) == LanguageCheck.BANGLAWORD) {
//			term = BnStemmer.stem(term);
//		} else if (LanguageCheck.checkWords(term) == LanguageCheck.BASICLATINWORD) {
//			term = kstemmer.stem(term);
//		}
//		term = StopwordFilter.filter(term);
		return term;
	}

}
