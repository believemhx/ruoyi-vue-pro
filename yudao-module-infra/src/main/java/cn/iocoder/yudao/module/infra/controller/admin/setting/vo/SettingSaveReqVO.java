package cn.iocoder.yudao.module.infra.controller.admin.setting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 业务配置新增/修改 Request VO")
@Data
public class SettingSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED, example = "agt.earnings.fee")
    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名长度不能超过 100 个字符")
    private String configKey;

    @Schema(description = "参数键值(JSON)", requiredMode = Schema.RequiredMode.REQUIRED, example = "{\"enabled\":true}")
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 4000, message = "参数键值长度不能超过 4000 个字符")
    private String configValue;

    @Schema(description = "状态", example = "true")
    private Boolean status;

    @Schema(description = "是否可见", example = "true")
    private Boolean visible;

    @Schema(description = "备注", example = "收益配置")
    private String remark;

}
