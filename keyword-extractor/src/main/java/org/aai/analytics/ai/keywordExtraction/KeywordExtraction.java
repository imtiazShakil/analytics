package org.aai.analytics.ai.keywordExtraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import lombok.extern.log4j.Log4j2;

import org.aai.analytics.config.Configuration;

/**
 * Extracts Keywords from a given content.
 * The algorithm is based on { http://www.aaai.org/Papers/FLAIRS/2003/Flairs03-076.pdf}
 *
 * @author imtiaz
 */
@Log4j2
public class KeywordExtraction {
    private static KeywordExtraction INSTANCE = new KeywordExtraction();
    /**
     * defines the value of topFrequentWords i.e. topFrequentWords = CONTENT_RATIO*TOTAL_WORDS;
     */
    private static final Double CONTENT_RATIO = 0.30;
    /**
     * defines maximum Keyword Size
     */
    private static final String MAX_KEYWORDS_SIZE = "100";
    /**
     * defines the minimum score2 that is required of each {@link GeneralTerm} to be selected as a keyword.
     */
    private static final Double MIN_SCORE_THRESHOLD = 0.4;
    private static Configuration conf;

    private KeywordExtraction() {
        if(INSTANCE != null) {
            throw new IllegalStateException("Keyword Extraction class already initialized");
        }
        conf = new Configuration(KeywordExtraction.class.getClassLoader().getResourceAsStream("keyword-extractor.properties"));
    }

    public static KeywordExtraction getInstance() {
        return INSTANCE;
    }

    private ArrayList<ArrayList<String>> dataFormatter(String doc) {
        if (doc == null)
            return null;

        // splitting the sentence
        String[] sentenceList = doc.split("\\.|\\?|\\!|\\;|\\।");
        if (sentenceList == null)
            return null;
        if (log.isTraceEnabled()) {
            log.trace("Splitting the sentence");
            for (String str : sentenceList) {
                log.trace("sentence-->" + str);
                log.trace("===========================================");
            }
        }

        // splitting the words from the sentence
        // and putting them in newdoc
        ArrayList<ArrayList<String>> newdoc = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < sentenceList.length; i++) {
            String[] words = sentenceList[i].split("\\r|\\n|\\t|\\,| ");
            ArrayList<String> sentence = new ArrayList<String>();
            for (String word : words) {
                word = Filters.filter(word);
                if (word != null)
                    sentence.add(word);
            }
            if (sentence != null)
                newdoc.add(sentence);
        }

