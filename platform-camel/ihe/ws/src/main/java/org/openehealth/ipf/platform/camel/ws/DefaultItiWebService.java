/*
 * Copyright 2009 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ws;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.commons.lang.Validate;

/**
 * Base class for web services that are aware of a {@link DefaultItiConsumer}.
 *
 * @author Jens Riemschneider
 */
public class DefaultItiWebService {
    private DefaultItiConsumer consumer;

    /**
     * Calls the consumer for processing via Camel.
     *
     * @param body
     *          contents of the in-message body to be processed.
     * @return the resulting exchange.
     */
    protected Exchange process(Object body) {
        Validate.notNull(consumer);

        Exchange exchange = consumer.getEndpoint().createExchange(ExchangePattern.InOut);
        exchange.getIn().setBody(body);
        consumer.process(exchange);
        return exchange;
    }

    /**
     * Sets the consumer to be used to process exchanges
     * @param consumer
     *          the consumer to be used
     */
    public void setConsumer(DefaultItiConsumer consumer) {
        Validate.notNull(consumer, "consumer");
        this.consumer = consumer;
    }
}
