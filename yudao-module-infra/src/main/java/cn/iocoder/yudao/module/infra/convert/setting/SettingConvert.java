package cn.iocoder.yudao.module.infra.convert.setting;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.setting.InfraSettingDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SettingConvert {

    SettingConvert INSTANCE = Mappers.getMapper(SettingConvert.class);

    InfraSettingDO convert(SettingSaveReqVO bean);

    SettingRespVO convert(InfraSettingDO bean);

    List<SettingRespVO> convertList(List<InfraSettingDO> list);

    PageResult<SettingRespVO> convertPage(PageResult<InfraSettingDO> page);

}
