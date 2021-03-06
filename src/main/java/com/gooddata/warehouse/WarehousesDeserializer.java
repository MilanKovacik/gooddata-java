/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;

import java.util.List;
import java.util.Map;

class WarehousesDeserializer extends PageableListDeserializer<Warehouses, Warehouse> {

    protected WarehousesDeserializer() {
        super(Warehouse.class);
    }

    @Override
    protected Warehouses createList(final List<Warehouse> items, final Paging paging, final Map<String, String> links) {
        return new Warehouses(items, paging, links);
    }
}
