package red.asuka.alice.commons.springExtension.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import red.asuka.alice.commons.jacksonExtension.UnifyFilterProvider;
import red.asuka.alice.commons.model.PropertyFilterInfo;
import red.asuka.alice.commons.support.JsonUtility;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 18:32
 * @Desc: AbstractView: 实现的抽象基类View 。子类应该是JavaBean，以方便配置为Spring托管的bean实例。
 *                      提供对静态属性的支持，该属性可通过多种方式指定给视图，以供视图使用。对于每个渲
 *                      染操作，静态属性将与给定的动态属性（控制器返回的模型）合并。
 *
 *        UnifyView： 返回值视图包装器，对视图的返回值的处理，过滤返回值
 */
public class UnifyView extends AbstractView {

    public static final String CODE = "code";
    public static final String DATA = "data";
    public static final String THROWTYPE = "throwType";
    public static final String MESSAGE = "message";
    public static final String FIELD = "fields";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 使用ObjectMapper进行读写 readValue API 是简单的入门起点：
     * 可以解析或反序列化json内容为java对象。 反之，writeValue API可以序列化任何java对象为json字符串
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ContentType CONTENT_TYPE = ContentType.APPLICATION_JSON;

    public static final UnifyFilterProvider DEFAULT_FILTER_PROVIDER = new UnifyFilterProvider();
    //暂时不统一过滤
//    public static final String[] DEFAULT_EXCLUDE_PROPERTIES = {"createTime", "dataState", "class"};
    public static final String[] DEFAULT_EXCLUDE_PROPERTIES = {};

    protected Set<String> renderedAttributes;
    protected boolean disableCaching = true;
    protected boolean updateContentLength = false;
    //是否从单个key model中获取值
    protected boolean extractValueFromSingleKeyModel = false;

    //jackson过滤器
    protected UnifyFilterProvider filterProvider;

    /************************
     * constructor
     *
     */
    public UnifyView(PropertyFilterInfo...filters) {
        if (null == filters) {
            this.filterProvider = DEFAULT_FILTER_PROVIDER;
        } else {
            //创建jackson过滤器
            this.filterProvider = createProvider(filters);
        }
        setContentType(CONTENT_TYPE.getMimeType());
        setExposePathVariables(false);
    }

    public UnifyView(UnifyFilterProvider filterProvider) {
        if (null == filterProvider) {
            filterProvider = DEFAULT_FILTER_PROVIDER;
        }
        this.filterProvider = filterProvider;
        setContentType(CONTENT_TYPE.getMimeType());
        setExposePathVariables(false);
    }

    /******************
     * Public
     * @param renderedAttributes
     */
    public void setRenderedAttributes(Set<String> renderedAttributes) {
        this.renderedAttributes = renderedAttributes;
    }

    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public void setUpdateContentLength(boolean updateContentLength) {
        this.updateContentLength = updateContentLength;
    }

    /**
     * 创建Jackson统一属性过滤器提供者
     * @param filters
     * @return
     */
    private UnifyFilterProvider createProvider(PropertyFilterInfo[] filters) {
        List<String> include = new ArrayList<>();
        List<String> exclude = new ArrayList<>();
        //过滤信息类数组,当构造的类型类属性值为null，将设置为全局过滤
        List<PropertyFilterInfo> classFilters = new ArrayList<>();
        //构造默认过滤器
        UnifyFilterProvider provider = new UnifyFilterProvider();

        /**
         * 如果有需要过滤的信息
         */
        for (PropertyFilterInfo filterInfo : filters) {
            Class<?> clazz = filterInfo.get_clazz();
            boolean includeFlag = filterInfo.is_include();

            /**
             * 如果没有类型类，这根据include和exclude区分那些是除了此其他过滤和除了此过滤其他不过滤
             * 如果有，则添加到classFilters后面for循环再做处理
             */
            if (null != clazz) {
                classFilters.add(filterInfo);
            } else {
                if (includeFlag) {
                    include.addAll(CollectionUtils.arrayToList(filterInfo.get_properties()));
                } else {
                    exclude.addAll(CollectionUtils.arrayToList(filterInfo.get_properties()));
                }
            }
        }

        String[] includeNames = include.toArray(new String[include.size()]);
        String[] excludeNames = exclude.toArray(new String[exclude.size()]);

        /**
         * 处理传入类型类的filter信息类
         */
        for (PropertyFilterInfo filterInfo : classFilters) {
            boolean includeFlag = filterInfo.is_include();

            /**
             * 判断filter信息类是否是包含
             * 是： 此类型类的除了包含的properties所有属性，其余过滤
             */
            if (includeFlag) {
                provider.addFilter(filterInfo.getClass(),
                        /**
                         * SimpleBeanPropertyFilter: PropertyFilter的实现，仅使用属性名称来确定是按原样序列化属性还是将其过滤掉
                         */
                        /**
                         * 构造过滤器的工厂方法，该过滤器过滤掉除 数组中包含的属性外的所有属性
                         */
                        SimpleBeanPropertyFilter.filterOutAllExcept(
                                JsonUtility.addAll(filterInfo.get_properties(), includeNames)
                        )
                );
            } else {
                provider.addFilter(filterInfo.get_clazz(),
                        SimpleBeanPropertyFilter.serializeAllExcept(
                                JsonUtility.addAll(
                                        JsonUtility.addAll(filterInfo.get_properties(), excludeNames), DEFAULT_EXCLUDE_PROPERTIES
                                )
                        )
                );
            }
        }

        PropertyFilter defaultFilter;
        /**
         * 如果包含全局包含字段，
         * 直接设置默认provider为FilterOut
         */
        if (!CollectionUtils.isEmpty(include)) {
            /**
             * UnifyViewMap: jackson 过滤器使用，
             *               UnifyView类中，默认code, data等字段，使用此Map包含
             */
            provider.addFilter(UnifyViewMap.class, SimpleBeanPropertyFilter.filterOutAllExcept(
                    CODE, DATA, THROWTYPE, MESSAGE, FIELD
            ));
            defaultFilter = SimpleBeanPropertyFilter.filterOutAllExcept(include.toArray(new String[include.size()]));
        } else {
            /**
             * 构造过滤器的工厂方法，该过滤器过滤掉除 数组中包含的属性外的所有属性
             */
            String[] strings = JsonUtility.addAll(exclude.toArray(new String[exclude.size()]), DEFAULT_EXCLUDE_PROPERTIES);
            /**
             * 序列化所有，但是过滤掉指定的属性
             */
            defaultFilter = SimpleBeanPropertyFilter.serializeAllExcept(strings);
        }

        provider.setDefaultFilter(defaultFilter);
        return provider;
    }

