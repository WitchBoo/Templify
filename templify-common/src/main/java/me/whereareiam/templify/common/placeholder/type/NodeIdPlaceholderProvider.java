package me.whereareiam.templify.common.placeholder.type;

import java.util.Objects;
import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.provider.PlaceholderProvider;

public final class NodeIdPlaceholderProvider implements PlaceholderProvider {

  @Override
  public String getValue(PlaceholderContext context) {
    var serviceInfo = context.getServiceInfo();
    if (serviceInfo == null) return null;
    return Objects.toString(serviceInfo.serviceId().nodeUniqueId(), "");
  }
}
