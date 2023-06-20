/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.instrumentation;

public class NoopSpan implements Span {
    private final String spanName;
    private final Span parentSpan;
    private final Tracer.Level level;

    public NoopSpan(String spanName, Span parentSpan, Tracer.Level level) {
        this.spanName = spanName;
        this.parentSpan = parentSpan;
        this.level = level;
    }

    @Override
    public Span getParentSpan() {
        return parentSpan;
    }

    @Override
    public Tracer.Level getLevel() {
        return level;
    }

    @Override
    public String getSpanName() {
        return spanName;
    }

}
