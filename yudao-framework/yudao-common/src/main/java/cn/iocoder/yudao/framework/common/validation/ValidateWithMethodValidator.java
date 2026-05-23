package cn.iocoder.yudao.framework.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

public class ValidateWithMethodValidator implements ConstraintValidator<ValidateWithMethod, Object> {

    private String methodName;

    @Override
    public void initialize(ValidateWithMethod constraintAnnotation) {
        this.methodName = constraintAnnotation.methodName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Method method = value.getClass().getMethod(methodName);
            if (method.getReturnType() != boolean.class) {
                return false;
            }
            return (boolean) method.invoke(value);
        } catch (Exception e) {
            return false;
        }
    }

}
