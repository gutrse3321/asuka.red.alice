package red.asuka.alice.commons.filter;

import red.asuka.alice.commons.support.SqlUtility;
import lombok.Data;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-22 10:32
 */
@Data
public class QueryFactor {

    private String paramName;       //参数名
    private Object paramValue;      //参数值
    private Integer operator;       //操作枚举值：0-等于  1-不等于  2-大于  3-小于   4-大于等于  5-小于等于  6-包含
    private String operatorStr;

    public String getOperatorStr(){
        return SqlUtility.convertSymbol(operator);
    }

    public QueryFactor() {
    }

    public QueryFactor(String paramName, Object paramValue, Integer operator) {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.operator = operator;
        this.operatorStr = SqlUtility.convertSymbol(operator);
    }

}
