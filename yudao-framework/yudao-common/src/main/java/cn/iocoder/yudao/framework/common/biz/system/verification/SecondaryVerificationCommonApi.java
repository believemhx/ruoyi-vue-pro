package cn.iocoder.yudao.framework.common.biz.system.verification;

import cn.iocoder.yudao.framework.common.biz.system.verification.dto.SecondaryVerificationReqDTO;

import jakarta.validation.Valid;

/**
 * 二次验证 Common API 接口
 */
public interface SecondaryVerificationCommonApi {

    /**
     * 验证用户身份。
     *
     * @param reqDTO 验证请求
     */
    void verify(@Valid SecondaryVerificationReqDTO reqDTO);

    /**
     * 生成验证令牌。
     *
     * @param userId 用户编号
     * @param scene 场景
     * @return 验证令牌
     */
    String generateVerificationToken(Long userId, Integer scene);

    /**
     * 验证并消费验证令牌。
     *
     * @param userId 用户编号
     * @param token 验证令牌
     * @param scene 场景
     * @return 是否验证通过
     */
    boolean verifyAndConsumeToken(Long userId, String token, Integer scene);

}
