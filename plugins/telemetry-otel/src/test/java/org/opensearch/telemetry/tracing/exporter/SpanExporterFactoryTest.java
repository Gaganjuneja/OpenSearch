/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing.exporter;

import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.opensearch.common.settings.Settings;
import org.opensearch.test.OpenSearchTestCase;


public class SpanExporterFactoryTest extends OpenSearchTestCase {

    private final SpanExporterFactory spanExporterFactory = new SpanExporterFactory();

    public void testSpanExporterDefault() {
        Settings settings = Settings.builder().build();
        SpanExporter spanExporter = spanExporterFactory.create(settings);
        assertTrue(spanExporter instanceof LoggingSpanExporter);
    }

    public void testSpanExporterLogging() {
        Settings settings = Settings.builder().put(SpanExporterFactory.TRACER_SPAN_EXPORTER_TYPE_SETTING.getKey(), "logging").build();
        SpanExporter spanExporter = spanExporterFactory.create(settings);
        assertTrue(spanExporter instanceof LoggingSpanExporter);
    }

    public void testSpanExporterGrpc() {
        Settings settings = Settings.builder().put(SpanExporterFactory.TRACER_SPAN_EXPORTER_TYPE_SETTING.getKey(), "OLTP_GRPC").build();
        SpanExporter spanExporter = spanExporterFactory.create(settings);
        assertTrue(spanExporter instanceof OtlpGrpcSpanExporter);
    }

    public void testSpanExporterInvalid() {
        Settings settings = Settings.builder().put(SpanExporterFactory.TRACER_SPAN_EXPORTER_TYPE_SETTING.getKey(), "abc").build();
        assertThrows(IllegalArgumentException.class, () -> spanExporterFactory.create(settings));
    }
}
