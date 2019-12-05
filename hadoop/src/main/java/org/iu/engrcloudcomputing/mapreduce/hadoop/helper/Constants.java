package org.iu.engrcloudcomputing.mapreduce.hadoop.helper;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class Constants {

    public static final String APPLICATION_NAME = "CloudComputing-MapReduce/1.0";
    public static final String PROJECT_ID = "gopikiran-talangalashama";
    public static final String ZONE_NAME = "us-east1-b";
    public static final long OPERATION_TIMEOUT_MILLIS = 120 * 1000;
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
}
