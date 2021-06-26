package red.asuka.alice.commons.springExtension.view;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @Author: Tomonori
 * @Date: 2019/11/21 18:32
 * @Desc: 失败返回包装
 */
public class UnifyFailureView extends UnifyView {

    //默认需要输出的字段 {code: "", throwType: "", message: "", fields: ""}
    protected static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");

    @Getter
    @Setter
    private String[] jsonp;

    public UnifyFailureView() {
        this(null);
    }

    public UnifyFailureView(String[] jsonp) {
        //构造UnifyView，使用默认jackson过滤器
        super();

        this.jsonp = jsonp;
        renderedAttributes = new HashSet<>();
        renderedAttributes.add(CODE);
        renderedAttributes.add(THROWTYPE);
        renderedAttributes.add(MESSAGE);
        renderedAttributes.add(FIELD);
    }

    /**
     * 子类(就是现在继承AbstractView抽象类的这个UnifyView类)必须实现此方法才能实际呈现视图
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //filterModel: 继承的UnifyView类的方法，将返回传参map的属性判断是实现BindingResult接口的值
        Object value = filterModel(model);

        //ObjectMapper: writeValue API可以序列化任何java对象为json字符串
        String text = OBJECT_MAPPER.writer(filterProvider).writeValueAsString(value);

        if (null != jsonp && jsonp.length > 0) {
            text = buildJsonpString(text, request, response);
        }

        //ContentType.APPLICATION_JSON
        byte[] bytes = text.getBytes(CONTENT_TYPE.getCharset());
        /**
         * OutputStream: 此抽象类是表示字节输出流的所有类的超类。输出流接受输出字节并将其发送到某个接收器
         * 需要定义的子类的应用程序 OutputStream必须始终提供至少一个写入一个字节输出的方法
         *
         * createTemporaryOutputStream(): 为此视图创建一个临时的OutputStream。
         * 这通常用作IE解决方法，用于在将内容实际写入HTTP响应之前设置临时流的内容长度标头。
         */
        OutputStream stream = this.updateContentLength ? createTemporaryOutputStream() : response.getOutputStream();
        //写入json字符串字节数组到输出流中
        stream.write(bytes);

        if (this.updateContentLength) {
            //将给定的OutputStream写入HTTP响应
            writeToResponse(response, (ByteArrayOutputStream) stream);
        }
    }

    /**
     * 生成jsonp字符串
     * @param json
     * @param request
     * @param response
     * @return
     */
    protected String buildJsonpString(String json, HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();

        for (String name : this.jsonp) {
            String value = request.getParameter(name);

            if (value != null) {
                if (!isValidJsonpQueryParam(value)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring invalid jsonp parameter value: " + value);
                    }
                    continue;
                }
                setContentType(getJsonpContentType().toString());
                setResponseContentType(request, response);
                builder.append("/**/").append(value).append("(").append(json).append(");");
                break;
            }
        }
        return builder.length() > 0 ? builder.toString() : json;
    }

    private boolean isValidJsonpQueryParam(String value) {
        //正则表达式校验
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }

    /**
     * ContentType: application/javascript
     * @return
     */
    private MediaType getJsonpContentType() {
        return new MediaType("application", "javascript");
    }

    public void addRenderedAttribute(String str) {
        renderedAttributes.add(str);
    }

    public void setRenderedAttribute(Set<String> attributes) {
        renderedAttributes = attributes;
    }
}
