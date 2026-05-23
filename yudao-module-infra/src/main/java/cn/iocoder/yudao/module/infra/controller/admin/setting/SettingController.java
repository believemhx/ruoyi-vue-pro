package cn.iocoder.yudao.module.infra.controller.admin.setting;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.setting.vo.SettingSaveReqVO;
import cn.iocoder.yudao.module.infra.convert.setting.SettingConvert;
import cn.iocoder.yudao.module.infra.dal.dataobject.setting.InfraSettingDO;
import cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.infra.service.setting.InfraSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 业务配置")
@RestController
@RequestMapping("/infra/setting")
@Validated
public class SettingController {

    @Resource
    private InfraSettingService infraSettingService;

    @PostMapping("/create")
    @Operation(summary = "创建业务配置")
    @PreAuthorize("@ss.hasPermission('infra:setting:create')")
    public CommonResult<Long> createSetting(@Valid @RequestBody SettingSaveReqVO createReqVO) {
        return success(infraSettingService.createSetting(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改业务配置")
    @PreAuthorize("@ss.hasPermission('infra:setting:update')")
    public CommonResult<Boolean> updateSetting(@Valid @RequestBody SettingSaveReqVO updateReqVO) {
        infraSettingService.updateSetting(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:setting:delete')")
    public CommonResult<Boolean> deleteSetting(@RequestParam("id") Long id) {
        infraSettingService.deleteSetting(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得业务配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:setting:query')")
    public CommonResult<SettingRespVO> getSetting(@RequestParam("id") Long id) {
        return success(SettingConvert.INSTANCE.convert(infraSettingService.getSetting(id)));
    }

    @GetMapping("/get-by-key")
    @Operation(summary = "根据 key 获得业务配置")
    @PreAuthorize("@ss.hasPermission('infra:setting:query')")
    public CommonResult<SettingRespVO> getSettingByKey(@RequestParam("configKey") String configKey) {
        return success(SettingConvert.INSTANCE.convert(infraSettingService.getSettingByKey(configKey)));
    }

    @GetMapping("/get-value-by-key")
    @Operation(summary = "根据 key 获得业务配置值", description = "不可见的配置，不允许返回给前端")
    public CommonResult<String> getSettingValueByKey(@RequestParam("configKey") String configKey) {
        InfraSettingDO setting = infraSettingService.getSettingByKey(configKey);
        if (setting == null) {
            return success(null);
        }
        if (Boolean.FALSE.equals(setting.getVisible())) {
            throw exception(ErrorCodeConstants.CONFIG_GET_VALUE_ERROR_IF_VISIBLE);
        }
        return success(setting.getConfigValue());
    }

    @GetMapping("/page")
    @Operation(summary = "获取业务配置分页")
    @PreAuthorize("@ss.hasPermission('infra:setting:query')")
    public CommonResult<PageResult<SettingRespVO>> getSettingPage(@Valid SettingPageReqVO pageReqVO) {
        PageResult<InfraSettingDO> page = infraSettingService.getSettingPage(pageReqVO);
        return success(SettingConvert.INSTANCE.convertPage(page));
    }

}
