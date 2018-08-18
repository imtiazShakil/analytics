package org.aai.analytics.stopword;

import org.junit.Test;

public class StopwordFilterTest {

    @Test
    public void test1() {
        StopwordFilter stp = StopwordFilter.getInstance();
        String text = "পরদিন বৃহস্পতিবার আমরা ওসি এবং এসআই গিয়াস উদ্দিনের সঙ্গে দেখা করি। " +
                "এরপর ওইদিন সন্ধ্যায় তাকে আদালতে নেয়ার উদ্দেশে থানা থেকে নিয়ে যাওয়া হয়। " +
                "তার সঙ্গে আরও একজন আসামি ছিল। পুলিশ ওই আসামিকে আদালতে সোপর্দ করে। " +
                "কিন্তু মোহাম্মাদ আলীকে আদালতে সোপর্দ করেননি। বৃহস্পতিবার রাতে আবার থানায় ফিরিয়ে আনে তাকে। " +
                "এরপর থানা ভবনের দোতলায় একটি ঘরে আটকে রেখে বেদম মারপিট করা হয়। " +
                "জোহরা খাতুন অভিযোগ করেন, মোহাম্মদ আলীকে ক্রসফায়ারের ভয় দেখানো হয়। " +
                "তাকে ক্রসফায়ার থেকে বাঁচাতে পুলিশ আমাদের কাছে ১২ লাখ টাকা দাবি করে। " +
                "আমরা গরিব মানুষ। এত টাকা কোথায় পাব। আজ (শুক্রবার) সকালে থানায় গিয়ে আহত অবস্থায় আমার ভাই মোহাম্মাদ আলীকে দেখেছি।";

        for(String word: text.split(" ")) {
            if(stp.filter(word).isPresent() == false ) {
                System.out.println(word + "<- removed!");
            }
        }
    }
}
