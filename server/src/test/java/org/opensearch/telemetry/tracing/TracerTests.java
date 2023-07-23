/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.concurrent.ThreadContext;
import org.opensearch.test.OpenSearchTestCase;
import org.opensearch.test.telemetry.tracing.MockTracingTelemetry;

public class TracerTests extends OpenSearchTestCase {

    private final ThreadContextBasedTracerContextStorage contextStorage =
        new ThreadContextBasedTracerContextStorage(new ThreadContext(Settings.EMPTY), new MockTracingTelemetry());

    public void testIterationScenario() throws Exception{
        List<SpanScope> spansToBeClosed = new ArrayList<>();
        DefaultTracer defaultTracer = new DefaultTracer(new MockTracingTelemetry(), contextStorage);
        try (SpanScope parentSpanScope = defaultTracer.startSpan("parentSpan")) {
            Span parentSpan = defaultTracer.getCurrentSpan();
            for (int i = 0; i < 3; i++) {
                String spanName = "childSpan_" + i;
                try (AutoCloseable a = contextStorage.newTracerContextStorage()){
                SpanScope child = defaultTracer.startSpan(spanName);
                spansToBeClosed.add(child);
                assertEquals(parentSpan, defaultTracer.getCurrentSpan().getParentSpan());
            }
            }
        }
        spansToBeClosed.forEach(a -> a.close());
    }

}
