package org.aai.analytics.stopword;

import lombok.extern.log4j.Log4j2;
import org.aai.analytics.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * @author imtiaz
 */
@Log4j2
public class StopwordFilter {
    private static final StopwordFilter INSTANCE = new StopwordFilter();
    private static Set<String> stopWordSet;

    private StopwordFilter() {
        if (INSTANCE != null) {
            throw new IllegalStateException("StopwordFilter Already instantiated");
        }
        stopWordSet = new HashSet<>();
        Configuration conf = new Configuration(StopwordFilter.class.getClassLoader().getResourceAsStream("stopword.properties"));
        init(conf);
    }

    public static StopwordFilter getInstance() {
        return INSTANCE;
    }

    private static void init(Configuration conf) {
        String stopWordFileName = conf.getProperty("stopword.file", "stopwords.txt");

        BufferedReader br = null;
        InputStream is = StopwordFilter.class.getClassLoader().getResourceAsStream(stopWordFileName);
        br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        try {
            String line = br.readLine();
            while (line != null) {
                line = line.toLowerCase(Locale.ROOT);

                if (line.length() > 0) stopWordSet.add(line);

                line = br.readLine();
            }
            log.info("Stopword loaded!");
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return;
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }


    public Optional<String> filter(String term) {
        term = term.toLowerCase(Locale.ROOT);

        term = term.replaceAll("[\\+\\^\"\\{\\}\\(\\[\\]\\)\\'\\/\\:\\n\\`\\।]", "");
        term = term.replaceAll("\\p{Punct}", "");

        if (term.length() <= 2) return Optional.empty();


        if (stopWordSet.contains(term)) return Optional.empty();
        else return Optional.ofNullable(term);
    }


}
