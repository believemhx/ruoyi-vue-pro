package cn.iocoder.yudao.module.system.dal.dataobject.verification;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 二次验证日志 DO。
 */
@TableName("system_secondary_verification_log")
@KeySequence("system_secondary_verification_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecondaryVerificationLogDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private Integer userType;

    private Integer verificationType;

    private Integer scene;

    private Boolean result;

    private String failReason;

    private String requestMethod;

    private String requestUrl;

    private String userIp;

    private String userAgent;

}
