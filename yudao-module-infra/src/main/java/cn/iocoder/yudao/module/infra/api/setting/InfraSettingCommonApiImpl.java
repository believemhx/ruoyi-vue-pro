package cn.iocoder.yudao.module.infra.api.setting;

import cn.iocoder.yudao.framework.common.biz.infra.setting.InfraSettingCommonApi;
import cn.iocoder.yudao.framework.common.biz.infra.setting.dto.SettingBaseDTO;
import cn.iocoder.yudao.module.infra.service.setting.InfraSettingService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class InfraSettingCommonApiImpl implements InfraSettingCommonApi {

    @Resource
    private InfraSettingService infraSettingService;

    @Override
    public <T extends SettingBaseDTO> T getSystemConfigValue(String key, Class<T> clazz) {
        return infraSettingService.getConfigValue(key, clazz);
    }

    @Override
    public <T extends SettingBaseDTO> void saveSystemConfig(String key, T value) {
        infraSettingService.saveConfigValue(key, value);
    }

}
