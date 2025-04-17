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

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class BothDifference extends AbstractBothDifference {

    @Override
    void doDifference() {
        List<DifferenceUnique> sourcetList = sourceHandler.getData();
        List<DifferenceUnique> targetList = targetHandler.getData();
        if (CollectionUtils.isEmpty(sourcetList)) {
            /**
             *   TODO
             *       有这种极端环境吗？
             *       两边都删除 缓存为空
             *
             */
            this.deleteData.addAll(targetList);
        } else if (targetList.isEmpty()) {
            // TODO 全量加入缓存
            this.insertData.addAll(sourcetList);
            targetList.forEach((value) -> {
                this.allData.put(value.nodeUnique(), value);
            });
        }
        this.allData = this.difference(sourcetList, targetList);
    }
}
