# Graph Report - zeus-parent  (2026-09-24)

## Corpus Check
- 70 files · ~7,047 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 359 nodes · 896 edges · 18 communities (9 shown, 9 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 38 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Community 0
- Community 1
- Community 2
- Community 3
- Community 4
- Community 5
- Community 6
- Community 7
- Community 8
- Community 9
- Community 10
- Community 11
- Community 12
- Community 13
- Community 14
- Community 15
- Community 16
- Community 17

## God Nodes (most connected - your core abstractions)
1. `ExcelExportTaskEntity` - 32 edges
2. `ExcelExportTaskRepository` - 28 edges
3. `ExportTaskService` - 23 edges
4. `ExcelFeignBeanFactory` - 17 edges
5. `DefaultExcelFeignHandler` - 17 edges
6. `TableRowBaseData` - 17 edges
7. `PageData` - 15 edges
8. `ExcelFeignBean` - 14 edges
9. `TemplateConfig` - 14 edges
10. `DefaultExcelExportExecutor` - 14 edges

## Surprising Connections (you probably didn't know these)
- `Application` --references--> `EnableExcelFeign`  [EXTRACTED]
  zeus-data-exchange-center-example/src/main/java/com/wss/zeus/data/exchange/center/example/Application.java → zeus-data-exchange/src/main/java/com/wss/zeus/data/exchange/annotation/EnableExcelFeign.java
- `ExcelFeignBean` --references--> `ExcelFeign`  [EXTRACTED]
  zeus-data-exchange/src/main/java/com/wss/zeus/data/exchange/beans/ExcelFeignBean.java → zeus-core/src/main/java/com/wss/zeus/core/annotation/ExcelFeign.java
- `NormalTemplateConfig` --implements--> `TemplateConfig`  [EXTRACTED]
  zeus-data-exchange-center-example/src/main/java/com/wss/zeus/data/exchange/center/example/template/NormalTemplateConfig.java → zeus-data-exchange/src/main/java/com/wss/zeus/data/exchange/enums/TemplateConfig.java
- `NormalTemplateConfig` --references--> `ExcelTableRowConvertor`  [EXTRACTED]
  zeus-data-exchange-center-example/src/main/java/com/wss/zeus/data/exchange/center/example/template/NormalTemplateConfig.java → zeus-data-exchange/src/main/java/com/wss/zeus/data/exchange/handler/ExcelTableRowConvertor.java
- `ExportTransactionHandler` --references--> `ExcelExportTaskRepository`  [EXTRACTED]
  zeus-data-exchange-starter/src/main/java/com/wss/zeus/data/exchange/starter/mq/ExportTransactionHandler.java → zeus-data-exchange/src/main/java/com/wss/zeus/data/exchange/repository/ExcelExportTaskRepository.java

## Import Cycles
- None detected.

## Communities (18 total, 9 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.07
Nodes (36): arraylist, collectionutils, com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper, com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper, com.baomidou.mybatisplus.core.mapper.BaseMapper, com.baomidou.mybatisplus.extension.service.impl.ServiceImpl, com.wss.zeus.redis.lock.DistributedLockExecutor, com.xxl.job.core.handler.annotation.XxlJob (+28 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (33): arrays, excelproperty, GetMapping, inputstream, list, localdatetime, lombok.Data, mappers (+25 more)

### Community 2 - "Community 2"
Cohesion: 0.07
Nodes (25): annotatedelementutils, aoputils, bytearrayinputstream, bytearrayoutputstream, classutils, com.alibaba.fastjson2.JSONObject, easyexcel, exceltypeenum (+17 more)

### Community 3 - "Community 3"
Cohesion: 0.10
Nodes (20): collections, duration, localtransactionstate, lombok.RequiredArgsConstructor, message, messagebuilder, org.apache.rocketmq.spring.core.RocketMQTemplate, org.springframework.data.redis.core.RedisTemplate (+12 more)

### Community 4 - "Community 4"
Cohesion: 0.13
Nodes (18): concurrenthashmap, map, org.apache.rocketmq.spring.annotation.RocketMQTransactionListener, org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener, org.apache.rocketmq.spring.core.RocketMQLocalTransactionState, org.springframework.beans.factory.annotation.Autowired, org.springframework.messaging.Message, rocketmqheaders (+10 more)

### Community 5 - "Community 5"
Cohesion: 0.14
Nodes (16): annotation, beandefinitionbuilder, optional, org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.context.annotation.ImportBeanDefinitionRegistrar, org.springframework.core.annotation.AnnotationAttributes, org.springframework.core.type.AnnotationMetadata, SuppressWarnings (+8 more)

### Community 6 - "Community 6"
Cohesion: 0.13
Nodes (16): lombok.AllArgsConstructor, lombok.Getter, BizError, BizException, SystemException, PARAM_ERROR, REPEAT, SUBMIT_FAILED (+8 more)

### Community 7 - "Community 7"
Cohesion: 0.16
Nodes (11): beansexception, objects, org.springframework.boot.autoconfigure.AutoConfiguration, org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean, org.springframework.context.annotation.Import, org.springframework.context.ApplicationContext, org.springframework.context.ApplicationContextAware, ZeusCoreAutoConfiguration (+3 more)

### Community 8 - "Community 8"
Cohesion: 0.32
Nodes (9): org.springframework.boot.autoconfigure.SpringBootApplication, org.springframework.cloud.client.discovery.EnableDiscoveryClient, org.springframework.cloud.openfeign.EnableFeignClients, springapplication, ExportFeign, BusinessExampleApplication, Application, NormalTemplateConfig (+1 more)

## Knowledge Gaps
- **19 isolated node(s):** `zeus-data-exchange`, `PENDING`, `PROCESSING`, `SUCCESS`, `FAIL` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 80 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ExcelExportTaskEntity` connect `Community 0` to `Community 1`, `Community 2`, `Community 3`, `Community 4`?**
  _High betweenness centrality (0.124) - this node is a cross-community bridge._
- **Why does `ExcelExportTaskRepository` connect `Community 0` to `Community 3`, `Community 4`?**
  _High betweenness centrality (0.071) - this node is a cross-community bridge._
- **Why does `ExportTaskService` connect `Community 3` to `Community 0`, `Community 1`?**
  _High betweenness centrality (0.061) - this node is a cross-community bridge._
- **What connects `zeus-data-exchange`, `PENDING`, `PROCESSING` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.06775268419104036 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.062004662004662 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.07428571428571429 - nodes in this community are weakly interconnected._