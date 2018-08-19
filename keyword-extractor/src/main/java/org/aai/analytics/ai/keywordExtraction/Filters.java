/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aai.analytics.ai.keywordExtraction;


import lombok.extern.log4j.Log4j2;
import org.aai.analytics.lang.LanguageChecker;
import org.aai.analytics.stemmer.bangla.BnStemmer;
import org.aai.analytics.stemmer.english.kstemmer.KStemmer;
import org.aai.analytics.stopword.StopwordFilter;

import java.util.Optional;

/**
 *
 * @author imtiaz
 */
@Log4j2
public class Filters {
	private static KStemmer kstemmer = new KStemmer();
	private static BnStemmer bnStemmer = BnStemmer.getInstance();
	private static StopwordFilter stopwordFilter = StopwordFilter.getInstance();
	private static LanguageChecker langChecker = new LanguageChecker();

	public static String filter(String term) {
		Optional<String> optTerm = stopwordFilter.filter(term);
		if (optTerm.isPresent() == false) {
			log.debug(term + " removed!");
			return null;
		}
		term = optTerm.get();
		String stemmed;

		if (langChecker.checkWords(term) == langChecker.BANGLAWORD) {
			stemmed = bnStemmer.stem(term);
			log.debug("BanglaStemmer: "+term +"->"+stemmed);
			return stemmed;
		} else if (langChecker.checkWords(term) == langChecker.BASICLATINWORD) {
			stemmed = kstemmer.stem(term);
			log.debug("EnglishStemmer: "+term +"->"+stemmed);
			return  stemmed;
		}
		log.debug("No Stemming: "+term);
		return term;
	}
}
