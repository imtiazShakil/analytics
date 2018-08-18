package org.aai.analytics.normalizer.bangla;

import org.junit.Test;

import java.util.Arrays;
import java.util.StringTokenizer;

public class BanglaNormalizerTest {

    @Test
    public void testNormalizeCharArrayInt() {
        BanglaNormalizer bNorm = BanglaNormalizer.getInstance();
        String testCase = "রেখেছেন শির্কসহ সব রকমের পাপাচার অশ্লীলতা থেকে মুক্ত জানুয়ারি জানুয়ারি জানুয়ারী জানুয়ারী";
        System.out.print("CharArray:\t");
        StringTokenizer tok = new StringTokenizer(testCase, " ");
        char arr[];
        while (tok.hasMoreTokens()) {
            String str = tok.nextToken();
            int len = str.length();
            arr = Arrays.copyOf(str.toCharArray(), len*2);
            len = bNorm.normalize(arr, len);
            System.out.print(new String(arr, 0, len)+" ");
        }
        System.out.println();
    }

    @Test
    public void testNormalizeString() {
        BanglaNormalizer bNorm = BanglaNormalizer.getInstance();
        String testCase = "জীবন আমারই শেষ। ঋণের বোঝা আর সইতে পারি না। ";
        StringTokenizer tok = new StringTokenizer(testCase, " ");

        while (tok.hasMoreTokens()) {
            String str = tok.nextToken();
            String output = bNorm.normalize(str);
            System.out.println(str+"-->"+output);
        }
    }
}
