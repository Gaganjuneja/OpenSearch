/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.instrumentation;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.util.concurrent.TimeUnit;
import org.opensearch.instrumentation.span.exporter.FileSpanExporter;

/**
 * CLass
 */
public class OTelMain {
    private static OpenTelemetry openTelemetry;
    private static final Resource resource;
    static {
        resource = Resource.getDefault()
            .merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "OpenSearch-Search")));
    }


    public static void initialize() {
        if(openTelemetry == null) {
            SpanProcessor spanProcessor = BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder()
                .setTimeout(2, TimeUnit.SECONDS).build()).build();
            //SpanProcessor spanProcessor = BatchSpanProcessor.builder(FileSpanExporter.create()).build();
            SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setResource(resource)
                .build();
            openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
        }
    }

    public static void initialize(SpanProcessor spanProcessor) {
        if(openTelemetry == null) {
            SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setResource(resource)
                .build();
            openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
        }
    }

    public static OpenTelemetry getOpenTelemetry(){
         if(openTelemetry == null){
             initialize();
         }
         return openTelemetry;
    }

    public static void shutdown(){
        ((OpenTelemetrySdk)openTelemetry).close();
    }
}
