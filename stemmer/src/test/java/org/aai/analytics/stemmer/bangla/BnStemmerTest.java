package org.aai.analytics.stemmer.bangla;

import org.junit.Test;

public class BnStemmerTest {

    @Test
    public void test1() {
        BnStemmer bns = BnStemmer.getInstance();
        String text = "তারা বলল আমরা মাটি দিয়ে ইঁট তৈরী করব তারপর আরও শক্ত করার জন্যে ইঁটগুলো " +
                "পোড়াব তখন মানুষ পাথরের বদলে ইঁট দিয়ে বাড়ী তৈরী করল আর গাঁথনি শক্ত করার জন্যে " +
                "সিমেন্টের বদলে আলকাতরা ব্যবহার করল";

        String[] wordList = text.split(" ");
        for(String word: wordList) {
            System.out.println(word +"->"+bns.stem(word));
        }
    }
}
