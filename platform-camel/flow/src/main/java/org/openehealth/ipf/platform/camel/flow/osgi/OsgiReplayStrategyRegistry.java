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
package org.openehealth.ipf.platform.camel.flow.osgi;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehealth.ipf.platform.camel.flow.ReplayStrategy;
import org.openehealth.ipf.platform.camel.flow.ReplayStrategyRegistration;
import org.openehealth.ipf.platform.camel.flow.ReplayStrategyRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Martin Krasser
 */
public class OsgiReplayStrategyRegistry implements ReplayStrategyRegistry {

    private static final Log LOG = LogFactory.getLog(OsgiReplayStrategyRegistry.class);
    
    public static final String REPLAY_STRATEGY_IDENTIFIER_KEY = 
        OsgiReplayStrategyRegistry.class.getPackage().getName();

    private BundleContext bundleContext;
    
    public OsgiReplayStrategyRegistry(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    public ReplayStrategyRegistration register(ReplayStrategy replayStrategy) {
        ServiceRegistration registration = bundleContext.registerService(ReplayStrategy.class.getName(), 
                replayStrategy, createRegistrationProperties(replayStrategy.getIdentifier()));
        LOG.info("Registered replay strategy with identifier " + 
                replayStrategy.getIdentifier() + " at OSGi service registry");
        return new OsgiReplayStrategyRegistration(registration, replayStrategy.getIdentifier());
    }

    private static Properties createRegistrationProperties(String replayStrategyId) {
        Properties properties = new Properties();
        properties.setProperty(REPLAY_STRATEGY_IDENTIFIER_KEY, replayStrategyId);
        return properties;
    }
    
    private static class OsgiReplayStrategyRegistration implements ReplayStrategyRegistration {
        
        private ServiceRegistration serviceRegistration;

        private String replayStrategyId;
        
        public OsgiReplayStrategyRegistration(ServiceRegistration serviceRegistration, String replayStrategyId) {
            this.serviceRegistration = serviceRegistration;
            this.replayStrategyId = replayStrategyId;
        }
        
        public void terminate() {
            serviceRegistration.unregister();
            LOG.info("Unregistered replay strategy with identifier " + 
                    replayStrategyId + " from OSGi service registry");
        }
        
    }
    
}