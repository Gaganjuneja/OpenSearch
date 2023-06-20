/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.index.shard;

import org.opensearch.common.inject.Inject;
import org.opensearch.instrumentation.SpanName;
import org.opensearch.instrumentation.Tracer;
import org.opensearch.instrumentation.TracerFactory;
import org.opensearch.search.internal.SearchContext;

/**
 * Class
 */
public class SearchOperationListenerTracerImpl implements SearchOperationListener {


    @Override
    public void onPreQueryPhase(SearchContext searchContext) {
        TracerFactory.getInstance().startSpan("onQueryPhase", null, Tracer.Level.INFO);
    }

    @Override
    public void onQueryPhase(SearchContext searchContext, long tookInNanos) {
        TracerFactory.getInstance().endSpan();
    }

    @Override
    public void onFailedQueryPhase(SearchContext searchContext) {
        TracerFactory.getInstance().endSpan();
    }


    @Override
    public void onPreFetchPhase(SearchContext searchContext) {
        TracerFactory.getInstance().addEvent("Closing span because of query phase failure");
        TracerFactory.getInstance().startSpan("onFetchPhase", null, Tracer.Level.INFO);
    }

    @Override
    public void onFetchPhase(SearchContext searchContext, long tookInNanos) {
        TracerFactory.getInstance().endSpan();
    }

    @Override
    public void onFailedFetchPhase(SearchContext searchContext) {
        TracerFactory.getInstance().addEvent("Closing span because of fetch phase failure");
        TracerFactory.getInstance().endSpan();
    }
}
