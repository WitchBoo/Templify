package me.whereareiam.templify.common.replacement.operation;

import com.google.inject.Singleton;
import lombok.NonNull;
import me.whereareiam.templify.replacement.ReplacementOperation;
import me.whereareiam.templify.replacement.ReplacementOperationRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public final class DefaultReplacementOperationRegistry implements ReplacementOperationRegistry {
  private final List<OperationEntry> entries = new CopyOnWriteArrayList<>();

  @Override
  public void register(@NonNull ReplacementOperation operation) {
    this.register(operation, 0);
  }

  @Override
  public void register(@NonNull ReplacementOperation operation, int priority) {
    this.entries.add(new OperationEntry(operation, priority));
    this.entries.sort(Comparator.comparingInt(OperationEntry::priority).reversed());
  }

  @Override
  public @NonNull List<ReplacementOperation> getOperations() {
    var result = new ArrayList<ReplacementOperation>(this.entries.size());
    for (var entry : this.entries) {
      result.add(entry.operation());
    }
    return result;
  }

  private record OperationEntry(@NonNull ReplacementOperation operation, int priority) {}
}
