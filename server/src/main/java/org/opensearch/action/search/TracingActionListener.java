/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.action.search;

import org.opensearch.action.ActionListener;
import org.opensearch.instrumentation.Span;
import org.opensearch.instrumentation.Tracer;
import org.opensearch.instrumentation.TracerFactory;
import org.opensearch.search.SearchPhaseResult;
import org.opensearch.search.SearchShardTarget;

/**
 * A base action listener that ensures shard target and shard index is set on all responses
 * received by this listener.
 *
 * @opensearch.internal
 */
public class TracingActionListener<T extends SearchPhaseResult> extends SearchActionListener<T> {

    private final Span span;
    private String spanName;
    private final SearchActionListener<T> listener;

    public TracingActionListener(String spanName, Span span, SearchActionListener<T> listener) {
        super(null, 1);
        this.spanName = spanName;
        this.span = span;
        this.listener = listener;
        TracerFactory.getInstance().startSpan(spanName, null, span, Tracer.Level.INFO);
    }


    @Override
    public void innerOnResponse(T response) {
        listener.innerOnResponse(response);
    }

    @Override
    public void onFailure(Exception e) {
        listener.onFailure(e);
        TracerFactory.getInstance().endSpan();
    }

    @Override
    public void setSearchShardTarget(T response) {
        listener.setSearchShardTarget(response);
    }

}
