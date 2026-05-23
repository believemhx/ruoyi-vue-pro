package cn.iocoder.yudao.module.system.api.verification;

import cn.iocoder.yudao.framework.common.biz.system.verification.SecondaryVerificationCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.verification.dto.SecondaryVerificationReqDTO;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.system.service.verification.SecondaryVerificationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class SecondaryVerificationCommonApiImpl implements SecondaryVerificationCommonApi {

    @Resource
    private SecondaryVerificationService secondaryVerificationService;

    @Override
    public void verify(SecondaryVerificationReqDTO reqDTO) {
        secondaryVerificationService.verify(reqDTO);
    }

    @Override
    public String generateVerificationToken(Long userId, Integer scene) {
        return secondaryVerificationService.generateVerificationToken(userId, UserTypeEnum.ADMIN.getValue(), scene);
    }

    @Override
    public boolean verifyAndConsumeToken(Long userId, String token, Integer scene) {
        return secondaryVerificationService.verifyAndConsumeToken(userId, UserTypeEnum.ADMIN.getValue(), token, scene);
    }

}
