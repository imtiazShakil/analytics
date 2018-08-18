package org.aai.analytics.stemmer.bangla;

import lombok.extern.log4j.Log4j2;
import org.aai.analytics.config.Configuration;

import java.io.IOException;

@Log4j2
public final class BnStemmer {

    private static RuleFileParser parser;
    private static double threshold = .33f;

    private static final BnStemmer INSTANCE = new BnStemmer();
    private static Configuration conf;

    private BnStemmer() {
        if (INSTANCE != null) {
            throw new IllegalStateException("BnStemmer illegally Already instantiated");
        }
        try {
            conf = new Configuration(BnStemmer.class.getClassLoader().getResourceAsStream("stemmer.properties"));
            initStemmer();
            log.info("BanglaStemmer initialized.");
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static BnStemmer getInstance() {
        return INSTANCE;
    }

    private static void initStemmer() throws IOException {
        threshold = Double.parseDouble(conf.getProperty("stemmer.bangla.threshold", "25")) / 100f;
        ProtWordsList.loadProtWords(conf);
        parser = new RuleFileParser(conf);

    }

    public String stem(String word) {
        if (ProtWordsList.isProtectedWord(word)) {
            return word;
        } else {
            String stem = parser.stemOfWord(word);
            if (((double) stem.length() / (double) word.length()) < threshold) stem = word;
            return stem;
        }
    }
}