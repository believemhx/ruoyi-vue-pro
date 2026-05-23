package cn.iocoder.yudao.framework.security.core.annotations;

import cn.iocoder.yudao.framework.security.core.enums.SecondaryVerificationSceneEnum;
import cn.iocoder.yudao.framework.security.core.enums.SecondaryVerificationTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * 二次验证注解。
 */
@Target({ElementType.METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecondaryVerification {

    /**
     * 验证类型。
     */
    SecondaryVerificationTypeEnum[] value() default {
            SecondaryVerificationTypeEnum.PASSWORD,
            SecondaryVerificationTypeEnum.SMS_CODE
    };

    /**
     * 验证场景。
     */
    SecondaryVerificationSceneEnum scene() default SecondaryVerificationSceneEnum.SENSITIVE_OPERATION;

    /**
     * 是否必须验证。
     */
    boolean required() default true;

    /**
     * 验证描述。
     */
    String description() default "敏感操作";

}
