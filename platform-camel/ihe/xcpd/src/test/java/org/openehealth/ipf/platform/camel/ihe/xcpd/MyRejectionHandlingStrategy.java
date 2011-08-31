/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.xcpd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.message.Exchange;
import org.openehealth.ipf.commons.ihe.ws.cxf.AbstractWsRejectionHandlingStrategy;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample rejection handling strategy.
 * @author Dmytro Rud
 */
public class MyRejectionHandlingStrategy extends AbstractWsRejectionHandlingStrategy {
    private static final Log LOG = LogFactory.getLog(MyRejectionHandlingStrategy.class);

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static void resetCounter() {
        COUNTER.set(0);
    }

    public static int getCount() {
        return COUNTER.get();
    }

    @Override
    public void handleRejectedExchange(Exchange cxfExchange) {
        COUNTER.getAndIncrement();
    }
}