package utilTest;

import com.alibaba.fastjson.JSON;
import com.haiyi.utils.EsClientImpl;
import com.haiyi.utils.EsServerManager;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mr.Xu
 * @create 2020-10-14 10:47
 */
public class EsClientTest {

    public void testEsClient(){
        EsServerManager.getInstance();
    }

    @Test
    public void testInsertIndexWithJson(){
        EsClientImpl esClient = new EsClientImpl();
        Student student = new Student(3, "xiaoming", 18);
        String jsonString = JSON.toJSONString(student);
        System.out.println(jsonString);
        Boolean isInsert = esClient.insertIndexWithJsonStr("esutilstest", student.id.toString(), jsonString);
        System.out.println(isInsert);
    }

    @Test
    public void testInsertIndexWithJsonAsnyc(){
        EsClientImpl esClient = new EsClientImpl();
        Student student = new Student(12, "xiaoming", 18);
        String jsonString = JSON.toJSONString(student);
        esClient.insertIndexWithJsonStrAsync("esutilstest", student.id.toString(), jsonString);
    }

    @Test
    public void testQueryIndex(){
        EsClientImpl esClient = new EsClientImpl();
        String queryIndexStr = esClient.queryIndex("esutilstest", "1");
        System.out.println(queryIndexStr);
    }

    @Test
    public void testQueryAllData(){
        EsClientImpl esClient = new EsClientImpl();
        String queryIndexStr = esClient.queryIndex("esutilstest", 0 , 5);
        System.out.println(queryIndexStr);
    }

    @Test
    public void testDeleteIndex(){
        EsClientImpl esClient = new EsClientImpl();
        Boolean deleteIndexBool = esClient.deleteIndex("esutilstest_20201019");
        System.out.println(deleteIndexBool);
    }

    @Test
    public void testDeleteIndexAsync(){
        EsClientImpl esClient = new EsClientImpl();
        esClient.deleteIndexAsync("esutilstest", "7");
    }

    @Test
    public void testDeleteIndexAllData(){
        EsClientImpl esClient = new EsClientImpl();
        Boolean deleteIndexBool = esClient.deleteIndex("esutilstest2");
        System.out.println(deleteIndexBool);
    }

    @Test
    public void testDeleteIndexAllDataAsnyc(){
        EsClientImpl esClient = new EsClientImpl();
        esClient.deleteIndexAsync("esutilstest");
    }

    @Test
    public void testQueryAllIndex(){
        EsClientImpl esClient = new EsClientImpl();
        Set<String> allIndex = esClient.queryAllIndex();
        System.out.println(allIndex);
    }

    @Test
    public void testUpdateIndex(){
        Student xiaoming = new Student(1, "xiaoming", 18);
        EsClientImpl esClient = new EsClientImpl();
        Boolean esutilstestBool = esClient.updateIndex("esutilstest", "1", JSON.toJSONString(xiaoming));
        System.out.println(esutilstestBool);
    }

    @Test
    public void testUpdateIndexAsync(){
        Student xiaoming = new Student(1, "xiaohong", 18);
        EsClientImpl esClient = new EsClientImpl();
        esClient.updateIndexAsync("esutilstest", "1", JSON.toJSONString(xiaoming));
    }

    @Test
    public void testSearch(){
        EsClientImpl esClient = new EsClientImpl();
        String searchList = esClient.search("name", "xiaoming", "esutilstest", 1, 5);
        System.out.println(searchList);
    }

    @Test
    public void testSearchFuzzy(){
        EsClientImpl esClient = new EsClientImpl();
        String searchFuzzy = esClient.searchFuzzy("name", "xiao", "esutilstest", 11, 5);
        System.out.println(searchFuzzy);
    }

    @Test
    public void testTotalHitsCount(){
        EsClientImpl esClient = new EsClientImpl();
        Long totalHitsNum = esClient.searchTotalHitsNum("esutilstest");
        System.out.println(totalHitsNum);
    }

    @Test
    public void testReIndex(){
        EsClientImpl esClient = new EsClientImpl();
        Long reIndexNum = esClient.reIndex("esutilstest", "esutilstest_20201019");
        System.out.println(reIndexNum);
    }

    @Test
    public void testReindexAsync(){
        EsClientImpl esClient = new EsClientImpl();
        esClient.reIndexAsync("esutilstest", "esutilstest_20201019");
    }
}

class Student{
    Integer id;
    String name;
    Integer age;

    public Student(Integer id, String name, Integer age){
        this.id = id;
        this.age = age;
        this.name = name;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
