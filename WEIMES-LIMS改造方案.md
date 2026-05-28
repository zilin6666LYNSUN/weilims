# WEIMES ERP → 医药行业 LIMS 完整改造方案

> **文档版本**: V3.0  
> **生成日期**: 2026-05-24  
> **适用项目**: weimes-erp / weimes-ui  

---

## 目录

- [一、项目现状分析](#一项目现状分析)
- [二、LIMS 完整功能架构](#二lims-完整功能架构)
- [三、需要删除的MES模块与数据表](#三需要删除的mes模块与数据表)
- [四、可复用模块改造方案](#四可复用模块改造方案)
- [五、合规基础层](#五合规基础层)
- [六、工作流引擎](#六工作流引擎)
- [七、核心业务模块](#七核心业务模块)
- [八、质量管理模块](#八质量管理模块)
- [九、资源管理模块](#九资源管理模块)
- [十、专项监测模块](#十专项监测模块)
- [十一、供应商质量管理](#十一供应商质量管理)
- [十二、文档与记录管理](#十二文档与记录管理)
- [十三、报告模板可配置化](#十三报告模板可配置化)
- [十四、医药伦理与GxP合规流程](#十四医药伦理与gxp合规流程)
- [十五、统计分析与仪表盘](#十五统计分析与仪表盘)
- [十六、集成接口](#十六集成接口)
- [十七、后端项目结构重组](#十七后端项目结构重组)
- [十八、前端项目结构重组](#十八前端项目结构重组)
- [十九、完整数据库表清单](#十九完整数据库表清单)
- [二十、实施路线图](#二十实施路线图)
- [二十一、合规要点清单](#二十一合规要点清单)

---

## 一、项目现状分析

### 1.1 技术栈

| 层面 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.3.13 |
| JDK | Java | 17 |
| 混合编程 | Kotlin | 2.0.0 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis + Redisson | 3.40.1 |
| 安全 | Spring Security + JWT | — |
| 报表 | 积木报表 JimuReport | 1.9.4 |
| 前端 | Vue 2 + Element UI | 2.6.12 / 2.15.6 |
| 移动端 | UniApp | — |
| 打印 | vue-plugin-hiprint / kr-print-designer | — |

### 1.2 现有业务模块

| 包名 | 模块 | 说明 | LIMS改造策略 |
|------|------|------|-------------|
| `md` | 主数据 | 物料/供应商/客户/车间/工作站 | **部分复用** → LIMS主数据 |
| `pro` | 生产管理 | 工单/工序/报工/工艺路线 | **删除** |
| `qc` | 质量管理 | IQC/IPQC/OQC/检测模板/缺陷 | **核心改造** → 检验管理 |
| `wm` | 仓库管理 | 出入库/库存/条码/包装 | **深度改造** → 样品/留样管理 |
| `dv` | 设备管理 | 设备台账/点检/维修/保养 | **核心改造** → 仪器管理 |
| `cal` | 排班日历 | 班次/班组/排班 | **删除** |
| `tm` | 工装管理 | 工具/工装类型 | **删除** → 替换为试剂管理 |
| `sales` | 销售管理 | 销售订单/调拨单/采购单 | **删除** → 替换为委托检验 |
| `analytics` | 数据分析 | 空壳接口 | **扩展** → SPC/趋势/仪表盘 |
| `hrm` | 人力资源 | 员工/面试/考勤 | **部分复用** → 人员资质/培训 |

### 1.3 现有审批模式

项目**无独立工作流引擎**，审批全部硬编码在Service层：

```java
// SalesOrderServiceImpl.refuse() — 典型的硬编码状态转换
if (salesOrder.getStatus().equals(OrderStatusEnum.PREPARE.getCode())) {
    salesOrder.setStatus(OrderStatusEnum.CONFIRMED.getCode());
} else if (salesOrder.getStatus().equals(OrderStatusEnum.CONFIRMED.getCode())) {
    salesOrder.setStatus(OrderStatusEnum.APPROVING.getCode());
}
```

现有状态枚举 `OrderStatusEnum`: `PREPARE → CONFIRMED → APPROVING → APPROVED/REFUSE → FINISHED`

**结论**: LIMS审批流程远比MES复杂（检验10+状态、偏差15+状态、变更12+状态），必须引入工作流引擎。

### 1.4 现有检测模板体系

项目已有 `qc_template` / `qc_template_index` / `qc_template_product` 三表：

| 表 | 说明 | 现有字段 |
|----|------|---------|
| `qc_template` | 检测模板头 | templateCode, templateName, qcTypes, itemId, enableFlag |
| `qc_template_index` | 模板-检测项 | indexId/Code/Name/Type, qcTool, checkMethod, standerVal, thresholdMax/Min, unitOfMeasure |
| `qc_template_product` | 模板-产品 | itemId/Code/Name, specification, quantityCheck, quantityUnqualified, crRate/majRate/minRate |

**改造方向**: 模板体系已有基础但过于简陋，需扩展为**可配置的报告模板引擎 + 检验模板引擎**双重体系。

---

## 二、LIMS 完整功能架构

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                               医药LIMS 完整功能架构                                          │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│  ┌─── 合规基础层 ──────────────────────────────────────────────────────────────────────┐    │
│  │  审计追踪(全字段) │ 电子签名(21CFR P11) │ 数据完整性(ALCOA+) │ 权限管理             │    │
│  │  医药伦理合规 │ GxP合规流程 │ 计算机系统验证(CSV)                               │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 工作流引擎 ──────────────────────────────────────────────────────────────────────┐    │
│  │  Flowable BPMN │ 流程定义 │ 流程实例 │ 任务分配 │ 审批记录 │ 超时提醒             │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 核心业务层 ──────────────────────────────────────────────────────────────────────┐    │
│  │                                                                                      │    │
│  │  ┌── 主数据 ──────────────┐  ┌── 样品管理 ───────────────────────────────────┐     │    │
│  │  │ 产品/物料              │  │ 样品登记 │ 样品接收 │ 样品分配                  │     │    │
│  │  │ 供应商                 │  │ 样品存储 │ 留样管理 │ 样品处置                  │     │    │
│  │  │ 客户                   │  │ 采样计划 │ 标签打印 │ 样品追踪                  │     │    │
│  │  │ 实验室/部门            │  └─────────────────────────────────────────────────┘     │    │
│  │  │ 检测项目/方法          │                                                            │    │
│  │  │ 质量标准(规格)         │                                                            │    │
│  │  └────────────────────────┘                                                            │    │
│  │                                                                                      │    │
│  │  ┌── 检验管理 ────────────────────────────────────────────────────────────┐         │    │
│  │  │ 检验订单 │ 检验结果录入 │ 自动判定 │ 修约规则 │ 复测/重测                   │         │    │
│  │  │ 检验复核 │ 检验批准 │ 微生物专项 │ 理化专项 │ 仪器分析专项                   │         │    │
│  │  │ 检验排程 │ 资源调度 │ 采样计划                                       │         │    │
│  │  └────────────────────────────────────────────────────────────────────────┘         │    │
│  │                                                                                      │    │
│  │  ┌── 报告管理 ────────────────────────────────────────────────────────────┐        │    │
│  │  │ 可配置报告模板 │ COA/CofA │ 检验报告 │ 稳定性报告 │ 环境监测报告            │        │    │
│  │  │ 批放行证书 │ 报告签发 │ 报告打印 │ 报告归档 │ OOT趋势报告               │        │    │
│  │  └────────────────────────────────────────────────────────────────────────┘        │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 质量管理 ──────────────────────────────────────────────────────────────────────┐    │
│  │  OOS/OOT调查 │ 偏差管理 │ CAPA │ 变更控制 │ 客户投诉 │ 召回管理                  │    │
│  │  批放行 │ 年度产品质量回顾(APQR) │ 自检管理 │ 风险评估                              │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 资源管理 ──────────────────────────────────────────────────────────────────────┐    │
│  │  ┌── 仪器管理 ───────────────────┐  ┌── 试剂/标准品 ─────────────────┐          │    │
│  │  │ 仪器台账 │ 校准管理 │ 维护保养    │  试剂台账 │ 标准品/对照品            │          │    │
│  │  │ 仪器确认(IQ/OQ/PQ)             │  │  培养基 │ 菌种管理              │          │    │
│  │  │ 仪器预约 │ 数据采集(CDS集成)    │  │  试剂领用 │ 效期管理              │          │    │
│  │  └─────────────────────────────────┘  └─────────────────────────────────┘          │    │
│  │  ┌── 人员管理 ────────────────────────────────────────────────────────────┐      │    │
│  │  │ 检验人员资质 │ 培训管理 │ 人员-检测项授权 │ 人员-仪器授权                     │      │    │
│  │  │ 证书管理 │ 再确认 │ 外部人员管理                                           │      │    │
│  │  └────────────────────────────────────────────────────────────────────────┘      │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 专项监测 ──────────────────────────────────────────────────────────────────────┐    │
│  │  稳定性考察(加速/长期/中间) │ 环境监测(洁净区) │ 水系统监测(PW/WFI)                │    │
│  │  培养基适用性检查 │ 菌种传代管理 │ 消毒剂效力验证                                    │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 医药伦理与GxP合规 ────────────────────────────────────────────────────────────┐    │
│  │  GLP合规流程 │ GMP合规流程 │ GCP合规流程 │ 药物警戒(PV) │ 伦理审查               │    │
│  │  药典合规(ChP/USP/EP/BP) │ 注册申报支持 │ MAH制度合规                                │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 文档与记录 ────────────────────────────────────────────────────────────────────┐    │
│  │  受控文档管理(DMS) │ 电子实验记录本(ELN) │ 原始数据管理 │ 记录归档                 │    │
│  │  文档阅读确认 │ 文档版本控制 │ 文档回顾                                             │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 统计分析 ──────────────────────────────────────────────────────────────────────┐    │
│  │  SPC控制图 │ 趋势分析 │ 批间比较 │ 能力分析(Cpk) │ 趋势预警                        │    │
│  │  自定义报表 │ 仪表盘 │ 多维度分析 │ APQR数据汇总                                     │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─── 集成接口 ──────────────────────────────────────────────────────────────────────┐    │
│  │  LIMS-ERP │ LIMS-MES │ LIMS-CDS │ LIMS-SCADA │ 仪器直连                             │    │
│  │  条码/RFID │ 移动端 │ Webservice/REST API                                        │    │
│  └──────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、需要删除的MES模块与数据表

### 3.1 后端代码删除清单

#### 3.1.1 完整删除的包（Java后端）

| 包路径 | 模块 | 删除原因 |
|--------|------|---------|
| `com.wei.mes.pro` | 生产管理 | 工单/工序/报工属MES，LIMS不需要 |
| `com.wei.mes.cal` | 排班日历 | 生产排班不适用LIMS |
| `com.wei.mes.tm` | 工装管理 | 工装/夹具管理不适用LIMS |
| `com.wei.mes.sales` | 销售管理 | 销售订单/采购单不适用（改为委托检验） |
| `com.wei.hrm.sm` (部分) | 面试/入职 | 面试流程不适用LIMS人员管理 |
| `com.wei.mobile.mes.controller.pro` | 移动端生产 | 移动端生产报工不需要 |
| `com.wei.mobile.mes.controller.cal` | 移动端排班 | 不需要 |
| `com.wei.mobile.mes.controller.sales` | 移动端销售 | 不需要 |

#### 3.1.2 部分删除的包

| 包路径 | 保留部分 | 删除部分 |
|--------|---------|---------|
| `com.wei.mes.md` | MdItem,MdVendor,MdClient,MdUnitMeasure | MdWorkstation,MdWorkstationMachine/Tool/Worker,MdProductBom,MdProductSop,MdProductSip,MdProductColor |
| `com.wei.mes.wm` | 基础出入库逻辑(改造用) | WmRtSalse(退货销售),WmPackage(包装),WmProductProduce(生产入库),WmItemConsume(物料消耗) |
| `com.wei.mes.dv` | DvMachinery,DvMachineryType,DvCheckPlan | DvRepair(改为维护保养) |
| `com.wei.mes.qc` | QcTemplate系列,QcIndex | 保留但重命名为LIMS命名 |
| `com.wei.hrm.sm` | HrmStaff | HrmInterviewRecord,HrmAttendance系列,HrmLeave/HrmOvertime |

### 3.2 前端代码删除清单

| 目录/文件 | 删除原因 |
|-----------|---------|
| `weimes-ui/src/views/mes/pro/` (23个Vue) | 生产管理页面全部删除 |
| `weimes-ui/src/views/mes/cal/` (10个Vue) | 排班日历页面全部删除 |
| `weimes-ui/src/views/mes/tm/` | 工装管理页面全部删除 |
| `weimes-ui/src/views/mes/sales/` | 销售管理页面全部删除 |
| `weimes-ui/src/views/mes/analytics/` | 空壳，后续重建 |
| `weimes-ui/src/api/mes/pro/` (13个JS) | 生产管理API全部删除 |
| `weimes-ui/src/api/mes/cal/` | 排班API全部删除 |
| `weimes-ui/src/api/mes/tm/` | 工装API全部删除 |
| `weimes-ui/src/api/mes/sales/` | 销售API全部删除 |
| `weimes-ui/src/api/mes/analytics/` | 空壳，后续重建 |
| `weimes-ui/src/views/mes/md/workstation*` | 工作站相关页面删除 |
| `weimes-ui/src/views/mes/md/product-bom*` | BOM相关页面删除 |
| `weimes-ui/src/views/mes/md/product-sop*` | SOP管理页面删除 |
| `weimes-ui/src/views/mes/md/product-sip*` | SIP管理页面删除 |
| `weimes-ui/src/views/mes/wm/rt-salse*` | 销售退货页面删除 |
| `weimes-ui/src/views/mes/wm/package*` | 包装页面删除 |
| `weimes-ui/src/views/mes/wm/product-produce*` | 生产入库页面删除 |
| `weimes-ui/src/views/mes/wm/item-consume*` | 物料消耗页面删除 |

### 3.3 数据库表删除清单

#### 3.3.1 完整删除的表（生产模块 — 16张）

```sql
-- ============================================================
-- 生产管理模块 (pro_) — 全部删除
-- ============================================================
DROP TABLE IF EXISTS pro_workorder;           -- 生产工单
DROP TABLE IF EXISTS pro_workorder_bom;        -- 工单BOM
DROP TABLE IF EXISTS pro_task;                 -- 生产任务
DROP TABLE IF EXISTS pro_task_issue;            -- 任务发料
DROP TABLE IF EXISTS pro_feedback;             -- 报工记录
DROP TABLE IF EXISTS pro_process;               -- 工序
DROP TABLE IF EXISTS pro_process_content;       -- 工序内容
DROP TABLE IF EXISTS pro_route;                 -- 工艺路线
DROP TABLE IF EXISTS pro_route_process;         -- 工艺路线-工序
DROP TABLE IF EXISTS pro_route_product;         -- 工艺路线-产品
DROP TABLE IF EXISTS pro_route_product_bom;     -- 工艺路线-产品BOM
DROP TABLE IF EXISTS pro_client_order;          -- 客户订单
DROP TABLE IF EXISTS pro_client_order_item;     -- 客户订单行
DROP TABLE IF EXISTS pro_trans_order;           -- 调拨单
DROP TABLE IF EXISTS pro_user_workstation;      -- 用户工作站
DROP TABLE IF EXISTS pro_workrecord;            -- 生产记录
```

#### 3.3.2 完整删除的表（排班模块 — 7张）

```sql
-- ============================================================
-- 排班日历模块 (cal_) — 全部删除
-- ============================================================
DROP TABLE IF EXISTS cal_holiday;              -- 节假日
DROP TABLE IF EXISTS cal_plan;                 -- 排班计划
DROP TABLE IF EXISTS cal_plan_team;            -- 排班计划-班组
DROP TABLE IF EXISTS cal_shift;                 -- 班次
DROP TABLE IF EXISTS cal_team;                  -- 班组
DROP TABLE IF EXISTS cal_team_member;           -- 班组成员
DROP TABLE IF EXISTS cal_teamshift;             -- 班组班次
```

#### 3.3.3 完整删除的表（工装模块 — 2张）

```sql
-- ============================================================
-- 工装管理模块 (tm_) — 全部删除
-- ============================================================
DROP TABLE IF EXISTS tm_tool;                   -- 工装
DROP TABLE IF EXISTS tm_tool_type;              -- 工装类型
```

#### 3.3.4 完整删除的表（会员模块 — 8张）

```sql
-- ============================================================
-- 会员模块 (huiyuan_) — 全部删除（与LIMS无关）
-- ============================================================
DROP TABLE IF EXISTS huiyuan_age;
DROP TABLE IF EXISTS huiyuan_fengongsi;
DROP TABLE IF EXISTS huiyuan_huoyuedu;
DROP TABLE IF EXISTS huiyuan_sex;
DROP TABLE IF EXISTS huiyuan_work;
DROP TABLE IF EXISTS huiyuan_wxtl;
DROP TABLE IF EXISTS huiyuan_wxtlshuliang;
DROP TABLE IF EXISTS huiyuan_xueli;
```

#### 3.3.5 部分删除的表（主数据模块）

```sql
-- ============================================================
-- 主数据模块 (md_) — 删除MES专用表，保留基础表
-- ============================================================
DROP TABLE IF EXISTS md_workstation;            -- 工作站（LIMS不需要）
DROP TABLE IF EXISTS md_workstation_machine;    -- 工作站-设备（LIMS不需要）
DROP TABLE IF EXISTS md_workstation_tool;       -- 工作站-工装（LIMS不需要）
DROP TABLE IF EXISTS md_workstation_worker;     -- 工作站-人员（LIMS不需要）
DROP TABLE IF EXISTS md_product_bom;            -- 产品BOM（LIMS不需要）
DROP TABLE IF EXISTS md_product_sop;            -- 产品SOP（改为文档管理）
DROP TABLE IF EXISTS md_product_sip;            -- 产品SIP（改为文档管理）
DROP TABLE IF EXISTS md_product_color;          -- 产品颜色（LIMS不需要）
DROP TABLE IF EXISTS md_produt_sop;             -- 产品SOP(拼写错误副本)

-- 保留以下表（后续改造）:
-- md_item          → 改造为 lims_product（产品/物料）
-- md_item_type     → 改造为 lims_product_type
-- md_vendor        → 改造为 lims_supplier（供应商）
-- md_client        → 改造为 lims_client（客户）
-- md_workshop      → 改造为 lims_lab（实验室）
-- md_unit_measure  → 保留，单位管理
```

#### 3.3.6 部分删除的表（仓库模块）

```sql
-- ============================================================
-- 仓库模块 (wm_) — 删除MES专用表
-- ============================================================
DROP TABLE IF EXISTS wm_rt_salse;               -- 销售退货
DROP TABLE IF EXISTS wm_rt_salse_line;          -- 销售退货行
DROP TABLE IF EXISTS wm_package;                -- 包装
DROP TABLE IF EXISTS wm_package_line;           -- 包装行
DROP TABLE IF EXISTS wm_product_produce;        -- 生产入库
DROP TABLE IF EXISTS wm_product_produce_line;   -- 生产入库存行
DROP TABLE IF EXISTS wm_item_consume;           -- 物料消耗
DROP TABLE IF EXISTS wm_item_consume_line;      -- 物料消耗行

-- 保留以下表（改造为样品管理）:
-- wm_item_recpt       → 改造为样品接收
-- wm_issue_header     → 改造为样品分发
-- wm_material_stock   → 改造为样品库存/留样
-- wm_warehouse        → 改造为存储位置
-- wm_storage_location → 保留，库位管理
-- wm_barcode_config   → 保留，条码配置
```

#### 3.3.7 部分删除的表（HRM模块）

```sql
-- ============================================================
-- HRM模块 (hrm_) — 删除不适用表
-- ============================================================
DROP TABLE IF EXISTS hrm_interview_record;      -- 面试记录（LIMS不需要）
DROP TABLE IF EXISTS hrm_attendance_records;    -- 考勤记录（LIMS不需要）
DROP TABLE IF EXISTS hrm_attendance_rules;       -- 考勤规则（LIMS不需要）
DROP TABLE IF EXISTS hrm_leave_records;          -- 请假记录（LIMS不需要）
DROP TABLE IF EXISTS hrm_overtime_records;       -- 加班记录（LIMS不需要）

-- 保留:
-- hrm_staff    → 改造为 lims_staff（检验人员）
-- hrm_rank    → 改造为 lims_position（岗位/职级）
```

#### 3.3.8 删除统计汇总

| 模块 | 删除表数 | 保留/改造表数 |
|------|---------|-------------|
| 生产管理 (pro_) | 16 | 0 |
| 排班日历 (cal_) | 7 | 0 |
| 工装管理 (tm_) | 2 | 0 |
| 会员模块 (huiyuan_) | 8 | 0 |
| 主数据 (md_) 部分 | 9 | 6 |
| 仓库 (wm_) 部分 | 8 | 6 |
| HRM (hrm_) 部分 | 5 | 2 |
| **合计** | **55** | **14** |

---

## 四、可复用模块改造方案

### 4.1 质量管理 qc → 检验管理 test

| 现有类 | 改造为 | 改造要点 |
|--------|-------|---------|
| `QcTemplate` | `LimsTestTemplate` | 增加模板类型(原料/成品/稳定性/环境)、版本管理、审批流 |
| `QcTemplateIndex` | `LimsTestTemplateItem` | 增加检验方法关联、修约规则、判定逻辑配置 |
| `QcTemplateProduct` | `LimsTestTemplateProduct` | 扩展为质量标准关联 |
| `QcIqc` | `LimsTestOrder` | 增加样品关联、多检测项、状态机驱动 |
| `QcIqcLine` | `LimsTestResult` | 增加原始值/计算值/报告值、OOS标记、仪器关联 |
| `QcIpqc` | `LimsProcessTestOrder` | 过程检验（保留但简化） |
| `QcOqc` | `LimsFinalTestOrder` | 成品检验（与IQC合并为统一检验订单） |
| `QcDefect` | `LimsOosRecord` | 深度改造为OOS/OOT调查 |
| `QcDefectRecord` | `LimsOosInvestigation` | OOS调查记录 |

### 4.2 设备管理 dv → 仪器管理 instrument

| 现有类 | 改造为 | 改造要点 |
|--------|-------|---------|
| `DvMachinery` | `LimsInstrument` | 增加资产编号、校准周期、IQ/OQ/PQ状态 |
| `DvMachineryType` | `LimsInstrumentType` | 增加、仪器分类(理化/微生物/分析) |
| `DvCheckPlan` | `LimsCalibrationPlan` | 改造为校准计划 |
| `DvCheckSubject` | `LimsCalibrationItem` | 校准项目 |
| `DvCheckMachinery` | `LimsCalibrationInstrument` | 校准计划-仪器关联 |
| `DvRepair` | `LimsInstrumentMaintenance` | 改造为维护保养 |

### 4.3 主数据 md → LIMS主数据

| 现有类 | 改造为 |
|--------|-------|
| `MdItem` | `LimsProduct` (产品/物料，增加药品分类、剂型、注册信息) |
| `MdItemType` | `LimsProductType` |
| `MdVendor` | `LimsSupplier` (增加资质、审计、评级) |
| `MdClient` | `LimsClient` (增加委托检验信息) |
| `MdWorkshop` | `LimsLab` (实验室，增加洁净度级别) |
| `MdUnitMeasure` | 保留不变 |

### 4.4 仓库管理 wm → 样品管理 sample

| 现有类 | 改造为 |
|--------|-------|
| `WmItemRecpt` | `LimsSampleReceive` (样品接收) |
| `WmIssueHeader` | `LimsSampleDistribute` (样品分发) |
| `WmMaterialStock` | `LimsSampleStock` (样品库存/留样) |
| `WmWarehouse` | `LimsStorageLocation` (存储位置) |
| `WmBarcodeConfig` | 保留，扩展为样品标签配置 |

---

## 五、合规基础层

### 5.1 审计追踪（全字段级）

```sql
-- ============================================================
-- 审计追踪表 — 满足21 CFR Part 11 / GMP计算机化系统附录
-- ============================================================
CREATE TABLE lims_audit_trail (
    audit_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_name        VARCHAR(100) NOT NULL COMMENT '变更表名',
    record_id         BIGINT NOT NULL COMMENT '变更记录ID',
    field_name        VARCHAR(100) COMMENT '变更字段',
    old_value         TEXT COMMENT '旧值',
    new_value         TEXT COMMENT '新值',
    operation_type    VARCHAR(20) NOT NULL COMMENT 'INSERT/UPDATE/DELETE',
    operator_id       BIGINT COMMENT '操作人ID',
    operator_name     VARCHAR(50) COMMENT '操作人',
    operation_time    DATETIME NOT NULL COMMENT '操作时间',
    reason            VARCHAR(500) COMMENT '变更原因(合规必填)',
    signature_id      BIGINT COMMENT '电子签名ID',
    ip_address        VARCHAR(50) COMMENT 'IP地址',
    application_name  VARCHAR(50) DEFAULT 'LIMS' COMMENT '应用标识',
    session_id        VARCHAR(100) COMMENT '会话ID',
    INDEX idx_table_record (table_name, record_id),
    INDEX idx_operation_time (operation_time),
    INDEX idx_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计追踪表(仅INSERT,不可UPDATE/DELETE)';
```

**AOP切面实现**:
```java
@Aspect
@Component
public class LimsAuditTrailAspect {
    @AfterReturning("execution(* com.wei.lims..service.impl.*ServiceImpl.insert*(..)) " +
                    "|| execution(* com.wei.lims..service.impl.*ServiceImpl.update*(..)) " +
                    "|| execution(* com.wei.lims..service.impl.*ServiceImpl.delete*(..))")
    public void auditTrail(JoinPoint joinPoint) {
        // 1. 获取操作类型
        // 2. 获取操作对象和字段变更(新旧值对比)
        // 3. 获取当前用户+IP
        // 4. 写入审计追踪表(仅INSERT)
    }
}
```

### 5.2 电子签名

```sql
-- ============================================================
-- 电子签名表 — 满足21 CFR Part 11
-- ============================================================
CREATE TABLE lims_electronic_signature (
    signature_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_type        VARCHAR(100) NOT NULL COMMENT '签名对象类型',
    record_id          BIGINT NOT NULL COMMENT '签名对象ID',
    signer_id          BIGINT NOT NULL COMMENT '签名人ID',
    signer_name        VARCHAR(50) COMMENT '签名人姓名',
    signature_meaning   VARCHAR(50) NOT NULL COMMENT '签名含义: DRAFTED/REVIEWED/APPROVED/REJECTED/VERIFIED',
    signature_time      DATETIME NOT NULL COMMENT '签名时间',
    reason             VARCHAR(500) COMMENT '签名理由',
    is_valid           TINYINT DEFAULT 1 COMMENT '签名是否有效',
    ip_address         VARCHAR(50) COMMENT 'IP地址',
    session_id         VARCHAR(100) COMMENT '会话ID',
    UNIQUE KEY uk_record_signer (record_type, record_id, signer_id, signature_meaning)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子签名表';
```

### 5.3 数据完整性校验规则

| 原则 | 要求 | 系统实现 |
|------|------|---------|
| **A**ttributable（可归因） | 所有操作可追溯到人 | createBy/updateBy + 审计追踪 |
| **L**egible（清晰可读） | 数据展示清晰 | 前端展示优化+打印 |
| **C**ontemporaneous（同步性） | 实时记录 | 服务端统一时间戳，精确到秒 |
| **O**riginal（原始性） | 原始记录不可修改 | 变更走修订流程，原记录保留 |
| **A**ccurate（准确性） | 数据准确 | 关键数据校验+计算结果不可手动覆盖 |
| **Complete（完整） | 数据不缺失 | 必填校验+默认值 |
| **Consistent（一致） | 数据一致 | 事务+外键约束 |
| **Enduring（持久） | 数据长期保存 | 归档策略+备份 |
| **Available（可用） | 数据可检索 | 索引+全文搜索 |

---

## 六、工作流引擎

### 6.1 Flowable集成

```sql
-- ============================================================
-- 流程定义表（业务级）
-- ============================================================
CREATE TABLE lims_process_definition (
    proc_def_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    proc_key          VARCHAR(100) NOT NULL UNIQUE COMMENT '流程KEY',
    proc_name         VARCHAR(200) COMMENT '流程名称',
    proc_type         VARCHAR(50) COMMENT 'TEST_ORDER/SAMPLE/INSTRUMENT/STABILITY/DEVIATION/CHANGE/COMPLAINT',
    bpmn_xml          LONGTEXT COMMENT 'BPMN2.0 XML',
    version           INT DEFAULT 1,
    status            VARCHAR(20) COMMENT '草稿/生效/停用',
    effective_date    DATE,
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义表';

-- ============================================================
-- 流程审批记录（补充审计追踪）
-- ============================================================
CREATE TABLE lims_process_approval (
    approval_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    proc_inst_id      VARCHAR(64) COMMENT 'Flowable流程实例ID',
    business_type     VARCHAR(50) COMMENT '业务类型',
    business_id       BIGINT COMMENT '业务ID',
    task_name         VARCHAR(200) COMMENT '当前节点名称',
    assignee_id       BIGINT COMMENT '审批人ID',
    assignee_name     VARCHAR(50) COMMENT '审批人',
    action            VARCHAR(20) COMMENT 'APPROVE/REJECT/DELEGATE/RETURN',
    comment           TEXT COMMENT '审批意见',
    signature_id      BIGINT COMMENT '电子签名ID',
    approval_time     DATETIME,
    duration_ms       BIGINT COMMENT '审批耗时(毫秒)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程审批记录';
```

### 6.2 LIMS核心业务流程定义

| 业务 | 流程KEY | 状态流转 |
|------|---------|---------|
| 检验订单 | `lims_test_order` | 起草→提交→分配检测人→录入结果→复核→批准→报告签发 |
| 稳定性方案 | `lims_stability` | 起草→审核→批准→执行→各时间点检测→报告→关闭 |
| OOS调查 | `lims_oos` | 发现→Phase I调查→Phase II调查(如需)→CAPA→关闭 |
| 偏差处理 | `lims_deviation` | 发现→分类→调查→CAPA→效果确认→关闭 |
| 变更控制 | `lims_change` | 申请→评估→审批→执行→效果确认→关闭 |
| 仪器校准 | `lims_calibration` | 计划→执行→结果评价→批准 |
| 供应商审计 | `lims_supplier_audit` | 计划→执行→报告→CAPA→关闭 |
| 批放行 | `lims_batch_release` | 检查→QP审核→放行/拒绝 |
| 客户投诉 | `lims_complaint` | 登记→调查→处理→CAPA→关闭 |
| CAPA | `lims_capa` | 提出→审批→执行→效果确认→关闭 |
| 文档审批 | `lims_document` | 起草→审核→批准→生效→回顾→废止 |
| 伦理审查 | `lims_ethics_review` | 提交→初审→伦理委员会审查→批准/修改后批准/不批准 |
| 临床样本检测 | `lims_clinical_sample` | 接收→知情同意验证→检测→报告→归档 |

---

## 七、核心业务模块

### 7.1 样品管理

```sql
-- ============================================================
-- 样品主表
-- ============================================================
CREATE TABLE lims_sample (
    sample_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_code       VARCHAR(50) NOT NULL UNIQUE COMMENT '样品编号(自动生成)',
    sample_name       VARCHAR(200) COMMENT '样品名称',
    sample_type       VARCHAR(30) NOT NULL COMMENT '原料/中间体/成品/稳定性/环境/水系统/临床',
    sample_source     VARCHAR(30) COMMENT '供应商/生产车间/研发/客户委托/临床',
    batch_no          VARCHAR(50) COMMENT '批号(GMP核心)',
    product_id        BIGINT COMMENT '关联产品',
    product_code      VARCHAR(50),
    product_name      VARCHAR(200),
    vendor_id         BIGINT COMMENT '供应商ID',
    vendor_code       VARCHAR(50),
    vendor_name       VARCHAR(200),
    vendor_batch      VARCHAR(50) COMMENT '供应商批号',
    specification     VARCHAR(200) COMMENT '规格型号',
    quantity_received DECIMAL(18,4) COMMENT '接收数量',
    quantity_remaining DECIMAL(18,4) COMMENT '剩余数量',
    unit_of_measure   VARCHAR(20) COMMENT '单位',
    expire_date       DATE COMMENT '效期',
    storage_condition VARCHAR(100) COMMENT '存储条件(2-8°C/室温/避光等)',
    storage_location  VARCHAR(200) COMMENT '存储位置',
    priority          VARCHAR(20) DEFAULT 'NORMAL' COMMENT 'NORMAL/URGENT/EMERGENCY',
    receive_date      DATE COMMENT '接收日期',
    sampler_id        BIGINT COMMENT '采样人',
    inspector_id      BIGINT COMMENT '检验人',
    specification_id  BIGINT COMMENT '关联质量标准',
    test_template_id  BIGINT COMMENT '关联检验模板',
    status            VARCHAR(20) COMMENT '待接收/已接收/检测中/检测完成/留样/已处置',
    proc_inst_id      VARCHAR(64) COMMENT '工作流实例ID',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT,
    attr1             VARCHAR(200),
    attr2             VARCHAR(200),
    attr3             BIGINT,
    attr4             BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='样品主表';

-- ============================================================
-- 样品-检测项关联
-- ============================================================
CREATE TABLE lims_sample_test_item (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id         BIGINT NOT NULL COMMENT '样品ID',
    test_item_id      BIGINT COMMENT '检测项ID',
    test_item_code    VARCHAR(50) COMMENT '检测项编码',
    test_item_name    VARCHAR(200) COMMENT '检测项名称',
    method_id         BIGINT COMMENT '检验方法',
    specification_line_id BIGINT COMMENT '质量标准行ID',
    is_mandatory      TINYINT DEFAULT 1 COMMENT '是否必检',
    status            VARCHAR(20) COMMENT '待检测/检测中/已完成'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='样品-检测项关联';

-- ============================================================
-- 样品状态变更日志
-- ============================================================
CREATE TABLE lims_sample_status_log (
    log_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id         BIGINT NOT NULL,
    from_status       VARCHAR(20),
    to_status         VARCHAR(20),
    changed_by        BIGINT,
    changed_time      DATETIME,
    reason            VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='样品状态变更日志';

-- ============================================================
-- 样品处置记录
-- ============================================================
CREATE TABLE lims_sample_dispose (
    dispose_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id         BIGINT NOT NULL,
    dispose_type      VARCHAR(20) COMMENT '正常销毁/退回/提前取样',
    dispose_quantity  DECIMAL(18,4),
    dispose_date      DATE,
    disposed_by       BIGINT,
    approved_by       BIGINT,
    reason            VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='样品处置记录';
```

### 7.2 检验管理

```sql
-- ============================================================
-- 质量标准（规格）
-- ============================================================
CREATE TABLE lims_specification (
    spec_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    spec_code         VARCHAR(50) NOT NULL COMMENT '标准编号',
    spec_name         VARCHAR(200) NOT NULL COMMENT '标准名称',
    product_id        BIGINT COMMENT '关联产品',
    spec_type         VARCHAR(20) COMMENT '药典标准/内控标准/注册标准/客户标准',
    spec_version      VARCHAR(20) COMMENT '版本号',
    pharmacopoeia_ref VARCHAR(200) COMMENT '药典引用(ChP/USP/EP/BP)',
    effective_date    DATE COMMENT '生效日期',
    expiry_date       DATE COMMENT '失效日期',
    status            VARCHAR(20) COMMENT '草稿/生效/停用',
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量标准';

-- ============================================================
-- 质量标准行（检测项+接受标准）
-- ============================================================
CREATE TABLE lims_specification_line (
    line_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    spec_id           BIGINT NOT NULL,
    test_item_id      BIGINT COMMENT '检测项ID',
    test_item_name    VARCHAR(200) COMMENT '检测项名称',
    method_id         BIGINT COMMENT '检验方法',
    specification_val VARCHAR(200) COMMENT '规格值(如: 95.0%~105.0%)',
    lower_limit       DECIMAL(18,6) COMMENT '下限',
    upper_limit       DECIMAL(18,6) COMMENT '上限',
    unit              VARCHAR(20) COMMENT '单位',
    sequence          INT COMMENT '排序',
    is_mandatory      TINYINT DEFAULT 1 COMMENT '是否必检',
    rounding_rule     VARCHAR(50) COMMENT '修约规则'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量标准行';

-- ============================================================
-- 检验方法
-- ============================================================
CREATE TABLE lims_test_method (
    method_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    method_code       VARCHAR(50) NOT NULL COMMENT '方法编号',
    method_name       VARCHAR(200) NOT NULL COMMENT '方法名称',
    method_version    VARCHAR(20) COMMENT '版本号',
    pharmacopoeia_ref VARCHAR(200) COMMENT '药典引用(ChP/USP/EP/BP)',
    method_type       VARCHAR(50) COMMENT '理化/微生物/仪器分析',
    method_category   VARCHAR(50) COMMENT '鉴别/检查/含量测定/微生物',
    validation_status VARCHAR(20) COMMENT '验证状态: 未验证/验证中/已验证',
    validated_date    DATE COMMENT '验证日期',
    approved_by       BIGINT COMMENT '批准人',
    status            VARCHAR(20) COMMENT '草稿/生效/停用',
    effective_date    DATE,
    expiry_date       DATE,
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验方法';

-- ============================================================
-- 检验订单
-- ============================================================
CREATE TABLE lims_test_order (
    test_order_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_order_code   VARCHAR(50) NOT NULL UNIQUE COMMENT '检验单编号',
    test_order_name   VARCHAR(200) COMMENT '检验单名称',
    sample_id         BIGINT NOT NULL COMMENT '关联样品',
    sample_code       VARCHAR(50),
    specification_id  BIGINT COMMENT '质量标准ID',
    test_template_id  BIGINT COMMENT '检验模板ID',
    source_doc_type   VARCHAR(30) COMMENT '来源类型: IQC/IPQC/OQC/STABILITY/ENV/WATER/CLINICAL',
    source_doc_id     BIGINT COMMENT '来源单据ID',
    source_doc_code   VARCHAR(50) COMMENT '来源单据编号',
    test_type         VARCHAR(30) COMMENT '原料检验/过程检验/成品检验/稳定性/环境监测',
    priority          VARCHAR(20) DEFAULT 'NORMAL' COMMENT 'NORMAL/URGENT/EMERGENCY',
    planned_start_date DATE COMMENT '计划开始日期',
    planned_end_date   DATE COMMENT '计划完成日期',
    actual_start_date  DATE COMMENT '实际开始日期',
    actual_end_date    DATE COMMENT '实际完成日期',
    tester_id         BIGINT COMMENT '主检验人',
    reviewer_id       BIGINT COMMENT '复核人',
    approver_id       BIGINT COMMENT '批准人',
    check_result      VARCHAR(20) COMMENT '合格/不合格/N/A',
    conclusion        TEXT COMMENT '检验结论',
    report_no         VARCHAR(50) COMMENT '报告编号',
    proc_inst_id      VARCHAR(64) COMMENT '工作流实例ID',
    status            VARCHAR(20) COMMENT '草稿/已提交/检测中/已检测/复核中/已复核/审批中/已批准/已报告',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验订单';

-- ============================================================
-- 检验结果（核心表）
-- ============================================================
CREATE TABLE lims_test_result (
    result_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_order_id     BIGINT NOT NULL COMMENT '检验订单',
    spec_line_id      BIGINT COMMENT '质量标准行',
    test_item_name    VARCHAR(200) COMMENT '检测项名称',
    method_id         BIGINT COMMENT '检验方法',
    test_item_type    VARCHAR(30) COMMENT '定性/定量/描述',
    original_value    VARCHAR(200) COMMENT '原始值(手工录入)',
    instrument_value  VARCHAR(200) COMMENT '仪器采集值',
    calculated_value  DECIMAL(18,6) COMMENT '计算值',
    reported_value    VARCHAR(200) COMMENT '报告值(修约后)',
    rounding_rule     VARCHAR(50) COMMENT '修约规则: GB/T 8170-2008',
    formula           VARCHAR(500) COMMENT '计算公式',
    lower_limit       DECIMAL(18,6) COMMENT '规格下限',
    upper_limit       DECIMAL(18,6) COMMENT '规格上限',
    specification_val VARCHAR(200) COMMENT '规格描述',
    result_conclusion VARCHAR(20) COMMENT 'PASS/FAIL/N/A',
    is_oos            TINYINT DEFAULT 0 COMMENT '是否超标',
    is_oot            TINYINT DEFAULT 0 COMMENT '是否超趋势',
    tester_id         BIGINT COMMENT '检验人',
    test_date         DATE COMMENT '检验日期',
    reviewer_id       BIGINT COMMENT '复核人',
    review_date       DATE COMMENT '复核日期',
    instrument_id     BIGINT COMMENT '使用仪器',
    reagent_lot       VARCHAR(50) COMMENT '试剂批号',
    standard_lot      VARCHAR(50) COMMENT '标准品批号',
    test_condition    TEXT COMMENT '检测条件(温度/湿度等)',
    remark            TEXT,
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验结果';

-- ============================================================
-- 采样计划
-- ============================================================
CREATE TABLE lims_sampling_plan (
    plan_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_code         VARCHAR(50) NOT NULL,
    plan_name         VARCHAR(200),
    plan_type         VARCHAR(30) COMMENT '常规/验证/投诉/稳定性',
    sample_type       VARCHAR(30) COMMENT '原料/成品/环境/水系统',
    sampling_points   TEXT COMMENT 'JSON: 采样点配置',
    frequency         VARCHAR(50) COMMENT '采样频率',
    sample_quantity   DECIMAL(18,4) COMMENT '采样数量',
    sampler_id        BIGINT COMMENT '采样人',
    next_sampling_date DATE,
    status            VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采样计划';
```

### 7.3 留样管理

```sql
-- ============================================================
-- 留样记录
-- ============================================================
CREATE TABLE lims_retain_sample (
    retain_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    retain_code        VARCHAR(50) NOT NULL COMMENT '留样编号',
    sample_id          BIGINT COMMENT '关联样品ID',
    product_name       VARCHAR(200),
    batch_no           VARCHAR(50) COMMENT '批号',
    retain_type        VARCHAR(20) COMMENT '常规留样/稳定性留样/投诉留样',
    retain_quantity    DECIMAL(18,4) COMMENT '留样数量',
    remain_quantity    DECIMAL(18,4) COMMENT '剩余数量',
    storage_location   VARCHAR(200) COMMENT '存放位置',
    storage_condition  VARCHAR(100) COMMENT '存储条件',
    retain_date        DATE COMMENT '留样日期',
    expire_date        DATE COMMENT '效期/留样截止日期',
    observation_cycle  VARCHAR(50) COMMENT '观察周期',
    last_observation   DATE COMMENT '上次观察日期',
    next_observation   DATE COMMENT '下次观察日期',
    observation_result VARCHAR(200) COMMENT '观察结果',
    dispose_type       VARCHAR(20) COMMENT '正常销毁/提前取样/退回',
    dispose_date       DATE,
    dispose_approver   BIGINT,
    status             VARCHAR(20) COMMENT '留样中/观察中/已处置'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留样记录';
```

---

## 八、质量管理模块

### 8.1 OOS/OOT管理

```sql
-- ============================================================
-- OOS/OOT调查记录
-- ============================================================
CREATE TABLE lims_oos_investigation (
    investigation_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    investigation_no   VARCHAR(50) NOT NULL UNIQUE COMMENT '调查编号',
    test_result_id     BIGINT NOT NULL COMMENT '关联检验结果',
    test_order_id      BIGINT COMMENT '关联检验订单',
    type               VARCHAR(20) COMMENT 'OOS/OOT',
    phase              VARCHAR(20) COMMENT 'PHASE_I/PHASE_II',
    oos_value          VARCHAR(200) COMMENT '超标值',
    specification_limit VARCHAR(200) COMMENT '规格限',
    lab_investigation  TEXT COMMENT '实验室调查结论',
    is_lab_cause       TINYINT COMMENT '是否实验室原因',
    lab_error_type     VARCHAR(50) COMMENT '实验室错误类型',
    full_investigation TEXT COMMENT '全面调查描述',
    root_cause         TEXT COMMENT '根本原因',
    capa_ids           VARCHAR(200) COMMENT '关联CAPA',
    corrective_action  TEXT COMMENT '纠正措施',
    original_result_valid TINYINT COMMENT '原始结果是否有效',
    retest_result      VARCHAR(200) COMMENT '复测结果',
    conclusion         VARCHAR(50) COMMENT '调查结论',
    investigator_id    BIGINT COMMENT '调查人',
    approver_id       BIGINT COMMENT '批准人',
    close_date         DATE,
    status             VARCHAR(20) COMMENT '调查中/Phase I/Phase II/CAPA中/已关闭',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OOS/OOT调查';
```

### 8.2 偏差管理

```sql
-- ============================================================
-- 偏差记录
-- ============================================================
CREATE TABLE lims_deviation (
    deviation_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    deviation_code     VARCHAR(50) NOT NULL UNIQUE COMMENT '偏差编号',
    deviation_title    VARCHAR(500) NOT NULL COMMENT '偏差标题',
    deviation_type     VARCHAR(20) COMMENT '计划内偏差/计划外偏差',
    deviation_category VARCHAR(20) COMMENT '工艺/设备/物料/环境/人员/文件/其他',
    severity_level     VARCHAR(20) COMMENT '微小/主要/严重',
    discovery_date     DATE NOT NULL COMMENT '发现日期',
    discovery_source  VARCHAR(50) COMMENT '检验/生产/投诉/审计/自检',
    affected_product   VARCHAR(500) COMMENT '涉及产品',
    affected_batch     VARCHAR(200) COMMENT '涉及批号',
    description        TEXT NOT NULL COMMENT '偏差描述',
    immediate_action   TEXT COMMENT '即时措施',
    is_capa_needed     TINYINT DEFAULT 0 COMMENT '是否需要CAPA',
    investigation_result TEXT COMMENT '调查结论',
    root_cause         TEXT COMMENT '根本原因',
    capa_ids           VARCHAR(200) COMMENT '关联CAPA',
    effectiveness_review TEXT COMMENT '效果确认',
    owner_id           BIGINT COMMENT '责任人',
    due_date           DATE COMMENT '计划完成日期',
    close_date         DATE COMMENT '实际关闭日期',
    proc_inst_id       VARCHAR(64),
    status             VARCHAR(20) COMMENT '草稿/调查中/CAPA执行中/效果确认中/已关闭',
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='偏差管理';

-- ============================================================
-- 偏差影响评估
-- ============================================================
CREATE TABLE lims_deviation_impact (
    impact_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    deviation_id       BIGINT NOT NULL,
    impact_area        VARCHAR(50) COMMENT '产品质量/验证状态/注册/安全性',
    impact_description TEXT,
    impact_level       VARCHAR(20) COMMENT '无影响/轻微/中等/严重',
    assessed_by        BIGINT,
    assessed_date       DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='偏差影响评估';
```

### 8.3 CAPA管理

```sql
-- ============================================================
-- CAPA记录
-- ============================================================
CREATE TABLE lims_capa (
    capa_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    capa_code          VARCHAR(50) NOT NULL UNIQUE COMMENT 'CAPA编号',
    capa_type          VARCHAR(20) COMMENT 'CA/PA/CAPA',
    source_type        VARCHAR(50) COMMENT '来源: 偏差/OOS/投诉/审计/自检/变更',
    source_code        VARCHAR(50) COMMENT '来源编号',
    description        TEXT NOT NULL COMMENT 'CAPA描述',
    root_cause         TEXT COMMENT '根本原因',
    corrective_action  TEXT COMMENT '纠正措施',
    preventive_action  TEXT COMMENT '预防措施',
    responsible_id     BIGINT COMMENT '责任人',
    due_date           DATE COMMENT '计划完成日期',
    completion_date    DATE COMMENT '实际完成日期',
    effectiveness_result TEXT COMMENT '有效性确认结果',
    effectiveness_date DATE COMMENT '有效性确认日期',
    status             VARCHAR(20) COMMENT '草稿/执行中/已完成/已关闭/逾期',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CAPA管理';

-- ============================================================
-- CAPA任务分解
-- ============================================================
CREATE TABLE lims_capa_task (
    task_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    capa_id            BIGINT NOT NULL,
    task_description   TEXT NOT NULL,
    assignee_id        BIGINT,
    due_date           DATE,
    completion_date    DATE,
    status             VARCHAR(20) COMMENT '待执行/执行中/已完成'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CAPA任务';
```

### 8.4 变更控制

```sql
-- ============================================================
-- 变更控制
-- ============================================================
CREATE TABLE lims_change_control (
    change_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    change_code        VARCHAR(50) NOT NULL UNIQUE COMMENT '变更编号',
    change_title       VARCHAR(500) NOT NULL,
    change_type        VARCHAR(30) COMMENT '重大/次要/临时/紧急',
    change_category    VARCHAR(30) COMMENT '工艺/设备/物料/方法/环境/文件/计算机系统',
    change_description TEXT NOT NULL,
    current_state      TEXT COMMENT '现有状态',
    proposed_change    TEXT COMMENT '拟变更内容',
    change_reason      TEXT COMMENT '变更理由',
    risk_assessment    TEXT COMMENT '风险评估',
    impact_analysis    TEXT COMMENT '影响分析',
    implementation_plan TEXT COMMENT '实施计划',
    training_needed    TINYINT DEFAULT 0 COMMENT '是否需要培训',
    revalidation_needed TINYINT DEFAULT 0 COMMENT '是否需要再验证',
    regulatory_impact  VARCHAR(20) COMMENT '注册影响: 无/需备案-需批准',
    requester_id       BIGINT,
    approver_id        BIGINT,
    effective_date     DATE,
    status             VARCHAR(20) COMMENT '申请中/评估中/审批中/执行中/效果确认/已关闭',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更控制';

-- ============================================================
-- 变更影响评估行
-- ============================================================
CREATE TABLE lims_change_impact (
    impact_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    change_id          BIGINT NOT NULL,
    impact_area        VARCHAR(50) COMMENT '产品/验证/注册/文件/培训/设备',
    impact_detail      TEXT,
    risk_level         VARCHAR(20) COMMENT '高/中/低',
    mitigation_plan    TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更影响评估';
```

### 8.5 批放行

```sql
-- ============================================================
-- 批放行记录
-- ============================================================
CREATE TABLE lims_batch_release (
    release_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    release_code       VARCHAR(50) NOT NULL UNIQUE COMMENT '放行编号',
    product_name       VARCHAR(200),
    product_code       VARCHAR(50),
    batch_no           VARCHAR(50) NOT NULL,
    batch_size         DECIMAL(18,4) COMMENT '批量',
    manufacture_date   DATE COMMENT '生产日期',
    test_order_ids     VARCHAR(500) COMMENT '关联检验单',
    raw_material_ok    TINYINT COMMENT '原料检验合格',
    process_ok         TINYINT COMMENT '过程检验合格',
    finished_product_ok TINYINT COMMENT '成品检验合格',
    packaging_ok       TINYINT COMMENT '包装检验合格',
    deviation_ok       TINYINT COMMENT '偏差已关闭',
    capa_ok            TINYINT COMMENT 'CAPA已关闭',
    change_ok          TINYINT COMMENT '变更已关闭',
    batch_record_ok    TINYINT COMMENT '批记录审核完成',
    stability_ok       TINYINT COMMENT '稳定性考察正常',
    release_decision   VARCHAR(20) COMMENT '放行/有条件放行/拒绝放行',
    release_condition  TEXT COMMENT '有条件放行条件',
    qp_id              BIGINT COMMENT '放行QP(质量受权人)',
    qp_name            VARCHAR(50),
    release_date       DATE,
    valid_until        DATE COMMENT '放行有效期',
    status             VARCHAR(20) COMMENT '检查中/待放行/已放行/已拒绝',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批放行';

-- ============================================================
-- 批放行检查清单
-- ============================================================
CREATE TABLE lims_batch_release_check (
    check_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    release_id         BIGINT NOT NULL,
    check_item         VARCHAR(200),
    check_result       VARCHAR(20) COMMENT '通过/不通过/N/A',
    check_remark       TEXT,
    checked_by         BIGINT,
    checked_date       DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批放行检查清单';
```

### 8.6 客户投诉与召回

```sql
-- ============================================================
-- 客户投诉
-- ============================================================
CREATE TABLE lims_complaint (
    complaint_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    complaint_code     VARCHAR(50) NOT NULL UNIQUE,
    complaint_title    VARCHAR(500),
    customer_id        BIGINT,
    customer_name      VARCHAR(200),
    complaint_date     DATE,
    received_by       BIGINT,
    product_name       VARCHAR(200),
    batch_no           VARCHAR(50),
    complaint_category VARCHAR(30) COMMENT '质量/包装/标签/交付/其他',
    complaint_detail   TEXT,
    severity           VARCHAR(20) COMMENT '严重/主要/轻微',
    investigation_result TEXT,
    root_cause         TEXT,
    corrective_action  TEXT,
    customer_response  TEXT,
    is_recall_needed  TINYINT DEFAULT 0,
    recall_id          BIGINT,
    capa_ids           VARCHAR(200),
    status             VARCHAR(20) COMMENT '登记中/调查中/处理中/已关闭',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户投诉';

-- ============================================================
-- 产品召回
-- ============================================================
CREATE TABLE lims_recall (
    recall_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    recall_code        VARCHAR(50) NOT NULL UNIQUE,
    recall_level       VARCHAR(20) COMMENT '一级/二级/三级',
    product_name       VARCHAR(200),
    batch_no           VARCHAR(50),
    recall_reason      TEXT,
    recall_scope       TEXT,
    recall_plan        TEXT,
    progress_description TEXT,
    effectiveness_check TEXT,
    authority_notified TINYINT COMMENT '是否通知药监部门',
    start_date         DATE,
    close_date         DATE,
    status             VARCHAR(20),
    proc_inst_id       VARCHAR(64)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品召回';
```

### 8.7 年度产品质量回顾(APQR)

```sql
-- ============================================================
-- 年度产品质量回顾
-- ============================================================
CREATE TABLE lims_apqr (
    apqr_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    apqr_code          VARCHAR(50) NOT NULL UNIQUE,
    product_name       VARCHAR(200),
    product_code       VARCHAR(50),
    review_year        INT,
    review_period_start DATE,
    review_period_end   DATE,
    total_batches      INT,
    passed_batches     INT,
    failed_batches     INT,
    yield_summary      TEXT COMMENT '收率汇总',
    test_result_summary TEXT COMMENT '检验结果趋势',
    deviation_summary  TEXT COMMENT '偏差汇总',
    complaint_summary  TEXT COMMENT '投诉汇总',
    capa_summary       TEXT COMMENT 'CAPA汇总',
    change_summary     TEXT COMMENT '变更汇总',
    oos_summary        TEXT COMMENT 'OOS汇总',
    stability_summary  TEXT COMMENT '稳定性汇总',
    return_summary     TEXT COMMENT '退货汇总',
    conclusion         TEXT COMMENT '回顾结论',
    recommendation    TEXT COMMENT '改进建议',
    prepared_by        BIGINT,
    reviewed_by        BIGINT,
    approved_by        BIGINT,
    status             VARCHAR(20),
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年度产品质量回顾';
```

---

## 九、资源管理模块

### 9.1 仪器管理

```sql
-- ============================================================
-- 仪器台账（改造自 dv_machinery）
-- ============================================================
CREATE TABLE lims_instrument (
    instrument_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    instrument_code    VARCHAR(50) NOT NULL UNIQUE COMMENT '仪器编号',
    instrument_name    VARCHAR(200) NOT NULL COMMENT '仪器名称',
    instrument_type_id BIGINT COMMENT '仪器类型ID',
    instrument_type_code VARCHAR(50),
    instrument_type_name VARCHAR(200),
    brand              VARCHAR(100) COMMENT '品牌',
    model              VARCHAR(100) COMMENT '规格型号',
    serial_number      VARCHAR(100) COMMENT '出厂编号',
    asset_number       VARCHAR(50) COMMENT '资产编号',
    manufacturer       VARCHAR(200) COMMENT '生产厂家',
    lab_id             BIGINT COMMENT '所属实验室',
    lab_name           VARCHAR(200),
    location           VARCHAR(200) COMMENT '安装位置',
    purchase_date      DATE COMMENT '购置日期',
    iq_status          VARCHAR(20) COMMENT 'IQ状态',
    oq_status          VARCHAR(20) COMMENT 'OQ状态',
    pq_status          VARCHAR(20) COMMENT 'PQ状态',
    calibration_cycle  INT COMMENT '校准周期(天)',
    last_calibration   DATE COMMENT '上次校准日期',
    next_calibration   DATE COMMENT '下次校准日期',
    calibration_status VARCHAR(20) COMMENT '校准状态: 合格/不合格/待校准/超期',
    maintenance_cycle  INT COMMENT '维护周期(天)',
    last_maintenance   DATE COMMENT '上次维护日期',
    next_maintenance   DATE COMMENT '下次维护日期',
    interface_type     VARCHAR(30) COMMENT '接口类型: 无/文件/串口/网络/数据库',
    status             VARCHAR(20) COMMENT '在用/停用/报废/维修中',
    create_by          VARCHAR(64),
    create_time        DATETIME,
    update_by          VARCHAR(64),
    update_time        DATETIME,
    remark             TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器台账';

-- ============================================================
-- 仪器类型
-- ============================================================
CREATE TABLE lims_instrument_type (
    type_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_code          VARCHAR(50) NOT NULL,
    type_name          VARCHAR(200) NOT NULL,
    category           VARCHAR(30) COMMENT '理化/微生物/分析天平/色谱/光谱/其他',
    parent_id          BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器类型';

-- ============================================================
-- 仪器校准记录
-- ============================================================
CREATE TABLE lims_instrument_calibration (
    calibration_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    instrument_id      BIGINT NOT NULL,
    calibration_code   VARCHAR(50) COMMENT '校准证书编号',
    calibration_type   VARCHAR(20) COMMENT '内部校准/外部校准',
    calibration_org    VARCHAR(200) COMMENT '校准机构',
    calibration_date   DATE COMMENT '校准日期',
    next_calibration   DATE COMMENT '下次校准日期',
    calibration_result VARCHAR(20) COMMENT '合格/不合格/限用',
    certificate_no     VARCHAR(100) COMMENT '证书编号',
    certificate_file   VARCHAR(500) COMMENT '证书附件',
    calibrated_by      BIGINT COMMENT '校准人',
    approved_by        BIGINT COMMENT '批准人',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器校准记录';

-- ============================================================
-- 仪器维护保养记录
-- ============================================================
CREATE TABLE lims_instrument_maintenance (
    maintenance_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    instrument_id      BIGINT NOT NULL,
    maintenance_type   VARCHAR(20) COMMENT '预防性维护/纠正性维护',
    maintenance_date   DATE,
    performed_by       BIGINT,
    description        TEXT,
    status             VARCHAR(20) COMMENT '计划中/执行中/完成'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器维护保养';

-- ============================================================
-- 仪器预约
-- ============================================================
CREATE TABLE lims_instrument_reservation (
    reservation_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    instrument_id      BIGINT NOT NULL,
    test_order_id      BIGINT,
    tester_id          BIGINT,
    reserved_date      DATE,
    start_time        TIME,
    end_time          TIME,
    purpose           VARCHAR(200),
    status            VARCHAR(20) COMMENT '已预约/使用中/已完成/已取消'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器预约';
```

### 9.2 试剂/标准品/培养基/菌种管理

```sql
-- ============================================================
-- 试剂台账
-- ============================================================
CREATE TABLE lims_reagent (
    reagent_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    reagent_code       VARCHAR(50) NOT NULL,
    reagent_name       VARCHAR(200) NOT NULL,
    reagent_type       VARCHAR(20) COMMENT '试剂/标准品/对照品/内标物',
    cas_number         VARCHAR(50) COMMENT 'CAS号',
    manufacturer       VARCHAR(200),
    hazard_level       VARCHAR(20) COMMENT '危险等级',
    storage_condition  VARCHAR(100) COMMENT '存储条件',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试剂台账';

-- ============================================================
-- 试剂入库（批号管理）
-- ============================================================
CREATE TABLE lims_reagent_lot (
    lot_id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    reagent_id         BIGINT NOT NULL,
    lot_number         VARCHAR(50) COMMENT '批号',
    received_date      DATE,
    expire_date        DATE COMMENT '效期',
    quantity           DECIMAL(18,4),
    remaining          DECIMAL(18,4),
    coa_file           VARCHAR(500) COMMENT 'CoA文件',
    status             VARCHAR(20) COMMENT '在用/过期/作废'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试剂批号管理';

-- ============================================================
-- 标准品/对照品台账
-- ============================================================
CREATE TABLE lims_reference_standard (
    standard_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    standard_code      VARCHAR(50) NOT NULL,
    standard_name      VARCHAR(200) NOT NULL,
    standard_type      VARCHAR(20) COMMENT '一级标准品/二级标准品/工作标准品/对照品/杂质对照品',
    source             VARCHAR(100) COMMENT 'USP/EP/BP/ChP/中检院/自制',
    catalog_number     VARCHAR(100),
    lot_number         VARCHAR(50),
    potency            DECIMAL(10,4) COMMENT '效价/含量',
    potency_unit       VARCHAR(20),
    received_date      DATE,
    opened_date        DATE,
    expire_date        DATE,
    storage_condition  VARCHAR(100),
    storage_location   VARCHAR(200),
    remaining_quantity DECIMAL(18,4),
    unit               VARCHAR(20),
    qualification_status VARCHAR(20) COMMENT '待标定/已标定/标定中',
    qualification_date DATE,
    next_qualification DATE,
    coa_file           VARCHAR(500),
    structure_file     VARCHAR(500),
    cas_number         VARCHAR(50),
    status             VARCHAR(20) COMMENT '在用/过期/耗尽/作废'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标准品/对照品';

-- ============================================================
-- 标准品标定记录
-- ============================================================
CREATE TABLE lims_standard_qualification (
    qual_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    standard_id        BIGINT NOT NULL,
    qual_type          VARCHAR(20) COMMENT '初次标定/复标',
    qual_date          DATE,
    qual_method        VARCHAR(200),
    test_result        TEXT,
    qualified_conclusion VARCHAR(20),
    qualified_by       BIGINT,
    approved_by       BIGINT,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标准品标定记录';

-- ============================================================
-- 标准品使用记录
-- ============================================================
CREATE TABLE lims_standard_usage (
    usage_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    standard_id        BIGINT NOT NULL,
    used_date          DATE,
    used_by            BIGINT,
    test_order_id      BIGINT,
    quantity_used      DECIMAL(18,4),
    remaining_after    DECIMAL(18,4)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标准品使用记录';

-- ============================================================
-- 菌种管理
-- ============================================================
CREATE TABLE lims_culture_collection (
    culture_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    culture_code       VARCHAR(50) NOT NULL,
    culture_name       VARCHAR(200) COMMENT '菌种名称',
    strain_number      VARCHAR(100) COMMENT '菌株编号(如ATCC 6538)',
    source             VARCHAR(100) COMMENT 'ATCC/CMCC等',
    storage_method     VARCHAR(50) COMMENT '冻干/低温/液氮',
    storage_location   VARCHAR(200),
    passage_count      INT DEFAULT 0 COMMENT '传代次数',
    max_passage        INT DEFAULT 5 COMMENT '最大传代次数',
    received_date      DATE,
    expire_date        DATE,
    viability_test_date DATE,
    viability_result   VARCHAR(20) COMMENT '存活/死亡',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菌种管理';

-- ============================================================
-- 培养基管理
-- ============================================================
CREATE TABLE lims_culture_media (
    media_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    media_code         VARCHAR(50) NOT NULL,
    media_name         VARCHAR(200) COMMENT '培养基名称',
    media_type         VARCHAR(30) COMMENT '类型',
    manufacturer       VARCHAR(200),
    lot_number         VARCHAR(50),
    preparation_date   DATE,
    sterilization_method VARCHAR(50),
    sterility_test     TINYINT COMMENT '无菌检查',
    growth_promotion_test TINYINT COMMENT '生长促进试验',
    growth_test_result VARCHAR(20),
    expire_date        DATE,
    storage_condition  VARCHAR(100),
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培养基管理';
```

### 9.3 检验人员资质与培训

```sql
-- ============================================================
-- 培训计划
-- ============================================================
CREATE TABLE lims_training_plan (
    plan_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_code          VARCHAR(50) NOT NULL,
    plan_name          VARCHAR(200),
    plan_year          INT,
    training_type      VARCHAR(30) COMMENT '岗位/GMP/安全/方法/仪器',
    status             VARCHAR(20),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训计划';

-- ============================================================
-- 培训记录
-- ============================================================
CREATE TABLE lims_training_record (
    training_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id            BIGINT,
    training_topic     VARCHAR(500) NOT NULL,
    training_type      VARCHAR(30),
    training_method    VARCHAR(30) COMMENT '面授/在线/实操/自学',
    trainer_id         BIGINT,
    trainer_name       VARCHAR(50),
    training_date      DATE,
    training_hours     DECIMAL(4,1),
    training_content   TEXT,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训记录';

-- ============================================================
-- 培训参与人员
-- ============================================================
CREATE TABLE lims_training_participant (
    participant_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    training_id       BIGINT NOT NULL,
    staff_id          BIGINT NOT NULL,
    staff_name        VARCHAR(50),
    attendance_status VARCHAR(20) COMMENT '出席/缺席',
    exam_score        DECIMAL(5,2),
    exam_passed       TINYINT,
    practical_passed  TINYINT,
    certificate_no    VARCHAR(100),
    certificate_expire DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训参与人员';

-- ============================================================
-- 人员资质管理（核心：持证上岗）
-- ============================================================
CREATE TABLE lims_qualification (
    qualification_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    staff_id           BIGINT NOT NULL,
    qualification_type VARCHAR(30) COMMENT '检验资质/仪器操作资质/安全资质',
    qualification_name VARCHAR(200),
    qualification_scope TEXT COMMENT '授权范围',
    granted_date      DATE,
    expire_date       DATE,
    granted_by        BIGINT,
    status            VARCHAR(20) COMMENT '有效/过期/吊销',
    requalification_date DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员资质';

-- ============================================================
-- 人员-检测项授权矩阵
-- ============================================================
CREATE TABLE lims_staff_test_authorization (
    auth_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    staff_id           BIGINT NOT NULL,
    test_item_id       BIGINT NOT NULL,
    test_item_name     VARCHAR(200),
    authorized_date    DATE,
    authorized_by      BIGINT,
    expire_date        DATE,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员-检测项授权';

-- ============================================================
-- 人员-仪器授权矩阵
-- ============================================================
CREATE TABLE lims_staff_instrument_authorization (
    auth_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    staff_id           BIGINT NOT NULL,
    instrument_id      BIGINT NOT NULL,
    instrument_name    VARCHAR(200),
    authorized_date    DATE,
    authorized_by      BIGINT,
    expire_date        DATE,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员-仪器授权';
```

---

## 十、专项监测模块

### 10.1 稳定性考察

```sql
-- ============================================================
-- 稳定性方案
-- ============================================================
CREATE TABLE lims_stability_protocol (
    protocol_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    protocol_code      VARCHAR(50) NOT NULL,
    product_id         BIGINT,
    product_name       VARCHAR(200),
    batch_no           VARCHAR(50),
    study_type         VARCHAR(20) COMMENT '长期/加速/中间条件/影响因素',
    storage_condition  VARCHAR(100) COMMENT '如25°C/60%RH',
    start_date         DATE,
    end_date           DATE,
    test_points        VARCHAR(500) COMMENT '检测时间点(0,3,6,9,12,18,24,36月)',
    specification_id   BIGINT COMMENT '质量标准',
    test_template_id   BIGINT COMMENT '检验模板',
    status             VARCHAR(20),
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='稳定性方案';

-- ============================================================
-- 稳定性检测记录
-- ============================================================
CREATE TABLE lims_stability_test (
    stability_test_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    protocol_id        BIGINT NOT NULL,
    test_point         VARCHAR(20) COMMENT '如12M',
    planned_date       DATE COMMENT '计划检测日期',
    actual_date        DATE COMMENT '实际检测日期',
    test_order_id      BIGINT COMMENT '关联检验订单',
    test_result_summary TEXT COMMENT '检测结果摘要',
    is_oot             TINYINT DEFAULT 0 COMMENT '是否超趋势',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='稳定性检测记录';
```

### 10.2 环境监测

```sql
-- ============================================================
-- 环境监测点
-- ============================================================
CREATE TABLE lims_env_monitor_point (
    point_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    point_code         VARCHAR(50) NOT NULL,
    point_name         VARCHAR(200),
    area_name          VARCHAR(100) COMMENT '洁净区名称',
    cleanliness_class  VARCHAR(20) COMMENT 'A/B/C/D',
    monitor_type       VARCHAR(50) COMMENT '沉降菌/浮游菌/表面微生物/悬浮粒子',
    alert_limit        DECIMAL(18,4) COMMENT '警戒限',
    action_limit       DECIMAL(18,4) COMMENT '纠偏限',
    frequency          VARCHAR(50) COMMENT '监测频率',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境监测点';

-- ============================================================
-- 环境监测记录
-- ============================================================
CREATE TABLE lims_env_monitor_record (
    record_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    point_id           BIGINT NOT NULL,
    monitor_date       DATE,
    sampler_id         BIGINT,
    result_value       DECIMAL(18,4),
    unit               VARCHAR(20),
    result_conclusion  VARCHAR(20) COMMENT '合格/超标',
    is_alert           TINYINT COMMENT '超警戒限',
    is_action          TINYINT COMMENT '超纠偏限',
    test_order_id      BIGINT,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境监测记录';
```

### 10.3 水系统监测

```sql
-- ============================================================
-- 水系统监测点
-- ============================================================
CREATE TABLE lims_water_monitor_point (
    point_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    point_code         VARCHAR(50) NOT NULL,
    point_name         VARCHAR(200),
    water_type         VARCHAR(20) COMMENT 'PW(纯化水)/WFI(注射用水)/纯蒸汽',
    system_name        VARCHAR(200),
    location           VARCHAR(200),
    use_point          VARCHAR(50),
    test_items         VARCHAR(500) COMMENT '检测项',
    sample_frequency   VARCHAR(50),
    alert_limit_micro  DECIMAL(10,2) COMMENT '微生物警戒限',
    action_limit_micro DECIMAL(10,2) COMMENT '微生物纠偏限',
    alert_limit_endotoxin DECIMAL(10,4) COMMENT '内毒素警戒限',
    action_limit_endotoxin DECIMAL(10,4) COMMENT '内毒素纠偏限',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水系统监测点';

-- ============================================================
-- 水系统检测记录
-- ============================================================
CREATE TABLE lims_water_test_record (
    record_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    point_id           BIGINT NOT NULL,
    sample_date        DATE,
    sample_time        TIME,
    sampler_id         BIGINT,
    conductivity       DECIMAL(10,4) COMMENT '电导率',
    toc                DECIMAL(10,4) COMMENT 'TOC',
    micro_count        DECIMAL(10,2) COMMENT '微生物数',
    endotoxin          DECIMAL(10,4) COMMENT '内毒素',
    ph_value           DECIMAL(4,2) COMMENT 'pH',
    result_conclusion  VARCHAR(20),
    is_alert           TINYINT,
    is_action          TINYINT,
    test_order_id      BIGINT,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水系统检测记录';
```

### 10.4 微生物检测专项

```sql
-- ============================================================
-- 微生物检测记录
-- ============================================================
CREATE TABLE lims_micro_test_record (
    record_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_order_id      BIGINT NOT NULL,
    test_type          VARCHAR(30) COMMENT '微生物限度/无菌检查/内毒素/抗生素效价',
    media_id           BIGINT COMMENT '使用培养基',
    inoculation_date   DATE,
    incubation_temp    DECIMAL(4,1),
    incubation_days    INT,
    positive_control   VARCHAR(50) COMMENT '阳性对照',
    negative_control   VARCHAR(50) COMMENT '阴性对照',
    culture_id         BIGINT COMMENT '使用菌种',
    result_description TEXT,
    colony_count       INT,
    result_value       DECIMAL(10,2),
    specification_limit VARCHAR(100),
    conclusion        VARCHAR(20),
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微生物检测记录';
```

---

## 十一、供应商质量管理

```sql
-- ============================================================
-- 供应商资质
-- ============================================================
CREATE TABLE lims_supplier_qualification (
    qualification_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_id          BIGINT NOT NULL,
    qualification_type VARCHAR(30) COMMENT '生产许可证/营业执照/GMP证书/DMF/CEP',
    certificate_no     VARCHAR(100),
    issue_date         DATE,
    expire_date        DATE,
    certificate_file   VARCHAR(500),
    status             VARCHAR(20) COMMENT '有效/即将过期/已过期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商资质';

-- ============================================================
-- 供应商审计
-- ============================================================
CREATE TABLE lims_supplier_audit (
    audit_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_id          BIGINT NOT NULL,
    audit_code         VARCHAR(50),
    audit_type         VARCHAR(20) COMMENT '现场审计/文件审计/免审计',
    audit_date         DATE,
    auditor_ids        VARCHAR(200),
    audit_scope        TEXT,
    audit_findings     TEXT,
    audit_classification VARCHAR(20) COMMENT '严重/主要/轻微/无',
    capa_required      TINYINT,
    capa_ids           VARCHAR(200),
    audit_score        DECIMAL(5,2),
    next_audit_date    DATE,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商审计';

-- ============================================================
-- 合格供应商名录
-- ============================================================
CREATE TABLE lims_supplier_approval (
    approval_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_id          BIGINT NOT NULL,
    product_id         BIGINT,
    approval_type      VARCHAR(20) COMMENT '批准/有条件批准/不批准',
    approved_date      DATE,
    approved_by        BIGINT,
    expire_date        DATE,
    status             VARCHAR(20) COMMENT '有效/暂停/撤销'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合格供应商名录';
```

---

## 十二、文档与记录管理

### 12.1 受控文档管理(DMS)

```sql
-- ============================================================
-- 文档分类
-- ============================================================
CREATE TABLE lims_doc_category (
    category_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_code      VARCHAR(50),
    category_name      VARCHAR(200),
    parent_id          BIGINT,
    sort_order         INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档分类';

-- ============================================================
-- 受控文档
-- ============================================================
CREATE TABLE lims_document (
    document_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code      VARCHAR(50) NOT NULL COMMENT '文件编号(如SOP-QC-001)',
    document_name      VARCHAR(500) NOT NULL,
    document_type      VARCHAR(30) COMMENT 'SOP/STP/SMP/SIP/COA/记录/模板',
    category_id        BIGINT,
    version_number     VARCHAR(20),
    effective_date     DATE,
    expiry_date        DATE,
    review_cycle_months INT DEFAULT 12,
    next_review_date   DATE,
    owner_id           BIGINT,
    department_id      BIGINT,
    file_path          VARCHAR(500),
    file_hash          VARCHAR(128) COMMENT '文件哈希(完整性校验)',
    file_size          BIGINT,
    status             VARCHAR(20) COMMENT '草稿/审核中/已生效/已废止',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='受控文档';

-- ============================================================
-- 文档阅读确认
-- ============================================================
CREATE TABLE lims_document_read_confirm (
    confirm_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id        BIGINT NOT NULL,
    user_id            BIGINT NOT NULL,
    read_time          DATETIME,
    esignature_id      BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档阅读确认';

-- ============================================================
-- 文档变更历史
-- ============================================================
CREATE TABLE lims_document_change_log (
    log_id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id        BIGINT NOT NULL,
    from_version       VARCHAR(20),
    to_version         VARCHAR(20),
    change_description TEXT,
    change_reason      TEXT,
    changed_by         BIGINT,
    changed_date       DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档变更历史';
```

### 12.2 电子实验记录本(ELN)

```sql
-- ============================================================
-- ELN模板
-- ============================================================
CREATE TABLE lims_eln_template (
    template_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code      VARCHAR(50) NOT NULL,
    template_name      VARCHAR(200),
    template_type      VARCHAR(30) COMMENT '理化/微生物/仪器分析/稳定性',
    template_content   LONGTEXT COMMENT 'JSON: 模板结构',
    version_number     VARCHAR(20),
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELN模板';

-- ============================================================
-- 实验记录
-- ============================================================
CREATE TABLE lims_eln_record (
    record_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id        BIGINT,
    test_order_id      BIGINT,
    record_title       VARCHAR(500),
    experiment_date    DATE,
    operator_id        BIGINT,
    lab_environment    TEXT COMMENT 'JSON: 温湿度等',
    content            LONGTEXT COMMENT 'JSON: 记录内容',
    attachments        TEXT COMMENT 'JSON: 附件列表',
    status             VARCHAR(20) COMMENT '起草/已提交/已复核/已批准',
    version_number     INT DEFAULT 1,
    parent_record_id   BIGINT COMMENT '修订时指向原记录',
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验记录';

-- ============================================================
-- 实验记录修订历史
-- ============================================================
CREATE TABLE lims_eln_record_revision (
    revision_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_id          BIGINT NOT NULL,
    revision_number    INT,
    revision_reason    TEXT,
    revised_content    LONGTEXT,
    revised_by         BIGINT,
    revised_time       DATETIME,
    approved_by        BIGINT,
    approved_time      DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验记录修订历史';
```

---

## 十三、报告模板可配置化

### 13.1 设计理念

报告模板可配置化是LIMS的核心需求之一。需要支持：

| 需求 | 说明 |
|------|------|
| **模板类型多样化** | COA/CofA、检验报告、稳定性报告、环境监测报告、OOS调查报告等 |
| **字段可配置** | 报告中包含哪些字段、字段顺序、字段格式均可配置 |
| **条件显示** | 根据检验类型/产品类型动态显示/隐藏字段 |
| **公式可配置** | 计算字段（如平均值、RSD）支持公式配置 |
| **样式可配置** | 字体、字号、边框、对齐、行高、页眉页脚等 |
| **多版本管理** | 模板版本化，历史版本可追溯 |
| **审批流** | 模板变更需走审批流程 |
| **PDF生成** | 基于模板+数据自动生成PDF报告 |

### 13.2 数据库设计

```sql
-- ============================================================
-- 报告模板主表
-- ============================================================
CREATE TABLE lims_report_template (
    template_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code      VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编号',
    template_name      VARCHAR(200) NOT NULL COMMENT '模板名称',
    template_type      VARCHAR(30) NOT NULL COMMENT 'COA/TEST_REPORT/STABILITY/ENV_MONITOR/OOS/WATER/RELEASE/APQR',
    template_category  VARCHAR(30) COMMENT '原料/成品/稳定性/环境/水系统',
    description        VARCHAR(500) COMMENT '模板描述',
    version_number     VARCHAR(20) DEFAULT 'V1.0' COMMENT '版本号',
    paper_size        VARCHAR(20) DEFAULT 'A4' COMMENT '纸张大小: A4/A3/Letter',
    orientation       VARCHAR(20) DEFAULT 'PORTRAIT' COMMENT '方向: PORTRAIT/LANDSCAPE',
    margin_top        DECIMAL(5,2) DEFAULT 2.0 COMMENT '上边距(cm)',
    margin_bottom     DECIMAL(5,2) DEFAULT 2.0 COMMENT '下边距(cm)',
    margin_left       DECIMAL(5,2) DEFAULT 2.5 COMMENT '左边距(cm)',
    margin_right      DECIMAL(5,2) DEFAULT 2.5 COMMENT '右边距(cm)',
    header_content    TEXT COMMENT 'JSON: 页眉内容(文本/图片/页码)',
    footer_content    TEXT COMMENT 'JSON: 页脚内容',
    watermark         VARCHAR(200) COMMENT '水印(如: DRAFT/CONFIDENTIAL)',
    is_default        TINYINT DEFAULT 0 COMMENT '是否默认模板',
    effective_date    DATE COMMENT '生效日期',
    expiry_date       DATE COMMENT '失效日期',
    status            VARCHAR(20) COMMENT '草稿/生效/停用',
    proc_inst_id      VARCHAR(64) COMMENT '审批流程ID',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板';

-- ============================================================
-- 报告模板-区域（页眉/标题区/样品信息区/结果表格区/签名区/页脚等）
-- ============================================================
CREATE TABLE lims_report_template_section (
    section_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id        BIGINT NOT NULL COMMENT '关联模板',
    section_code       VARCHAR(50) COMMENT '区域编码',
    section_name       VARCHAR(200) COMMENT '区域名称',
    section_type       VARCHAR(30) COMMENT 'HEADER/TITLE/SAMPLE_INFO/RESULT_TABLE/SIGNATURE/FOOTER/TEXT/IMAGE/CHART',
    sequence           INT COMMENT '排序',
    is_repeatable      TINYINT DEFAULT 0 COMMENT '是否可重复(如多检测项表格)',
    repeat_source      VARCHAR(100) COMMENT '重复数据源(如: testResults)',
    section_config     LONGTEXT COMMENT 'JSON: 区域配置(样式/条件/布局)',
    visibility_condition VARCHAR(500) COMMENT '显示条件(如: sampleType==\'RAW_MATERIAL\')',
    parent_section_id  BIGINT COMMENT '父区域ID(嵌套)',
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板-区域';

-- ============================================================
-- 报告模板-字段
-- ============================================================
CREATE TABLE lims_report_template_field (
    field_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_id         BIGINT NOT NULL COMMENT '关联区域',
    field_code         VARCHAR(100) NOT NULL COMMENT '字段编码(对应数据源)',
    field_label        VARCHAR(200) COMMENT '字段标签',
    field_type         VARCHAR(30) COMMENT 'TEXT/NUMBER/DATE/IMAGE/SIGNATURE/BARCODE/FORMULA',
    data_source        VARCHAR(100) COMMENT '数据源(如: sample.batchNo, testResult.reportedValue)',
    data_format        VARCHAR(50) COMMENT '格式(如: yyyy-MM-dd, #,##0.00)',
    formula            VARCHAR(500) COMMENT '计算公式(如: AVG(testResults)或RSD(values))',
    default_value      VARCHAR(200) COMMENT '默认值',
    font_family        VARCHAR(50) DEFAULT 'SimSun' COMMENT '字体',
    font_size          DECIMAL(4,1) DEFAULT 10.5 COMMENT '字号(pt)',
    font_bold          TINYINT DEFAULT 0 COMMENT '是否粗体',
    font_italic        TINYINT DEFAULT 0 COMMENT '是否斜体',
    alignment          VARCHAR(20) DEFAULT 'LEFT' COMMENT 'LEFT/CENTER/RIGHT',
    col_span           INT DEFAULT 1 COMMENT '跨列数',
    row_span           INT DEFAULT 1 COMMENT '跨行数',
    sequence           INT COMMENT '排序',
    visibility_condition VARCHAR(500) COMMENT '显示条件',
    is_required        TINYINT DEFAULT 1 COMMENT '是否必填',
    validation_rule    VARCHAR(500) COMMENT '校验规则',
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板-字段';

-- ============================================================
-- 报告模板-样式定义
-- ============================================================
CREATE TABLE lims_report_template_style (
    style_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id        BIGINT NOT NULL,
    style_name         VARCHAR(100) COMMENT '样式名称',
    style_type         VARCHAR(30) COMMENT 'TABLE/CELL/TEXT/IMAGE',
    style_config       TEXT COMMENT 'JSON: CSS-like样式(边框/背景色/内边距等)',
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板-样式';

-- ============================================================
-- 报告模板-产品关联（某产品使用哪套模板）
-- ============================================================
CREATE TABLE lims_report_template_product (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id        BIGINT NOT NULL,
    product_id         BIGINT COMMENT '产品ID(NULL表示通用)',
    product_code       VARCHAR(50),
    product_name       VARCHAR(200),
    is_default         TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板-产品关联';

-- ============================================================
-- 已生成的报告记录
-- ============================================================
CREATE TABLE lims_report (
    report_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_code        VARCHAR(50) NOT NULL UNIQUE COMMENT '报告编号',
    report_type        VARCHAR(30) COMMENT 'COA/TEST_REPORT/STABILITY/ENV/OOS/WATER/RELEASE/APQR',
    template_id        BIGINT COMMENT '使用的模板',
    template_version   VARCHAR(20),
    business_type      VARCHAR(50) COMMENT '关联业务类型',
    business_id        BIGINT COMMENT '关联业务ID',
    title              VARCHAR(500) COMMENT '报告标题',
    product_name       VARCHAR(200),
    batch_no           VARCHAR(50),
    report_data        LONGTEXT COMMENT 'JSON: 报告数据快照',
    pdf_path           VARCHAR(500) COMMENT 'PDF文件路径',
    pdf_hash           VARCHAR(128) COMMENT 'PDF哈希(完整性)',
    generated_by       BIGINT,
    generated_time      DATETIME,
    reviewed_by        BIGINT,
    reviewed_time      DATETIME,
    approved_by        BIGINT,
    approved_time      DATETIME,
    printed_count      INT DEFAULT 0 COMMENT '打印次数',
    last_printed_time  DATETIME,
    status             VARCHAR(20) COMMENT '已生成/已复核/已批准/已签发/已作废',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='已生成报告';
```

### 13.3 报告模板配置示例

**COA检验报告模板配置**：

```json
{
  "templateCode": "COA-001",
  "templateName": "成品检验COA报告",
  "templateType": "COA",
  "paperSize": "A4",
  "orientation": "PORTRAIT",
  "sections": [
    {
      "sectionType": "HEADER",
      "sectionName": "公司Logo+报告编号",
      "fields": [
        {"fieldCode": "companyLogo", "fieldType": "IMAGE", "dataSource": "system.logo", "colSpan": 2},
        {"fieldCode": "reportCode", "fieldLabel": "报告编号", "fieldType": "TEXT", "dataSource": "report.reportCode", "fontSize": 14, "bold": true}
      ]
    },
    {
      "sectionType": "TITLE",
      "sectionName": "报告标题",
      "fields": [
        {"fieldCode": "title", "fieldType": "TEXT", "defaultValue": "检验报告 / Certificate of Analysis", "fontSize": 18, "bold": true, "alignment": "CENTER", "colSpan": 4}
      ]
    },
    {
      "sectionType": "SAMPLE_INFO",
      "sectionName": "样品信息",
      "fields": [
        {"fieldCode": "productName", "fieldLabel": "产品名称", "dataSource": "sample.productName"},
        {"fieldCode": "productCode", "fieldLabel": "产品编码", "dataSource": "sample.productCode"},
        {"fieldCode": "batchNo", "fieldLabel": "批号", "dataSource": "sample.batchNo"},
        {"fieldCode": "specification", "fieldLabel": "规格", "dataSource": "sample.specification"},
        {"fieldCode": "manufacturer", "fieldLabel": "生产单位", "dataSource": "sample.vendorName"},
        {"fieldCode": "manufactureDate", "fieldLabel": "生产日期", "dataSource": "sample.manufactureDate", "dataFormat": "yyyy-MM-dd"},
        {"fieldCode": "sampleDate", "fieldLabel": "取样日期", "dataSource": "sample.receiveDate", "dataFormat": "yyyy-MM-dd"},
        {"fieldCode": "reportDate", "fieldLabel": "报告日期", "dataSource": "report.generatedTime", "dataFormat": "yyyy-MM-dd"}
      ]
    },
    {
      "sectionType": "RESULT_TABLE",
      "sectionName": "检验结果",
      "isRepeatable": true,
      "repeatSource": "testResults",
      "fields": [
        {"fieldCode": "sequence", "fieldLabel": "序号", "fieldType": "NUMBER", "dataSource": "index"},
        {"fieldCode": "testItem", "fieldLabel": "检测项目", "dataSource": "testItemName"},
        {"fieldCode": "method", "fieldLabel": "检验方法", "dataSource": "methodName"},
        {"fieldCode": "specification", "fieldLabel": "标准规定", "dataSource": "specificationVal"},
        {"fieldCode": "result", "fieldLabel": "检验结果", "dataSource": "reportedValue"},
        {"fieldCode": "conclusion", "fieldLabel": "单项结论", "dataSource": "resultConclusion"}
      ]
    },
    {
      "sectionType": "SIGNATURE",
      "sectionName": "签名区",
      "fields": [
        {"fieldCode": "testerSign", "fieldLabel": "检验人", "fieldType": "SIGNATURE", "dataSource": "esign.tester"},
        {"fieldCode": "reviewerSign", "fieldLabel": "复核人", "fieldType": "SIGNATURE", "dataSource": "esign.reviewer"},
        {"fieldCode": "approverSign", "fieldLabel": "批准人", "fieldType": "SIGNATURE", "dataSource": "esign.approver"}
      ]
    }
  ]
}
```

### 13.4 报告生成引擎

```java
/**
 * 报告生成服务
 */
public interface ILimsReportGeneratorService {
    
    /**
     * 基于模板+数据生成报告
     * @param templateId 模板ID
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 报告ID
     */
    Long generateReport(Long templateId, String businessType, Long businessId);
    
    /**
     * 根据产品自动选择模板
     */
    Long generateReportAuto(String reportType, Long productId, String businessType, Long businessId);
    
    /**
     * 生成PDF
     */
    String generatePdf(Long reportId);
    
    /**
     * 预览报告(不保存)
     */
    Map<String, Object> previewReport(Long templateId, String businessType, Long businessId);
}
```

---

## 十四、医药伦理与GxP合规流程

### 14.1 概述

医药行业的LIMS不仅要满足GMP（生产质量管理规范），还涉及GLP、GCP、药物警戒等多个合规领域。以下是完整的GxP合规流程体系。

```
┌──────────────────────────────────────────────────────────────────┐
│                    医药GxP合规体系                                │
├──────────────┬───────────────────────────────────────────────────┤
│   GMP        │  生产质量管理规范 — 已覆盖(偏差/CAPA/变更/放行等)   │
│   GLP        │  非临床研究质量管理规范 — 新增                      │
│   GCP        │  临床试验质量管理规范 — 新增                        │
│   GDP        │  药品分销质量管理规范 — 新增                        │
│   PV         │  药物警戒(Pharmacovigilance) — 新增               │
│   MAH        │  药品上市许可持有人制度 — 新增                      │
│   伦理审查    │  IRB/IEC伦理委员会审查 — 新增                      │
│   药典合规    │  ChP/USP/EP/BP — 扩展                            │
└──────────────┴───────────────────────────────────────────────────┘
```

### 14.2 GLP合规流程（非临床研究质量管理规范）

GLP适用于药物非临床安全性评价研究，LIMS在GLP环境下的关键要求：

```sql
-- ============================================================
-- GLP研究方案
-- ============================================================
CREATE TABLE lims_glp_study (
    study_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    study_code         VARCHAR(50) NOT NULL UNIQUE COMMENT '研究编号',
    study_title        VARCHAR(500) NOT NULL COMMENT '研究标题',
    study_type         VARCHAR(30) COMMENT '急性毒性/慢性毒性/遗传毒性/生殖毒性/致癌/药代',
    test_article       VARCHAR(200) COMMENT '受试物',
    control_article    VARCHAR(200) COMMENT '对照物',
    study_director_id   BIGINT COMMENT '研究负责人(SD)',
    sponsor            VARCHAR(200) COMMENT '委托方',
    protocol_no        VARCHAR(50) COMMENT '方案编号',
    start_date         DATE,
    end_date           DATE,
    glp_compliance     TINYINT DEFAULT 1 COMMENT '是否符合GLP',
    qau_audit_plan     TEXT COMMENT 'QAU审计计划',
    raw_data_location  VARCHAR(500) COMMENT '原始数据存放位置',
    archive_location   VARCHAR(500) COMMENT '归档位置',
    status             VARCHAR(20) COMMENT '方案阶段/进行中/完成报告/归档',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GLP研究方案';

-- ============================================================
-- GLP质量保证单位(QAU)审计记录
-- ============================================================
CREATE TABLE lims_glp_qau_audit (
    audit_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    study_id           BIGINT NOT NULL,
    audit_type         VARCHAR(30) COMMENT '设施审计/过程审计/研究审计/最终报告审计',
    audit_date         DATE,
    auditor_id         BIGINT,
    audit_findings     TEXT COMMENT '审计发现',
    audit_report_file  VARCHAR(500) COMMENT '审计报告',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='QAU审计记录';

-- ============================================================
-- GLP原始数据管理
-- ============================================================
CREATE TABLE lims_glp_raw_data (
    raw_data_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    study_id           BIGINT NOT NULL,
    data_type          VARCHAR(30) COMMENT '实验记录/仪器数据/影像/样品记录',
    data_description   VARCHAR(500),
    file_path          VARCHAR(500),
    file_hash          VARCHAR(128) COMMENT '文件哈希',
    data_collection_date DATE,
    collected_by       BIGINT,
    storage_location   VARCHAR(500),
    retention_period_years INT DEFAULT 5 COMMENT '保存年限',
    disposal_date      DATE,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GLP原始数据';
```

### 14.3 GCP合规流程（临床试验质量管理规范）

GCP环境下LIMS用于临床样本检测，需要满足以下合规要求：

```sql
-- ============================================================
-- 临床试验项目
-- ============================================================
CREATE TABLE lims_clinical_trial (
    trial_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    trial_code         VARCHAR(50) NOT NULL UNIQUE COMMENT '临床试验登记号',
    trial_title        VARCHAR(500) COMMENT '试验标题',
    trial_phase        VARCHAR(20) COMMENT 'I期/II期/III期/IV期/BE',
    sponsor            VARCHAR(200) COMMENT '申办方',
    cro                VARCHAR(200) COMMENT 'CRO',
    principal_investigator VARCHAR(200) COMMENT '主要研究者(PI)',
    investigational_product VARCHAR(200) COMMENT '试验药物',
    comparator         VARCHAR(200) COMMENT '对照药',
    indication         VARCHAR(500) COMMENT '适应症',
    start_date         DATE,
    end_date           DATE,
    status             VARCHAR(20),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='临床试验项目';

-- ============================================================
-- 伦理审查记录
-- ============================================================
CREATE TABLE lims_ethics_review (
    review_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_code        VARCHAR(50) NOT NULL UNIQUE COMMENT '伦理审查编号',
    trial_id           BIGINT COMMENT '关联临床试验',
    protocol_version   VARCHAR(20) COMMENT '方案版本',
    review_type       VARCHAR(30) COMMENT '初始审查/修正审查/年度审查/严重不良事件审查/结题审查',
    irb_name          VARCHAR(200) COMMENT '伦理委员会名称',
    submission_date    DATE COMMENT '提交日期',
    review_date       DATE COMMENT '审查日期',
    review_result     VARCHAR(30) COMMENT '同意/修改后同意/不同意/暂停/终止',
    conditions        TEXT COMMENT '同意条件/修改意见',
    approval_no       VARCHAR(50) COMMENT '批件编号',
    approval_date     DATE COMMENT '批件日期',
    valid_until        DATE COMMENT '批件有效期',
    annual_review_date DATE COMMENT '年度跟踪审查日期',
    next_annual_review DATE COMMENT '下次年度审查日期',
    status            VARCHAR(20) COMMENT '待审查/已批准/修改后批准/不批准/暂停',
    proc_inst_id      VARCHAR(64),
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='伦理审查记录';

-- ============================================================
-- 知情同意管理
-- ============================================================
CREATE TABLE lims_informed_consent (
    consent_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    trial_id           BIGINT NOT NULL,
    subject_id         VARCHAR(50) COMMENT '受试者编号',
    icf_version        VARCHAR(20) COMMENT '知情同意书版本',
    icf_date           DATE COMMENT '知情同意书签署日期',
    consent_given      TINYINT COMMENT '是否同意',
    withdrawal_date     DATE COMMENT '撤回同意日期',
    consent_file       VARCHAR(500) COMMENT '知情同意书扫描件',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知情同意管理';

-- ============================================================
-- 临床样本管理
-- ============================================================
CREATE TABLE lims_clinical_sample (
    clinical_sample_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trial_id           BIGINT NOT NULL,
    subject_id         VARCHAR(50) COMMENT '受试者编号',
    visit_code         VARCHAR(30) COMMENT '访视编号',
    sample_type        VARCHAR(30) COMMENT '血液/尿液/组织/其他',
    collection_date    DATE,
    collection_time    TIME,
    collection_site    VARCHAR(200) COMMENT '采集中心',
    consent_verified   TINYINT COMMENT '知情同意已确认',
    sample_condition   VARCHAR(100) COMMENT '样本状态',
    storage_condition  VARCHAR(100),
    status             VARCHAR(20) COMMENT '待采集/已采集/运输中/已接收/检测中/已检测/已归档'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='临床样本';
```

### 14.4 药物警戒(PV)管理

```sql
-- ============================================================
-- 不良事件报告(药物警戒)
-- ============================================================
CREATE TABLE lims_adverse_event (
    ae_id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    ae_code            VARCHAR(50) NOT NULL UNIQUE COMMENT 'AE编号',
    trial_id           BIGINT COMMENT '关联临床试验',
    subject_id         VARCHAR(50) COMMENT '受试者编号',
    event_type         VARCHAR(30) COMMENT 'AE/SAE/SUSAR',
    event_term         VARCHAR(200) COMMENT '事件术语(MedDRA编码)',
    event_date         DATE COMMENT '发生日期',
    severity           VARCHAR(20) COMMENT '1级/2级/3级/4级/5级(死亡)',
    seriousness_criteria VARCHAR(100) COMMENT '导致死亡/危及生命/需住院/致残/致畸/其他重要医学事件',
    causality_assessment VARCHAR(30) COMMENT '肯定有关/很可能有关/可能有关/可能无关/无关',
    outcome            VARCHAR(30) COMMENT '痊愈/好转/未好转/死亡/未知',
    action_taken       VARCHAR(30) COMMENT '停药/减量/剂量不变/不详',
    report_to_authority TINYINT COMMENT '是否需向药监报告',
    authority_report_date DATE COMMENT '向药监报告日期',
    ethics_notified    TINYINT COMMENT '是否通知伦理委员会',
    ethics_notify_date DATE,
    investigator_id    BIGINT COMMENT '报告人',
    status             VARCHAR(20) COMMENT '报告/评估/已报告/已关闭',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='不良事件报告';

-- ============================================================
-- 药物警戒信号管理
-- ============================================================
CREATE TABLE lims_pv_signal (
    signal_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    signal_code        VARCHAR(50) NOT NULL UNIQUE,
    product_name       VARCHAR(200),
    signal_description TEXT COMMENT '信号描述',
    signal_source      VARCHAR(30) COMMENT '自发报告/文献/临床/注册/其他',
    detected_date      DATE,
    assessment_result  TEXT COMMENT '评估结论',
    is_confirmed       TINYINT COMMENT '是否确认为信号',
    action_taken       TEXT COMMENT '采取措施',
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药物警戒信号';
```

### 14.5 MAH（药品上市许可持有人）制度合规

```sql
-- ============================================================
-- 药品上市许可持有人管理
-- ============================================================
CREATE TABLE lims_mah_registration (
    registration_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    registration_no    VARCHAR(50) NOT NULL COMMENT '注册号/批准文号',
    product_name       VARCHAR(200) COMMENT '药品名称',
    generic_name        VARCHAR(200) COMMENT '通用名',
    dosage_form        VARCHAR(50) COMMENT '剂型',
    specification_val  VARCHAR(200) COMMENT '规格',
    approval_date      DATE COMMENT '批准日期',
    valid_until        DATE COMMENT '有效期至',
    holder_name        VARCHAR(200) COMMENT '持有人名称',
    holder_license_no  VARCHAR(50) COMMENT '持有人许可证号',
    manufacturing_site VARCHAR(200) COMMENT '生产场地',
    registration_category VARCHAR(30) COMMENT '新药/仿制药/进口药/生物制品',
    is_annual_report_required TINYINT COMMENT '是否需年度报告',
    last_annual_report_date DATE,
    next_annual_report_date DATE,
    status             VARCHAR(20) COMMENT '有效/到期/撤销'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MAH注册管理';

-- ============================================================
-- MAH年度报告
-- ============================================================
CREATE TABLE lims_mah_annual_report (
    report_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    registration_id    BIGINT NOT NULL,
    report_year        INT,
    production_summary TEXT COMMENT '生产情况',
    quality_summary    TEXT COMMENT '质量情况',
    adverse_event_summary TEXT COMMENT '不良反应情况',
    complaint_summary  TEXT COMMENT '投诉情况',
    recall_summary     TEXT COMMENT '召回情况',
    deviation_summary  TEXT COMMENT '偏差情况',
    change_summary     TEXT COMMENT '变更情况',
    self_inspection_summary TEXT COMMENT '自检情况',
    overall_assessment TEXT COMMENT '总体评价',
    status             VARCHAR(20),
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MAH年度报告';
```

### 14.6 药典合规

```sql
-- ============================================================
-- 药典标准库
-- ============================================================
CREATE TABLE lims_pharmacopoeia (
    pharmacopoeia_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    pharmacopoeia_type VARCHAR(20) COMMENT 'ChP/USP/EP/BP/JP',
    edition            VARCHAR(20) COMMENT '版次(如2025版)',
    monograph_number   VARCHAR(50) COMMENT '各论编号',
    monograph_name     VARCHAR(200) COMMENT '各论名称',
    monograph_content  LONGTEXT COMMENT '各论内容(含检测项和标准)',
    effective_date     DATE,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药典标准库';

-- ============================================================
-- 自检管理
-- ============================================================
CREATE TABLE lims_self_inspection (
    inspection_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    inspection_code    VARCHAR(50) NOT NULL,
    inspection_type    VARCHAR(30) COMMENT '常规自检/专项自检/跟踪检查',
    inspection_scope   TEXT COMMENT '检查范围',
    inspection_date    DATE,
    inspector_ids      VARCHAR(200) COMMENT '检查员',
    findings           TEXT COMMENT '检查发现',
    classification     VARCHAR(20) COMMENT '严重/主要/轻微/无',
    capa_required      TINYINT,
    capa_ids           VARCHAR(200),
    status             VARCHAR(20),
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自检管理';
```

---

## 十五、统计分析与仪表盘

### 15.1 检验排程与资源调度

```sql
-- ============================================================
-- 检验排程
-- ============================================================
CREATE TABLE lims_test_schedule (
    schedule_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_order_id      BIGINT NOT NULL,
    test_item_name     VARCHAR(200),
    instrument_id      BIGINT,
    tester_id          BIGINT,
    planned_start      DATETIME,
    planned_end        DATETIME,
    actual_start       DATETIME,
    actual_end         DATETIME,
    priority           INT DEFAULT 5,
    status             VARCHAR(20) COMMENT '待排程/已排程/进行中/已完成'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验排程';

-- ============================================================
-- 实验室工作负荷
-- ============================================================
CREATE TABLE lims_lab_workload (
    workload_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    lab_id             BIGINT,
    stat_date          DATE,
    pending_count      INT,
    in_progress_count  INT,
    completed_count    INT,
    overdue_count      INT,
    avg_turnaround_days DECIMAL(5,1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室工作负荷';
```

### 15.2 SPC统计

```sql
-- ============================================================
-- SPC控制图数据
-- ============================================================
CREATE TABLE lims_spc_data (
    spc_id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id         BIGINT,
    test_item_id       BIGINT,
    batch_no           VARCHAR(50),
    test_date          DATE,
    test_value         DECIMAL(18,6),
    ucl                DECIMAL(18,6) COMMENT '上控制限',
    cl                 DECIMAL(18,6) COMMENT '中心线',
    lcl                DECIMAL(18,6) COMMENT '下控制限',
    is_out_of_control  TINYINT COMMENT '是否失控',
    rule_violated      VARCHAR(50) COMMENT '违反规则(如Nelson Rule 1)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPC控制图数据';
```

---

## 十六、集成接口

### 16.1 LIMS-CDS集成

```sql
-- ============================================================
-- 仪器接口配置
-- ============================================================
CREATE TABLE lims_instrument_interface (
    interface_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    instrument_id      BIGINT NOT NULL,
    interface_type     VARCHAR(30) COMMENT 'FILE_SHARE/RS232/TCP_API/WEB_SERVICE/DATABASE',
    connection_config  TEXT COMMENT 'JSON: 连接配置',
    data_format        VARCHAR(20) COMMENT 'CSV/XML/AIF/JDX/JSON',
    parsing_rule       TEXT COMMENT 'JSON: 解析规则映射',
    auto_import        TINYINT DEFAULT 0,
    import_frequency   VARCHAR(30) COMMENT 'REALTIME/HOURLY/DAILY',
    last_sync_time     DATETIME,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器接口配置';

-- ============================================================
-- 原始数据导入记录
-- ============================================================
CREATE TABLE lims_raw_data_import (
    import_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    interface_id       BIGINT,
    test_order_id      BIGINT,
    test_result_id     BIGINT,
    source_file_name   VARCHAR(500),
    source_file_path   VARCHAR(500),
    source_file_hash   VARCHAR(128),
    import_time        DATETIME,
    imported_by        BIGINT,
    parsed_values      TEXT COMMENT 'JSON: 解析键值对',
    import_status      VARCHAR(20),
    error_message      TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='原始数据导入';
```

---

## 十七、后端项目结构重组

```
weimes-erp/                              # 改名为 weimes-lims
├── pom.xml
├── wei-lims-app/                         # 启动入口（原 wei-erp）
│   └── src/main/java/com/wei/LIMSApplication.java
├── wei-common/                           # 通用模块（增强审计追踪）
│   ├── annotation/                       # 自定义注解
│   ├── core/                            # 基类
│   ├── enums/lims/                      # ★ LIMS枚举
│   │   ├── TestOrderStatusEnum.java
│   │   ├── SampleStatusEnum.java
│   │   ├── DeviationStatusEnum.java
│   │   ├── CapaStatusEnum.java
│   │   ├── ChangeStatusEnum.java
│   │   ├── OosStatusEnum.java
│   │   ├── ReleaseDecisionEnum.java
│   │   └── EthicsReviewResultEnum.java
│   └── ...
├── wei-framework/                         # 框架模块
│   ├── security/                         # Spring Security
│   ├── aspectj/                          # ★ LimsAuditTrailAspect
│   └── ...
├── wei-system/                           # 系统管理（增强审计+数据完整性）
├── wei-lims/                             # ★ 核心LIMS模块（替代 wei-mes）
│   ├── src/main/java/com/wei/lims/
│   │   ├── sample/                       # 样品管理
│   │   │   ├── domain/
│   │   │   │   ├── LimsSample.java
│   │   │   │   ├── LimsSampleTestItem.java
│   │   │   │   ├── LimsSampleStatusLog.java
│   │   │   │   ├── LimsSampleDispose.java
│   │   │   │   └── LimsRetainSample.java
│   │   │   ├── mapper/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   ├── test/                         # 检验管理
│   │   │   ├── domain/
│   │   │   │   ├── LimsTestOrder.java
│   │   │   │   ├── LimsTestResult.java
│   │   │   │   ├── LimsTestMethod.java
│   │   │   │   ├── LimsSpecification.java
│   │   │   │   ├── LimsSpecificationLine.java
│   │   │   │   ├── LimsTestTemplate.java
│   │   │   │   ├── LimsTestTemplateItem.java
│   │   │   │   ├── LimsSamplingPlan.java
│   │   │   │   └── LimsMicroTestRecord.java
│   │   │   ├── mapper/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   ├── instrument/                   # 仪器管理
│   │   │   ├── domain/
│   │   │   ├── mapper/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   ├── reagent/                      # 试剂/标准品
│   │   │   ├── domain/
│   │   │   │   ├── LimsReagent.java
│   │   │   │   ├── LimsReagentLot.java
│   │   │   │   ├── LimsReferenceStandard.java
│   │   │   │   ├── LimsStandardQualification.java
│   │   │   │   ├── LimsStandardUsage.java
│   │   │   │   ├── LimsCultureCollection.java
│   │   │   │   └── LimsCultureMedia.java
│   │   │   └── ...
│   │   ├── stability/                    # 稳定性考察
│   │   ├── envmonitor/                   # 环境监测
│   │   ├── water/                        # 水系统监测
│   │   ├── oos/                          # OOS/OOT管理
│   │   │   ├── domain/
│   │   │   │   └── LimsOosInvestigation.java
│   │   │   └── ...
│   │   ├── deviation/                    # 偏差管理
│   │   ├── capa/                         # CAPA管理
│   │   ├── change/                       # 变更控制
│   │   ├── release/                      # 批放行
│   │   │   ├── domain/
│   │   │   │   ├── LimsBatchRelease.java
│   │   │   │   └── LimsBatchReleaseCheck.java
│   │   │   └── ...
│   │   ├── complaint/                    # 客户投诉与召回
│   │   ├── supplier/                     # 供应商质量
│   │   ├── training/                     # 培训与资质
│   │   │   ├── domain/
│   │   │   │   ├── LimsTrainingPlan.java
│   │   │   │   ├── LimsTrainingRecord.java
│   │   │   │   ├── LimsTrainingParticipant.java
│   │   │   │   ├── LimsQualification.java
│   │   │   │   ├── LimsStaffTestAuthorization.java
│   │   │   │   └── LimsStaffInstrumentAuthorization.java
│   │   │   └── ...
│   │   ├── document/                     # 受控文档管理
│   │   ├── eln/                          # 电子实验记录本
│   │   ├── report/                       # ★ 可配置报告模板 + 报告生成
│   │   │   ├── domain/
│   │   │   │   ├── LimsReportTemplate.java
│   │   │   │   ├── LimsReportTemplateSection.java
│   │   │   │   ├── LimsReportTemplateField.java
│   │   │   │   ├── LimsReportTemplateStyle.java
│   │   │   │   ├── LimsReportTemplateProduct.java
│   │   │   │   └── LimsReport.java
│   │   │   ├── mapper/
│   │   │   ├── service/
│   │   │   │   ├── ILimsReportTemplateService.java
│   │   │   │   ├── ILimsReportGeneratorService.java
│   │   │   │   └── impl/
│   │   │   │       ├── LimsReportTemplateServiceImpl.java
│   │   │   │       └── LimsReportGeneratorServiceImpl.java
│   │   │   └── controller/
│   │   ├── esign/                        # 电子签名
│   │   │   ├── domain/
│   │   │   │   └── LimsElectronicSignature.java
│   │   │   └── ...
│   │   ├── workflow/                     # 工作流引擎
│   │   │   ├── config/
│   │   │   │   └── FlowableConfig.java
│   │   │   ├── service/
│   │   │   └── listener/
│   │   ├── glp/                          # ★ GLP合规
│   │   │   ├── domain/
│   │   │   │   ├── LimsGlpStudy.java
│   │   │   │   ├── LimsGlpQauAudit.java
│   │   │   │   └── LimsGlpRawData.java
│   │   │   └── ...
│   │   ├── gcp/                          # ★ GCP合规
│   │   │   ├── domain/
│   │   │   │   ├── LimsClinicalTrial.java
│   │   │   │   ├── LimsEthicsReview.java
│   │   │   │   ├── LimsInformedConsent.java
│   │   │   │   └── LimsClinicalSample.java
│   │   │   └── ...
│   │   ├── pv/                           # ★ 药物警戒
│   │   │   ├── domain/
│   │   │   │   ├── LimsAdverseEvent.java
│   │   │   │   └── LimsPvSignal.java
│   │   │   └── ...
│   │   ├── mah/                          # ★ MAH制度合规
│   │   │   ├── domain/
│   │   │   │   ├── LimsMahRegistration.java
│   │   │   │   └── LimsMahAnnualReport.java
│   │   │   └── ...
│   │   ├── pharmacopoeia/                # ★ 药典合规
│   │   │   ├── domain/
│   │   │   │   └── LimsPharmacopoeia.java
│   │   │   └── ...
│   │   ├── selfinspection/               # 自检管理
│   │   │   └── domain/LimsSelfInspection.java
│   │   ├── apqr/                         # 年度产品质量回顾
│   │   │   └── domain/LimsApqr.java
│   │   ├── spc/                          # SPC统计
│   │   │   └── domain/LimsSpcData.java
│   │   ├── schedule/                     # 检验排程
│   │   │   └── domain/LimsTestSchedule.java
│   │   ├── integration/                  # 仪器数据采集
│   │   │   ├── domain/
│   │   │   │   ├── LimsInstrumentInterface.java
│   │   │   │   └── LimsRawDataImport.java
│   │   │   └── ...
│   │   └── masterdata/                   # LIMS主数据
│   │       ├── domain/
│   │       │   ├── LimsProduct.java
│   │       │   ├── LimsProductType.java
│   │       │   ├── LimsSupplier.java
│   │       │   ├── LimsClient.java
│   │       │   ├── LimsLab.java
│   │       │   └── LimsUnitMeasure.java
│   │       └── ...
├── wei-print/                            # 打印模块（扩展COA/报告打印）
├── wei-mobile/                           # 移动端（适配LIMS操作）
├── wei-hrm/                              # 人力资源（精简，只保留员工+岗位）
├── wei-quartz/                           # 定时任务
├── wei-report/                           # 积木报表
└── doc/
    ├── lims-init.sql                     # ★ LIMS初始化SQL
    └── lims-cleanup.sql                  # ★ MES数据清理SQL
```

---

## 十八、前端项目结构重组

```
weimes-ui/src/
├── api/lims/                              # ★ LIMS API
│   ├── sample.js                          # 样品管理
│   ├── test.js                            # 检验管理
│   ├── test-result.js                    # 检验结果
│   ├── test-method.js                    # 检验方法
│   ├── specification.js                  # 质量标准
│   ├── instrument.js                      # 仪器管理
│   ├── calibration.js                    # 校准管理
│   ├── reagent.js                        # 试剂管理
│   ├── reference-standard.js             # 标准品管理
│   ├── culture.js                        # 菌种/培养基
│   ├── stability.js                      # 稳定性考察
│   ├── envmonitor.js                     # 环境监测
│   ├── water.js                          # 水系统监测
│   ├── micro-test.js                     # 微生物检测
│   ├── oos.js                            # OOS/OOT
│   ├── deviation.js                      # 偏差管理
│   ├── capa.js                           # CAPA
│   ├── change.js                         # 变更控制
│   ├── release.js                        # 批放行
│   ├── complaint.js                      # 客户投诉
│   ├── recall.js                         # 产品召回
│   ├── supplier.js                       # 供应商质量
│   ├── training.js                       # 培训管理
│   ├── qualification.js                  # 人员资质
│   ├── document.js                       # 受控文档
│   ├── eln.js                            # 电子实验记录本
│   ├── report-template.js               # ★ 报告模板配置
│   ├── report.js                         # 报告生成
│   ├── esign.js                          # 电子签名
│   ├── workflow.js                        # 工作流
│   ├── glp.js                            # GLP合规
│   ├── gcp.js                            # GCP合规
│   ├── ethics.js                         # 伦理审查
│   ├── pv.js                             # 药物警戒
│   ├── mah.js                            # MAH合规
│   ├── pharmacopoeia.js                  # 药典
│   ├── apqr.js                           # 年度产品质量回顾
│   ├── spc.js                            # SPC统计
│   ├── schedule.js                       # 检验排程
│   ├── selfinspection.js                # 自检管理
│   └── masterdata.js                    # 主数据
├── views/lims/                           # ★ LIMS 页面
│   ├── sample/                            # 样品管理
│   │   ├── index.vue                     # 样品列表
│   │   ├── receive.vue                  # 样品接收
│   │   ├── detail.vue                   # 样品详情
│   │   └── retain.vue                   # 留样管理
│   ├── test/                             # 检验管理
│   │   ├── order.vue                    # 检验订单
│   │   ├── result-entry.vue             # 结果录入
│   │   ├── result-review.vue            # 结果复核
│   │   ├── method.vue                   # 检验方法
│   │   ├── specification.vue            # 质量标准
│   │   └── template.vue                 # 检验模板
│   ├── instrument/                       # 仪器管理
│   ├── reagent/                          # 试剂/标准品
│   ├── stability/                        # 稳定性考察
│   ├── envmonitor/                       # 环境监测
│   ├── water/                            # 水系统监测
│   ├── micro/                            # 微生物检测
│   ├── oos/                              # OOS/OOT
│   ├── deviation/                        # 偏差管理
│   ├── capa/                             # CAPA
│   ├── change/                           # 变更控制
│   ├── release/                          # 批放行
│   ├── complaint/                        # 客户投诉/召回
│   ├── supplier/                         # 供应商质量
│   ├── training/                         # 培训/资质
│   ├── document/                         # 受控文档(DMS)
│   ├── eln/                              # 电子实验记录本
│   ├── report/                           # ★ 报告管理
│   │   ├── template-config.vue          # 报告模板配置
│   │   ├── template-designer.vue        # ★ 报告模板可视化设计器
│   │   ├── template-section.vue         # 区域配置
│   │   ├── template-field.vue           # 字段配置
│   │   ├── template-style.vue           # 样式配置
│   │   ├── report-list.vue              # 已生成报告列表
│   │   ├── report-preview.vue            # 报告预览
│   │   └── report-print.vue             # 报告打印
│   ├── esign/                            # 电子签名
│   ├── workflow/                         # 工作流管理
│   ├── glp/                              # GLP合规
│   ├── gcp/                              # GCP合规
│   │   ├── trial.vue                    # 临床试验
│   │   ├── ethics-review.vue            # 伦理审查
│   │   ├── informed-consent.vue         # 知情同意
│   │   └── clinical-sample.vue          # 临床样本
│   ├── pv/                               # 药物警戒
│   │   ├── adverse-event.vue            # 不良事件
│   │   └── signal.vue                   # 信号管理
│   ├── mah/                              # MAH合规
│   ├── pharmacopoeia/                    # 药典
│   ├── apqr/                             # 年度产品质量回顾
│   ├── spc/                              # SPC统计
│   │   ├── control-chart.vue            # 控制图
│   │   ├── trend-analysis.vue           # 趋势分析
│   │   └── capability.vue              # 能力分析
│   ├── schedule/                         # 检验排程
│   ├── selfinspection/                   # 自检管理
│   └── masterdata/                       # 主数据
├── components/lims/                      # ★ LIMS通用组件
│   ├── ESignDialog.vue                   # 电子签名对话框
│   ├── ESignVerify.vue                   # 电子签名验证（输入密码确认）
│   ├── AuditTrail.vue                    # 审计追踪组件
│   ├── AuditTrailDialog.vue              # 审计追踪弹窗
│   ├── TestResultForm.vue                # 检验结果录入表单
│   ├── SpecificationSelect.vue           # 质量标准选择器
│   ├── ApprovalFlow.vue                  # 审批流程组件
│   ├── ApprovalHistory.vue               # 审批历史记录
│   ├── ReportTemplateDesigner.vue        # ★ 报告模板可视化设计器
│   ├── ReportPreview.vue                 # 报告预览组件
│   ├── SampleStatusTag.vue               # 样品状态标签
│   ├── TestOrderStatusTag.vue            # 检验订单状态标签
│   ├── OosIndicator.vue                  # OOS超标指示器
│   ├── QualificationCheck.vue            # 人员资质校验组件
│   ├── EthicsReviewForm.vue              # ★ 伦理审查表单
│   ├── InformedConsentCheck.vue          # ★ 知情同意确认组件
│   ├── AdverseEventForm.vue              # ★ 不良事件报告表单
│   └── SpcChart.vue                      # SPC控制图组件
└── ...
```

---

## 十九、完整数据库表清单

### 19.1 新建表汇总（共73张）

| 序号 | 模块 | 表名 | 说明 |
|------|------|------|------|
| | **合规基础** | | |
| 1 | 审计追踪 | lims_audit_trail | 审计追踪(全字段) |
| 2 | 电子签名 | lims_electronic_signature | 电子签名 |
| | **工作流** | | |
| 3 | 工作流 | lims_process_definition | 流程定义 |
| 4 | 工作流 | lims_process_approval | 审批记录 |
| | **样品管理** | | |
| 5 | 样品 | lims_sample | 样品主表 |
| 6 | 样品 | lims_sample_test_item | 样品-检测项 |
| 7 | 样品 | lims_sample_status_log | 样品状态日志 |
| 8 | 样品 | lims_sample_dispose | 样品处置 |
| 9 | 留样 | lims_retain_sample | 留样记录 |
| | **检验管理** | | |
| 10 | 检验 | lims_specification | 质量标准 |
| 11 | 检验 | lims_specification_line | 质量标准行 |
| 12 | 检验 | lims_test_method | 检验方法 |
| 13 | 检验 | lims_test_order | 检验订单 |
| 14 | 检验 | lims_test_result | 检验结果 |
| 15 | 检验 | lims_sampling_plan | 采样计划 |
| | **质量管理** | | |
| 16 | OOS | lims_oos_investigation | OOS/OOT调查 |
| 17 | 偏差 | lims_deviation | 偏差管理 |
| 18 | 偏差 | lims_deviation_impact | 偏差影响评估 |
| 19 | CAPA | lims_capa | CAPA管理 |
| 20 | CAPA | lims_capa_task | CAPA任务 |
| 21 | 变更 | lims_change_control | 变更控制 |
| 22 | 变更 | lims_change_impact | 变更影响评估 |
| 23 | 放行 | lims_batch_release | 批放行 |
| 24 | 放行 | lims_batch_release_check | 批放行检查清单 |
| 25 | 投诉 | lims_complaint | 客户投诉 |
| 26 | 召回 | lims_recall | 产品召回 |
| 27 | APQR | lims_apqr | 年度产品质量回顾 |
| | **仪器管理** | | |
| 28 | 仪器 | lims_instrument | 仪器台账 |
| 29 | 仪器 | lims_instrument_type | 仪器类型 |
| 30 | 仪器 | lims_instrument_calibration | 仪器校准 |
| 31 | 仪器 | lims_instrument_maintenance | 仪器维护 |
| 32 | 仪器 | lims_instrument_reservation | 仪器预约 |
| | **试剂/标准品** | | |
| 33 | 试剂 | lims_reagent | 试剂台账 |
| 34 | 试剂 | lims_reagent_lot | 试剂批号 |
| 35 | 标准品 | lims_reference_standard | 标准品/对照品 |
| 36 | 标准品 | lims_standard_qualification | 标准品标定 |
| 37 | 标准品 | lims_standard_usage | 标准品使用 |
| 38 | 菌种 | lims_culture_collection | 菌种管理 |
| 39 | 培养基 | lims_culture_media | 培养基管理 |
| | **人员资质** | | |
| 40 | 培训 | lims_training_plan | 培训计划 |
| 41 | 培训 | lims_training_record | 培训记录 |
| 42 | 培训 | lims_training_participant | 培训参与人员 |
| 43 | 资质 | lims_qualification | 人员资质 |
| 44 | 授权 | lims_staff_test_authorization | 人员-检测项授权 |
| 45 | 授权 | lims_staff_instrument_authorization | 人员-仪器授权 |
| | **专项监测** | | |
| 46 | 稳定性 | lims_stability_protocol | 稳定性方案 |
| 47 | 稳定性 | lims_stability_test | 稳定性检测 |
| 48 | 环境 | lims_env_monitor_point | 环境监测点 |
| 49 | 环境 | lims_env_monitor_record | 环境监测记录 |
| 50 | 水系统 | lims_water_monitor_point | 水系统监测点 |
| 51 | 水系统 | lims_water_test_record | 水系统检测 |
| 52 | 微生物 | lims_micro_test_record | 微生物检测 |
| | **供应商质量** | | |
| 53 | 供应商 | lims_supplier_qualification | 供应商资质 |
| 54 | 供应商 | lims_supplier_audit | 供应商审计 |
| 55 | 供应商 | lims_supplier_approval | 合格供应商 |
| | **文档与记录** | | |
| 56 | 文档 | lims_doc_category | 文档分类 |
| 57 | 文档 | lims_document | 受控文档 |
| 58 | 文档 | lims_document_read_confirm | 文档阅读确认 |
| 59 | 文档 | lims_document_change_log | 文档变更历史 |
| 60 | ELN | lims_eln_template | ELN模板 |
| 61 | ELN | lims_eln_record | 实验记录 |
| 62 | ELN | lims_eln_record_revision | 实验记录修订 |
| | **报告模板** | | |
| 63 | 报告 | lims_report_template | 报告模板 |
| 64 | 报告 | lims_report_template_section | 报告模板-区域 |
| 65 | 报告 | lims_report_template_field | 报告模板-字段 |
| 66 | 报告 | lims_report_template_style | 报告模板-样式 |
| 67 | 报告 | lims_report_template_product | 报告模板-产品关联 |
| 68 | 报告 | lims_report | 已生成报告 |
| | **GxP合规** | | |
| 69 | GLP | lims_glp_study | GLP研究方案 |
| 70 | GLP | lims_glp_qau_audit | QAU审计 |
| 71 | GLP | lims_glp_raw_data | GLP原始数据 |
| 72 | GCP | lims_clinical_trial | 临床试验 |
| 73 | 伦理 | lims_ethics_review | 伦理审查 |
| 74 | 知情同意 | lims_informed_consent | 知情同意管理 |
| 75 | 临床样本 | lims_clinical_sample | 临床样本 |
| 76 | PV | lims_adverse_event | 不良事件报告 |
| 77 | PV | lims_pv_signal | 药物警戒信号 |
| 78 | MAH | lims_mah_registration | MAH注册管理 |
| 79 | MAH | lims_mah_annual_report | MAH年度报告 |
| 80 | 药典 | lims_pharmacopoeia | 药典标准库 |
| 81 | 自检 | lims_self_inspection | 自检管理 |
| | **统计分析** | | |
| 82 | 排程 | lims_test_schedule | 检验排程 |
| 83 | 负荷 | lims_lab_workload | 实验室工作负荷 |
| 84 | SPC | lims_spc_data | SPC控制图数据 |
| | **集成** | | |
| 85 | 接口 | lims_instrument_interface | 仪器接口配置 |
| 86 | 导入 | lims_raw_data_import | 原始数据导入 |

**共计: 86张新表**

### 19.2 删除表汇总（共55张）

| 模块 | 表数 | 表名前缀 |
|------|------|---------|
| 生产管理 | 16 | `pro_` |
| 排班日历 | 7 | `cal_` |
| 工装管理 | 2 | `tm_` |
| 会员 | 8 | `huiyuan_` |
| 主数据(部分) | 9 | `md_workstation*`, `md_product_bom/sop/sip/color` |
| 仓库(部分) | 8 | `wm_rt_salse*`, `wm_package*`, `wm_product_produce*`, `wm_item_consume*` |
| HRM(部分) | 5 | `hrm_interview/attendance/leave/overtime*` |

### 19.3 改造表汇总（共14张）

| 原表 | 改造为 |
|------|-------|
| `md_item` | `lims_product` |
| `md_item_type` | `lims_product_type` |
| `md_vendor` | `lims_supplier` |
| `md_client` | `lims_client` |
| `md_workshop` | `lims_lab` |
| `md_unit_measure` | 保留不变 |
| `qc_template` | `lims_test_template` |
| `qc_template_index` | `lims_test_template_item` |
| `qc_template_product` | `lims_test_template_product` |
| `qc_iqc` / `qc_oqc` | `lims_test_order` |
| `qc_iqc_line` / `qc_oqc_line` | `lims_test_result` |
| `dv_machinery` | `lims_instrument` |
| `dv_machinery_type` | `lims_instrument_type` |
| `hrm_staff` | `lims_staff` |

---

## 二十、实施路线图

```
Phase 1 (月1-4): 合规基础 + 工作流引擎 + 代码清理
├── 删除MES专用代码(pro/cal/tm/sales/会员等55张表+对应前后端代码)
├── 审计追踪增强(全字段级AOP)
├── 电子签名模块(21 CFR Part 11)
├── 数据完整性校验(ALCOA+)
├── Flowable工作流引擎集成
├── 通用审批流程框架
└── 项目结构重组(wei-mes → wei-lims)

Phase 2 (月4-8): 核心检验业务
├── 主数据扩展(产品/检测项/方法/质量标准)
├── 样品全生命周期(登记→接收→分配→存储→处置)
├── 留样管理
├── 检验订单+检验结果录入(含自动判定/修约)
├── 微生物检测专项
├── 采样计划与标签管理
├── 检验排程与资源调度
└── 试剂/培养基/菌种/标准品管理

Phase 3 (月8-12): 质量管理闭环 + 报告模板
├── OOS/OOT调查(深度流程)
├── 偏差管理
├── CAPA管理
├── 变更控制
├── 批放行管理
├── ★ 可配置报告模板引擎(模板设计器+PDF生成)
├── COA/CofA检验报告
└── 客户投诉与召回

Phase 4 (月12-16): 专项监测与GxP合规
├── 稳定性考察(方案→执行→报告)
├── 环境监测(洁净区)
├── 水系统监测(PW/WFI)
├── 检验人员资质与培训(持证上岗)
├── 人员-检测项/仪器授权矩阵
├── 供应商质量管理
├── ★ 伦理审查流程(IRB/IEC)
├── ★ GCP合规流程(临床试验+知情同意+临床样本)
└── ★ GLP合规流程(非临床研究+QAU审计)

Phase 5 (月16-20): 高级功能与集成
├── 仪器数据采集(CDS集成)
├── 电子实验记录本(ELN)
├── 受控文档管理(DMS)
├── SPC统计过程控制
├── 年度产品质量回顾(APQR)
├── ★ 药物警戒(PV)管理
├── ★ MAH制度合规
├── ★ 药典标准库
├── ★ 自检管理
├── 仪表盘与高级分析
├── ERP/MES双向集成
└── 移动端适配

Phase 6 (月20-24): 验证与上线
├── 计算机系统验证(CSV)
├── 用户验收测试(UAT)
├── 性能优化
├── 数据迁移
├── 用户培训
└── 正式上线
```

---

## 二十一、合规要点清单

| 法规/标准 | 关键要求 | 系统实现 |
|-----------|---------|---------|
| **中国GMP** 计算机化系统 | 数据审计追踪 | 全字段变更记录+不可篡改 |
| **21 CFR Part 11** | 电子签名/电子记录 | 电子签名模块+审计追踪+签名含义 |
| **EU Annex 11** | 系统验证 | Flowable流程可验证+CSV文档 |
| **ALCOA+原则** | 数据完整性 | 不可篡改、原始记录保护、服务端时间戳 |
| **ICH Q7** | API质量管理 | 偏差/OOS/CAPA全流程 |
| **ICH Q9** | 质量风险管理 | 风险评估(偏差/变更) |
| **ICH Q10** | 质量体系 | APQR+CAPA+变更+管理评审 |
| **USP <1058>** | 仪器确认 | IQ/OQ/PQ状态+校准管理 |
| **GLP** | 非临床研究质量 | QAU审计+原始数据保护+SD签名 |
| **GCP/ICH-GCP** | 临床试验质量 | 伦理审查+知情同意+SAE报告 |
| **中国GCP** | 临床试验管理 | 伦理委员会审查+AE/SAE报告流程 |
| **药物警戒** | 个例安全性报告 | SAE/SUSAR报告+信号检测+PSUR |
| **MAH制度** | 持有人责任 | 年度报告+全生命周期管理 |
| **药典通则** | 检验方法验证 | 方法验证/确认管理 |
| **赫尔辛基宣言** | 伦理原则 | 知情同意+伦理审查+受试者保护 |

---

> **文档结束**  
> 本文档为WEIMES ERP向医药行业LIMS改造的完整方案，涵盖：功能架构、代码清理、数据库设计(86张新表)、报告模板可配置化、GxP合规流程(GLP/GCP/伦理审查/药物警戒/MAH)、实施路线图及合规要点。
