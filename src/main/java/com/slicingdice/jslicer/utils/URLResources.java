/*
 * Copyright (C) 2016 Simbiose Ventures.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slicingdice.jslicer.utils;

/**
 * Enum with Slicing Dice endpoints
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public enum URLResources {
    COLUMN("/column/"),
    INSERT("/insert/"),
    QUERY_COUNT_ENTITY("/query/count/entity/"),
    QUERY_COUNT_ENTITY_TOTAL("/query/count/entity/total/"),
    QUERY_COUNT_EVENT("/query/count/event/"),
    QUERY_AGGREGATION("/query/aggregation/"),
    QUERY_TOP_VALUES("/query/top_values/"),
    QUERY_EXISTS_ENTITY("/query/exists/entity/"),
    QUERY_SAVED("/query/saved/"),
    QUERY_DATA_EXTRACTION_RESULT("/data_extraction/result/"),
    QUERY_DATA_EXTRACTION_SCORE("/data_extraction/score/"),
    DATABASE("/project/");

    public final String url;

    URLResources(String url) {
        this.url = url;
    }
}
