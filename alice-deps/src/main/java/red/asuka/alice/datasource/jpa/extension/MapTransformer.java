package red.asuka.alice.datasource.jpa.extension;

import org.hibernate.transform.ResultTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 10:08
 * @Desc: 查询结果转换器
 */
public class MapTransformer implements ResultTransformer {

    private Map<String, String> result = new HashMap<>();

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (tuple.length != 2) {
            return null;
        }
        result.put(String.valueOf(tuple[0]), String.valueOf(tuple[1]));
        return null;
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }

    public Map<String, String> getResult() {
        return result.isEmpty() ? null : result;
    }
}
