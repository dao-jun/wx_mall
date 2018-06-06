package com.dyf.core.utils

class CharUtil {

    static String getRandomString(Integer num) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789"
        def random = new Random()
        def sb = new StringBuffer()
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length())
            sb.append(base.charAt(number))
        }
        sb.toString()
    }
}
