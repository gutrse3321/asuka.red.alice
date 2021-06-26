package red.asuka.alice.datasource.jpa.extension;

import org.hibernate.transform.ResultTransformer;

import java.util.List;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 10:22
 * @Desc: 查询结果转换器
 */
public enum Transformer implements ResultTransformer {

    ID {
        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            return Long.valueOf(tuple[0].toString());
        }

        @SuppressWarnings("rawtypes")
        @Override
        public List transformList(List collection) {
            return collection;
        }
    }
}
