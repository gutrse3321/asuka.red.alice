package red.asuka.alice.commons.support;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Tomonori
 * @Mail: gutrse3321@live.com
 * @Date: 2020-09-20 下午7:53
 *
 * JWT工具箱
 */
public class JwtUtility {

    /**
     * 反序列化为Map
     * @param token
     * @return
     * @throws Exception
     */
    public static Map getMap(String token) throws Exception {
        Jwt decode = JwtHelper.decode(token);
        return JsonUtility.toMap(decode.getClaims());
    }

    public static Map getBase64Map(String str) throws Exception {
        return JsonUtility.toMap(str);
    }

    /**
     * 获取企业id
     * @param token
     * @return
     * @throws Exception
     */
    public static String getEnterpriseId(String token) throws Exception {
        Map map = getBase64Map(token);
        String id = String.valueOf(map.get("enterprise_id"));
        return id;
    }

    /**
     * 获取平台租户id
     * @param token
     * @return
     * @throws Exception
     */
    public static String getTenantId(String token) throws Exception {
        Map map = getBase64Map(token);
        return String.valueOf(map.get("tenant_id"));
    }

    /**
     * 获取平台用户id
     * @param token
     * @return
     * @throws Exception
     */
    public static String getUcsUserId(String token) throws Exception {
        Map map = getBase64Map(token);
        return String.valueOf(map.get("user_id"));
    }

    public static String getUserId(String blade) throws Exception {
        Map map = getBase64Map(blade);
        return String.valueOf(map.get("id"));
    }

    public static String getRealName(String blade) throws Exception {
        Map map = getBase64Map(blade);
        return String.valueOf(map.get("real_name"));
    }

    public static String getPhoneNumber(String blade) throws Exception {
        Map map = getBase64Map(blade);
        return String.valueOf(map.get("phone_number"));
    }

    public static String getShopId(String blade) throws Exception {
        Map map = getBase64Map(blade);
        return String.valueOf(map.get("shop_id"));
    }

    public static String getWxTenantId(String miniAppToken) throws Exception {
        Map map = getBase64Map(miniAppToken);
        return String.valueOf(map.get("tenant_id"));
    }

    public static String getWxVisitorId(String miniAppToken) throws Exception {
        Map map = getBase64Map(miniAppToken);
        return String.valueOf(map.get("visitor_id"));
    }

    public static List<Long> getWxVillageId(String miniAppToken) throws Exception {
        Map map = getBase64Map(miniAppToken);
        List<Long> res = new ArrayList<>();
        if (map.get("village_id") != null) {
            String temp = String.valueOf(map.get("village_id"));
            res.add(Long.valueOf(temp));
            return res;
        }
        res.addAll(JsonUtility.toArrayList(String.valueOf(map.get("village_ids")), Long.class));
        return res;
    }

    public static String getWxRealName(String miniAppToken) throws Exception {
        Map map = getBase64Map(miniAppToken);
        return String.valueOf(map.get("tenant_name"));
    }

    public static String getWxVillageName(String miniAppToken) throws Exception {
        Map map = getBase64Map(miniAppToken);
        return String.valueOf(map.get("village_name"));
    }

    public static String getWxContractId(String miniAppToken) throws Exception {
        Map map = getBase64Map(miniAppToken);
        return String.valueOf(map.get("contract_id"));
    }

    public static String getWxRentUtilId(String miniAppToken) throws Exception {
        Map map = getBase64Map(miniAppToken);
        return String.valueOf(map.get("rent_util_id"));
    }
}
