/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.telemetry.tracing.exporter;

import org.opensearch.SpecialPermission;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Factory class to create the Exporter instance.
 */
public class OTelExporterUtil {

    /**
     * Base constructor.
     */
    private OTelExporterUtil() {

    }

    /**
     * Utility to load and instantiate the Exporter classes.
     * @param exporterProviderClass exporter provider class
     * @return instance
     * @param <T> exporter type.
     */
    public static <T> T instantiateExporter(Class<T> exporterProviderClass) {
        try {
            // Check we ourselves are not being called by unprivileged code.
            SpecialPermission.check();
            return AccessController.doPrivileged((PrivilegedExceptionAction<T>) () -> {
                String methodName = "create";
                String getDefaultMethod = "getDefault";
                for (Method m : exporterProviderClass.getMethods()) {
                    if (m.getName().equals(getDefaultMethod)) {
                        methodName = getDefaultMethod;
                        break;
                    }
                }
                try {
                    return (T) MethodHandles.publicLookup()
                        .findStatic(exporterProviderClass, methodName, MethodType.methodType(exporterProviderClass))
                        .asType(MethodType.methodType(exporterProviderClass))
                        .invokeExact();
                } catch (Throwable e) {
                    if (e.getCause() instanceof NoSuchMethodException) {
                        throw new IllegalStateException("No create factory method exist in [" + exporterProviderClass.getName() + "]");
                    } else {
                        throw new IllegalStateException(
                            "Exporter instantiation failed for class [" + exporterProviderClass.getName() + "]",
                            e.getCause()
                        );
                    }
                }
            });
        } catch (PrivilegedActionException ex) {
            throw new IllegalStateException(
                "Exporter instantiation failed for class [" + exporterProviderClass.getName() + "]",
                ex.getCause()
            );
        }
    }
}
