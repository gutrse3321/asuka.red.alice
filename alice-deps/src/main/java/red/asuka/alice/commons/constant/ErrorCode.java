package red.asuka.alice.commons.constant;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-19 18:06
 */
public enum  ErrorCode {
    /**
     * 支付异常
     */
    PaymentError(508),
    /**
     * 超过资源上限
     */
    OverLimit(507),

    /********************华丽的分隔符***********************/

    /**
     * 获取临时上传凭证失败
     */
    GetUploadTokenFail(506),

    /**
     * 获取缓存锁异常
     */
    CacheLockError(504),
    /**
     * 服务端HTTP请求错误
     */
    HttpRequestError(503),
    /**
     * json反序列化失败
     */
    JsonDeserializer(502),
    /**
     * json序列化失败
     */
    JsonSerializer(501),
    /**
     * 订单已完成
     */
    PayOrderFinished(492),
    /**
     * 订单已关闭或超时
     */
    PayOrderTimeOut(491),
    /**
     * 订单已支付
     */
    PayOrderPaid(490),
    /**
     * 微信已被绑定
     */
    WechatAlreadyBound(458),
    /**
     * 支付宝已被绑定
     */
    AlipayAlreadyBound(457),

    /**
     * qq已被绑定
     */
    QqAlreadyBound(455),
    /**
     * 微信API异常
     */
    WechatApi(454),
    /**
     * 阿里云支付异常
     */
    AlipayApi(453),

    /**
     * qqAPI异常
     */
    QqApi(451),
    /**
     * 平台api异常
     */
    PlatformCenterApi(450),
    /**
     * 门禁api异常
     */
    EntranceApi(450),
    /**
     * 车辆api异常
     */
    CarCenterApi(451),
    /**
     * EZCloud api异常
     */
    EZCloudApi(452),
    /**
     * 人脸识别异常
     */
    VisionBoxApi(453),
    /**
     * 需要申请
     */
    NeedToApply(431),
    /**
     * 请求太频繁
     */
    RequestTooOften(429),
    /**
     * 该数据不存在
     */
    DataNotExists(409),
    /**
     * 不能操作该数据
     */
    CanNotOperate(408),
    /**
     * 数据已存在
     */
    DataDuplicated(406),
    /**
     * App版本过低
     */
    ClientVersion(405),
    /**
     * 资源未找到
     */
    ResourceNotFound(404),

    /**
     * 不能修改性别
     */
    UpdateGenderFailure(364),

    /**
     * 解析URL失败
     */
    ErrorResolveUrl(347),
    /**
     * 错误的体验码
     */
    ErrorExperienceCode(345),
    /**
     * 需要写入体验码
     */
    NeedExperienceCode(344),
    /**
     * 没有使用权限
     */
    NoPermissionToUse(342),
    /**
     * 未绑定手机号
     */
    NotBoundPhone(341),

    /**
     * 绑定冲突
     */
    BindingConflict(338),
    /**
     * 资产异常
     */
    AssetError(337),

    /**
     * 接口限流
     */
    RateLimiter(336),
    /**
     * 分布式锁处理中
     */
    InProgress(335),

    /**
     * 不是圈子成员
     */
    NotGroupMember(324),
    /**
     * 设备使用中
     */
    DeviceInUsed(323),
    /**
     * 设备异常
     */
    DeviceError(350),
    /**
     * token错误
     */
    TokenError(322),
    /**
     * 数据异常
     */
    DataException(321),

    /**
     * 内容解析失败
     */
    ContentResolveFail(319),


    /**
     * token过期
     */
    TokenExpire(317),

    /**
     * client没有权限
     */
    ClientPermissionDenied(316),
    /**
     * 用户处于离线状态
     */
    UserOffLine(315),
    /**
     * 用户不存在
     */
    UserNotExists(314),

    /**
     * 短信验证码尝试次数过多
     */
    SmsCodeNoMoreTry(313),
    /**
     * 短信验证码发送次数超过时段限制
     */
    SmsCodeOverLimit(312),
    /**
     * 短信验证码发送失败
     */
    SmsCodeSendFailed(311),
    /**
     * 短信验证码发送过于频繁
     */
    SmsCodeTooOften(310),

    /**
     * 短信验证码过期
     */
    SmsExpire(309),

    /**
     * 手机号冲突
     */
    PhoneNumberConflict(308),

    /**
     * 用户被封号
     */
    UserBanned(307),

    /**
     * 请求头Drivers格式错误
     */
    DriversHeader(306),
    /**
     * 没有权限
     */
    AUTH(305),
    /**
     * 短信验证码错误
     */
    SmsCodeCheck(304),
    /**
     * 需要注册
     */
    NeedRegister(303),
    /**
     * 未登录
     */
    NotLogin(302),
    /**
     * 请求冲突或数据库唯一冲突
     */
    Conflict(301),
    /**
     * 提交的参数有异常
     */
    Parameter(300),

    /**
     * ok
     */
    OK(200),
    /**
     * 服务器异常
     */
    Server(500);
    private static final String PREFIX = "{red.asuka.alice.exception.";
    private static final String SUFFIX = ".message}";
    private int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    /**
     * 去resources目录下的Resource Bundle取值
     * @return
     */
    public String getTemplate() {
        return PREFIX + code + SUFFIX;
    }
}
