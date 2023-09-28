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

import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.sdk.metrics.export.AggregationTemporalitySelector;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

/**
 * Factory class to create the {@link MetricExporter} instance.
 */
public class OTelMetricsExporterFactory {

    private static final Logger logger = LogManager.getLogger(OTelMetricsExporterFactory.class);

    /**
     * Base constructor.
     */
    private OTelMetricsExporterFactory() {

    }

    /**
     * Creates the {@link MetricExporter} instances based on the OTEL_METRIC_EXPORTER_CLASS_SETTING value.
     * As of now, it expects the MetricExporter implementations to have a create factory method to instantiate the
     * MetricExporter.
     * @param settings settings.
     * @return MetricExporter instance.
     */
    public static MetricExporter create(Settings settings) {
        Class<MetricExporter> metricsExporterProviderClass = OTelTelemetrySettings.OTEL_METRICS_EXPORTER_CLASS_SETTING.get(settings);
        MetricExporter metricExporter;
        if (metricsExporterProviderClass.getName().equals("io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter")) {
            metricExporter = OtlpGrpcMetricExporter.builder()
                .setAggregationTemporalitySelector(AggregationTemporalitySelector.deltaPreferred())
                .build();
        } else {
            metricExporter = OTelExporterUtil.<MetricExporter>instantiateExporter(metricsExporterProviderClass);
        }
        logger.info("Successfully instantiated the Metrics Exporter class {}", metricsExporterProviderClass);
        return metricExporter;
    }
}
