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

package org.apache.shardingsphere.elasticjob.cloud;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Reflection utilities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtils {
    
    /**
     * Set field value.
     *
     * @param target target object
     * @param fieldName field name
     * @param fieldValue field value
     */
    @SneakyThrows
    public static void setFieldValue(final Object target, final String fieldName, final Object fieldValue) {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, fieldValue);
    }
    
    /**
     * Set static field value.
     *
     * @param target target object
     * @param fieldName field name
     * @param fieldValue field value
     */
    @SneakyThrows
    public static void setStaticFieldValue(final Class target, final String fieldName, final Object fieldValue) {
        Field field = target.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiers = getModifierField();
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(target, fieldValue);
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
