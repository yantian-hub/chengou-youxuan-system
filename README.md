# 宸购优选 - 高并发秒杀系统

基于 Spring Cloud Alibaba 的分布式电商平台，核心解决秒杀场景下的 **库存超卖、重复下单、数据库瓶颈** 问题。

> 压测报告：[掘金技术博客](https://juejin.cn/post/7627095341071450152)

---

## 核心成果

| 指标 | MySQL 悲观锁方案 | Redis+Lua 优化方案 | 提升幅度 |
|------|-----------------|-------------------|---------|
| **QPS** | 381 | 5,519 | **14.5 倍** |
| **P95 延迟** | 204ms | 28ms | **降低 86%** |
| **流量过滤率** | 75% 无效竞争 | 99% 精准拦截 | — |

> 验证方式：K6 梯度压测（50→100 并发），Arthas 监控，最终库存无超卖

---

## 系统架构

```
┌─────────────┐     ┌──────────────┐     ┌─────────────────┐
│  浏览器/Vue  │────▶│  Spring Cloud │────▶│  Nacos 注册中心  │
│  (port 3000) │     │  Gateway网关  │     │  + 配置中心      │
└─────────────┘     │  JWT鉴权限流  │     └─────────────────┘
                    └──────┬───────┘
                           │
          ┌────────────────┼────────────────────┐
          ▼                ▼                     ▼
   ┌────────────┐   ┌────────────┐    ┌────────────────┐
   │ 商品服务    │   │ 用户服务    │    │  秒杀服务 ★核心  │
   │ port 8081  │   │ port 8082  │    │  port 8089     │
   └────────────┘   └────────────┘    └───────┬────────┘
          │                │                   │
          ▼                ▼                   ▼
   ┌────────────┐   ┌────────────┐    ┌────────────────┐
   │ MySQL      │   │ MySQL      │    │  Redis(预加载)  │
   │ (goods库)  │   │ (usr库)    │    │  Lua原子扣库存   │
   └────────────┘   └────────────┘    └───────┬────────┘
                                              │
                                              ▼
                                       ┌──────────────┐
                                       │  RocketMQ     │
                                       │  异步削峰     │
                                       └───────┬──────┘
                                               │
                                               ▼
                                        ┌──────────────┐
                                        │  MySQL       │
                                        │  (seckill库) │
                                        └──────────────┘
```

### 秒杀链路时序

```
用户 → Gateway鉴权 → Sentinel限流
                        │
                        ▼
             ┌─────────────────────┐
             │  Redisson 用户级锁   │ ← 防重复提交
             └─────────┬───────────┘
                       │
                       ▼
             ┌─────────────────────┐
             │  Redis Lua 原子脚本  │ ← 查库存+扣库存+标记用户 一步完成
             └─────────┬───────────┘
                       │
            ┌──────────┴──────────┐
            ▼                     ▼
     库存不足(返回)          库存充足
                              │
                              ▼
              ┌─────────────────────────┐
              │  RocketMQ 普通消息       │ ← 异步创建订单
              │  + 延迟消息(15min)       │ ← 超时取消
              └─────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
              MySQL写订单          支付成功 → 完成
                                 支付超时 → 回滚库存+取消订单
                                 支付后取消 → 标记退款
```

---

## 模块说明

| 模块 | 端口 | 说明 |
|------|------|------|
| `mail_gateway` | 10010 | Spring Cloud Gateway 网关，统一路由 + JWT 鉴权 + Sentinel 限流 |
| `mail_service/mail_service_goods` | 8081 | 商品服务：SPU/SKU/品牌/分类管理 |
| `mail_service/mail_usr_service` | 8082 | 用户服务：注册/登录/地址管理 |
| `mail_service/mail_cart_service` | — | 购物车服务（基于 Redis） |
| `mail_service/mail_order_service` | — | 订单服务：RocketMQ 事务消息 |
| `mail_service/mail_seckill_service` | 8089 | 秒杀服务：Redis+Lua+RocketMQ |
| `mail_service/mail_pay_service` | — | 支付服务：模拟微信支付 |
| `mali_api/*` | — | API 公共模块：Feign 接口、DTO 模型 |
| `mail_util` | — | 通用工具：JWT、响应封装、配置 |
| `mail_web` | 3000 | Vue 3 前端（Element Plus） |

---

## 技术栈

| 分类 | 技术 |
|------|------|
| **核心框架** | Spring Boot 3.2 / Spring Cloud Alibaba / Java 17 |
| **高并发** | Redis / Redisson / RocketMQ / Sentinel |
| **数据层** | MySQL 8.0 / MyBatis-Plus |
| **注册配置** | Nacos |
| **前端** | Vue 3 / Element Plus / Vite |
| **工具链** | K6 / Arthas / Maven |

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.x
- RocketMQ 5.x
- Nacos 2.x
- Sentinel 控制台 1.8.x
- Node.js 18+（前端）

### 启动步骤

1. **启动基础设施**
   ```bash
   # 启动 Nacos（注册中心+配置中心）
   sh bin/startup.sh -m standalone

   # 启动 Redis
   redis-server

   # 启动 RocketMQ（NameServer + Broker）
   sh bin/mqnamesrv
   sh bin/mqbroker -n localhost:9876
   ```

2. **初始化数据库**
   ```sql
   -- 执行项目中的 SQL 脚本创建各业务库
   -- goods、usr、seckill、order、pay
   ```

3. **修改配置**
   - 在各服务的 `application.yml` 中配置 MySQL、Redis、Nacos 连接信息

4. **编译运行后端**
   ```bash
   mvn clean install -DskipTests
   # 按顺序启动各服务（推荐从 Gateway → 基础服务 → 业务服务）
   ```

5. **启动前端**
   ```bash
   cd mail_web
   npm install
   npm run dev
   ```

6. **访问**
   - 前端：http://localhost:3000
   - 后端网关：http://localhost:10010

---

## 核心设计

### 1. 秒杀服务（seckill-service）

| 问题 | 方案 | 关键点 |
|------|------|--------|
| 库存超卖 | Redis Lua 原子脚本 | `KEYS[1]` 查库存 → 扣库存 → `SADD` 用户标记，单次 Redis 调用完成 |
| 重复提交 | Redisson 分布式锁 | 用户级粒度（`seckill:lock:{userId}:{skuId}`），非请求级 |
| 数据库打崩 | RocketMQ 异步削峰 | 普通消息异步写订单，数据库单一消费者写入 |
| 流量洪峰 | Sentinel 双层限流 | 网关层 QPS 限流 + 热点参数限流 |
| 订单超时 | RocketMQ 延迟消息 | 15min 延迟 Level 4，消费时检查支付状态 |
| 兜底 | 死信队列 | `%DLQ%seckill-cancel-consumer` 记录，人工补偿 |

### 2. 数据一致性保障

- **普通订单**：RocketMQ 事务消息（Half 消息 → 本地事务 → 回查机制）+ Redis 幂等键
- **秒杀订单**：Lua 原子预扣 + MQ 异步落库 + 死信兜底
- **支付回调**：MQ 异步通知 + Redis `setex` 防重复处理
- **超时取消**：延迟消息消费时检测支付状态，已支付则标记退款

### 3. 微服务基础设施

- **网关层**：Gateway 统一路由 + JWT 鉴权 + 白名单 + Sentinel 限流（控制台配置）
- **服务治理**：Nacos 注册发现/配置中心 + OpenFeign 远程调用 + ThreadLocal 用户透传
- **缓存体系**：Spring Cache + Redis 二级缓存，Cache Aside 模式 + 随机 TTL 防雪崩

---

## 压测发现与总结

| 发现 | 原因分析 |
|------|---------|
| MySQL 并发 100 时 QPS 反降 17% | 悲观锁持有时间过长，连接池耗尽 |
| Redis 并发 100 时 QPS 从 5500→4300 | 单节点网络 IO 成为瓶颈 |
| 两组方案均无超卖 | 校验链路正确，库存最终一致 |

**结论**："缓存 + 异步"架构在高并发秒杀场景下有量级优势，但引入了系统复杂度与最终一致性的权衡。

---

## 项目结构

```
cloud_shop/
├── mail_gateway/            # 网关服务
├── mail_service/
│   ├── mail_service_goods/  # 商品服务
│   ├── mail_usr_service/    # 用户服务
│   ├── mail_cart_service/   # 购物车服务
│   ├── mail_order_service/  # 订单服务
│   ├── mail_seckill_service/ # 秒杀服务（核心）
│   └── mail_pay_service/    # 支付服务
├── mali_api/                # API 公共模块
│   ├── mail_goods_api/
│   ├── mail_usr_api/
│   ├── mail_cart_api/
│   ├── mail_order_api/
│   ├── mail_pay_api/
│   └── mail_seckill_api/
├── mail_util/               # 工具模块
│   ├── mail_common/
│   └── mail_service_dependency/
├── mail_web/                # Vue 3 前端
└── pom.xml                  # 父 POM
```

---

## 前端页面

| 页面 | 路由 | 功能 |
|------|------|------|
| 首页 | `/` | 商品搜索、品牌筛选、商品列表 |
| 登录 | `/login` | 手机号+密码登录 |
| 注册 | `/register` | 注册、短信验证码 |
| 购物车 | `/cart` | 增删改查、清空、结算 |
| 秒杀 | `/seckill` | 限时抢购、倒计时、秒杀记录 |
| 确认订单 | `/order/confirm` | 地址管理、订单确认 |
| 支付成功 | `/pay/success/:id` | 支付结果展示、模拟回调 |

---

## 相关链接

- GitHub：https://github.com/yantian-hub/chengou-youxuan-system
- 压测报告：https://juejin.cn/post/7627095341071450152
