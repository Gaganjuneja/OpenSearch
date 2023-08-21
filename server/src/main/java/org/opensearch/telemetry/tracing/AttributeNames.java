/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing;

/**
 * Hold the Attribute names to avoid the duplication and consistensy.
 */
public class AttributeNames {

    /**
     * HTTP Protocol Version
     */
    public static final String SPAN_ATTR_KEY_HTTP_PROTOCOL_VERSION = "version";

    /**
     * HTTP method
     */
    public static final String SPAN_ATTR_KEY_HTTP_METHOD = "method";

    /**
     * HTTP Request URI.
     */
    public static final String SPAN_ATTR_KEY_HTTP_URI = "uri";

    /**
     * HTTP REQ Inbound Exception.
     */
    public static final String SPAN_ATTR_KEY_HTTP_REQ_INBOUND_EX = "req_inbound_ex";

    /**
     * Rest Request ID.
     */
    public static final String SPAN_ATTR_KEY_REST_REQ_ID = "request_id";

    /**
     * Rest Request Raw Path.
     */
    public static final String SPAN_ATTR_KEY_REST_REQ_RAW_PATH = "raw_path";

    /**
     * Trace key. To be used for on demaand sampling.
     */
    public static final String SPAN_ATTR_KEY_TRACE = "trace";

    /**
     * Transport Service send request target host.
     */
    public static final String SPAN_ATTR_KEY_TARGET_HOST = "target_host";

    /**
     * Action Name.
     */
    public static final String SPAN_ATTR_KEY_ACTION = "action";
}
