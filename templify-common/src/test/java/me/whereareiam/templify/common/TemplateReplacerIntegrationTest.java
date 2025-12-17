package me.whereareiam.templify.common;

import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceId;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.templify.common.files.FileSelector;
import me.whereareiam.templify.common.files.content.ContentReader;
import me.whereareiam.templify.common.files.content.ContentWriter;
import me.whereareiam.templify.common.match.RuleMatcher;
import me.whereareiam.templify.common.match.RulePlanFactory;
import me.whereareiam.templify.common.replacement.operation.DefaultReplacementOperationRegistry;
import me.whereareiam.templify.model.ReplacementDefinition;
import me.whereareiam.templify.model.ReplacementContext;
import me.whereareiam.templify.model.config.Settings;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.replacement.ReplacementOperation;
import me.whereareiam.templify.replacement.ReplacementOperationRegistry;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TemplateReplacer Integration")
class TemplateReplacerIntegrationTest {
  @TempDir
  Path tempDir;

  @Mock
  private ServiceInfoSnapshot serviceInfo;
  @Mock
  private ServiceId serviceId;
  @Mock
  private ServiceConfiguration serviceConfiguration;

  private TemplateReplacer templateReplacer;
  private Settings settings;

	@BeforeAll
  static void setupConfigura() {
    Config.setReader(Config.reader(Format.YAML));
    Config.setWriter(Config.writer(Format.YAML));
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    when(serviceInfo.serviceId()).thenReturn(serviceId);
    when(serviceInfo.configuration()).thenReturn(serviceConfiguration);
    when(serviceId.taskName()).thenReturn("Lobby");
    when(serviceId.name()).thenReturn("Lobby-1");
    when(serviceId.environmentName()).thenReturn("MINECRAFT_SERVER");
    when(serviceConfiguration.groups()).thenReturn(Set.of("Global"));
    when(serviceConfiguration.templates()).thenReturn(List.of());

    settings = new Settings();
    settings.setPaths(new Settings.PathSection(List.of("*.yml", "*.properties")));
    settings.setDefaults(new Settings.DefaultSection(SearchType.ALL, ReplaceType.FIRST));
  }

  private void initializeReplacer(List<Replacement> replacementRules, List<ReplacementOperation> operations) {
    var ruleMatcher = new RuleMatcher();
    var rulePlanFactory = new RulePlanFactory(() -> settings, ruleMatcher);
    var fileSelector = new FileSelector();
    var contentReader = new ContentReader();
    var contentWriter = new ContentWriter();

    ReplacementOperationRegistry operationRegistry = new DefaultReplacementOperationRegistry();
    for (var op : operations) {
      operationRegistry.register(op);
    }

    templateReplacer = new TemplateReplacer(
            () -> settings,
            fileSelector,
            contentReader,
            contentWriter,
            rulePlanFactory,
            operationRegistry,
            () -> replacementRules
    );
  }

  @Test
  void shouldProcessFilesMatchingGlobs() throws IOException {
    // Create test file using Configura
    var configFile = tempDir.resolve("config.yml");
    Config.getDefaultWriter().write(configFile, Map.of("server", "%server%", "port", 25565));

    // Create non-matching file
    var otherFile = tempDir.resolve("readme.txt");
    Files.writeString(otherFile, "server: %server%");

    var placeholder = new ReplacementDefinition("%server%", SearchType.ALL, ReplaceType.FIRST, List.of("lobby"), null);
    var rule = new Replacement("test", true, null, List.of("*.yml"), List.of(placeholder));

    ReplacementOperation simpleOp = ctx -> ctx.getContent().replace("%server%", "lobby");

    initializeReplacer(List.of(rule), List.of(simpleOp));
    templateReplacer.apply(serviceInfo, tempDir, null);

    // Matching file should be updated - placeholder replaced
    var updatedContent = Files.readString(configFile);
    assertTrue(updatedContent.contains("lobby"));
    assertFalse(updatedContent.contains("%server%"));
    // Non-matching file should remain unchanged
    assertEquals("server: %server%", Files.readString(otherFile));
  }

