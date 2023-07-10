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
import java.net.NetPermission;
import org.opensearch.common.settings.Setting;
import org.opensearch.common.settings.Settings;

/**
 * Factory class to create the {@link SpanExporter} instance.
 */
public class SpanExporterFactory {

    /**
     * Span Exporter type setting.
     */
    public static final Setting<SpanExporterType> TRACER_SPAN_EXPORTER_TYPE_SETTING = new Setting<>(
        "telemetry.tracer.span.exporter.type",
        SpanExporterType.OLTP_GRPC.name(),
        SpanExporterType::fromString,
        Setting.Property.NodeScope,
        Setting.Property.Final
    );

    /**
     * Base constructor.
     */
    public SpanExporterFactory() {

    }

    /**
     * Creates the {@link SpanExporter} instances based on the TRACER_SPAN_EXPORTER_TYPE_SETTING value.
     * @param settings settings.
     * @return SpanExporter instance.
     */
    public SpanExporter create(Settings settings) {
        System.out.println(SpanExporterType.class.getClassLoader().getName());
        System.out.println("Parent " + SpanExporterType.class.getClassLoader().getParent().getName());
        SpanExporterType exporterType = SpanExporterType.OLTP_GRPC;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new NetPermission("getProxySelector"));
        }
        switch (exporterType) {
            case OLTP_GRPC:
                //TODO: For now just giving default implementation, but we need to use otel autoconfigue to expose
                // all the configurations.
                return OtlpGrpcSpanExporter.builder().build();
            case LOGGING:
            default:
                return LoggingSpanExporter.create();
        }
    }
}
