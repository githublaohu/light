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


package com.lamp.light.api.data.difference;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractBothDifference extends AbstractBufferDifference {


    protected Map<String, DifferenceUnique> difference(List<DifferenceUnique> sourcetList, List<DifferenceUnique> targetList) {
        Map<String, DifferenceUnique> targetAllData = new HashMap<>();
        targetList.forEach((value) -> {
            targetAllData.put(value.unique(), value);
        });
        return this.difference(sourcetList, targetAllData);
    }

    /**
     * database 与 cluster 求结果
     */
    protected Map<String, DifferenceUnique> difference(List<DifferenceUnique> sourcetList, Map<String, DifferenceUnique> targetAllData) {
        if (sourcetList.isEmpty()) {
            return targetAllData;
        }
        Map<String, DifferenceUnique> newAllData = new HashMap<>();
        sourcetList.forEach((value) -> {
            String key = value.unique();
            DifferenceUnique oldValue = this.allData.remove(key);
            if (Objects.isNull(oldValue)) {
                this.insertData.add(value);
                newAllData.put(key, value);
            } else if (!Objects.equals(oldValue, value)) {
                this.updateData.add(value);
                newAllData.put(key, value);
            } else {
                newAllData.put(key, value);
            }

        });
        this.deleteData.addAll(this.allData.values());
        return newAllData;
    }
}
