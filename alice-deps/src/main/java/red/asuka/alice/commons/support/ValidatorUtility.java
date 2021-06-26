package red.asuka.alice.commons.support;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Tomonori
 * @Mail: gutrse3321@live.com
 * @Date: 2020-09-20 下午7:46
 */
@UtilityClass
public class ValidatorUtility {

    protected static final Logger log = LoggerFactory.getLogger(ValidatorUtility.class);

    public final int START_YEAR = 1900;
    public final int END_YEAR   = 2100;

    private final int[] DAYS_OF_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String ZERO_STRING = "0";
    private final HashSet<String> AREA_CODE = new HashSet<>();
    {
        AREA_CODE.add("091");
        AREA_CODE.add("082");
        AREA_CODE.add("081");
        AREA_CODE.add("071");
        AREA_CODE.add("065");
        AREA_CODE.add("064");
        AREA_CODE.add("063");
        AREA_CODE.add("062");
        AREA_CODE.add("061");
        AREA_CODE.add("054");
        AREA_CODE.add("053");
        AREA_CODE.add("052");
        AREA_CODE.add("051");
        AREA_CODE.add("050");
        AREA_CODE.add("046");
        AREA_CODE.add("045");
        AREA_CODE.add("044");
        AREA_CODE.add("043");
        AREA_CODE.add("042");
        AREA_CODE.add("041");
        AREA_CODE.add("037");
        AREA_CODE.add("036");
        AREA_CODE.add("035");
        AREA_CODE.add("034");
        AREA_CODE.add("033");
        AREA_CODE.add("032");
        AREA_CODE.add("031");
        AREA_CODE.add("023");
        AREA_CODE.add("022");
        AREA_CODE.add("021");
        AREA_CODE.add("015");
        AREA_CODE.add("014");
        AREA_CODE.add("013");
        AREA_CODE.add("012");
        AREA_CODE.add("011");
    }

    /**
     * 检查是否中英文
     * @param str
     * @return
     */
    public boolean isEnglishOrChinese(String str) {
        /**
         * 1. [A-Za-z]* 英文字母的匹配 一次或者多次
         * 2. [\u4E00-\u9FA5]* Unicode汉字编码范围 汉字匹配 一次或多次
         */
        if (null == str) return false;
        Pattern p = Pattern.compile("^[A-Za-z]*|[\\u4E00-\\u9FA5]*$");
        Matcher matcher = p.matcher(str);
        return matcher.matches();
    }

    /**
     * 验证字符串的长度范围
     * min为空max不为空，验证是否小于max，min不为空max为空，验证是否大于min，如果忽略中文
     * 1个中文将按1长度计算，如果不忽略中文，1个中文将按2长度计算
     * @param str
     * @param minLength
     * @param maxLength
     * @param isConfine
     * @param ignoreChinese
     * @return
     */
    public boolean isLengthScope(String str, Integer minLength, Integer maxLength, boolean isConfine, boolean ignoreChinese) {
        if (null == str) {
            return false;
        }

        int strLength = str.length();

        /**
         * 包含汉字的话
         */
        if (!ignoreChinese) {
            strLength = getStringLength(str);
        }

        /**
         * 判断入参是否为空
         */
        if (null == str || "".equals(str) || (minLength == null && maxLength == null)) {
            return false;
        }

        if (minLength != null && maxLength == null) {
            /**
             * 验证是否大于min长度
             */
            //大于等于min
            if (isConfine && strLength >= minLength) {
                return true;
            } else if (!isConfine && strLength > minLength) {
                //大于min
                return true;
            }
        } else if (minLength == null && maxLength != null) {
            /**
             * 验证是否小于max
             */
            //小于等于max
            if (isConfine && strLength <= maxLength) {
                return true;
            } else if (!isConfine && strLength < maxLength) {
                //小于max
                return true;
            }
        } else {
            /**
             * 判断范围内
             */
            if (isConfine && (minLength <= strLength && strLength <= maxLength)) {
                return true;
            } else if (!isConfine && (minLength < strLength && strLength < maxLength)){
                return true;
            }
        }

        return false;
    }

    /**
     * 返回字符串长度，如果包含汉字，把汉字当做2 byte计算
     * @param str
     * @return
     */
    public int getStringLength(String str) {
        int stringLength = str.length();
        //获取中文个数
        int chineseCount = getChineseCharCount(str);
        int otherCount = str.length() - chineseCount;

        if (stringLength != otherCount) {
            stringLength = otherCount + chineseCount * 2;
        }
        return stringLength;
    }

    /**
     * 返回汉字个数
     * @param str
     * @return
     */
    private int getChineseCharCount(String str) {
        int count = 0;
        //Unicode汉字编码范围
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }

    /**
     * 检查APP版本格式 例：1.0.0
     * 标准版本格式
     * alpha - 内部版本
     * Beta  - 测试版本
     * RC    - 即将发布
     * Release - 发布版
     * 1   .   1    .    1    .   170707_base
     * 主版本  子版本   阶段版本    日期版本   字母版本
     * @param clientVersion
     * @return
     */
    public boolean checkClientVersion(String clientVersion) {
        if (!StringUtility.hasText(clientVersion)) return false;
        Pattern pattern = Pattern.compile("^[0-9][0-9]*.([0-9]|[1-9][0-9]*).([0-9]|[1-9][0-9]*)$");
        return pattern.matcher(clientVersion).find();
    }

    /**
     * 对比版本号
     * @param clientVersion app版本号
     * @param apiVersion    服务端指定版本号
     * @return 大于0代表服务端指定版本号较高
     */
    public int compare(String clientVersion, String apiVersion) {
        /**
         * 检查接口版本格式，格式错误返回需要更新信息
         */
        if (!ValidatorUtility.checkClientVersion(clientVersion)) {
            return  1;
        }

        String[] clientVersions = clientVersion.split("\\.");
        String[] apiVersions = apiVersion.split("\\.");

        for (int i = 0; i < apiVersions.length; i++) {
            Integer appv = Integer.valueOf(clientVersions[i]);
            Integer apiv = Integer.valueOf(apiVersions[i]);

            if (apiv == appv) {
                continue;
            } else if (apiv > appv) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }

    public boolean isJsonString(String json) {
        try {
            if (null == json) return false;
            json = json.replaceAll(" ", "");
            if (!StringUtility.hasLength(json)) return false;
            JsonUtility.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
