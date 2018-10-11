/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.provider;

import org.gradle.util.DeprecationLogger;

public abstract class AbstractProperty<T> extends AbstractMinimalProvider<T> implements PropertyInternal<T> {
    private enum State {
        Mutable, FinalLenient, FinalStrict
    }
    private State state = State.Mutable;

    @Override
    public void finalizeValue() {
        if (state == State.Mutable) {
            makeFinal();
        }
        state = State.FinalStrict;
    }

    @Override
    public void finalizeValueAndWarnAboutChanges() {
        if (state == State.Mutable) {
            makeFinal();
            state = State.FinalLenient;
        }
    }

    protected abstract void makeFinal();

    protected boolean assertMutable() {
        if (state == State.FinalStrict) {
            throw new IllegalStateException("The value for this property is final and cannot be changed any further.");
        } else if (state == State.FinalLenient) {
            DeprecationLogger.nagUserOfDiscontinuedInvocation("Changing the value for a property with a final value");
            return false;
        }
        return true;
    }
}
