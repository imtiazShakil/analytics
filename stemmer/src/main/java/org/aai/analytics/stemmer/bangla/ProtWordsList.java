package org.aai.analytics.stemmer.bangla;


import lombok.extern.log4j.Log4j2;
import org.aai.analytics.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by sazid on 1/28/16.
 */
@Log4j2
public class ProtWordsList {

	private static HashSet<String> protWords = new HashSet<String>();

	public static void loadProtWords(Configuration conf) throws IOException {
		protWords.clear();
		String protWordFilePath = conf.getProperty(
				"stemmer.bangla.protected.file", "bangla.protwords.txt");
		InputStream is = ProtWordsList.class.getClassLoader()
				.getResourceAsStream(protWordFilePath);
		if (is == null) {
			log.debug(protWordFilePath + " couldn't be loaded");
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is,
				StandardCharsets.UTF_8));

		String inp = br.readLine();
		while (inp != null) {
			inp = inp.trim();
			Collections.addAll(protWords, inp.split("[\\s।%,ঃ]+"));
			inp = br.readLine();
		}
		br.close();
		is.close();
		log.info("Protected words list is loaded.");
	}

	public static boolean isProtectedWord(String word) {
		return protWords.contains(word);
	}
}
