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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti10;

import org.apache.camel.CamelContext;
import org.openehealth.ipf.modules.hl7.parser.PipeParser;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2TransactionConfiguration;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.NakFactory;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpAuditStrategy;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpComponent;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.QueryAuditDataset;

/**
 * Camel component for ITI-10 (PIX Update Notification).
 * @author Dmytro Rud
 */
public class Iti10Component extends MllpComponent<QueryAuditDataset> {
    public static final Hl7v2TransactionConfiguration CONFIGURATION =
        new Hl7v2TransactionConfiguration(
                "2.5", 
                "PIX adapter", 
                "IPF", 
                207, 
                207,
                new String[] {"ADT"},
                new String[] {"A31"},
                new String[] {"ACK"},
                new String[] {"*"}, 
                new boolean[] {true},
                new boolean[] {false},
                new PipeParser());
  
    private static final MllpAuditStrategy<QueryAuditDataset> CLIENT_AUDIT_STRATEGY = 
        new Iti10ClientAuditStrategy();
    private static final MllpAuditStrategy<QueryAuditDataset> SERVER_AUDIT_STRATEGY = 
        new Iti10ServerAuditStrategy();
    private static final NakFactory NAK_FACTORY = new NakFactory(CONFIGURATION);


    public Iti10Component() {
        super();
    }

    public Iti10Component(CamelContext camelContext) {
        super(camelContext);
    }
    
    @Override
    public MllpAuditStrategy<QueryAuditDataset> getClientAuditStrategy() {
        return CLIENT_AUDIT_STRATEGY;
    }

    @Override
    public MllpAuditStrategy<QueryAuditDataset> getServerAuditStrategy() {
        return SERVER_AUDIT_STRATEGY;
    }
    
    @Override
    public Hl7v2TransactionConfiguration getHl7v2TransactionConfiguration() {
        return CONFIGURATION;
    }

    @Override
    public NakFactory getNakFactory() {
        return NAK_FACTORY;
    }
}
