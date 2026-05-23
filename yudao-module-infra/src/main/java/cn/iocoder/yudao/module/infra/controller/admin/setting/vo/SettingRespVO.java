package cn.iocoder.yudao.module.infra.controller.admin.setting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 业务配置信息 Response VO")
@Data
public class SettingRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED, example = "agt.earnings.fee")
    private String configKey;

    @Schema(description = "参数键值(JSON)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configValue;

    @Schema(description = "参数类型", example = "1")
    private Integer type;

    @Schema(description = "状态", example = "true")
    private Boolean status;

    @Schema(description = "是否可见", example = "true")
    private Boolean visible;

    @Schema(description = "备注", example = "收益配置")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
