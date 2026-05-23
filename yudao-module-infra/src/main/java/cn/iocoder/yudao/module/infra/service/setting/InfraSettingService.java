package cn.iocoder.yudao.module.infra.service.setting;

import cn.iocoder.yudao.framework.common.biz.infra.setting.dto.SettingBaseDTO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.setting.InfraSettingDO;
import jakarta.validation.Valid;

public interface InfraSettingService {

    Long createSetting(@Valid SettingSaveReqVO createReqVO);

    void updateSetting(@Valid SettingSaveReqVO updateReqVO);

    void deleteSetting(Long id);

    InfraSettingDO getSetting(Long id);

    InfraSettingDO getSettingByKey(String key);

    PageResult<InfraSettingDO> getSettingPage(SettingPageReqVO reqVO);

    <T extends SettingBaseDTO> T getConfigValue(String key, Class<T> clazz);

    <T extends SettingBaseDTO> void saveConfigValue(String key, T value);

    void validateSettingValue(String key, String value);

}
