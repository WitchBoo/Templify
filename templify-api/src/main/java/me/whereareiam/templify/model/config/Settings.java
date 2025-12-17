package me.whereareiam.templify.model.config;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.templify.type.ReplaceType;
import me.whereareiam.templify.type.SearchType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class Settings {
  private DefaultSection defaults;
  private PathSection paths;
  private LimitSection limits;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static final class DefaultSection {
    private SearchType searchType;
    private ReplaceType replaceType;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static final class PathSection {
    private List<String> filePatterns;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static final class LimitSection {
    private long maxFileSizeBytes;
  }
}

