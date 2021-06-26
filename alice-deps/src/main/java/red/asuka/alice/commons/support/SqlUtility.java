package red.asuka.alice.commons.support;

import red.asuka.alice.commons.constant.Constant;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-22 10:34
 */
public class SqlUtility {

    // 空或者0-等于  1-不等于  2-大于  3-小于   4-大于等于  5-小于等于  6-包含
    public static String convertSymbol(Integer method){
        if(method == null || method < Constant.SqlCommonMethod.Equal.ordinal() || method > Constant.SqlCommonMethod.Instr.ordinal()) method = 0;
        return Constant.SqlCommonMethod.values()[method].getVal();
    }
}
