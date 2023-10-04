/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.telemetry.OTelAttributesConverter;
import org.opensearch.telemetry.OTelTelemetryPlugin;

import java.io.Closeable;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;

/**
 * OTel based Telemetry provider
 */
public class OTelTracingTelemetry implements TracingTelemetry {

    private static final Logger logger = LogManager.getLogger(OTelTracingTelemetry.class);
    private final OpenTelemetry openTelemetry;
    private final Closeable tracerProviderClosable;
    private final io.opentelemetry.api.trace.Tracer otelTracer;

    /**
     * Creates OTel based {@link TracingTelemetry}
     * @param openTelemetry OpenTelemetry instance
     * @param tracerProviderCloseable closable to close the tracer
     */
    public OTelTracingTelemetry(OpenTelemetry openTelemetry, Closeable tracerProviderCloseable) {
        this.openTelemetry = openTelemetry;
        this.otelTracer = openTelemetry.getTracer(OTelTelemetryPlugin.INSTRUMENTATION_SCOPE_NAME);
        this.tracerProviderClosable = tracerProviderCloseable;
    }

    @Override
    public void close() {
        try {
            tracerProviderClosable.close();
        } catch (Exception e) {
            logger.warn("Error while closing Opentelemetry TracerProvider", e);
        }
    }

    @Override
    public Span createSpan(SpanCreationContext spanCreationContext, Span parentSpan) {
        return createOtelSpan(spanCreationContext, parentSpan);
    }

    @Override
    public TracingContextPropagator getContextPropagator() {
        return new OTelTracingContextPropagator(openTelemetry);
    }

    private Span createOtelSpan(SpanCreationContext spanCreationContext, Span parentSpan) {
        io.opentelemetry.api.trace.Span otelSpan = otelSpan(
            spanCreationContext.getSpanName(),
            parentSpan,
            OTelAttributesConverter.convert(spanCreationContext.getAttributes()),
            OTelSpanKindConverter.convert(spanCreationContext.getSpanKind())
        );
        Span newSpan = new OTelSpan(spanCreationContext.getSpanName(), otelSpan, parentSpan);
        return newSpan;
    }

    io.opentelemetry.api.trace.Span otelSpan(
        String spanName,
        Span parentOTelSpan,
        io.opentelemetry.api.common.Attributes attributes,
        io.opentelemetry.api.trace.SpanKind spanKind
    ) {
        return parentOTelSpan == null || !(parentOTelSpan instanceof OTelSpan)
            ? otelTracer.spanBuilder(spanName).setAllAttributes(attributes).startSpan()
            : otelTracer.spanBuilder(spanName)
                .setParent(Context.current().with(((OTelSpan) parentOTelSpan).getDelegateSpan()))
                .setAllAttributes(attributes)
                .setSpanKind(spanKind)
                .startSpan();
    }
}
