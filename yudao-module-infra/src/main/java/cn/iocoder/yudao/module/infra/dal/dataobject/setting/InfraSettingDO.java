package cn.iocoder.yudao.module.infra.dal.dataobject.setting;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.infra.enums.config.ConfigTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * 业务配置表。
 */
@TableName(value = "infra_setting", autoResultMap = true)
@KeySequence("infra_setting_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InfraSettingDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 枚举 {@link ConfigTypeEnum}
     */
    private Integer type;

    private String configKey;

    private String configValue;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extraData;

    private Boolean status;

    private Boolean visible;

    private String remark;

}
