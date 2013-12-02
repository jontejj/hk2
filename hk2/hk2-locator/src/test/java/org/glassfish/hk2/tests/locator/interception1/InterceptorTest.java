/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.hk2.tests.locator.interception1;

import java.util.List;
import java.util.Map;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InterceptorTest {
    /**
     * Very simple intercepted service
     */
    @Test
    public void testMostBasicInterceptor() {
        ServiceLocator locator = LocatorHelper.create(null, new InterceptorModule());
        
        SimpleInterceptedService sis = locator.getService(SimpleInterceptedService.class);
        
        sis.callMe();
        
        RecordingInterceptor ri = locator.getService(RecordingInterceptor.class);
        
        List<String> cim = ri.getCalledInMethod();
        Assert.assertEquals(1, cim.size());
        Assert.assertEquals("callMe", cim.get(0));
        
        Map<String, Object> com = ri.getCalledOutMethod();
        Assert.assertEquals(1, com.size());
        
        Object value = com.get("callMe");
        Assert.assertEquals(new Integer(0), value);
    }
    
    /**
     * Tests an interceptor that does not proceed
     */
    @Test
    public void testNoProceedInterceptor() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(
                CountingService.class,
                NoProceedInterceptorService.class);
        
        CountingService counter = locator.getService(CountingService.class);
        
        counter.callMe();
        
        // Should be zero because the interceptor short-circuited it
        Assert.assertEquals(0, counter.gotCalled());
    }
    
    /**
     * Tests an interceptor that changes the input parameter
     */
    @Test
    public void testChangeInputInterceptor() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(
                RecordInputService.class,
                NegateTheInputInterceptorService.class);
        
        RecordInputService recorder = locator.getService(RecordInputService.class);
        
        recorder.recordInput(1);
        
        // Should be negative one because the interceptor negated the input
        Assert.assertEquals(-1, recorder.getLastInput());
    }
}