package cn.iocoder.yudao.framework.common.biz.infra.setting;

import cn.iocoder.yudao.framework.common.biz.infra.setting.dto.SettingBaseDTO;

/**
 * 通用配置 Common API。
 */
public interface InfraSettingCommonApi {

    /**
     * 获取系统级配置值。
     *
     * @param key 配置 key
     * @param clazz 配置类型
     * @return 配置值
     * @param <T> DTO 类型
     */
    <T extends SettingBaseDTO> T getSystemConfigValue(String key, Class<T> clazz);

    /**
     * 保存系统级配置值。
     *
     * @param key 配置 key
     * @param value 配置值
     * @param <T> DTO 类型
     */
    <T extends SettingBaseDTO> void saveSystemConfig(String key, T value);

}
