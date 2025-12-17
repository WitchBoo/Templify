package me.whereareiam.templify.common.placeholder;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.templify.common.replacement.SearchReplacer;
import me.whereareiam.templify.common.replacement.selector.base.ValueSelector;
import me.whereareiam.templify.common.replacement.selector.base.ValueSelectorFactory;
import me.whereareiam.templify.model.ReplacementDefinition;
import me.whereareiam.templify.model.config.Settings;
import me.whereareiam.templify.model.replacement.Replacement;
import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Singleton
public final class RulePlaceholderApplier {
  private final Provider<Settings> settingsProvider;
  private final SearchReplacer searchReplacer;
  private final ValueSelectorFactory selectorFactory;

  @Inject
  public RulePlaceholderApplier(Provider<Settings> settingsProvider, SearchReplacer searchReplacer, ValueSelectorFactory selectorFactory) {
    this.settingsProvider = settingsProvider;
    this.searchReplacer = searchReplacer;
    this.selectorFactory = selectorFactory;
  }

  public String applyBuiltIns(String content, Map<String, String> builtIns, PlaceholderContext context) {
    var updated = content;
    for (var entry : builtIns.entrySet()) {
      updated = this.applyPlaceholder(
              updated,
              entry.getKey(),
              SearchType.ALL,
              ReplaceType.FIRST,
              new ReplacementDefinition(entry.getKey(), SearchType.ALL, ReplaceType.FIRST, List.of(entry.getValue()), null),
              context);
    }

    return updated;
  }

  public String applyRule(String content, Replacement rule, PlaceholderContext context) {
    List<ReplacementDefinition> replacements = rule.getDefinitions();
    if (replacements == null || replacements.isEmpty())
      return content;

    var settings = this.settingsProvider.get();
    var result = content;
    var defaultSearchType = settings.getDefaults() != null
            ? settings.getDefaults().getSearchType()
            : SearchType.ALL;
    var defaultReplaceType = settings.getDefaults() != null
            ? settings.getDefaults().getReplaceType()
            : ReplaceType.FIRST;

    for (ReplacementDefinition replacement : replacements) {
      var searchType = replacement.getSearchType() != null ? replacement.getSearchType() : defaultSearchType;
      var replaceType = replacement.getReplaceType() != null ? replacement.getReplaceType() : defaultReplaceType;

      result = this.applyPlaceholder(result, replacement.getToken(), searchType, replaceType, replacement, context);
    }

    return result;
  }

  private String applyPlaceholder(
          String content,
          @Nullable String token,
          SearchType searchType,
          ReplaceType replaceType,
          ReplacementDefinition replacement,
          PlaceholderContext context
  ) {
    if (token == null || token.isEmpty()) return content;

    ValueSelector selector = this.selectorFactory.selector(replaceType, replacement, context);
    if (selector == null) return content;

    return this.searchReplacer.apply(content, token, searchType, selector);
  }
}