package cn.iocoder.yudao.framework.security.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 二次验证场景枚举。
 */
@Getter
@AllArgsConstructor
public enum SecondaryVerificationSceneEnum {

    SENSITIVE_OPERATION(1, "敏感操作"),
    FINANCIAL_OPERATION(2, "资金操作"),
    DATA_DELETE(3, "数据删除"),
    PERMISSION_CHANGE(4, "权限变更"),
    ACCOUNT_CHANGE(5, "账号变更");

    private final Integer scene;
    private final String name;

}
