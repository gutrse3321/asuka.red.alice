package red.asuka.alice.commons.hibernateExtension.validator;

import red.asuka.alice.commons.hibernateExtension.annotation.EnumValue;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author Tomonori
 * @Mail gutrse3321@live.com
 * @Date 2020-09-20 2:21 AM
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Integer> {

    int enumLength;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        enumLength = constraintAnnotation.clazz().getEnumConstants().length;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (context instanceof HibernateConstraintValidatorContext) {
            HibernateConstraintValidatorContext validatorContext = (HibernateConstraintValidatorContext) context;
            validatorContext.addExpressionVariable("min", 0);
            validatorContext.addExpressionVariable("max", enumLength - 1);
        }

        if (null == value) {
            return true;
        }

        return enumLength > value;
    }
}
