package me.whereareiam.templify.common.match;

import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceId;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import me.whereareiam.templify.model.TargetDefinition;
import me.whereareiam.templify.model.config.Settings;
import me.whereareiam.templify.model.replacement.Replacement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleMatcherTest {
  private RuleMatcher ruleMatcher;

  @Mock
  private ServiceInfoSnapshot serviceInfo;
  @Mock
  private ServiceId serviceId;
  @Mock
  private ServiceConfiguration serviceConfiguration;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ruleMatcher = new RuleMatcher();

    when(serviceInfo.serviceId()).thenReturn(serviceId);
    when(serviceInfo.configuration()).thenReturn(serviceConfiguration);
    when(serviceId.taskName()).thenReturn("Lobby");
    when(serviceId.name()).thenReturn("Lobby-1");
    when(serviceId.environmentName()).thenReturn("MINECRAFT_SERVER");
    when(serviceConfiguration.groups()).thenReturn(Set.of("Global", "Lobby"));
    when(serviceConfiguration.templates()).thenReturn(List.of());
  }

  @Nested
  @DisplayName("matchingRules")
  class MatchingRulesTests {
    @Test
    void shouldMatchRuleWithNoTargets() {
      var rule = new Replacement("test", true, null, null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }

    @Test
    void shouldMatchRuleWithEmptyTargets() {
      var rule = new Replacement("test", true, List.of(), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }

    @Test
    void shouldExcludeDisabledRules() {
      var rule = new Replacement("test", false, null, null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertTrue(matches.isEmpty());
    }

    @Test
    void shouldMatchByTask() {
      var target = new TargetDefinition("Lobby", null, null, null, null);
      var rule = new Replacement("test", true, List.of(target), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }

    @Test
    void shouldNotMatchWrongTask() {
      var target = new TargetDefinition("Proxy", null, null, null, null);
      var rule = new Replacement("test", true, List.of(target), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertTrue(matches.isEmpty());
    }

    @Test
    void shouldMatchByService() {
      var target = new TargetDefinition(null, "Lobby-1", null, null, null);
      var rule = new Replacement("test", true, List.of(target), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }

    @Test
    void shouldMatchByEnvironment() {
      var target = new TargetDefinition(null, null, "MINECRAFT_SERVER", null, null);
      var rule = new Replacement("test", true, List.of(target), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }

    @Test
    void shouldMatchEnvironmentCaseInsensitive() {
      var target = new TargetDefinition(null, null, "minecraft_server", null, null);
      var rule = new Replacement("test", true, List.of(target), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }

    @Test
    void shouldMatchByGroup() {
      var target = new TargetDefinition(null, null, null, "Lobby", null);
      var rule = new Replacement("test", true, List.of(target), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }

    @Test
    void shouldNotMatchWrongGroup() {
      var target = new TargetDefinition(null, null, null, "NonExistent", null);
      var rule = new Replacement("test", true, List.of(target), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertTrue(matches.isEmpty());
    }

    @Test
    void shouldMatchAnyTarget() {
      var target1 = new TargetDefinition("WrongTask", null, null, null, null);
      var target2 = new TargetDefinition("Lobby", null, null, null, null);
      var rule = new Replacement("test", true, List.of(target1, target2), null, null);

      var matches = ruleMatcher.matchingRules(List.of(rule), serviceInfo, null);

      assertEquals(1, matches.size());
    }
  }

  @Nested
  @DisplayName("collectGlobs")
  class CollectGlobsTests {
    @Test
    void shouldCollectGlobsFromSettings() {
      var settings = new Settings();
      settings.setPaths(new Settings.PathSection(List.of("*.yml", "*.json")));

      var globs = ruleMatcher.collectGlobs(settings, List.of());

      assertTrue(globs.contains("*.yml"));
      assertTrue(globs.contains("*.json"));
    }

    @Test
    void shouldCollectGlobsFromRules() {
      var settings = new Settings();
      var rule = new Replacement("test", true, null, List.of("*.properties"), null);

      var globs = ruleMatcher.collectGlobs(settings, List.of(rule));

      assertTrue(globs.contains("*.properties"));
    }

    @Test
    void shouldDeduplicateGlobs() {
      var settings = new Settings();
      settings.setPaths(new Settings.PathSection(List.of("*.yml")));
      var rule = new Replacement("test", true, null, List.of("*.yml"), null);

      var globs = ruleMatcher.collectGlobs(settings, List.of(rule));

      assertEquals(1, globs.stream().filter(g -> g.equals("*.yml")).count());
    }
  }

  @Nested
  @DisplayName("matchesAny")
  class MatchesAnyTests {
    @Test
    void shouldMatchPathWithGlob() {
      var matchers = ruleMatcher.toPathMatchers(List.of("*.yml"));
      // Use absolute path for proper matching
      var path = Path.of("/tmp/config.yml").toAbsolutePath();

      assertTrue(ruleMatcher.matchesAny(path, matchers));
    }

    @Test
    void shouldNotMatchNonMatchingPath() {
      var matchers = ruleMatcher.toPathMatchers(List.of("*.yml"));
      var path = Path.of("/tmp/config.json").toAbsolutePath();

      assertFalse(ruleMatcher.matchesAny(path, matchers));
    }

    @Test
    void shouldMatchNestedPaths() {
      var matchers = ruleMatcher.toPathMatchers(List.of("*.yml"));
      var path = Path.of("/tmp/plugins/plugin/config.yml").toAbsolutePath();

      assertTrue(ruleMatcher.matchesAny(path, matchers));
    }
  }
}