  @Test
  void shouldNotProcessFilesWhenNoRulesMatch() throws IOException {
    var configFile = tempDir.resolve("config.yml");
    Config.getDefaultWriter().write(configFile, Map.of("server", "%server%"));
    var original = Files.readString(configFile);

    // Rule that won't match (wrong task) and no global file patterns
    settings.setPaths(null); // No global patterns
    var placeholder = new ReplacementDefinition("%server%", SearchType.ALL, ReplaceType.FIRST, List.of("lobby"), null);
    var rule = new Replacement("test", true, 
      List.of(new me.whereareiam.templify.model.TargetDefinition("DifferentTask", null, null, null, null)),
      List.of("*.yml"), 
      List.of(placeholder));

    ReplacementOperation simpleOp = ctx -> ctx.getContent().replace("%server%", "lobby");

    initializeReplacer(List.of(rule), List.of(simpleOp));
    templateReplacer.apply(serviceInfo, tempDir, null);

    assertEquals(original, Files.readString(configFile));
  }

  @Test
  void shouldRespectFileSizeLimit() throws IOException {
    settings.setLimits(new Settings.LimitSection(20)); // 20 bytes limit

    var smallFile = tempDir.resolve("small.yml");
    Config.getDefaultWriter().write(smallFile, Map.of("x", "%y%"));

    var largeFile = tempDir.resolve("large.yml");
    Config.getDefaultWriter().write(largeFile, Map.of("x", "%y%", "extra", "more content here to exceed limit"));
    var largeOriginal = Files.readString(largeFile);

    var rule = new Replacement("test", true, null, List.of("*.yml"), null);
    ReplacementOperation op = ctx -> ctx.getContent().replace("%y%", "replaced");

    initializeReplacer(List.of(rule), List.of(op));
    templateReplacer.apply(serviceInfo, tempDir, null);

    // Small file should be processed
    assertTrue(Files.readString(smallFile).contains("replaced"));
    // Large file should remain unchanged
    assertEquals(largeOriginal, Files.readString(largeFile));
  }

  @Test
  void shouldApplyMultipleOperationsInOrder() throws IOException {
    var configFile = tempDir.resolve("config.yml");
    Config.getDefaultWriter().write(configFile, Map.of("value", "%a%"));

    var rule = new Replacement("test", true, null, List.of("*.yml"), null);

    ReplacementOperation op1 = ctx -> ctx.getContent().replace("%a%", "%b%");
    ReplacementOperation op2 = ctx -> ctx.getContent().replace("%b%", "final");

    initializeReplacer(List.of(rule), List.of(op1, op2));
    templateReplacer.apply(serviceInfo, tempDir, null);

    var content = Files.readString(configFile);
    assertTrue(content.contains("final"));
    assertFalse(content.contains("%a%"));
    assertFalse(content.contains("%b%"));
  }

  @Test
  void shouldNotWriteWhenContentUnchanged() throws IOException {
    var configFile = tempDir.resolve("config.yml");
    Config.getDefaultWriter().write(configFile, Map.of("key", "no placeholders here"));
    var lastModified = Files.getLastModifiedTime(configFile);

    var rule = new Replacement("test", true, null, List.of("*.yml"), null);
    ReplacementOperation noOpOperation = ReplacementContext::getContent; // Returns unchanged

    initializeReplacer(List.of(rule), List.of(noOpOperation));

    // Small delay to ensure the timestamp difference would be detectable
    try { Thread.sleep(100); } catch (InterruptedException ignored) {}

    templateReplacer.apply(serviceInfo, tempDir, null);

    // File should not have been modified
    assertEquals(lastModified, Files.getLastModifiedTime(configFile));
  }
}
