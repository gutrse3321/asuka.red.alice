package red.asuka.alice.commons.support;

import red.asuka.alice.commons.exception.EXPF;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2020-11-11 17:22
 */
public class BeanValidatorUtility {

    private static ValidatorImpl validator;

    public BeanValidatorUtility(ApplicationContext context) {
        Validator bean = context.getBean(Validator.class);
        LocalValidatorFactoryBean validatorFactoryBean = (LocalValidatorFactoryBean) bean;
        validator = (ValidatorImpl) validatorFactoryBean.getValidator();
    }

    public static Set<ConstraintViolation<Object>> validBean(Object object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    public static Exception validBeanBackException(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validBean(object, groups);
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            Map<String, String> error = new HashMap<>();
            constraintViolations.forEach(violation -> {
                String s = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                error.put(s, message);
            });
            return EXPF.E300(error, true);
        }
        return null;
    }

    public static Exception validBeanBackException(String prefix,Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validBean(object, groups);
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            Map<String, String> error = new HashMap<>();
            constraintViolations.forEach(violation -> {
                String s = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                error.put(prefix+"."+s, message);
            });
            return EXPF.E300(error, true);
        }
        return null;
    }
}
