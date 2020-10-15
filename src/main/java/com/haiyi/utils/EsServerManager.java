package com.haiyi.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.omg.CORBA.AnyHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Xu
 * @create 2020-10-14 8:51
 * 以单例模式提供EsClient实例
 */
public class EsServerManager {

    private static EsServerManager instance;
    private static RestHighLevelClient client;

    private EsServerManager(){
        HttpHost host = new HttpHost("192.168.2.201", 9200, "http");
        client = new RestHighLevelClient(RestClient.builder(host));
    }

    public static synchronized EsServerManager getInstance(){
        if(instance == null){
            instance = new EsServerManager();
        }
        return instance;
    }

    public RestHighLevelClient getClient(){
        return client;
    }

}
