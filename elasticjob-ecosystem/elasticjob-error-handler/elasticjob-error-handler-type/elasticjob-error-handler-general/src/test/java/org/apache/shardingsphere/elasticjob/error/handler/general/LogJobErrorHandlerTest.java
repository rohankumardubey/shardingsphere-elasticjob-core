/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.error.handler.general;

import lombok.SneakyThrows;
import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandlerFactory;
import org.apache.shardingsphere.elasticjob.infra.exception.JobConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public final class LogJobErrorHandlerTest {
    
    @Mock
    private Logger log;
    
    @Test
    public void assertHandleException() {
        LogJobErrorHandler actual = (LogJobErrorHandler) JobErrorHandlerFactory.createHandler("LOG", new Properties()).orElseThrow(() -> new JobConfigurationException("LOG error handler not found."));
        setStaticFieldValue(actual);
        Throwable cause = new RuntimeException("test");
        actual.handleException("test_job", cause);
        verify(log).error("Job 'test_job' exception occur in job processing", cause);
    }
    
    @SneakyThrows
    private void setStaticFieldValue(final LogJobErrorHandler logJobErrorHandler) {
        Field field = logJobErrorHandler.getClass().getDeclaredField("log");
        field.setAccessible(true);
        Field modifiers = getModifierField();
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(logJobErrorHandler, log);
    }
    
    @SneakyThrows({NoSuchMethodException.class, IllegalAccessException.class, InvocationTargetException.class})
    private static Field getModifierField() {
        Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
        getDeclaredFields0.setAccessible(true);
        for (Field each : (Field[]) getDeclaredFields0.invoke(Field.class, false)) {
            if ("modifiers".equals(each.getName())) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }
}
