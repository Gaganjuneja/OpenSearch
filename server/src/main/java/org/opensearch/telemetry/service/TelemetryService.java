/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.service;

import java.io.IOException;
import java.util.List;
import org.opensearch.common.annotation.ExperimentalApi;
import org.opensearch.common.lifecycle.AbstractLifecycleComponent;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.FeatureFlags;
import org.opensearch.plugins.PluginsService;
import org.opensearch.plugins.TelemetryPlugin;
import org.opensearch.telemetry.TelemetryModule;
import org.opensearch.telemetry.TelemetrySettings;
import org.opensearch.telemetry.metrics.MetricsRegistry;
import org.opensearch.telemetry.metrics.MetricsRegistryFactory;
import org.opensearch.telemetry.metrics.NoopMetricsRegistryFactory;
import org.opensearch.telemetry.tracing.NoopTracerFactory;
import org.opensearch.telemetry.tracing.Tracer;
import org.opensearch.telemetry.tracing.TracerFactory;
import org.opensearch.threadpool.ThreadPool;

import static org.opensearch.common.util.FeatureFlags.TELEMETRY;

/**
 * Telemetry Service.
 * @opensearch.experimental
 */
@ExperimentalApi
public class TelemetryService extends AbstractLifecycleComponent {

    private TracerFactory tracerFactory = new NoopTracerFactory();
    private MetricsRegistryFactory metricsRegistryFactory = new NoopMetricsRegistryFactory();

    public TelemetryService(Settings settings, ClusterSettings clusterSettings, PluginsService pluginsService, ThreadPool threadPool) {
        init(settings, clusterSettings, pluginsService, threadPool);
    }

    private void init(Settings settings, ClusterSettings clusterSettings, PluginsService pluginsService, ThreadPool threadPool) {
        if (FeatureFlags.isEnabled(TELEMETRY)) {
            final TelemetrySettings telemetrySettings = new TelemetrySettings(settings, clusterSettings);
            if (telemetrySettings.isTracingFeatureEnabled() || telemetrySettings.isMetricsFeatureEnabled()) {
                List<TelemetryPlugin> telemetryPlugins = pluginsService.filterPlugins(TelemetryPlugin.class);
                TelemetryModule telemetryModule = new TelemetryModule(telemetryPlugins, telemetrySettings);
                if (telemetrySettings.isTracingFeatureEnabled()) {
                    tracerFactory = new TracerFactory(telemetrySettings, telemetryModule.getTelemetry(), threadPool.getThreadContext());
                }
                if (telemetrySettings.isMetricsFeatureEnabled()) {
                    metricsRegistryFactory = new MetricsRegistryFactory(telemetrySettings, telemetryModule.getTelemetry());
                }
            }
        }
    }

    public Tracer getTracer(){
        return tracerFactory.getTracer();
    }

    public MetricsRegistry getMetricsRegistry(){
        return metricsRegistryFactory.getMetricsRegistry();
    }

    @Override
    protected void doStart() {
    }

    @Override
    protected void doStop() {
    }

    @Override
    protected void doClose() throws IOException {
        metricsRegistryFactory.getMetricsRegistry().close();
        tracerFactory.getTracer().close();
    }
}
