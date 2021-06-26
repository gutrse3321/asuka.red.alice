package red.asuka.alice.commons.controller;

import red.asuka.alice.commons.constant.DefaultConstant;
import red.asuka.alice.commons.constant.ErrorCode;
import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.commons.support.JwtUtility;
import red.asuka.alice.commons.support.encrypt.EncryptorUtility;
import red.asuka.alice.commons.version.DriversHeader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @Author Tomonori
 * @Mail gutrse3321@live.com
 * @Date 2020-09-20 2:10 AM
 *
 * 控制器基类
 * eg: 获取传入的token的信息或头部信息
 */
public class BaseController extends HttpServletController {

    @Autowired
    EncryptorUtility encryptorUtility;

    protected String getMiniAppToken() throws UnsupportedEncodingException {
        String miniToken = request.getHeader(DefaultConstant.WECHAT_TOKEN);
        miniToken = encryptorUtility.base64Decrypt(StringUtils.substringAfter(miniToken, "alice "));
        return miniToken;
    }

    protected String getWxTenantId() throws Exception {
        return JwtUtility.getWxTenantId(getMiniAppToken());
    }

    protected String getWxVisitorId() throws Exception {
        return JwtUtility.getWxVisitorId(getMiniAppToken());
    }

    protected List<Long> getWxVillageId() throws Exception {
        return JwtUtility.getWxVillageId(getMiniAppToken());
    }

    protected String getWxRealName() throws Exception {
        return JwtUtility.getWxRealName(getMiniAppToken());
    }

    protected String getWxVillageName() throws Exception {
        return JwtUtility.getWxVillageName(getMiniAppToken());
    }

    protected String getWxContractId() throws Exception {
        return JwtUtility.getWxContractId(getMiniAppToken());
    }

    protected String getWxRentUtilId() throws Exception {
        return JwtUtility.getWxRentUtilId(getMiniAppToken());
    }

    /**
     * 获取请求的面纱信息
     */
    protected String getBlade() throws UnsupportedEncodingException {
        String blade = request.getHeader(DefaultConstant.ALICE_VORPAL_BLADE);
        blade = encryptorUtility.base64Decrypt(blade);
        return blade;
    }

    /**
     * 获取面纱的用户id
     */
    protected String getUserId() throws Exception {
        return JwtUtility.getUserId(getBlade());
    }

    /**
     * 获取面纱的真实名称
     */
    protected String getAccount() throws Exception {
        return JwtUtility.getRealName(getBlade());
    }

    /**
     * 获取面纱的电话
     */
    protected String getPhoneNumber() throws Exception {
        return JwtUtility.getPhoneNumber(getBlade());
    }

    /**
     * 获取面纱所属的门店的id
     */
    protected String getShopId() throws Exception {
        return JwtUtility.getShopId(getBlade());
    }

    /**
     * 获取http请求头部 ""
     * @return
     * @throws Exception
     */
    protected DriversHeader getDriversHeader() throws Exception {
//        Object attribute = request.getHeader(HeaderCheckUtility.HEADER_REIMU_DRIVERS);
//        if (null != attribute) return JsonUtility.toObject(attribute.toString(), DriversHeader.class);
        return null;
    }

    /**
     * 获取请求的token值
     * @return
     */
    protected String getToken() throws Exception {
        String authorization = request.getHeader(DefaultConstant.SAILMI_AUTH_HEADER);
        if (StringUtils.isEmpty(authorization)) {
            throw EXPF.exception(ErrorCode.AUTH.getCode(), "平台账号认证异常", true);
        }
        return StringUtils.substringAfter(authorization, DefaultConstant.OAUTH2_TOKEN_SPLIT);
    }

    /**
     * 获取请求的authorization
     * @return
     */
    protected String getAuthorization() {
        return request.getHeader(DefaultConstant.OAUTH2_REQ_HEADER);
    }

    protected String getUcsTokenJson() throws Exception {
        String[] tokenArr = StringUtils.split(getToken(), ".");
        String json = encryptorUtility.base64Decrypt(tokenArr[1]);
        return json;
    }

    /**
     * 获取企业id
     * @return
     * @throws Exception
     */
    protected String getEnterpriseId() throws Exception {
        return JwtUtility.getEnterpriseId(getUcsTokenJson());
    }

    protected String getTenantId() throws Exception {
        return JwtUtility.getTenantId(getUcsTokenJson());
    }

    protected String getUcsUserId() throws Exception {
        return JwtUtility.getUcsUserId(getUcsTokenJson());
    }
}