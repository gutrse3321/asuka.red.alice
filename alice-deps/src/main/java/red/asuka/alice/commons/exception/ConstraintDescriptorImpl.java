package red.asuka.alice.commons.exception;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ValidateUnwrappedValue;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Tomonori
 * @Date: 2019/10/25 14:31
 * @Desc: Hibernate ConstraintDescriptor
 *
 * 在转换器中使用Hibernate的模板消息转换器
 */
public class ConstraintDescriptorImpl<T extends Annotation> implements ConstraintDescriptor<T>, Serializable {

    private Map<String, Object> attributes;

    public ConstraintDescriptorImpl() {}

    public ConstraintDescriptorImpl(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public T getAnnotation() {
        return null;
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    @Override
    public Set<Class<?>> getGroups() {
        return null;
    }

    @Override
    public Set<Class<? extends Payload>> getPayload() {
        return null;
    }

    @Override
    public ConstraintTarget getValidationAppliesTo() {
        return null;
    }

    @Override
    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return CollectionUtils.isEmpty(attributes) ? new HashMap<>(0) : attributes;
    }

    @Override
    public Set<ConstraintDescriptor<?>> getComposingConstraints() {
        return null;
    }

    @Override
    public boolean isReportAsSingleViolation() {
        return false;
    }

    @Override
    public ValidateUnwrappedValue getValueUnwrapping() {
        return null;
    }

    @Override
    public <U> U unwrap(Class<U> aClass) {
        return null;
    }
}
