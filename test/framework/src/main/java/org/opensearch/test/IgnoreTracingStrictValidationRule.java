/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.test;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class IgnoreTracingStrictValidationRule extends TestWatcher {
    private boolean shouldIgnoreTracingStrictValidation = false;
    @Override
    public void starting(Description description) {
        IgnoreTracingStrictValidation annotation = description.getAnnotation(IgnoreTracingStrictValidation.class);
        if(annotation != null){
            shouldIgnoreTracingStrictValidation = true;
        }else{
            shouldIgnoreTracingStrictValidation = false;
        }
    }

    public boolean shouldIgnoreTracingStrictValidation() {
        return shouldIgnoreTracingStrictValidation;
    }
}