        return newdoc;
    }

    public List<String> process(String content) {
        ArrayList<ArrayList<String>> doc = dataFormatter(content);
        ArrayList<ArrayList<Integer>> newdoc = new ArrayList<ArrayList<Integer>>();
        // hm is the hashmap which maps the word with an unique id
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        int newId, COUNTER = 0;

        ArrayList<BasicTerm> basicTermList = new ArrayList<BasicTerm>();
        ArrayList<GeneralTerm> GeneralTermList = new ArrayList<GeneralTerm>();

        // converting doc to newdoc
        for (ArrayList<String> sentence : doc) {
            ArrayList<Integer> intSentence = new ArrayList<Integer>();

            for (int j = 0; j < sentence.size(); j++) {
                if (hm.containsKey(sentence.get(j)))
                    newId = hm.get(sentence.get(j));
                else {
                    hm.put(sentence.get(j), COUNTER);
                    newId = COUNTER;
                    basicTermList.add(new BasicTerm(newId));
                    GeneralTermList.add(new GeneralTerm(sentence.get(j)));
                    COUNTER++;
                }
                // System.out.println(sentence.get(j) +"--"+newId);
                intSentence.add(newId);
            }
            newdoc.add(intSentence);
        }

        // =======================================================================
        // calculating frequency for all words
        ArrayList<Sentence> finaldoc = new ArrayList<Sentence>();
        for (ArrayList<Integer> terms : newdoc) {
            Sentence sent = new Sentence(terms);
            sent.process(basicTermList, GeneralTermList);
            finaldoc.add(sent);
        }
        Collections.sort(basicTermList);

        // for(BasicTerm word : basicTermList) {
        // System.out.println( word.getId()+"<-->"+word.getFreq() );
        // }
        // for(GeneralTerm word : GeneralTermList) {
        // System.out.println( word.toString() );
        // }

        // =======================================================================
        int TOTAL_UNIQUE_WORDS = hm.size();
        int topFrequentWords = (int) (CONTENT_RATIO * (double) TOTAL_UNIQUE_WORDS);
        topFrequentWords = Integer.min(topFrequentWords, Integer
                .parseInt(conf.getProperty("keywords.size", MAX_KEYWORDS_SIZE)));

        if (log.isTraceEnabled()) {
            log.trace("Total-->" + TOTAL_UNIQUE_WORDS + " selected-->"
                    + topFrequentWords);
        }

        // =======================================================================
        // calculating sentence freq for each term w in GeneralTermList
        for (Sentence sent : finaldoc) {
            sent.process(topFrequentWords, basicTermList, GeneralTermList);
        }

        // =======================================================================
        // calculating prob1 for each term t found in basicTermList
        // prob1 = freq(t) / Sum( freq(t1)+freq(t2)+freq(t3)+...upto
        // top20percent )
        int sumOfFreqOfTopFreqWords = 0;
        for (int i = 0; i < topFrequentWords; i++) {
            BasicTerm t = basicTermList.get(i);
            sumOfFreqOfTopFreqWords += t.getFreq();
        }
        for (int i = 0; i < topFrequentWords; i++) {
            BasicTerm t = basicTermList.get(i);
            t.setProb1((double) t.getFreq() / (sumOfFreqOfTopFreqWords * 1.0));
        }
        // =======================================================================
        // calculating prob2 for each term t found in basicTermList
        for (int i = 0; i < topFrequentWords; i++) {
            BasicTerm t = basicTermList.get(i);
            int nc = 0;
            for (GeneralTerm w : GeneralTermList) {
                if (w.sentenceFreq == null)
                    w.initializeSentenceFreq(topFrequentWords);
                nc += ((w.sentenceFreq[i] > 0) ? 1 : 0);
            }
            t.setProb2((double) nc / (TOTAL_UNIQUE_WORDS * 1.0));
        }

        // =======================================================================
        // display basicTerm sorted in desc order according to frequency
        if (log.isTraceEnabled()) {
            for (int i = 0; i < topFrequentWords; i++) {
                BasicTerm word = basicTermList.get(i);
                log.trace("id-->" + word.getId() + ",term-->"
                        + GeneralTermList.get(word.getId()).term + "<->"
                        + word.getFreq() + "<-->" + word.getProb2());
            }
        }

        // =======================================================================
        // calculating score1,score2 for each term w in GeneralTermList
        // with respective to top20percent terms t
        // then sorting all the generalTerm
        for (GeneralTerm word : GeneralTermList) {
            word.generateScore(basicTermList, topFrequentWords);
        }
        Collections.sort(GeneralTermList);

        // =======================================================================
        // display the result
        if (log.isTraceEnabled()) {
            for (int i = 0; i < topFrequentWords; i++) {
                GeneralTerm word = GeneralTermList.get(i);
                log.trace(word.toString());
            }
        }

        // =======================================================================
        // Returning the result
        ArrayList<String> result = new ArrayList<>();
        Double bestScore = 0.0;
        if (GeneralTermList.size() > 0)
            bestScore = GeneralTermList.get(0).score2;

        if (bestScore > 0.0) {
            for (int i = 0; i < topFrequentWords; i++) {
                if (GeneralTermList.get(i).score2 / bestScore < MIN_SCORE_THRESHOLD)
                    break;
                result.add(GeneralTermList.get(i).term);
                log.debug(GeneralTermList.get(i).term);
            }
        }
        return result;
    }

}