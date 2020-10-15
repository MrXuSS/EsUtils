package com.haiyi.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.util.*;

/**
 * @author Mr.Xu
 * @create 2020-10-14 8:52
 */
public class EsClientImpl implements EsClient{

    private static Log log = LogFactory.getLog(EsClientImpl.class);

    /**
     * 向es中创建文档索引, 若es中已存在改index，则插入失败。
     *
     * @param indexName 索引名称
     * @param document  id
     * @param jsonStr   json字符串， 要存入的数据
     */
    public Boolean insertIndexWithJsonStr(String indexName, String document, String jsonStr) {
        Boolean indexExists = isIndexExists(indexName, document);
        Boolean result = false;
        if (!indexExists) {
            IndexRequest indexRequest = new IndexRequest(indexName)
                    .id(document)
                    .source(jsonStr, XContentType.JSON);
            IndexResponse indexResponse = null;
            try {
                indexResponse = EsServerManager.getInstance().getClient().index(indexRequest, RequestOptions.DEFAULT);
                if(indexResponse.getResult() == DocWriteResponse.Result.CREATED){
                    result = true;
                }
                log.info(indexResponse.getIndex() + "--" + indexResponse.getId() + "--" + "插入成功");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 查询指定id下索引数据
     *
     * @param indexName 索引名称
     * @param document  id
     * @return 返回得到的字符串, 没有返回null
     * {"age":18,"id":1,"name":"xuchenglei"}
     */
    public String queryIndex(String indexName, String document) {
        Boolean indexExists = isIndexExists(indexName, document);
        if (indexExists) {
            GetResponse getResponse = null;
            GetRequest getRequest = new GetRequest(indexName, document);
            try {
                getResponse = EsServerManager.getInstance().getClient().get(getRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return getResponse == null ? null : getResponse.getSourceAsString().toString();
        } else {
            return null;
        }
    }

    /**
     * 查询索引下的所有数据
     * @param indexName 索引名称
     * @return 数据的字符串， json格式； 没有返回null
     *  [{"name":"xuchenglei","id":1,"age":18},{"name":"xuchenglei","id":2,"age":18}]
     */
    public String queryIndex(String indexName, Integer startNum, Integer pageSize){
        Boolean indexExists = isIndexExists(indexName);
        if (indexExists) {
            SearchResponse searchResponse = null;
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(indexName);
            searchSourceBuilder.from(startNum);
            searchSourceBuilder.size(pageSize);
            searchRequest.source(searchSourceBuilder);
            try {
                searchResponse = EsServerManager.getInstance().getClient().search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(searchResponse != null){
                SearchHit[] searchHits = searchResponse.getHits().getHits();
                List<Map<String, Object>> arrayListMap = new ArrayList<Map<String, Object>>();
                for (SearchHit searchHit : searchHits) {
                    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                    arrayListMap.add(sourceAsMap);
                }
                return JSON.toJSONString(arrayListMap);
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * 根据索引名称和id删除数据
     * @param indexName 索引名称
     * @param document id
     * @return 是否删除成功； 索引不存在返货false
     */
    public Boolean deleteIndex(String indexName, String document){
        Boolean indexExists = isIndexExists(indexName, document);
        Boolean result = false;
        if(indexExists) {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, document);
            DeleteResponse deleteResponse = null;
            try {
                deleteResponse = EsServerManager.getInstance().getClient().delete(deleteRequest, RequestOptions.DEFAULT);
               if(deleteResponse.getResult() == DocWriteResponse.Result.DELETED){
                    result = true;
               }
                log.info(deleteResponse.getIndex()+"--"+deleteResponse.getId()+":已删除");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 删除indexName下的所有索引数据
     * @param indexName 索引名称
     * @return 删除是否成功。
     */
    public Boolean deleteIndex(String indexName){
        Boolean indexExists = isIndexExists(indexName);
        Boolean result = false;
        if(indexExists){
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
            AcknowledgedResponse acknowledgedResponse = null;
            try {
                acknowledgedResponse = EsServerManager.getInstance().getClient().indices().delete(deleteRequest, RequestOptions.DEFAULT);
                if(acknowledgedResponse.isAcknowledged()){
                    result = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  result;
    }

    /**
     * 判断索引是否存在
     * @param indexName 索引名称
     * @param document id
     * @return true or false
     */
    public Boolean isIndexExists(String indexName, String document){
        GetRequest getRequest = new GetRequest(indexName, document);
        // 禁用提取源
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        // 禁用提取存储字段
        getRequest.storedFields("_none_");

        Boolean exists = false;
        try {
            exists = EsServerManager.getInstance().getClient().exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 判断索引是否存在
     * @param indexName 索引名称
     * @return true or false
     */
    public Boolean isIndexExists(String indexName){
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        Boolean exists = false;
        try {
            exists = EsServerManager.getInstance().getClient().indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 查询出所有的index
     * @return 查询出所有的索引 Set<String>
     */
    public Set<String> queryAllIndex(){
        GetAliasesRequest getAliasesRequest = new GetAliasesRequest();
        Set<String> indexNameKeySet = new HashSet<String>();
        try {
            GetAliasesResponse getAliasesResponse = EsServerManager.getInstance().getClient().indices().getAlias(getAliasesRequest, RequestOptions.DEFAULT);
            Set<String> keySet = getAliasesResponse.getAliases().keySet();
            for (String s : keySet) {
                if(!s.startsWith(".")){
                    indexNameKeySet.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexNameKeySet;
    }

    /**
     * 根据 index 和 id 更新 索引数据
     * @param indexName 索引名称
     * @param document id
     * @return 是否更新成功
     */
    public Boolean updateIndex(String indexName, String document, String jsonStr){
        Boolean indexExists = isIndexExists(indexName, document);
        Boolean result = false;
        if(indexExists){
            UpdateRequest updateRequest = new UpdateRequest(indexName, document).doc(jsonStr,XContentType.JSON);
            UpdateResponse updateResponse = null;
            try {
                updateResponse = EsServerManager.getInstance().getClient().update(updateRequest, RequestOptions.DEFAULT);
                if(updateResponse.getResult() == DocWriteResponse.Result.UPDATED){
                    result = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 全量匹配查询
     * @param field 属性
     * @param text 值
     * @param indexName 索引名称
     * @param startNum 开始的位置
     * @param pageSize 分页的大小
     * @return 返回符合条件的值
     */
    public String search(String field, String text, String indexName, Integer startNum, Integer pageSize){
        List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.query(QueryBuilders.termQuery(field, text));

        searchSourceBuilder.from(startNum);
        searchSourceBuilder.size(pageSize);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = EsServerManager.getInstance().getClient().search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                resultMapList.add(sourceAsMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(resultMapList);
    }

    /**
     * 模糊查询, 并实现高亮
     * @param field 属性名
     * @param text value
     * @param indexName 索引名称
     * @return json
     */
    public String searchFuzzy(String field, String text, String indexName, Integer startNum, Integer pageSize) {
        List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.wildcardQuery(field, "*"+text+"*"));
        searchSourceBuilder.from(startNum);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field(field);
        highlightTitle.highlighterType("unified");
        highlightBuilder.field(highlightTitle);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = EsServerManager.getInstance().getClient().search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlightField = highlightFields.get(field);
                Text[] fragments = highlightField.fragments();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put(field, fragments[0].string());
                resultMapList.add(sourceAsMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(resultMapList);
    }

    /**
     * 获取当前索引下的数据量
     * @param indexName 索引名称
     * @return 数据量条数
     */
    public Long searchTotalHitsNum(String indexName) {
        Boolean indexExists = isIndexExists(indexName);
        Long result = -1L;
        if(indexExists){
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchResponse searchResponse = null;
            try {
                searchResponse = EsServerManager.getInstance().getClient().search(searchRequest, RequestOptions.DEFAULT);
                result = searchResponse.getHits().getTotalHits().value;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}