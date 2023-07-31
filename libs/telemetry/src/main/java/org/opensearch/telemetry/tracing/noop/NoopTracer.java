/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing.noop;

import org.opensearch.telemetry.tracing.Span;
import org.opensearch.telemetry.tracing.SpanScope;
import org.opensearch.telemetry.tracing.Tracer;

/**
 * No-op implementation of Tracer
 */
public class NoopTracer implements Tracer {

    /**
     * No-op Tracer instance
     */
    public static final Tracer INSTANCE = new NoopTracer();

    private NoopTracer() {}

    @Override
    public SpanScope startSpan(String spanName) {
        return SpanScope.NO_OP;
    }

    @Override
    public SpanScope startSpan(Span parentSpan, String spanName) {
        return SpanScope.NO_OP;
    }

    @Override
    public Span getCurrentSpan() {
        return null;
    }

    @Override
    public void close() {

    }
}
