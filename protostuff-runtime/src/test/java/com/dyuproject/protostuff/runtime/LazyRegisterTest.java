//========================================================================
//Copyright 2016 David Yu
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.protostuff.runtime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.dyuproject.protostuff.AbstractTest;
import com.dyuproject.protostuff.GraphIOUtil;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.Schema;

/**
 * TODO
 * 
 * @author David Yu
 * @created Oct 29, 2016
 */
public class LazyRegisterTest extends AbstractTest
{
    
    public static class ObjPackage
    {
        public Object getObj()
        {
            return this.obj;
        }

        public ObjPackage setObj(Object obj)
        {
            this.obj = obj;
            olist = new ArrayList<Object>();
            olist.add(obj);
            omap = new HashMap<String, Object>();
            omap.put("obj", obj);
            
            if (obj instanceof MainData)
            {
                list = new ArrayList<MainData>();
                list.add((MainData)obj);
                map = new HashMap<String, MainData>();
                map.put("obj", (MainData)obj);
            }

            return this;
        }

        Object obj;
        List<MainData> list;
        List<Object> olist;
        Map<String,MainData> map;
        Map<String,Object> omap;

        public String toString()
        {
            return obj.toString();
        }
    }

    public static class MainData implements Map<String, Object>
    {

        protected ConcurrentHashMap<String, Object> voProperties = new ConcurrentHashMap<String, Object>();

        private String name;

        private int id;

        public MainData()
        {

        }

        public MainData(String name, int id)
        {
            this.name = name;
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public int size()
        {
            return voProperties.size();
        }

        public boolean isEmpty()
        {
            return voProperties.size() == 0;
        }

        public boolean containsKey(Object key)
        {
            return voProperties.containsKey(key);
        }

        public boolean containsValue(Object value)
        {
            return voProperties.containsValue(value);
        }

        public Object get(Object key)
        {
            return voProperties.get(key);
        }

        public Object put(String key, Object value)
        {
            return voProperties.put(key, value);
        }

        public Object remove(Object key)
        {
            return voProperties.remove(key);
        }

        public void putAll(Map<? extends String, ? extends Object> m)
        {
            voProperties.putAll(m);
        }

        public void clear()
        {
            voProperties.clear();
        }
        
        public Set<String> keySet()
        {
            return voProperties.keySet();
        }

        public Collection<Object> values()
        {
            return voProperties.values();
        }

        public Set<java.util.Map.Entry<String, Object>> entrySet()
        {
            return voProperties.entrySet();
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("name=").append(name).append(", id=").append(id);
            for (String key : voProperties.keySet())
            {
                sb.append(" ,key=").append(key).append(", value=").append(voProperties.get(key));
            }

            return sb.toString();
        }
    }
    
    static void verifyEquals(MainData expected, MainData value)
    {
        assertEquals(expected.name, value.name);
        assertEquals(expected.id, value.id);
    }
    
    public void testIt() throws IOException
    {
        RuntimeSchema.register(MainData.class);
        
        MainData testdata = new MainData("test", 123);
        ObjPackage obj = new ObjPackage();
        obj.setObj(testdata);
        testdata.put("aaaa", "bbb");
        Schema<ObjPackage> schema = RuntimeSchema.getSchema(ObjPackage.class);
        LinkedBuffer buffer = LinkedBuffer.allocate(256);
        ByteArrayOutputStream oss = new ByteArrayOutputStream(1024);
        
        GraphIOUtil.writeTo(oss, obj, schema, buffer);
        
        ObjPackage objParsed = schema.newMessage();
        GraphIOUtil.mergeFrom(oss.toByteArray(), objParsed, schema);
        
        assertTrue(objParsed.obj instanceof MainData);
        verifyEquals(testdata, (MainData)objParsed.obj);
        
        assertTrue(objParsed.olist != null && 
                objParsed.olist.size() == 1 &&
                objParsed.olist.get(0) instanceof MainData);
        verifyEquals(testdata, (MainData)objParsed.olist.get(0));
        
        assertTrue(objParsed.list != null && 
                objParsed.list.size() == 1);
        verifyEquals(testdata, (MainData)objParsed.list.get(0));
        
        assertTrue(objParsed.omap != null && 
                objParsed.omap.size() == 1 &&
                objParsed.omap.get("obj") instanceof MainData);
        verifyEquals(testdata, (MainData)objParsed.omap.get("obj"));
        
        assertTrue(objParsed.map != null && 
                objParsed.map.size() == 1 &&
                objParsed.map.get("obj") instanceof MainData);
        verifyEquals(testdata, (MainData)objParsed.map.get("obj"));
    }
}
