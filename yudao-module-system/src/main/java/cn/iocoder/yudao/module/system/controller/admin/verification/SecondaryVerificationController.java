package cn.iocoder.yudao.module.system.controller.admin.verification;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.common.biz.system.verification.dto.SecondaryVerificationReqDTO;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.verification.vo.SecondaryVerificationReqVO;
import cn.iocoder.yudao.module.system.service.verification.SecondaryVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 二次验证")
@RestController
@RequestMapping("/system/secondary-verification")
@Validated
public class SecondaryVerificationController {

    @Resource
    private SecondaryVerificationService secondaryVerificationService;

    @PostMapping("/verify")
    @Operation(summary = "执行二次验证")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<String> verify(@Valid @RequestBody SecondaryVerificationReqVO reqVO,
                                       HttpServletRequest request) {
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
                getLoginUserId(), WebFrameworkUtils.getLoginUserType(), reqVO.getScene());
        return success(token);
    }

    @GetMapping("/check-token")
    @Operation(summary = "检查验证令牌是否有效")
    @Parameter(name = "token", description = "验证令牌", required = true)
    @Parameter(name = "scene", description = "验证场景", required = true)
    public CommonResult<Boolean> checkToken(@RequestParam("token") String token,
                                            @RequestParam("scene") Integer scene) {
        boolean valid = secondaryVerificationService.verifyAndConsumeToken(
                getLoginUserId(), WebFrameworkUtils.getLoginUserType(), token, scene);
        return success(valid);
    }

}
