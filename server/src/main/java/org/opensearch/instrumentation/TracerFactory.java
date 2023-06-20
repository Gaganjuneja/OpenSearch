/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.instrumentation;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.trace.SpanProcessor;
import java.util.Map;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.util.concurrent.ThreadContext;
import org.opensearch.instrumentation.span.exporter.StrictCheckSimpleSpanProcessor;
import org.opensearch.threadpool.ThreadPool;

/**
 * CLass
 */
public class TracerFactory {

    private static Tracer defaultTracer;
    private static Tracer noopTracer = new Tracer(){

        @Override
        public void startSpan(String spanName, Map<String, Object> attributes, Span parentSpan, Level level) {

        }

        @Override
        public void endSpan() {

        }

        @Override
        public void addAttribute(String key, Object value) {

        }

        @Override
        public void addEvent(String event) {

        }

        @Override
        public void setCurrentSpanInContext(Span span) {

        }

        @Override
        public Span getCurrentSpan() {
            return null;
        }
    };

    /**
     * initializeTracer
     * @param threadPool
     */
    public static synchronized void initializeTracer(ThreadPool threadPool, ClusterService clusterService){
        OpenTelemetry openTelemetry = OTelMain.getOpenTelemetry();
        io.opentelemetry.api.trace.Tracer openTelemetryTracer = openTelemetry.getTracer("instrumentation-library-name", "1.0.0");
        //if(defaultTracer == null) {
            defaultTracer = new DefaultTracer(openTelemetryTracer, threadPool, clusterService);
       // }else{
            //throw new IllegalStateException("Double-initialization not allowed!");
        //}
    }

    public static Tracer getInstance(){
        return defaultTracer != null? defaultTracer : noopTracer;
    }

}
