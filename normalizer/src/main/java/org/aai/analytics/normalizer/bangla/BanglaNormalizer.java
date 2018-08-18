package org.aai.analytics.normalizer.bangla;


import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.aai.analytics.normalizer.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sazid
 * @version 1.0
 */
@Log4j2
public final class BanglaNormalizer {
    private static HashMap<String, String> rules = new HashMap<>();
    private static final BanglaNormalizer INSTANCE = new BanglaNormalizer();

    private BanglaNormalizer() {
        if(INSTANCE != null) {
            throw new IllegalStateException("BanglaNormalizer Class already instantiated");
        }
        String ruleFileName = "bengali-normalizer-rules.txt";
        initialize(BanglaNormalizer.class.getClassLoader().getResourceAsStream(
                ruleFileName));
        log.info("Bangla Normalizer rules loaded");
    }

    public static BanglaNormalizer getInstance() {
        return INSTANCE;
    }

    private BanglaNormalizer(@NonNull  List<String> ruleFile) {
        rules.clear();
        String line;
        Iterator<String> it = ruleFile.iterator();
        while (it.hasNext()) {
            line = it.next();
            line = whiteSpaceTrim(line);
            line = commentTrim(line);
            if (line.equals("")) continue;

            String replace = extractReplaceRule(line);
            line = line.replaceAll("->.*", "");

            rules.put(line, replace);
        }
    }

    private static void initialize(InputStream is) {
        rules.clear();
        String line = null;
        BufferedReader br = new BufferedReader( new InputStreamReader(is, StandardCharsets.UTF_8) );

        try {
            // Gather the knowledge from the external file
            line = br.readLine();
            while (line != null) {
                line = whiteSpaceTrim(line);
                line = commentTrim(line);

                if (!line.equals("")) {
                    String replace = extractReplaceRule(line);
                    line = line.replaceAll("->.*", "");

                    rules.put(line, replace);
                }

                line = br.readLine();
            }
            br.close();


        } catch (IOException e) {
			log.error(e.getMessage(),e);
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
				log.error(e.getMessage(),e);
            }
        }
    }


    public int normalize(char s[], int len) {

        for (int i = 0; i < len; i++) {
            String t1, t2;
            if (i+1 < len) {
                t1 = new String(s, i, 1);
                t2 = new String(s, i, 2);

                if (rules.containsKey(t2)) {
                    char[] replace = rules.get(t2).toCharArray();
                    len = Utils.replace(s, replace, i, len, replace.length, 2);
                    i++;
                } else if (rules.containsKey(t1)) {
                    char[] replace = rules.get(t1).toCharArray();
                    len = Utils.replace(s, replace, i, len, replace.length, 1);
                }
            } else {
                t1 = new String(s, i, 1);
                if (rules.containsKey(t1)) {
                    char[] replace = rules.get(t1).toCharArray();
                    len = Utils.replace(s, replace, i, len, replace.length, 1);
                }
            }
        }
        return len;
    }

    public String normalize(String str) {

        for (int i = 0; i < str.length(); i++) {
            String t1, t2;
            if (i+1 < str.length()) {
                t1 = new String(str.toCharArray(), i, 1);
                t2 = new String(str.toCharArray(), i, 2);

                if (rules.containsKey(t2)) {
                    String replace = rules.get(t2);
                    str = Utils.replace(str, replace, i, 2);
                    i++;
                } else if (rules.containsKey(t1)) {
                    String replace = rules.get(t1);
                    str = Utils.replace(str, replace, i, 1);
                }
            } else {
                t1 = new String(str.toCharArray(), i, 1);
                if (rules.containsKey(t1)) {
                    String replace = rules.get(t1);
                    str = Utils.replace(str, replace, i, 1);
                }
            }
        }
        return str;
    }

    private static String whiteSpaceTrim(String str) {
        return str.replaceAll("[\t' ']+", "");
    }

    private static String commentTrim(String str) {
        return str.replaceAll("#.*", "");
    }

    private static String extractReplaceRule(String str) {
        if (str.matches(".*->.*")) {
            String[] l = str.split("->");
            return l[1];
        }
        return "";
    }
}
