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


package com.lamp.light.api.data.channel;

import java.util.List;

public interface UpdateDataChannel<T> {


    void addData(T data);

    default void addData(List<T> data) {
        if (data != null) {
            data.forEach(this::addData);
        }
    }

    default void replaceData(List<Object> data) {
        if (data != null) {
            deleteData((List<T>) data);
            addData((List<T>) data);
        }
    }

    default void updateData(T data) {
        this.addData(data);
    }

    default void updateData(List<T> data) {
        if (data != null) {
            data.forEach(this::updateData);
        }
    }

    void deleteData(T data);

    default void deleteData(List<T> data) {
        if (data != null) {
            data.forEach(this::deleteData);
        }
    }
}
