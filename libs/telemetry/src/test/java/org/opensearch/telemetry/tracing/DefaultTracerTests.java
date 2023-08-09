/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing;

import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.concurrent.ThreadContext;
import org.opensearch.test.OpenSearchTestCase;

import java.io.IOException;
import org.opensearch.test.telemetry.tracing.MockSpan;
import org.opensearch.test.telemetry.tracing.MockTracingTelemetry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class DefaultTracerTests extends OpenSearchTestCase {

    private TracingTelemetry mockTracingTelemetry;
    private TracerContextStorage<String, Span> mockTracerContextStorage;
    private Span mockSpan;
    private Span mockParentSpan;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setupMocks();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateSpan() {
        DefaultTracer defaultTracer = new DefaultTracer(mockTracingTelemetry, mockTracerContextStorage);

        defaultTracer.startSpan("span_name");

        Assert.assertEquals("span_name", defaultTracer.getCurrentSpan().getSpan().getSpanName());
    }

    public void testCreateSpanWithAttributesWithMock() {
        DefaultTracer defaultTracer = new DefaultTracer(mockTracingTelemetry, mockTracerContextStorage);
        Map<String, String> attributeMap = Collections.singletonMap("name", "value");
        when(mockTracingTelemetry.createSpan("span_name", mockParentSpan, attributeMap)).thenReturn(mockSpan);
        defaultTracer.startSpan("span_name", attributeMap);
        verify(mockTracingTelemetry).createSpan("span_name", mockParentSpan, attributeMap);
    }

    public void testCreateSpanWithAttributesWithParentMock() {
        DefaultTracer defaultTracer = new DefaultTracer(mockTracingTelemetry, mockTracerContextStorage);
        Map<String, String> attributeMap = Collections.singletonMap("name", "value");
        when(mockTracingTelemetry.createSpan("span_name", mockParentSpan, attributeMap)).thenReturn(mockSpan);
        defaultTracer.startSpan("span_name", new SpanContext(mockParentSpan), attributeMap);
        verify(mockTracingTelemetry).createSpan("span_name", mockParentSpan, attributeMap);
        verify(mockTracerContextStorage, never()).get(TracerContextStorage.CURRENT_SPAN);
    }

    public void testCreateSpanWithAttributes() {
        TracingTelemetry tracingTelemetry = new MockTracingTelemetry();
        DefaultTracer defaultTracer = new DefaultTracer(
            tracingTelemetry,
            new ThreadContextBasedTracerContextStorage(new ThreadContext(Settings.EMPTY), tracingTelemetry)
        );

        defaultTracer.startSpan("span_name", Collections.singletonMap("name", "value"));

        Assert.assertEquals("span_name", defaultTracer.getCurrentSpan().getSpan().getSpanName());
        Assert.assertEquals("value", ((MockSpan) defaultTracer.getCurrentSpan().getSpan()).getAttribute("name"));
    }

    public void testCreateSpanWithParent() {
        TracingTelemetry tracingTelemetry = new MockTracingTelemetry();
        DefaultTracer defaultTracer = new DefaultTracer(
            tracingTelemetry,
            new ThreadContextBasedTracerContextStorage(new ThreadContext(Settings.EMPTY), tracingTelemetry)
        );

        defaultTracer.startSpan("span_name", null);

        SpanContext parentSpan = defaultTracer.getCurrentSpan();

        defaultTracer.startSpan("span_name_1", parentSpan, Collections.emptyMap());

        Assert.assertEquals("span_name_1", defaultTracer.getCurrentSpan().getSpan().getSpanName());
        Assert.assertEquals(parentSpan.getSpan(), defaultTracer.getCurrentSpan().getSpan().getParentSpan());
    }

    public void testCreateSpanWithNullParent() {
        TracingTelemetry tracingTelemetry = new MockTracingTelemetry();
        DefaultTracer defaultTracer = new DefaultTracer(
            tracingTelemetry,
            new ThreadContextBasedTracerContextStorage(new ThreadContext(Settings.EMPTY), tracingTelemetry)
        );

        defaultTracer.startSpan("span_name", null);

        Assert.assertEquals("span_name", defaultTracer.getCurrentSpan().getSpan().getSpanName());
        Assert.assertEquals(null, defaultTracer.getCurrentSpan().getSpan().getParentSpan());
    }

    public void testEndSpanByClosingScope() {
        DefaultTracer defaultTracer = new DefaultTracer(mockTracingTelemetry, mockTracerContextStorage);
        try (SpanScope spanScope = defaultTracer.startSpan("span_name")) {
            verify(mockTracerContextStorage).put(TracerContextStorage.CURRENT_SPAN, mockSpan);
        }
        verify(mockTracerContextStorage).put(TracerContextStorage.CURRENT_SPAN, mockParentSpan);
    }

    public void testClose() throws IOException {
        Tracer defaultTracer = new DefaultTracer(mockTracingTelemetry, mockTracerContextStorage);

        defaultTracer.close();

        verify(mockTracingTelemetry).close();
    }

    @SuppressWarnings("unchecked")
    private void setupMocks() {
        mockTracingTelemetry = mock(TracingTelemetry.class);
        mockSpan = mock(Span.class);
        mockParentSpan = mock(Span.class);
        mockTracerContextStorage = mock(TracerContextStorage.class);
        when(mockSpan.getSpanName()).thenReturn("span_name");
        when(mockSpan.getSpanId()).thenReturn("span_id");
        when(mockSpan.getTraceId()).thenReturn("trace_id");
        when(mockSpan.getParentSpan()).thenReturn(mockParentSpan);
        when(mockParentSpan.getSpanId()).thenReturn("parent_span_id");
        when(mockParentSpan.getTraceId()).thenReturn("trace_id");
        when(mockTracerContextStorage.get(TracerContextStorage.CURRENT_SPAN)).thenReturn(mockParentSpan, mockSpan);
        when(mockTracingTelemetry.createSpan("span_name", mockParentSpan, Collections.emptyMap())).thenReturn(mockSpan);
    }
}
