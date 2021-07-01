/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PSL v2.
 *You can use this software according to the terms and conditions of the Mulan PSL v2.
 *You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PSL v2 for more details.
 */
package com.lamp.light.handler;

import java.util.HashMap;
import java.util.Map;

public interface TypeToString<T> {
    
    @SuppressWarnings("unchecked")
    public static String  ObjectToString(Object object) {
        if(!object.getClass().isPrimitive()) {
           return object instanceof String ? (String)object: object.toString();
        }
        String cn = object.getClass().getSimpleName();
        return TypeToStringWrapper.TPYE_TO_OBJECT.get(cn).typeToString(object);
    }
    
    String typeToString(T t);

    static class TypeToStringWrapper{
        @SuppressWarnings("rawtypes")
        static Map<String, TypeToString> TPYE_TO_OBJECT = new HashMap<>();
        
        static {
            TPYE_TO_OBJECT.put("int",  new IntToString());
            TPYE_TO_OBJECT.put("long", new LongToString());
            TPYE_TO_OBJECT.put("short", new ShortToString());
            TPYE_TO_OBJECT.put("byte", new ShortToString());
            TPYE_TO_OBJECT.put("double", new DoubleToString());
            TPYE_TO_OBJECT.put("float", new FloatToString());
        }
    }
    
    static class LongToString implements TypeToString<Long> {

        @Override
        public String typeToString(Long t) {
            return t.toString();
        }

    }

    static class IntToString implements TypeToString<Integer> {

        @Override
        public String typeToString(Integer t) {
            return t.toString();
        }

    }

    static class ShortToString implements TypeToString<Short> {

        @Override
        public String typeToString(Short t) {
            return t.toString();
        }

    }

    static class ByteToString implements TypeToString<Byte> {

        @Override
        public String typeToString(Byte t) {
            return t.toString();
        }

    }

    static class DoubleToString implements TypeToString<Double> {

        @Override
        public String typeToString(Double t) {
            return t.toString();
        }

    }

    static class FloatToString implements TypeToString<Float> {
        @Override
        public String typeToString(Float t) {
            return t.toString();
        }

    }
}
