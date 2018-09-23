// Copyright (C) 2012 The Android Open Source Project
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

import com.google.gerrit.audit.AuditEvent;
import com.google.gerrit.audit.AuditListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LoggerAudit implements AuditListener {
  private final AuditWriter auditWriter;
  private final AuditRenderer auditRenderer;

  public static final String AUDIT_LOGGER_NAME = LoggerAudit.class.getName();

  @Inject
  LoggerAudit(AuditWriter auditWriter, AuditRenderer auditRenderer) {
    this.auditWriter = auditWriter;
    this.auditRenderer = auditRenderer;

    writeHeaders(auditWriter);
  }

  private void writeHeaders(AuditWriter auditWriter) {
    auditWriter.write(
        "EventId | EventTS | SessionId | User | Protocol data | Action | Parameters | Result | StartTS | Elapsed");
  }

  public static class Module extends AbstractModule {
    @Override
    protected void configure() {
      DynamicSet.bind(binder(), AuditListener.class).to(LoggerAudit.class);
    }
  }

  @Override
  public void onAuditableAction(AuditEvent auditEvent) {
    String auditString = auditRenderer.render(auditEvent);
    auditWriter.write(auditString);
  }
}
