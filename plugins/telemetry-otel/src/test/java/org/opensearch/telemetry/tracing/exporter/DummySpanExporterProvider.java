/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing.exporter;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Collection;
import org.opensearch.common.settings.Settings;

public class DummySpanExporterProvider implements SpanExporterProvider {

    public DummySpanExporterProvider(String abc) {

    }

    @Override
    public SpanExporter create(Settings settings) {
        return new SpanExporter() {
            @Override
            public CompletableResultCode export(Collection<SpanData> spans) {
                return null;
            }

            @Override
            public CompletableResultCode flush() {
                return null;
            }

            @Override
            public CompletableResultCode shutdown() {
                return null;
            }
        };
    }
}