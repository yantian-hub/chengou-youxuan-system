[RADAME.md](https://github.com/user-attachments/files/26705505/RADAME.md)
# 宸购优选 - 高并发秒杀系统

基于Spring Cloud Alibaba的分布式电商平台模型，核心解决秒杀场景下的**库存超卖、重复下单、数据库瓶颈**问题。

> 压测报告链接：[掘金技术博客](https://juejin.cn/user/1294596079359515)

---

## 核心成果
| 指标 | MySQL悲观锁方案 | Redis+Lua优化方案 | 提升幅度 |
|------|----------------|------------------|---------|
| **QPS** | 381 | 5,519 | **14.5倍** |
| **P95延迟** | 204ms | 28ms | **降低86%** |
| **成功率** | 25%（无效竞争多） | 1%（精准拦截） | 流量过滤率99% |

**技术验证**：K6梯度压测（50→100并发），Arthas监控，压测验证无超卖

---

## 我负责的核心模块

### 1. 秒杀服务（seckill-service）
- **库存扣减**：Redis Lua脚本原子操作（查询+扣减+拦截一步完成）
- **防重复提交**：Redisson分布式锁（用户级，而非请求级）
- **异步削峰**：RocketMQ普通消息，订单创建异步化，保护数据库
- **流量控制**：Sentinel网关限流（控制台） + 热点参数（代码）限流双层防护

### 2. 微服务基础设施
- **网关层**：Gateway统一路由 + JWT鉴权 + 白名单机制 + Sentinel(控制台配置)
- **服务治理**：Nacos配置中心 + OpenFeign远程调用 + ThreadLocal用户透传
- **缓存体系**：Spring Cache + Redis二级缓存，Cache Aside + 随机TTL防雪崩

### 3. 数据一致性保障
- **普通订单**：RocketMQ事务消息（Half消息 + 本地事务 + 回查机制）+ Redis幂等校验
- **秒杀订单**：Redis Lua原子操作（库存预扣减 + 防重校验）+ 异步订单创建 + 死信队列兜底
- **支付回调**：RocketMQ异步通知 + Redis防重复处理
- **异常处理**：死信队列记录失败订单，日志告警人工介入
---

## 技术栈

**核心框架**：Spring Boot 3.2 + Spring Cloud Alibaba + Java 17  
**高并发组件**：Redis + Redisson + RocketMQ + Sentinel  
**数据层**：MySQL 8.0 + MyBatis-Plus   
**压测工具**：K6 + Arthas + Linux性能分析  

---

## 项目结构
cloud_shop/ 
├── mail_gateway/ # API网关（鉴权、限流、路由） 
├── mail_api/ # Feign接口定义层 
├── mail_service/ # 微服务实现层 
│ ├── mail_usr_service/ # 用户服务 
│ ├── mail_service_goods/ # 商品服务 
│ ├── mail_cart_service/ # 购物车服务 
│ ├── mail_order_service/ # 订单服务 
│ ├── mail_pay_service/ # 支付服务 
│ └── mail_seckill_service/ # 秒杀服务（高并发核心） 
├── mail_util/ # 公共工具类 


---

## 运行说明

> 本项目为微服务架构，依赖MySQL、Redis、Nacos、RocketMQ、Sentinel等组件。  
> 因中间件较多，**完整运行需本地环境配置**，核心代码和接口设计可直接查看源码。

---

## 压测过程关键发现

1. **MySQL连接池瓶颈**：并发100时QPS反降17%，悲观锁持有时间过长
2. **Redis单节点天花板**：并发100时QPS从5500降至4300，网络IO成为瓶颈
3. **库存精准验证**：两组方案均无超卖

---
## 后续优化

- [ ] 补充Docker Compose一键启动（降低运行门槛）
- [ ] 集成SkyWalking链路追踪
---
**联系方式**：1158905968@qq.com
