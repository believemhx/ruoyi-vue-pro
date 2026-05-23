package cn.iocoder.yudao.module.system.service.verification;

import cn.iocoder.yudao.framework.common.biz.system.verification.dto.SecondaryVerificationReqDTO;

import jakarta.validation.Valid;

public interface SecondaryVerificationService {

    void verify(@Valid SecondaryVerificationReqDTO reqDTO);

    String generateVerificationToken(Long userId, Integer userType, Integer scene);

    boolean verifyAndConsumeToken(Long userId, Integer userType, String token, Integer scene);

    void createVerificationLog(Long userId, Integer userType, Integer verificationType, Integer scene,
                               Boolean result, String failReason, String requestMethod, String requestUrl,
                               String userIp, String userAgent);

}
