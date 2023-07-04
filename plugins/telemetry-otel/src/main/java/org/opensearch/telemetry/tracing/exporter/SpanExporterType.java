/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing.exporter;

import java.util.Arrays;

/**
 * Span Exporter type. For now, we are just supporting the 2 main types. But the idea is to make it configurable
 * through otel autoconfigure.
 */
public enum SpanExporterType {

    LOGGING, OLTP_GRPC;

    /**
     * Creates the {@link SpanExporterType} instance from the String.
     * @param name
     * @return
     */
    public static SpanExporterType fromString(String name) {
        for (SpanExporterType exporterType : values()) {
            if (exporterType.name().equalsIgnoreCase(name)) {
                return exporterType;
            }
        }
        throw new IllegalArgumentException(
            "invalid value for tracing level [" + name + "], " + "must be in " + Arrays.asList(SpanExporterType.values())
        );
    }
}
