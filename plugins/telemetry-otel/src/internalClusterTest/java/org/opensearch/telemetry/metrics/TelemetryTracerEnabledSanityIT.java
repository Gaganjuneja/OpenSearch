/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.metrics;

import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.opensearch.client.Client;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.plugins.Plugin;
import org.opensearch.search.SearchService;
import org.opensearch.telemetry.OTelTelemetrySettings;
import org.opensearch.telemetry.TelemetrySettings;
import org.opensearch.telemetry.tracing.InMemorySingletonSpanExporter;
import org.opensearch.telemetry.tracing.IntegrationTestOTelTelemetryPlugin;
import org.opensearch.telemetry.tracing.attributes.Attributes;
import org.opensearch.test.OpenSearchIntegTestCase;
import org.opensearch.test.OpenSearchSingleNodeTestCase;
import org.opensearch.test.telemetry.tracing.TelemetryValidators;
import org.opensearch.test.telemetry.tracing.validators.AllSpansAreEndedProperly;
import org.opensearch.test.telemetry.tracing.validators.AllSpansHaveUniqueId;
import org.opensearch.test.telemetry.tracing.validators.NumberOfTraceIDsEqualToRequests;
import org.opensearch.test.telemetry.tracing.validators.TotalRootSpansEqualToRequests;

import static org.opensearch.index.query.QueryBuilders.queryStringQuery;

public class TelemetryTracerEnabledSanityIT extends OpenSearchSingleNodeTestCase {

    @Override
    protected Settings nodeSettings() {
        return Settings.builder()
            .put(super.nodeSettings())
            .put(
                OTelTelemetrySettings.OTEL_METRICS_EXPORTER_CLASS_SETTING.getKey(),
                "org.opensearch.telemetry.metrics.InMemorySingletonMetricsExporter"
            )
            .put(OTelTelemetrySettings.METRICS_PUBLISH_INTERVAL_SETTING.getKey(), TimeValue.timeValueSeconds(1))
            .build();
    }

    @Override
    protected Collection<Class<? extends Plugin>> getPlugins() {
        return Arrays.asList(IntegrationTestOTelTelemetryPlugin.class);
    }

    @Override
    protected boolean addMockTelemetryPlugin() {
        return false;
    }

    public void testSanityChecksWhenMetricsEnabled() throws Exception {
        updateTelemetrySetting(client(), true);
        MetricsRegistry metricsRegistry = node().injector().getInstance(MetricsRegistry.class);

        Counter counter = metricsRegistry.createCounter("test-counter", "test", "1");
        counter.add(1.0);
        // Sleep for about 2s to wait for metrics to be published.
        Thread.sleep(2000);

        InMemorySingletonMetricsExporter exporter = InMemorySingletonMetricsExporter.INSTANCE;
        double value = ((DoublePointData)((ArrayList)exporter.getFinishedMetricItems().get(0).getDoubleSumData().getPoints()).get(0)).getValue();
        assertTrue(1.0 == value);
        cleanupSettings(client());
    }

    private static void updateTelemetrySetting(Client client, boolean value) {
        client.admin()
            .cluster()
            .prepareUpdateSettings()
            .setTransientSettings(Settings.builder().put(TelemetrySettings.METRICS_ENABLED_SETTING.getKey(), value))
            .get();
    }

    private void cleanupSettings(Client client){
        client().admin()
            .cluster()
            .prepareUpdateSettings()
            .setTransientSettings(Settings.builder().putNull(TelemetrySettings.METRICS_ENABLED_SETTING.getKey()))
            .get();
    }

}
