package cn.iocoder.yudao.module.system.service.verification;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.biz.system.verification.dto.SecondaryVerificationReqDTO;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.verification.SecondaryVerificationLogDO;
import cn.iocoder.yudao.module.system.dal.mysql.verification.SecondaryVerificationLogMapper;
import cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SECONDARY_VERIFICATION_FAILED;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SECONDARY_VERIFICATION_PASSWORD_ERROR;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SECONDARY_VERIFICATION_PASSWORD_REQUIRED;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SECONDARY_VERIFICATION_SMS_CODE_REQUIRED;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SECONDARY_VERIFICATION_TYPE_NOT_SUPPORT;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SECONDARY_VERIFICATION_USER_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class SecondaryVerificationServiceImpl implements SecondaryVerificationService {

    private static final Integer VERIFICATION_TYPE_PASSWORD = 1;
    private static final Integer VERIFICATION_TYPE_SMS_CODE = 2;
    private static final Long VERIFICATION_TOKEN_EXPIRE_MINUTES = 5L;

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private SmsCodeApi smsCodeApi;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SecondaryVerificationLogMapper verificationLogMapper;

    @Override
    public void verify(SecondaryVerificationReqDTO reqDTO) {
        if (!VERIFICATION_TYPE_PASSWORD.equals(reqDTO.getVerificationType())
                && !VERIFICATION_TYPE_SMS_CODE.equals(reqDTO.getVerificationType())) {
            throw new ServiceException(SECONDARY_VERIFICATION_TYPE_NOT_SUPPORT);
        }
        if (VERIFICATION_TYPE_PASSWORD.equals(reqDTO.getVerificationType())) {
            verifyPassword(reqDTO);
            return;
        }
        verifySmsCode(reqDTO);
    }

    private void verifyPassword(SecondaryVerificationReqDTO reqDTO) {
        if (StrUtil.isBlank(reqDTO.getPassword())) {
            throw new ServiceException(SECONDARY_VERIFICATION_PASSWORD_REQUIRED);
        }
        String encodedPassword = getAdminUserPassword(reqDTO.getUserId());
        if (!passwordEncoder.matches(reqDTO.getPassword(), encodedPassword)) {
            throw new ServiceException(SECONDARY_VERIFICATION_PASSWORD_ERROR);
        }
    }

    private void verifySmsCode(SecondaryVerificationReqDTO reqDTO) {
        if (StrUtil.isBlank(reqDTO.getMobile()) || StrUtil.isBlank(reqDTO.getCode())) {
            throw new ServiceException(SECONDARY_VERIFICATION_SMS_CODE_REQUIRED);
        }
        try {
            smsCodeApi.validateSmsCode(new SmsCodeValidateReqDTO()
                    .setMobile(reqDTO.getMobile())
                    .setCode(reqDTO.getCode())
                    .setScene(reqDTO.getScene()));
        } catch (Exception e) {
            log.error("[verifySmsCode][验证码校验失败，mobile({}) code({})]", reqDTO.getMobile(), reqDTO.getCode(), e);
            throw new ServiceException(SECONDARY_VERIFICATION_FAILED);
        }
    }

    private String getAdminUserPassword(Long userId) {
        AdminUserDO user = adminUserService.getUser(userId);
        if (user == null) {
            throw new ServiceException(SECONDARY_VERIFICATION_USER_NOT_EXISTS);
        }
        return user.getPassword();
    }

    @Override
    public String generateVerificationToken(Long userId, Integer userType, Integer scene) {
        String token = RandomUtil.randomString(32);
        String key = buildTokenKey(userId, userType, scene);
        stringRedisTemplate.opsForValue().set(key, token, VERIFICATION_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public boolean verifyAndConsumeToken(Long userId, Integer userType, String token, Integer scene) {
        String key = buildTokenKey(userId, userType, scene);
        String storedToken = stringRedisTemplate.opsForValue().get(key);
        if (!StrUtil.equals(storedToken, token)) {
            return false;
        }
        stringRedisTemplate.delete(key);
        return true;
    }

    @Override
    public void createVerificationLog(Long userId, Integer userType, Integer verificationType, Integer scene,
                                      Boolean result, String failReason, String requestMethod, String requestUrl,
                                      String userIp, String userAgent) {
        verificationLogMapper.insert(SecondaryVerificationLogDO.builder()
                .userId(userId)
                .userType(userType)
                .verificationType(verificationType)
                .scene(scene)
                .result(result)
                .failReason(failReason)
                .requestMethod(requestMethod)
                .requestUrl(requestUrl)
                .userIp(userIp)
                .userAgent(userAgent)
                .build());
    }

    private String buildTokenKey(Long userId, Integer userType, Integer scene) {
        return String.format(RedisKeyConstants.SECONDARY_VERIFICATION_TOKEN, userType, userId, scene);
    }

}
