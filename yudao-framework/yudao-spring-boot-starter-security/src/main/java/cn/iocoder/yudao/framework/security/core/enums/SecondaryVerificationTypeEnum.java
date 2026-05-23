package cn.iocoder.yudao.framework.security.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 二次验证类型枚举。
 */
@Getter
@AllArgsConstructor
public enum SecondaryVerificationTypeEnum {

    PASSWORD(1, "密码验证"),
    SMS_CODE(2, "短信验证码");

    private final Integer type;
    private final String name;

}
