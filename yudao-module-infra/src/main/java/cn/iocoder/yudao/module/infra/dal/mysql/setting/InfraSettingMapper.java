package cn.iocoder.yudao.module.infra.dal.mysql.setting;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingPageReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.setting.InfraSettingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InfraSettingMapper extends BaseMapperX<InfraSettingDO> {

    default InfraSettingDO selectByKey(String key) {
        return selectOne(InfraSettingDO::getConfigKey, key);
    }

    default PageResult<InfraSettingDO> selectPage(SettingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InfraSettingDO>()
                .likeIfPresent(InfraSettingDO::getConfigKey, reqVO.getConfigKey())
                .eqIfPresent(InfraSettingDO::getType, reqVO.getType())
                .betweenIfPresent(InfraSettingDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(InfraSettingDO::getId));
    }

}
