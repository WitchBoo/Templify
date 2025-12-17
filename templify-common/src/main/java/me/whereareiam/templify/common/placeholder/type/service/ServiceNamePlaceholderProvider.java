package me.whereareiam.templify.common.placeholder.type.service;

import me.whereareiam.templify.placeholder.PlaceholderContext;
import me.whereareiam.templify.placeholder.provider.PlaceholderProvider;

public final class ServiceNamePlaceholderProvider implements PlaceholderProvider {

  @Override
  public String getValue(PlaceholderContext context) {
    var serviceInfo = context.getServiceInfo();
    if (serviceInfo == null) return null;
    return serviceInfo.serviceId().name();
  }
}
