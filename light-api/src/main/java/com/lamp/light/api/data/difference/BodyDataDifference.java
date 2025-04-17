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



import java.util.List;

public class BodyDataDifference extends AbstractBufferDifference {


    @Override
    void doDifference() {
        List<DifferenceUnique> objectList = sourceHandler.getData();
        if (objectList.isEmpty()) {
            return;
        }
        objectList.forEach((value) -> {
            String key = value.nodeUnique();
            if (value.isInsert()) {
                this.insertData.add(value);
                this.allData.put(key, value);
            } else if (value.isUpdate()) {
                this.updateData.add(value);
                this.allData.put(key, value);
            } else if (value.isDelete()) {
                this.deleteData.add(value);
                this.allData.remove(key);
            }
        });

    }
}
