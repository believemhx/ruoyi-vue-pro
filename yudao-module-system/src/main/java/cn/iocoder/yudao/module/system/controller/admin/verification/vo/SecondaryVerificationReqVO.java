package cn.iocoder.yudao.module.system.controller.admin.verification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 二次验证请求 VO")
@Data
public class SecondaryVerificationReqVO {

    @Schema(description = "验证类型: 1-密码 2-短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "验证类型不能为空")
    private Integer verificationType;

    @Schema(description = "密码（验证类型为密码时必填）", example = "123456")
    private String password;

    @Schema(description = "手机号（验证类型为短信时必填）", example = "15601691300")
    private String mobile;

    @Schema(description = "验证码（验证类型为短信时必填）", example = "1234")
    private String code;

    @Schema(description = "验证场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "验证场景不能为空")
    private Integer scene;

}