    /**
     * 子类(就是现在继承AbstractView抽象类的这个UnifyView类)必须实现此方法才能实际呈现视图
     * @param map
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws Exception
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        //filterModel将返回传参map的属性判断是实现BindingResult接口的值
        Object value = filterModel(map);

        //ObjectMapper: writeValue API可以序列化任何java对象为json字符串
        String text = OBJECT_MAPPER.writer(filterProvider).writeValueAsString(value);
        //Charset: ContentType.APPLICATION_JSON
        byte[] bytes = text.getBytes(CONTENT_TYPE.getCharset());

        /**
         * OutputStream: 此抽象类是表示字节输出流的所有类的超类。输出流接受输出字节并将其发送到某个接收器
         * 需要定义的子类的应用程序 OutputStream必须始终提供至少一个写入一个字节输出的方法
         *
         * createTemporaryOutputStream(): 为此视图创建一个临时的OutputStream。
         * 这通常用作IE解决方法，用于在将内容实际写入HTTP响应之前设置临时流的内容长度标头。
         */
        OutputStream stream = this.updateContentLength ? createTemporaryOutputStream() : httpServletResponse.getOutputStream();
        //写入json字符串字节数组到输出流中
        stream.write(bytes);

        if (this.updateContentLength) {
            //将给定的OutputStream写入HTTP响应
            writeToResponse(httpServletResponse, (ByteArrayOutputStream) stream);
        }
    }

    /**
     * 返回传参model的属性实现BindingResult的值
     * 目前为UnifyFailureView类使用
     * @param model
     * @return
     */
    protected Object filterModel(Map<String, Object> model) {
        /**
         * UnifyViewMap继承HashMap
         * jackson 过滤器使用，
         * UnifyView类中，默认code, data等字段，使用此Map包含
         */
        Map<String, Object> result = new UnifyViewMap<>(model.size());
        Set<String> renderedAttributes = !CollectionUtils.isEmpty(this.renderedAttributes) ? this.renderedAttributes : model.keySet();

        model.forEach((key, value) -> {
            /**
             * BindingResult: 表示绑定结果的常规接口。扩展了 interface错误注册功能，允许Validator应用，并增加了特定于绑定的分析和模型构建。
             * BindingResult实现也可以直接使用，例如，对其调用Validator（例如，作为单元测试的一部分）
             */
            if (!(value instanceof BindingResult) && renderedAttributes.contains(key)) {
                result.put(key, value);
            }
        });

        //是否从单个key model中获取值
        if (extractValueFromSingleKeyModel) {
            if (result.size() == 1) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    return entry.getValue();
                }
            }
        }
        return result;
    }

    /**
     * 返回合并处理的返回值模型
     * @param model
     * @param request
     * @param response
     * @return
     */
    protected Map<String, Object> createMergedOutputModel(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        /**
         * isExposePathVariables: 是否向模型添加路径变量
         * true: 请求中获取包含具有路径变量的Map的属性
         */
        Map<String, Object> pathVars = isExposePathVariables() ? (Map<String, Object>) request.getAttribute(View.PATH_VARIABLES) : null;

        if (CollectionUtils.isEmpty(this.renderedAttributes)) {
            //返回此视图的静态属性Map<String, Object>转Set<String>,即所有静态属性名称
            this.renderedAttributes = getStaticAttributes().keySet();
        }

        //所有的视图的静态属性的数量
        int size = getStaticAttributes().size();
        //加上指定的model的属性数量和路径变量的属性数量
        size += (model != null ? model.size() : 0);
        size += (pathVars != null ? pathVars.size() : 0);

        //构造初始大小的链表哈希map，保存顺序
        Map<String, Object> mergedModel = new LinkedHashMap<>(size);
        //先将所有的静态熟悉加到map中
        mergedModel.putAll(getStaticAttributes());

        //判断是否有路径变量 或 其他属性model 或 RequestContext属性 加入到mao中
        if (pathVars != null) {
            mergedModel.putAll(pathVars);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }
        //返回RequestContext属性的名称（如果有）
        if (getRequestContextAttribute() != null) {
            mergedModel.put(getRequestContextAttribute(), createRequestContext(request, response, mergedModel));
        }
        return mergedModel;
    }


    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(request, response);
        response.setCharacterEncoding(CONTENT_TYPE.getCharset().name());

        if (this.disableCaching) {
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
            response.addDateHeader("Expires", 1L);
        }
    }
}
