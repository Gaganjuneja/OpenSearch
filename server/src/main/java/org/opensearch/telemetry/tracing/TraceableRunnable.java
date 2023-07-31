/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing;

/**
 * TraceableRunnable
 */
public class TraceableRunnable implements Runnable{
    private Runnable runnable;
    private Span parent;
    private TracerFactory tracerFactory;

    /**
     * Constructor
     * @param tracerFactory tracerFactory
     * @param parent parent
     * @param runnable runnable
     */
    public TraceableRunnable(TracerFactory tracerFactory, Span parent, Runnable runnable) {
        this.runnable = runnable;
        this.parent = parent;
        this.tracerFactory = tracerFactory;
    }

    @Override
    public void run() {
        try(SpanScope spanScope = tracerFactory.getTracer().startSpan(parent, "traceableRunnable")){
            runnable.run();
        }
    }
}
