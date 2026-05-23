package cn.iocoder.yudao.framework.common.biz.infra.setting;

import cn.iocoder.yudao.framework.common.biz.infra.setting.dto.SettingBaseDTO;

/**
 * 配置定义策略接口。
 *
 * @param <T> 配置 DTO 类型
 */
public interface ConfigStrategy<T extends SettingBaseDTO> {

    /**
     * 配置唯一标识。
     */
    String getConfigKey();

    /**
     * 配置值类型。
     */
    Class<T> getType();

    /**
     * 对应权限标识。
     */
    default String getPermissionKey() {
        return "infra:setting:" + getConfigKey();
    }

}
