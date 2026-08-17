# Zeus 领域模型

## 核心概念

### 导出任务（Export Task）
用户发起的数据导出请求。包含模板标识、查询参数、操作人信息。任务经历 **待处理 → 处理中 → 成功/失败** 的生命周期。

### 模板（Template）
通过 `@ExcelFeign` 注解标记的 Feign 接口方法。每个模板对应一种数据导出能力，由 `templateCode` 唯一标识。

### 文件存储（File Storage）
导出的 Excel 文件通过 `FileStorageService` 抽象接口上传到对象存储（OSS/MinIO），返回 `fileId` 作为文件标识。

## 关键术语

| 术语 | 定义 |
|------|------|
| **taskId** | 导出任务的唯一标识，UUID 格式 |
| **templateCode** | 模板编码，对应 `@ExcelFeign(name=...)` 的值 |
| **fileId** | 文件存储后的唯一标识，由 `FileStorageService.upload()` 返回 |
| **Pending** | 任务初始状态，等待执行 |
| **Processing** | 任务正在执行中 |
| **Success** | 任务执行成功，文件已生成 |
| **Fail** | 任务执行失败，记录 `errorReason` |

## 模块职责

| 模块 | 职责 |
|------|------|
| `zeus-core` | 基础能力抽象（FileStorageService、TableRowBaseData） |
| `zeus-mq` | 通用 MQ 能力封装（Producer/Consumer） |
| `zeus-redis` | Redis/Redisson 基础配置 |
| `zeus-data-exchange` | 导出业务逻辑（任务管理、MQ 收发、执行器） |
