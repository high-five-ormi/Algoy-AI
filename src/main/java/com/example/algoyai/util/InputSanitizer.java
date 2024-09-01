package com.example.algoyai.util;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class InputSanitizer {

  public static String sanitize(String input) {
    if (input == null) {
      return null;
    }

    // HTML 엔티티 이스케이프
    String escaped = StringEscapeUtils.escapeHtml4(input);

    // Jsoup을 사용하여 안전한 HTML만 허용
    return Jsoup.clean(escaped, Safelist.basic());
  }
}