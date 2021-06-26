package red.asuka.alice.commons.filter;

import red.asuka.alice.commons.support.JsonUtility;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-22 10:32
 *
 * 运营后台查询参数处理工具类
 */
@UtilityClass
public class QueryFactorUtility {

    public Map<String, QueryFactor> JsonToMap(String json)throws Exception{
        if(StringUtils.isBlank(json)) return new HashMap<>();
        List<QueryFactor> list = JsonUtility.toArrayList(json, QueryFactor.class);
        HashMap<String, QueryFactor> result = new HashMap<>();
        for(QueryFactor query:list){
            result.put(query.getParamName(),query);
        }
        return result;
    }

    public String MapToJson(Map<String, QueryFactor> queryMap) throws Exception {
        if (queryMap.size() == 0) return new String();
        List<QueryFactor> list = new ArrayList<>();
        queryMap.forEach((key, value) -> {
            list.add(value);
        });
        return JsonUtility.toString(list);
    }
}
