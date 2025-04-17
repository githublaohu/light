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

import org.apache.eventmesh.dashboard.common.model.base.DifferenceUnique;
import org.apache.eventmesh.dashboard.core.metadata.DataMetadataHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lamp.light.common.difference.Difference;

import lombok.Setter;

public abstract class AbstractDifference implements Difference<DifferenceUnique> {


    protected Map<String, DifferenceUnique> allData = new HashMap<>();


    protected List<DifferenceUnique> deleteData = new ArrayList<>();

    protected List<DifferenceUnique> insertData = new ArrayList<>();

    protected List<DifferenceUnique> updateData = new ArrayList<>();

    @Setter
    protected DataMetadataHandler<DifferenceUnique> sourceHandler;

    @Setter
    protected DataMetadataHandler<DifferenceUnique> targetHandler;


    @Override
    public void difference() {
        try {
            this.doDifference();
            targetHandler.handleAll(this.insertData, this.updateData, this.deleteData);
        } catch (Exception e) {
            // TODO
        } finally {
            this.closeUpdate();
        }
    }

    public void closeAll() {
        this.allData.clear();
    }

    public void closeUpdate() {
        this.deleteData.clear();
        this.updateData.clear();
        this.insertData.clear();
    }


    abstract void doDifference();

}
