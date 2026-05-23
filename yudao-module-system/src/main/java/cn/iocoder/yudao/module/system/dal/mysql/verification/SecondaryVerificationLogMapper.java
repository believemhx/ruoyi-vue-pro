package cn.iocoder.yudao.module.system.dal.mysql.verification;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.verification.SecondaryVerificationLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SecondaryVerificationLogMapper extends BaseMapperX<SecondaryVerificationLogDO> {
}
