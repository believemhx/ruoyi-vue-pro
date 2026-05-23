# framework / infra / system 迁移交付说明

## 本轮完成范围

本轮已在新版 `ruoyi-vue-pro/master-jdk17` 底座上完成以下三块迁移：

1. `framework`
2. `infra`
3. `system`

目标不是原样搬运旧代码，而是先得到一版可复用、可编译、边界更清晰的新底座。

## 已完成项

### framework

1. `CommonStatusEnum` 已统一为 `1=开启 / 0=关闭`
2. `EnableStatusEnum` 不再迁入新底座
3. 新增 `SecondaryVerification` 注解、切面、枚举、自动配置接缝
4. 新增 `common.biz.system.verification.*`
5. 新增 `common.biz.infra.setting.*`
6. 新增 `ValidateWithMethod` 和 `ValidateWithMethodValidator`

### infra

1. 新增 `InfraSettingCommonApi` 的 `infra` 实现
2. 新增 `InfraSettingDO / Mapper / Service / Controller / VO / Convert`
3. `UserConfig` 不再迁入，统一收敛到 `InfraSetting`
4. 新版已有的 `FileApi / WebSocketSenderApi` 保持沿用

### system

1. 新增 `SecondaryVerificationCommonApi` 的 `system` 实现
2. 新增 `SecondaryVerificationService`
3. 新增 `SecondaryVerificationLogDO / Mapper`
4. 新增管理端二次验证控制器
5. 新增 APP 端二次验证控制器
6. 新增二次验证错误码和 Redis Token Key

## 编译验证

本轮完成后，已通过以下定向编译：

```powershell
mvn -pl yudao-framework/yudao-common,yudao-framework/yudao-spring-boot-starter-security,yudao-module-infra,yudao-module-system -am -DskipTests compile
```

以及后续增量验证：

```powershell
mvn -pl yudao-module-infra,yudao-module-system -am -DskipTests compile -q
```

## 当前有意保留的限制

1. 会员密码型二次验证本轮未强接，当前优先保证管理员密码验证与短信验证链路
2. `infra_setting` 和 `system_secondary_verification_log` 的 DDL 还需你结合实际库策略确认
3. 菜单、权限点、前端页面本轮没有一并补齐

## 建议下一步

1. 先审阅 `CommonStatusEnum`、`InfraSetting`、`SecondaryVerification`
2. 确认 DDL / 菜单 / 权限策略
3. 再推进 `pay / member`
