package me.whereareiam.templify.common.replacement.selector;

import me.whereareiam.templify.common.replacement.selector.type.FirstValueSelector;
import me.whereareiam.templify.common.replacement.selector.type.RandomValueSelector;
import me.whereareiam.templify.common.replacement.selector.type.SequentialValueSelector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueSelectorTest {

  @Nested
  @DisplayName("FirstValueSelector")
  class FirstValueSelectorTests {
    @Test
    void shouldAlwaysReturnFirstValue() {
      var selector = new FirstValueSelector(List.of("first", "second", "third"));

      assertEquals("first", selector.nextValue(0));
      assertEquals("first", selector.nextValue(1));
      assertEquals("first", selector.nextValue(100));
    }

    @Test
    void shouldReturnNullForEmptyList() {
      var selector = new FirstValueSelector(List.of());
      assertNull(selector.nextValue(0));
    }
  }

  @Nested
  @DisplayName("SequentialValueSelector")
  class SequentialValueSelectorTests {
    @Test
    void shouldCycleThroughValues() {
      var selector = new SequentialValueSelector(List.of("a", "b", "c"));

      assertEquals("a", selector.nextValue(0));
      assertEquals("b", selector.nextValue(1));
      assertEquals("c", selector.nextValue(2));
      assertEquals("a", selector.nextValue(3)); // wraps around
      assertEquals("b", selector.nextValue(4));
    }

    @Test
    void shouldReturnNullForEmptyList() {
      var selector = new SequentialValueSelector(List.of());
      assertNull(selector.nextValue(0));
    }

    @Test
    void shouldHandleSingleValue() {
      var selector = new SequentialValueSelector(List.of("only"));

      assertEquals("only", selector.nextValue(0));
      assertEquals("only", selector.nextValue(1));
      assertEquals("only", selector.nextValue(99));
    }
  }

  @Nested
  @DisplayName("RandomValueSelector")
  class RandomValueSelectorTests {
    @Test
    void shouldReturnSameValueForAllOccurrences() {
      var selector = new RandomValueSelector(List.of("a", "b", "c"));

      var first = selector.nextValue(0);
      assertNotNull(first);
      assertTrue(List.of("a", "b", "c").contains(first));

      // Should return same value for subsequent calls
      assertEquals(first, selector.nextValue(1));
      assertEquals(first, selector.nextValue(2));
    }

    @Test
    void shouldReturnNullForEmptyList() {
      var selector = new RandomValueSelector(List.of());
      assertNull(selector.nextValue(0));
    }

    @Test
    void shouldReturnOnlyValueForSingletonList() {
      var selector = new RandomValueSelector(List.of("single"));
      assertEquals("single", selector.nextValue(0));
    }
  }
}
