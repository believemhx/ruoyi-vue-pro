package cn.iocoder.yudao.module.infra.service.setting;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.biz.infra.setting.ConfigStrategy;
import cn.iocoder.yudao.framework.common.biz.infra.setting.dto.SettingBaseDTO;
import cn.iocoder.yudao.framework.common.biz.infra.setting.event.InfraSettingChangedEvent;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.validation.ValidationUtils;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingSaveReqVO;
import cn.iocoder.yudao.module.infra.convert.setting.SettingConvert;
import cn.iocoder.yudao.module.infra.dal.dataobject.setting.InfraSettingDO;
import cn.iocoder.yudao.module.infra.dal.mysql.setting.InfraSettingMapper;
import cn.iocoder.yudao.module.infra.enums.config.ConfigTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.SETTING_CONFIG_VALUE_INVALID;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.SETTING_KEY_DUPLICATE;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.SETTING_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class InfraSettingServiceImpl implements InfraSettingService {

    @Resource
    private InfraSettingMapper infraSettingMapper;
    @Autowired(required = false)
    private List<ConfigStrategy<?>> configStrategies;
    @Resource
    private ApplicationEventPublisher eventPublisher;

    private final Map<String, ConfigStrategy<?>> strategyMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (configStrategies == null) {
            return;
        }
        configStrategies.forEach(strategy -> strategyMap.put(strategy.getConfigKey(), strategy));
    }

    @Override
    public Long createSetting(SettingSaveReqVO createReqVO) {
        validateConfigKeyUnique(null, createReqVO.getConfigKey());
        validateSettingValue(createReqVO.getConfigKey(), createReqVO.getConfigValue());
        InfraSettingDO setting = SettingConvert.INSTANCE.convert(createReqVO);
        setting.setType(ConfigTypeEnum.CUSTOM.getType());
        if (setting.getStatus() == null) {
            setting.setStatus(Boolean.TRUE);
        }
        if (setting.getVisible() == null) {
            setting.setVisible(Boolean.TRUE);
        }
        infraSettingMapper.insert(setting);
        publishSettingChangedEvent(setting.getConfigKey());
        return setting.getId();
    }

    @Override
    public void updateSetting(SettingSaveReqVO updateReqVO) {
        validateSettingExists(updateReqVO.getId());
        validateConfigKeyUnique(updateReqVO.getId(), updateReqVO.getConfigKey());
        validateSettingValue(updateReqVO.getConfigKey(), updateReqVO.getConfigValue());
        InfraSettingDO updateObj = SettingConvert.INSTANCE.convert(updateReqVO);
        infraSettingMapper.updateById(updateObj);
        publishSettingChangedEvent(updateObj.getConfigKey());
    }

    @Override
    public void deleteSetting(Long id) {
        InfraSettingDO setting = validateSettingExists(id);
        infraSettingMapper.deleteById(id);
        publishSettingChangedEvent(setting.getConfigKey());
    }

    @Override
    public InfraSettingDO getSetting(Long id) {
        return infraSettingMapper.selectById(id);
    }

    @Override
    public InfraSettingDO getSettingByKey(String key) {
        return infraSettingMapper.selectByKey(key);
    }

    @Override
    public PageResult<InfraSettingDO> getSettingPage(SettingPageReqVO reqVO) {
        return infraSettingMapper.selectPage(reqVO);
    }

    @Override
    public <T extends SettingBaseDTO> T getConfigValue(String key, Class<T> clazz) {
        InfraSettingDO setting = getSelf().getSettingByKey(key);
        if (setting == null || Boolean.FALSE.equals(setting.getStatus())) {
            return getDefaultConfigValue(key, clazz);
        }
        return JsonUtils.parseObject(setting.getConfigValue(), clazz);
    }

    @Override
    public <T extends SettingBaseDTO> void saveConfigValue(String key, T value) {
        String configValue = JsonUtils.toJsonString(value);
        validateSettingValue(key, configValue);
        InfraSettingDO oldSetting = infraSettingMapper.selectByKey(key);
        if (oldSetting == null) {
            InfraSettingDO setting = new InfraSettingDO();
            setting.setConfigKey(key);
            setting.setConfigValue(configValue);
            setting.setType(ConfigTypeEnum.CUSTOM.getType());
            setting.setStatus(Boolean.TRUE);
            setting.setVisible(Boolean.TRUE);
            infraSettingMapper.insert(setting);
        } else {
            InfraSettingDO updateObj = new InfraSettingDO();
            updateObj.setId(oldSetting.getId());
            updateObj.setConfigKey(key);
            updateObj.setConfigValue(configValue);
            infraSettingMapper.updateById(updateObj);
        }
        publishSettingChangedEvent(key);
    }

    @Override
    public void validateSettingValue(String key, String value) {
        ConfigStrategy<?> strategy = strategyMap.get(key);
        if (strategy == null) {
            return;
        }
        try {
            Object configObj = JsonUtils.parseObject(value, strategy.getType());
            if (configObj == null) {
                throw exception(SETTING_CONFIG_VALUE_INVALID, "配置值解析为空");
            }
            ValidationUtils.validate(configObj);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw e;
            }
            throw exception(SETTING_CONFIG_VALUE_INVALID, e.getMessage());
        }
    }

    private <T extends SettingBaseDTO> T getDefaultConfigValue(String key, Class<T> clazz) {
        ConfigStrategy<?> strategy = strategyMap.get(key);
        if (strategy == null || !strategy.getType().isAssignableFrom(clazz)) {
            return null;
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.warn("[getDefaultConfigValue][key({}) clazz({}) 默认实例创建失败]", key, clazz.getName(), e);
            return null;
        }
    }

    @VisibleForTesting
    InfraSettingDO validateSettingExists(Long id) {
        InfraSettingDO setting = infraSettingMapper.selectById(id);
        if (setting == null) {
            throw exception(SETTING_NOT_EXISTS);
        }
        return setting;
    }

    @VisibleForTesting
    void validateConfigKeyUnique(Long id, String key) {
        InfraSettingDO setting = infraSettingMapper.selectByKey(key);
        if (setting == null) {
            return;
        }
        if (id == null || !setting.getId().equals(id)) {
            throw exception(SETTING_KEY_DUPLICATE);
        }
    }

    private void publishSettingChangedEvent(String configKey) {
        eventPublisher.publishEvent(new InfraSettingChangedEvent(configKey));
    }

    private InfraSettingService getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
