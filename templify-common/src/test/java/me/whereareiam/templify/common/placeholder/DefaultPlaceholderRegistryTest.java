package me.whereareiam.templify.common.placeholder;

import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.provider.DynamicPlaceholderProvider;
import me.whereareiam.templify.placeholder.provider.PlaceholderProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPlaceholderRegistryTest {
  private DefaultPlaceholderRegistry registry;

  @Mock
  private PlaceholderContext context;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    registry = new DefaultPlaceholderRegistry();
  }

  @Nested
  @DisplayName("Static Provider Registration")
  class StaticProviderTests {
    @Test
    void shouldRegisterAndRetrieveStaticProvider() {
      PlaceholderProvider provider = _ -> "test-value";
      registry.register("custom", provider);

      assertEquals("test-value", registry.getValue("custom", context));
    }

    @Test
    void shouldNormalizeKeysToLowercase() {
      PlaceholderProvider provider = _ -> "value";
      registry.register("MyKey", provider);

      assertEquals("value", registry.getValue("mykey", context));
      assertEquals("value", registry.getValue("MYKEY", context));
      assertEquals("value", registry.getValue("MyKey", context));
    }

    @Test
    void shouldStripPercentDelimiters() {
      PlaceholderProvider provider = _ -> "value";
      registry.register("%key%", provider);

      assertEquals("value", registry.getValue("key", context));
      assertEquals("value", registry.getValue("%key%", context));
    }

    @Test
    void shouldRespectPriority() {
      PlaceholderProvider lowPriority = _ -> "low";
      PlaceholderProvider highPriority = _ -> "high";

      registry.register("key", lowPriority, 0);
      registry.register("key", highPriority, 10);

      assertEquals("high", registry.getValue("key", context));
    }

    @Test
    void shouldFallbackToLowerPriorityWhenHigherReturnsNull() {
      PlaceholderProvider highPriority = _ -> null;
      PlaceholderProvider lowPriority = _ -> "fallback";

      registry.register("key", highPriority, 10);
      registry.register("key", lowPriority, 0);

      assertEquals("fallback", registry.getValue("key", context));
    }
  }

  @Nested
  @DisplayName("Dynamic Provider Registration")
  class DynamicProviderTests {
    @Test
    void shouldRegisterAndMatchDynamicProvider() {
      DynamicPlaceholderProvider provider = mock(DynamicPlaceholderProvider.class);
      when(provider.matches("env_test")).thenReturn(true);
      when(provider.getValue("env_test", context)).thenReturn("dynamic-value");

      registry.register(provider);

      assertEquals("dynamic-value", registry.getValue("env_test", context));
    }

    @Test
    void shouldTryStaticProvidersBeforeDynamic() {
      PlaceholderProvider staticProvider = _ -> "static-value";
      DynamicPlaceholderProvider dynamicProvider = mock(DynamicPlaceholderProvider.class);
      when(dynamicProvider.matches(anyString())).thenReturn(true);
      when(dynamicProvider.getValue(anyString(), any())).thenReturn("dynamic-value");

      registry.register("key", staticProvider);
      registry.register(dynamicProvider);

      assertEquals("static-value", registry.getValue("key", context));
    }
  }

  @Nested
  @DisplayName("getAll")
  class GetAllTests {
    @Test
    void shouldReturnAllRegisteredPlaceholders() {
      PlaceholderProvider provider1 = _ -> "value1";
      PlaceholderProvider provider2 = _ -> "value2";

      registry.register("key1", provider1);
      registry.register("key2", provider2);

      var all = registry.getAll(context, false);

      assertEquals("value1", all.get("key1"));
      assertEquals("value2", all.get("key2"));
    }

    @Test
    void shouldIncludePercentDelimitersWhenRequested() {
      PlaceholderProvider provider = _ -> "value";
      registry.register("test", provider);

      var all = registry.getAll(context, true);

      assertTrue(all.containsKey("%test%"));
      assertEquals("value", all.get("%test%"));
    }
  }

  @Nested
  @DisplayName("Built-in Providers")
  class BuiltInTests {
    @Test
    void shouldHaveBuiltInProvidersRegistered() {
      // Just verify they don't throw - actual values depend on context
      assertDoesNotThrow(() -> registry.getValue("nodeid", context));
      assertDoesNotThrow(() -> registry.getValue("servicename", context));
      assertDoesNotThrow(() -> registry.getValue("taskname", context));
    }
  }
}
