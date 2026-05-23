package cn.iocoder.yudao.framework.common.biz.system.verification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 二次验证请求 DTO
 */
@Schema(description = "二次验证请求 DTO")
@Data
public class SecondaryVerificationReqDTO implements Serializable {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "验证类型: 1-密码 2-短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "验证类型不能为空")
    private Integer verificationType;

    @Schema(description = "密码（验证类型为密码时必填）", example = "123456")
    private String password;

    @Schema(description = "手机号（验证类型为短信时必填）", example = "15601691300")
    private String mobile;

    @Schema(description = "验证码（验证类型为短信时必填）", example = "1234")
    private String code;

    @Schema(description = "验证场景", example = "1")
    private Integer scene;

    @Schema(description = "客户端 IP", example = "127.0.0.1")
    private String ip;

}
