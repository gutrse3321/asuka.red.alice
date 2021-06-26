package red.asuka.alice.commons.hibernateExtension.validator;

import red.asuka.alice.commons.hibernateExtension.annotation.StringJson;
import red.asuka.alice.commons.support.StringUtility;
import red.asuka.alice.commons.support.ValidatorUtility;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @date 2020-09-22 10:44
 */
public class StringJsonValidator implements ConstraintValidator<StringJson, String> {

    @Override
    public void initialize(StringJson constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtility.hasLength(s)) return true;
        return ValidatorUtility.isJsonString(s);
    }
}
