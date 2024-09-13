package com.example.algoyai.util;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * @author JSW
 *
 * InputSanitizer 클래스는 사용자로부터 입력된 문자열을 안전하게 처리하기 위한 유틸리티 클래스입니다.
 * 이 클래스는 HTML 엔티티 이스케이프와 Jsoup을 사용한 입력 값의 정화를 수행합니다.
 */
public class InputSanitizer {

  /**
   * 주어진 문자열 입력을 정화하여 안전한 HTML만을 허용합니다.
   * 입력 값이 null인 경우 null을 반환합니다.
   *
   * @param input 정화할 입력 문자열
   * @return 정화된 문자열, 또는 입력이 null인 경우 null
   */
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