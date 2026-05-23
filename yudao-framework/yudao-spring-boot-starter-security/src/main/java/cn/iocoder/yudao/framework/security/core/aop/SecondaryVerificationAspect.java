package cn.iocoder.yudao.framework.security.core.aop;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.biz.system.verification.SecondaryVerificationCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.verification.dto.SecondaryVerificationReqDTO;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.annotations.SecondaryVerification;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 二次验证 AOP 切面。
 */
@Aspect
@Slf4j
public class SecondaryVerificationAspect {

    private static final String HEADER_VERIFICATION_TYPE = "X-Verification-Type";
    private static final String HEADER_VERIFICATION_PASSWORD = "X-Verification-Password";
    private static final String HEADER_VERIFICATION_MOBILE = "X-Verification-Mobile";
    private static final String HEADER_VERIFICATION_CODE = "X-Verification-Code";
    private static final String HEADER_VERIFICATION_TOKEN = "X-Verification-Token";

    @Resource
    private SecondaryVerificationCommonApi secondaryVerificationApi;

    @Before("@annotation(secondaryVerification)")
    public void doBefore(JoinPoint joinPoint, SecondaryVerification secondaryVerification) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw new ServiceException(401, "用户未登录");
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new ServiceException(500, "无法获取请求上下文");
        }
        HttpServletRequest request = attributes.getRequest();

        String verificationToken = request.getHeader(HEADER_VERIFICATION_TOKEN);
        if (StrUtil.isNotBlank(verificationToken)) {
            boolean valid = secondaryVerificationApi.verifyAndConsumeToken(
                    loginUser.getId(), verificationToken, secondaryVerification.scene().getScene());
            if (!valid) {
                throw new ServiceException(403, "二次验证失败：验证令牌无效或已过期");
            }
            log.info("[二次验证] 用户 {} 使用令牌通过验证, 场景: {}", loginUser.getId(), secondaryVerification.scene());
            return;
        }

        String verificationType = request.getHeader(HEADER_VERIFICATION_TYPE);
        if (StrUtil.isBlank(verificationType)) {
            throw new ServiceException(403, "缺少二次验证参数");
        }

        SecondaryVerificationReqDTO reqDTO = new SecondaryVerificationReqDTO();
        reqDTO.setUserId(loginUser.getId());
        reqDTO.setVerificationType(Integer.parseInt(verificationType));
        reqDTO.setScene(secondaryVerification.scene().getScene());
        reqDTO.setIp(ServletUtils.getClientIP(request));

        if (Integer.valueOf(1).equals(reqDTO.getVerificationType())) {
            reqDTO.setPassword(request.getHeader(HEADER_VERIFICATION_PASSWORD));
        } else if (Integer.valueOf(2).equals(reqDTO.getVerificationType())) {
            reqDTO.setMobile(request.getHeader(HEADER_VERIFICATION_MOBILE));
            reqDTO.setCode(request.getHeader(HEADER_VERIFICATION_CODE));
        }

        try {
            secondaryVerificationApi.verify(reqDTO);
            log.info("[二次验证] 用户 {} 验证成功, 类型: {}, 场景: {}",
                    loginUser.getId(), reqDTO.getVerificationType(), secondaryVerification.scene().getScene());
        } catch (ServiceException e) {
            log.error("[二次验证] 用户 {} 验证失败, 类型: {}, 场景: {}, 原因: {}",
                    loginUser.getId(), reqDTO.getVerificationType(), secondaryVerification.scene().getScene(), e.getMessage());
            throw e;
        }
    }

}
