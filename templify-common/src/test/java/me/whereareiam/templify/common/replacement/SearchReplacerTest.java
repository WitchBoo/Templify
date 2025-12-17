package me.whereareiam.templify.common.replacement;

import me.whereareiam.templify.common.replacement.selector.base.ValueSelector;
import me.whereareiam.templify.type.SearchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchReplacerTest {
  private SearchReplacer replacer;

  @BeforeEach
  void setUp() {
    replacer = new SearchReplacer();
  }

  @Nested
  @DisplayName("replaceFirst")
  class ReplaceFirst {
    @Test
    void shouldReplaceFirstOccurrence() {
      var result = replacer.replaceFirst("Hello %name%, welcome %name%!", "%name%", "John");
      assertEquals("Hello John, welcome %name%!", result);
    }

    @Test
    void shouldReturnOriginalWhenTokenNotFound() {
      var result = replacer.replaceFirst("Hello World!", "%name%", "John");
      assertEquals("Hello World!", result);
    }

    @Test
    void shouldReturnOriginalWhenValueIsNull() {
      var result = replacer.replaceFirst("Hello %name%!", "%name%", null);
      assertEquals("Hello %name%!", result);
    }

    @Test
    void shouldHandleEmptyContent() {
      var result = replacer.replaceFirst("", "%name%", "John");
      assertEquals("", result);
    }

    @Test
    void shouldHandleTokenAtStart() {
      var result = replacer.replaceFirst("%name% is here", "%name%", "John");
      assertEquals("John is here", result);
    }

    @Test
    void shouldHandleTokenAtEnd() {
      var result = replacer.replaceFirst("Hello %name%", "%name%", "John");
      assertEquals("Hello John", result);
    }
  }

  @Nested
  @DisplayName("replaceAll")
  class ReplaceAll {
    @Test
    void shouldReplaceAllOccurrences() {
      ValueSelector selector = _ -> "John";
      var result = replacer.replaceAll("Hello %name%, welcome %name%!", "%name%", selector);
      assertEquals("Hello John, welcome John!", result);
    }

    @Test
    void shouldReturnOriginalWhenTokenNotFound() {
      ValueSelector selector = _ -> "John";
      var result = replacer.replaceAll("Hello World!", "%name%", selector);
      assertEquals("Hello World!", result);
    }

    @Test
    void shouldStopWhenSelectorReturnsNull() {
      ValueSelector selector = occurrence -> occurrence == 0 ? "John" : null;
      var result = replacer.replaceAll("Hello %name%, welcome %name%!", "%name%", selector);
      assertEquals("Hello John, welcome %name%!", result);
    }

    @Test
    void shouldPassCorrectOccurrenceIndex() {
      ValueSelector selector = occurrence -> "value" + occurrence;
      var result = replacer.replaceAll("%x% %x% %x%", "%x%", selector);
      assertEquals("value0 value1 value2", result);
    }
  }

  @Nested
  @DisplayName("apply")
  class Apply {
    @Test
    void shouldDelegateToReplaceFirstForFirstSearchType() {
      ValueSelector selector = _ -> "John";
      var result = replacer.apply("Hello %name%, welcome %name%!", "%name%", SearchType.FIRST, selector);
      assertEquals("Hello John, welcome %name%!", result);
    }

    @Test
    void shouldDelegateToReplaceAllForAllSearchType() {
      ValueSelector selector = _ -> "John";
      var result = replacer.apply("Hello %name%, welcome %name%!", "%name%", SearchType.ALL, selector);
      assertEquals("Hello John, welcome John!", result);
    }
  }
}
