// Copyright (C) 2018 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.auditsl4j;

import com.google.gerrit.audit.AuditListener;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class Module extends AbstractModule {
  private final PluginConfig config;

  @Inject
  public Module(@PluginName String pluginName, PluginConfigFactory configFactory) {
    config = configFactory.getFromGerritConfig(pluginName);
  }

  @Override
  protected void configure() {
    DynamicSet.bind(binder(), AuditListener.class).to(LoggerAudit.class);

    AuditRenderTypes rendererType = config.getEnum("renderer", AuditRenderTypes.CSV);
    switch (rendererType) {
      case CSV:
        bind(AuditRenderer.class).to(AuditRendererToCsv.class);
        break;
      case JSON:
        bind(AuditRenderer.class).to(AuditRendererToJson.class);
        break;
      default:
        throw new IllegalArgumentException("Unsupported renderer '" + rendererType + "'");
    }
  }
}