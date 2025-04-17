/*
 *Copyright (c) [Year] [name of copyright holder]
 *[Software Name] is licensed under Mulan PubL v2.
 *You can use this software according to the terms and conditions of the Mulan PubL v2.
 *You may obtain a copy of Mulan PubL v2 at:
 *         http://license.coscl.org.cn/MulanPubL-2.0
 *THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *See the Mulan PubL v2 for more details.
 */
package com.lamp.light.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author laohu
 *
 */
public class WrongLastingData {

	private Map<Class<?>, List<Object>> wrongLastingDataList = new HashMap<>();
	
	
	public void addWrongLastingData(Object object) {
		List<Object> arrayList = wrongLastingDataList.computeIfAbsent(object.getClass(), k -> new ArrayList<>());
		synchronized (arrayList) {
			arrayList.add(object);
		}
	}
}
