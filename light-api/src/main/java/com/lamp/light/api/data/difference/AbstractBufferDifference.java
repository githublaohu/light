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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public abstract class AbstractBufferDifference extends AbstractDifference implements DataMetadataHandler<DifferenceUnique> {


    protected Map<String, DifferenceUnique> allData = new HashMap<>();


    public void handleAll(List<DifferenceUnique> addData, List<DifferenceUnique> updateData, List<DifferenceUnique> deleteData) {
        this.targetHandler.handleAll(addData, updateData, deleteData);
    }

    /**
     * BothDifference 是否进行一次识别
     *
     * @return
     */
    public List<DifferenceUnique> getData() {
        return new ArrayList<>(allData.values());
    }

    public Map<String, DifferenceUnique> getAllData() {
        return allData;
    }

    public void setAllData(Map<String, DifferenceUnique> allData) {
        this.allData = allData;
    }
}
