package cn.iocoder.yudao.framework.common.biz.infra.setting.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置变更事件。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfraSettingChangedEvent {

    private String configKey;

}
