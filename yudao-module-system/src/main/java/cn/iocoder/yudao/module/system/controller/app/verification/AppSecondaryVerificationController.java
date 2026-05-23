package cn.iocoder.yudao.module.system.controller.app.verification;

import cn.iocoder.yudao.framework.common.biz.system.verification.dto.SecondaryVerificationReqDTO;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.system.controller.app.verification.vo.AppSecondaryVerificationReqVO;
import cn.iocoder.yudao.module.system.service.verification.SecondaryVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SECONDARY_VERIFICATION_TYPE_NOT_SUPPORT;

@Tag(name = "APP - 二次验证")
@RestController
@RequestMapping("/app-api/system/secondary-verification")
@Validated
public class AppSecondaryVerificationController {

    @Resource
    private SecondaryVerificationService secondaryVerificationService;

    @PostMapping("/verify")
    @Operation(summary = "执行二次验证")
    public CommonResult<String> verify(@Valid @RequestBody AppSecondaryVerificationReqVO reqVO,
                                       HttpServletRequest request) {
        if (Integer.valueOf(1).equals(reqVO.getVerificationType())) {
            throw new ServiceException(SECONDARY_VERIFICATION_TYPE_NOT_SUPPORT);
        }
        SecondaryVerificationReqDTO reqDTO = new SecondaryVerificationReqDTO();
        reqDTO.setUserId(getLoginUserId());
        reqDTO.setVerificationType(reqVO.getVerificationType());
        reqDTO.setPassword(reqVO.getPassword());
        reqDTO.setMobile(reqVO.getMobile());
        reqDTO.setCode(reqVO.getCode());
        reqDTO.setScene(reqVO.getScene());
        reqDTO.setIp(ServletUtils.getClientIP(request));
        secondaryVerificationService.verify(reqDTO);
        String token = secondaryVerificationService.generateVerificationToken(
                getLoginUserId(), UserTypeEnum.MEMBER.getValue(), reqVO.getScene());
        return success(token);
    }

    @GetMapping("/check-token")
    @Operation(summary = "检查验证令牌是否有效")
    @Parameter(name = "token", description = "验证令牌", required = true)
    @Parameter(name = "scene", description = "验证场景", required = true)
    public CommonResult<Boolean> checkToken(@RequestParam("token") String token,
                                            @RequestParam("scene") Integer scene) {
        boolean valid = secondaryVerificationService.verifyAndConsumeToken(
                getLoginUserId(), UserTypeEnum.MEMBER.getValue(), token, scene);
        return success(valid);
    }

}
