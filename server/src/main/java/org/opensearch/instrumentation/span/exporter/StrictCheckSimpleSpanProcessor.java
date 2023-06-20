/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.instrumentation.span.exporter;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.common.util.concurrent.ThreadContext;

/**
 * This span processor maintains a state to make sure that all the started spans are closed. This will be adding the
 * performance overhead so shouldn't be used for production environments. This should just be used for testing purposes to verify if all the spans are closed.
 */
public class StrictCheckSimpleSpanProcessor implements SpanProcessor {
    private static final Logger logger = LogManager.getLogger(StrictCheckSimpleSpanProcessor.class);

    private final SpanExporter spanExporter;

    private Map<String, StackTraceElement[]> spanMap = new ConcurrentHashMap<>();

    public StrictCheckSimpleSpanProcessor(SpanExporter spanExporter) {
        this.spanExporter = spanExporter;
    }

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        String a = Thread.currentThread().getName();
        spanMap.put(span.getSpanContext().getSpanId(), Thread.currentThread().getStackTrace());
    }

    @Override
    public boolean isStartRequired() {
        return true;
    }

    @Override
    public void onEnd(ReadableSpan span) {
        spanMap.remove(span.getSpanContext().getSpanId());
        List<SpanData> spans = Collections.singletonList(span.toSpanData());
        spanExporter.export(spans);
    }

    @Override
    public boolean isEndRequired() {
        return true;
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode forceFlush() {
        return CompletableResultCode.ofSuccess();
    }

    public void ensureAllSpansAreClosed() throws InterruptedException{
        if (spanMap !=null && !spanMap.isEmpty()) {
            Thread.sleep(1000);
            for (Map.Entry<String, StackTraceElement[]> entry : spanMap.entrySet()) {
                StackTraceElement[] filteredStackTrace = getFilteredStackTrace(entry.getValue());
                AssertionError error = new AssertionError(String.format(" Total [%d] spans are not ended properly. " +
                    "Find below the stack trace of first un-ended span", spanMap.size()));
                error.setStackTrace(filteredStackTrace);
                spanMap.clear();
                throw error;
            }
        }
    }

    public void clear() {
        spanMap.clear();
    }

    private StackTraceElement[] getFilteredStackTrace(StackTraceElement[] stackTraceElements) {
        int filteredElementsCount = 0;
        while (filteredElementsCount < stackTraceElements.length) {
            String className = stackTraceElements[filteredElementsCount].getClassName();
            if (className.startsWith("java.lang.Thread") || className.startsWith("io.opentelemetry.sdk.") ||
                className.startsWith("org.opensearch.instrumentation")) {
                filteredElementsCount++;
            } else {
                break;
            }
        }
        return Arrays.copyOfRange(stackTraceElements, filteredElementsCount, stackTraceElements.length);
    }

}
