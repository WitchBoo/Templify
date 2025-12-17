package me.whereareiam.templify.common.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.node.event.service.CloudServicePostPrepareEvent;
import eu.cloudnetservice.node.service.CloudService;
import lombok.RequiredArgsConstructor;
import me.whereareiam.templify.replacement.ReplacementService;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class ServicePrepareListener {
  private final ReplacementService replacementService;

  @EventListener
  public void handlePostPrepare(@NotNull CloudServicePostPrepareEvent event) {
    CloudService service = event.service();
    replacementService.apply(service.serviceInfo(), service.directory(), null);
  }
}
