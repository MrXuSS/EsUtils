package com.haiyi.utils;

import java.util.Set;

/**
 * @author Mr.Xu
 * @create 2020-10-15 9:24
 */
public interface EsClient {
    /**
     * 向es中创建文档索引, 若es中已存在改index，则插入失败。
     *
     * @param indexName 索引名称
     * @param document  id
     * @param jsonStr   json字符串， 要存入的数据
     */
    public Boolean insertIndexWithJsonStr(String indexName, String document, String jsonStr);

    /**
     * 查询指定id下索引数据
     *
     * @param indexName 索引名称
     * @param document  id
     * @return 返回得到的字符串, 没有返回null
     * {"age":18,"id":1,"name":"xuchenglei"}
     */
    public String queryIndex(String indexName, String document);

    /**
     * 查询索引下的所有数据
     * @param indexName 索引名称
     * @param startNum 开始的位置
     * @param pageSize 分页的大小
     * @return 数据的字符串， json格式； 没有返回null
     *  [{"name":"xuchenglei","id":1,"age":18},{"name":"xuchenglei","id":2,"age":18}]
     */
    public String queryIndex(String indexName, Integer startNum, Integer pageSize);

    /**
     * 根据索引名称和id删除数据
     * @param indexName 索引名称
     * @param document id
     * @return 是否删除成功； 索引不存在返货false
     */
    public Boolean deleteIndex(String indexName, String document);

    /**
     * 删除indexName下的所有索引数据
     * @param indexName 索引名称
     * @return 删除是否成功。
     */
    public Boolean deleteIndex(String indexName);

    /**
     * 判断索引是否存在
     * @param indexName 索引名称
     * @param document id
     * @return true or false
     */
    public Boolean isIndexExists(String indexName, String document);

    /**
     * 判断索引是否存在
     * @param indexName 索引名称
     * @return true or false
     */
    public Boolean isIndexExists(String indexName);

    /**
     * 查询出所有的index
     * @return 查询出所有的索引 Set<String>
     */
    public Set<String> queryAllIndex();

    /**
     * 根据 index 和 id 更新 索引数据
     * @param indexName 索引名称
     * @param document id
     * @param jsonStr 要更新的json
     * @return 是否更新成功
     */
    public Boolean updateIndex(String indexName, String document, String jsonStr);

    /**
     * index下的搜索
     * @param field 属性
     * @param text 值
     * @param indexName 索引名称
     * @param startNum 开始的位置
     * @param pageSize 分页的大小
     * @return 返回符合条件的值
     */
    public String search(String field, String text, String indexName, Integer startNum, Integer pageSize);

    /**
     * 模糊查询, 并实现高亮
     * @param field 属性名
     * @param text value
     * @param indexName 索引名
     * @param startNum 开始的位置
     * @param pageSize 分页大小
     * @return json
     */
    public String searchFuzzy(String field, String text, String indexName, Integer startNum, Integer pageSize);

    /**
     * 获取当前索引下的数据量
     * @param indexName 索引名称
     * @return 数据量条数
     */
    public Long searchTotalHitsNum(String indexName);

    /**
     * 向es中创建文档索引, 若es中已存在改index，则插入失败。（异步）
     *
     * @param indexName 索引名称
     * @param document  id
     */
    public void insertIndexWithJsonStrAsync(String indexName, String document, String jsonStr);

    /**
     * 根据索引名称和id删除数据，（异步）
     * @param indexName 索引名称
     * @param document id
     */
    public void deleteIndexAsync(String indexName, String document);

    /**
     * 删除indexName下的所有索引数据，（异步）
     * @param indexName 索引名称
     */
    public void deleteIndexAsync(String indexName);

    /**
     * 根据 index 和 id 更新 索引数据 （异步）
     * @param indexName 索引名称
     * @param document id
     * @param jsonStr 要更新的json
     */
    public void updateIndexAsync(String indexName, String document, String jsonStr);

    /**
     * 索引重建(同步)。 注意：目标索引需要提前创建好
     * @param fromIndex 重新索引的索引名
     * @param destIndex 重新索引后的索引名
     * @return 新创建的文档数
     */
    public Long reIndex(String fromIndex, String destIndex);

    /**
     * 索引重建（异步）。注意：目标索引需要提前创建好
     * @param fromIndex 重新索引的索引名
     * @param destIndex 重新索引后的索引名
     */
    public void reIndexAsync(String fromIndex, String destIndex);
}
