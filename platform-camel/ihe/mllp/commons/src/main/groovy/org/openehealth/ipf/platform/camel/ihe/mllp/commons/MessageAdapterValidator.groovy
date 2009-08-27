/*
 * Copyright 2008 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.mllp.commons

import org.openehealth.ipf.commons.core.modules.api.Validator
import org.openehealth.ipf.modules.hl7.validation.DefaultValidationContext

import java.util.Map
import ca.uhn.hl7v2.model.Group
import ca.uhn.hl7v2.validation.MessageValidator

/**
 * @author Dmytro Rud
 */
class MessageAdapterValidator implements Validator<MessageAdapter, Object> {
     
     /**
      * List of relevant segments for particular message types.
      */
     def static final RULES =
         [
          'ADT' : ['A01 A04 A05 A08' : ['MSH', 'EVN', 'PID', 'PV1'],
                   'A40'             : ['MSH', 'EVN', 'PID', 'MRG'],
                  ],
          'ACK' : ['*'               : ['MSH', 'MSA'],
                  ],
         ]

     
     /**
      * Performs validation of an HL7 message
      * @param msg
      *     {@link MessageAdapter} with the mesasge to be validated
      * @param dummy
      *     unused fictive parameter  
      */
     void validate(Object msg, Object dummy) throws ValidationException {
         def msh91 = msg.MSH[9][1].value
         def msh92 = msg.MSH[9][2].value

         // find rules that correspond the type of the given message
         def segmentNames = null
         for(triggerEvents in RULES[msh91]?.keySet()) {
             if(triggerEvents.contains(msh92) || (triggerEvents == '*')) {
                 segmentNames = RULES[msh91][triggerEvents]
                 break
             }
         }
         if( ! segmentNames) {
             throw new ValidationException("No validation rules defined for ${msh91}^${msh92}")
         }
         
         // perform validation
         def exceptions = checkMessage(msg, segmentNames)
         if(exceptions) {
             throw new ValidationException('Message validation failed', exceptions)
         }
     }

         
     /**
      * Validates a message.
      */
     static def checkMessage(msg, segmentNames) {
         def exceptions = []
         exceptions += checkUnrecognizedSegments(msg.group)
         for(segmentName in segmentNames) {
             exceptions += "check${segmentName}"(msg)
         }
         exceptions
     }
     
     /**
      * Validates structure of a message segment.
      */
     static def checkSegmentStructure(msg, segmentName, fieldNumbers) {
         def exceptions = []
         def segment = msg."${segmentName}"
         for(i in fieldNumbers) {
             if( ! segment[i].value) {
                 exceptions += new Exception("Missing ${segmentName}-${i}")
             }
         }
         exceptions
     }

     /**
      * Validates segment MSH.
      */
     static def checkMSH(msg) {
         checkSegmentStructure(msg, 'MSH', [1, 2, 7, 9, 10, 11, 12])
     }

     /**
      * Validates segment EVN.
      */
     static def checkEVN(msg) {
         checkSegmentStructure(msg, 'EVN', [2])
     }

     /*
      * Validates segment PV1.
      */
     static def checkPV1(msg) {
         checkSegmentStructure(msg, 'PV1', [2])
     }

     /**
      * Validates segment MSA.
      */
     static def checkMSA(msg) {
         checkSegmentStructure(msg, 'MSA', [1, 2])
     }

     /**
      * Validates segment PID.
      */
     static def checkPID(msg) {
         def exceptions = []
         exceptions += checkPatientName(msg.PID[5])
         exceptions += checkPatientId(msg.PID[3])
         exceptions
     }

     /**
      * Validates segment MRG.
      */
     static def checkMRG(msg) {
         checkPatientId(msg.MRG[1])
     }

     /**
      * Validates patient name (datatype XPN).
      */
     static def checkPatientName(xpn) {
         def exceptions = []
         if( ! (xpn[1].value || xpn[2].value)) {
             exceptions += new Exception('Missing patient name')
         }
         exceptions
     }
     
     /**
      * Validates patient ID (datatype CX).
      */
     static def checkPatientId(cx) {
         def exceptions = []
         if( ! (cx[1].value && (cx[4][1].value || (cx[4][2].value && (cx[4][3].value == 'ISO'))))) {
             exceptions += new Exception('Patient ID assigning authority not specified')
         }
         exceptions
     }

     /**
      * Searches for unrecognized segments in a Group.
      */
     static def checkUnrecognizedSegments(Group group) {
         def exceptions = []
         for(name in group.names) {
             def c = group.getClass(name)
             if(c == GenericSegment.class) {
                 exceptions += new Exception("Unknown segment ${name}")
             } else if(Group.class.isAssignableFrom(c)) {
                 exceptions += checkUnrecognizedSegments(group.get(name))
             }
         }
         exceptions
     }
     
}



