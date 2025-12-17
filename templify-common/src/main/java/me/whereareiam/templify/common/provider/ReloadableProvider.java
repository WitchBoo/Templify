package me.whereareiam.templify.common.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.templify.Reloadable;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class ReloadableProvider implements Provider<Set<Reloadable>> {
  private final Set<Reloadable> reloadables = new HashSet<>();

  public void register(Reloadable reloadable) {
    reloadables.add(reloadable);
  }

  @Override
  public Set<Reloadable> get() {
    return reloadables;
  }
}
