/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing.exporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.common.settings.Settings;
import org.opensearch.telemetry.OTelTelemetrySettings;

import io.opentelemetry.sdk.trace.export.SpanExporter;

/**
 * Factory class to create the {@link SpanExporter} instance.
 */
public class OTelSpanExporterFactory {

    private static final Logger logger = LogManager.getLogger(OTelSpanExporterFactory.class);

    /**
     * Base constructor.
     */
    private OTelSpanExporterFactory() {

    }

    /**
     * Creates the {@link SpanExporter} instances based on the OTEL_TRACER_SPAN_EXPORTER_CLASS_SETTING value.
     * As of now, it expects the SpanExporter implementations to have a create factory method to instantiate the
     * SpanExporter.
     * @param settings settings.
     * @return SpanExporter instance.
     */
    public static SpanExporter create(Settings settings) {
        Class<SpanExporter> spanExporterProviderClass = OTelTelemetrySettings.OTEL_TRACER_SPAN_EXPORTER_CLASS_SETTING.get(settings);
        SpanExporter spanExporter = OTelExporterUtil.<SpanExporter>instantiateExporter(spanExporterProviderClass);
        logger.info("Successfully instantiated the SpanExporter class {}", spanExporterProviderClass);
        return spanExporter;
    }
}
