-- =====================================================
-- LIMS报告系统数据库表 - GMP/CMA/CNAS合规
-- 包含：报告模板配置、数据校验规则、仪器数据采集、检验报告、报告签章
-- =====================================================

-- 1. 报告模板主表
CREATE TABLE IF NOT EXISTS `lims_report_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(64) NOT NULL COMMENT '模板编号',
  `template_name` varchar(200) NOT NULL COMMENT '模板名称',
  `report_type` varchar(32) NOT NULL COMMENT '报告类型: CMA/CNAS/INTERNAL/CUSTOM',
  `template_category` varchar(32) NOT NULL COMMENT '模板分类: COA/STABILITY/OOS/CALIBRATION',
  `file_path` varchar(500) DEFAULT NULL COMMENT '模板文件路径(Word/Excel)',
  `header_config` json DEFAULT NULL COMMENT '报告头部配置(JSON): 公司信息、Logo、CMA/CNAS标识',
  `footer_config` json DEFAULT NULL COMMENT '报告尾部配置(JSON): 声明、签章位置',
  `field_mapping` json DEFAULT NULL COMMENT '模板占位符与数据源映射(JSON)',
  `validation_rules` json DEFAULT NULL COMMENT '数据校验规则配置(JSON)',
  `approval_flow_config` json DEFAULT NULL COMMENT '审批流程配置(JSON)',
  `version` int DEFAULT 1 COMMENT '版本号',
  `status` varchar(32) DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿/ACTIVE-生效/OBSOLETE-废止',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `expiry_date` date DEFAULT NULL COMMENT '废止日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='报告模板';

-- 2. 报告模板字段映射表
CREATE TABLE IF NOT EXISTS `lims_report_template_field` (
  `field_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字段ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `field_name` varchar(200) NOT NULL COMMENT '字段名称',
  `field_code` varchar(64) NOT NULL COMMENT '字段编码',
  `data_source_type` varchar(32) NOT NULL COMMENT '数据源类型: SYSTEM/SAMPLE/RESULT/INSTRUMENT/CALCULATED/FIXED',
  `data_source_expr` varchar(500) DEFAULT NULL COMMENT '数据源表达式',
  `format_rule` varchar(200) DEFAULT NULL COMMENT '格式化规则',
  `validation_type` varchar(32) DEFAULT NULL COMMENT '校验类型: RANGE/ENUM/REGEX/REQUIRED/CUSTOM',
  `validation_params` json DEFAULT NULL COMMENT '校验参数(JSON)',
  `is_required` tinyint DEFAULT 0 COMMENT '是否必填: 0-否 1-是',
  `default_value` varchar(500) DEFAULT NULL COMMENT '默认值',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`field_id`),
  KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='报告模板字段映射';

-- 3. 报告模板章节表
CREATE TABLE IF NOT EXISTS `lims_report_template_section` (
  `section_id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `section_name` varchar(200) NOT NULL COMMENT '章节名称',
  `section_type` varchar(32) NOT NULL COMMENT '章节类型: HEADER/RESULT_TABLE/SUMMARY/SIGNATURE/ATTACHMENT',
  `section_config` json DEFAULT NULL COMMENT '章节配置(JSON)',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `is_printable` tinyint DEFAULT 1 COMMENT '是否打印: 0-否 1-是',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`section_id`),
  KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='报告模板章节';

-- 4. 仪器数据采集配置表
CREATE TABLE IF NOT EXISTS `lims_instrument_data_acquisition` (
  `acquisition_id` bigint NOT NULL AUTO_INCREMENT COMMENT '采集配置ID',
  `instrument_id` bigint NOT NULL COMMENT '仪器ID',
  `instrument_code` varchar(64) DEFAULT NULL COMMENT '仪器编号',
  `interface_type` varchar(32) NOT NULL COMMENT '接口类型: RS232/LAN/USB/FILE/OPC/MQTT',
  `connection_config` json DEFAULT NULL COMMENT '连接参数(JSON): IP/端口/波特率等',
  `data_protocol` varchar(32) DEFAULT 'ASCII' COMMENT '数据协议: ASCII/BINARY/HL7/ASTM/JSON',
  `parsing_rules` json DEFAULT NULL COMMENT '数据解析规则(JSON)',
  `field_mappings` json DEFAULT NULL COMMENT '采集字段与检验结果字段映射(JSON)',
  `auto_import` tinyint DEFAULT 0 COMMENT '是否自动导入: 0-否 1-是',
  `poll_interval` int DEFAULT 0 COMMENT '轮询间隔(秒), 0表示不轮询',
  `status` varchar(32) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/INACTIVE',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`acquisition_id`),
  KEY `idx_instrument_id` (`instrument_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='仪器数据采集配置';

-- 5. 数据校验规则表
CREATE TABLE IF NOT EXISTS `lims_data_validation_rule` (
  `rule_id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_code` varchar(64) NOT NULL COMMENT '规则编号',
  `rule_name` varchar(200) NOT NULL COMMENT '规则名称',
  `rule_type` varchar(32) NOT NULL COMMENT '规则类型: RANGE/OUTLIER/REPEATABILITY/ACCURACY/COMPLETENESS/CUSTOM',
  `spec_line_id` bigint DEFAULT NULL COMMENT '关联标准行ID',
  `test_item_name` varchar(200) DEFAULT NULL COMMENT '检测项名称(通用规则时使用)',
  `rule_expression` varchar(1000) DEFAULT NULL COMMENT '校验表达式',
  `rule_params` json DEFAULT NULL COMMENT '校验参数(JSON)',
  `error_level` varchar(32) DEFAULT 'ERROR' COMMENT '错误级别: WARNING/ERROR/CRITICAL',
  `error_message` varchar(500) DEFAULT NULL COMMENT '不符合时的提示信息',
  `is_enabled` tinyint DEFAULT 1 COMMENT '是否启用: 0-否 1-是',
  `status` varchar(32) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/INACTIVE',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`rule_id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='数据校验规则';

-- 6. 数据校验日志表
CREATE TABLE IF NOT EXISTS `lims_data_validation_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `test_order_id` bigint NOT NULL COMMENT '检验订单ID',
  `result_id` bigint DEFAULT NULL COMMENT '检验结果ID',
  `rule_id` bigint DEFAULT NULL COMMENT '校验规则ID',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '规则编号',
  `validation_status` varchar(32) NOT NULL COMMENT '校验状态: PASS/FAIL/WARNING',
  `actual_value` varchar(500) DEFAULT NULL COMMENT '实际值',
  `expected_value` varchar(500) DEFAULT NULL COMMENT '期望值',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `validated_by` varchar(64) DEFAULT NULL COMMENT '校验执行人',
  `validated_at` datetime DEFAULT NULL COMMENT '校验时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_test_order_id` (`test_order_id`),
  KEY `idx_result_id` (`result_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='数据校验日志';

-- 7. 检验报告表
CREATE TABLE IF NOT EXISTS `lims_test_report` (
  `report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `report_code` varchar(64) NOT NULL COMMENT '报告编号',
  `report_name` varchar(200) NOT NULL COMMENT '报告名称',
  `test_order_id` bigint NOT NULL COMMENT '检验订单ID',
  `report_template_id` bigint DEFAULT NULL COMMENT '报告模板ID',
  `report_type` varchar(32) NOT NULL COMMENT '报告类型: CMA/CNAS/INTERNAL',
  `report_data` json DEFAULT NULL COMMENT '报告数据快照(JSON)',
  `generated_file_path` varchar(500) DEFAULT NULL COMMENT '生成的报告文件路径',
  `conclusion` varchar(500) DEFAULT NULL COMMENT '总结论',
  `prepared_by` bigint DEFAULT NULL COMMENT '编制人ID',
  `prepared_date` datetime DEFAULT NULL COMMENT '编制日期',
  `reviewed_by` bigint DEFAULT NULL COMMENT '复核人ID',
  `reviewed_date` datetime DEFAULT NULL COMMENT '复核日期',
  `review_comment` varchar(500) DEFAULT NULL COMMENT '复核意见',
  `approved_by` bigint DEFAULT NULL COMMENT '批准人ID',
  `approved_date` datetime DEFAULT NULL COMMENT '批准日期',
  `approve_comment` varchar(500) DEFAULT NULL COMMENT '批准意见',
  `released_by` bigint DEFAULT NULL COMMENT '放行人ID',
  `released_date` datetime DEFAULT NULL COMMENT '放行日期',
  `status` varchar(32) DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PREPARING/REVIEWING/APPROVING/RELEASING/RELEASED/VOIDED',
  `void_reason` varchar(500) DEFAULT NULL COMMENT '作废原因',
  `version` int DEFAULT 1 COMMENT '报告版本',
  `parent_report_id` bigint DEFAULT NULL COMMENT '原始报告ID(修订时关联)',
  `proc_inst_id` varchar(64) DEFAULT NULL COMMENT '流程实例ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`report_id`),
  UNIQUE KEY `uk_report_code` (`report_code`),
  KEY `idx_test_order_id` (`test_order_id`),
  KEY `idx_template_id` (`report_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='检验报告';

-- 8. 报告签章记录表
CREATE TABLE IF NOT EXISTS `lims_report_signature` (
  `signature_id` bigint NOT NULL AUTO_INCREMENT COMMENT '签章ID',
  `report_id` bigint NOT NULL COMMENT '报告ID',
  `signature_type` varchar(32) NOT NULL COMMENT '签章类型: PREPARE/REVIEW/APPROVE/RELEASE',
  `signer_id` bigint NOT NULL COMMENT '签章人ID',
  `signer_name` varchar(64) DEFAULT NULL COMMENT '签章人姓名',
  `signature_time` datetime DEFAULT NULL COMMENT '签章时间',
  `signature_meaning` varchar(200) DEFAULT NULL COMMENT '签章含义',
  `is_valid` tinyint DEFAULT 1 COMMENT '是否有效: 0-否 1-是',
  `ip_address` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `reason` varchar(500) DEFAULT NULL COMMENT '签章原因',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`signature_id`),
  KEY `idx_report_id` (`report_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='报告签章记录';

-- =====================================================
-- 报告管理菜单已统一并入 doc/lims-init.sql（menu_id 3700 系列）
-- 本文件不再插入 sys_menu / sys_role_menu 数据，避免与主菜单脚本冲突
-- 原菜单(3100/3101/3102/3103/3104/3111-3144)与角色授权(role 1)全部废弃
-- 本文件保留用途：报告相关建表与初始数据（lims_test_report 等建表）
-- =====================================================

-- =====================================================
-- 插入初始报告模板数据
-- =====================================================

-- CMA检验报告模板
INSERT INTO lims_report_template (template_id, template_code, template_name, report_type, template_category, header_config, footer_config, approval_flow_config, version, status, effective_date, create_by, create_time)
VALUES (1, 'TPL-CMA-COA-001', 'CMA检验报告模板', 'CMA', 'COA',
  '{"companyName":"XX检测技术有限公司","logoPath":"/static/logo.png","cmaMark":true,"cnasMark":false,"certificateNo":"CMA-2025-001","address":"XX省XX市XX路XX号","tel":"0XX-XXXXXXXX"}',
  '{"declaration":"本报告未经本公司书面批准，不得部分复制。报告结果仅对所送样品负责。","signatures":[{"type":"prepared","label":"编制"},{"type":"reviewed","label":"审核"},{"type":"approved","label":"批准"}]}',
  '{"steps":[{"role":"PREPARE","name":"编制","required":true},{"role":"REVIEW","name":"审核","required":true,"fourEyePrinciple":true},{"role":"APPROVE","name":"批准","required":true,"fourEyePrinciple":true},{"role":"RELEASE","name":"放行","required":true,"fourEyePrinciple":true}]}',
  1, 'ACTIVE', '2025-01-01', 'admin', sysdate());

-- CNAS检验报告模板
INSERT INTO lims_report_template (template_id, template_code, template_name, report_type, template_category, header_config, footer_config, approval_flow_config, version, status, effective_date, create_by, create_time)
VALUES (2, 'TPL-CNAS-COA-001', 'CNAS检验报告模板', 'CNAS', 'COA',
  '{"companyName":"XX检测技术有限公司","logoPath":"/static/logo.png","cmaMark":true,"cnasMark":true,"certificateNo":"CNAS-2025-001","address":"XX省XX市XX路XX号","tel":"0XX-XXXXXXXX"}',
  '{"declaration":"本报告未经本公司书面批准，不得部分复制。报告结果仅对所送样品负责。","signatures":[{"type":"prepared","label":"编制"},{"type":"reviewed","label":"审核"},{"type":"approved","label":"批准"}]}',
  '{"steps":[{"role":"PREPARE","name":"编制","required":true},{"role":"REVIEW","name":"审核","required":true,"fourEyePrinciple":true},{"role":"APPROVE","name":"批准","required":true,"fourEyePrinciple":true},{"role":"RELEASE","name":"放行","required":true,"fourEyePrinciple":true}]}',
  1, 'ACTIVE', '2025-01-01', 'admin', sysdate());

-- 内部检验报告模板
INSERT INTO lims_report_template (template_id, template_code, template_name, report_type, template_category, header_config, footer_config, approval_flow_config, version, status, effective_date, create_by, create_time)
VALUES (3, 'TPL-INT-COA-001', '内部检验报告模板', 'INTERNAL', 'COA',
  '{"companyName":"XX检测技术有限公司","logoPath":"/static/logo.png","cmaMark":false,"cnasMark":false}',
  '{"declaration":"内部报告，仅供参考。","signatures":[{"type":"prepared","label":"编制"},{"type":"reviewed","label":"审核"},{"type":"approved","label":"批准"}]}',
  '{"steps":[{"role":"PREPARE","name":"编制","required":true},{"role":"REVIEW","name":"审核","required":true,"fourEyePrinciple":true},{"role":"APPROVE","name":"批准","required":true,"fourEyePrinciple":true}]}',
  1, 'ACTIVE', '2025-01-01', 'admin', sysdate());

-- =====================================================
-- 插入初始校验规则数据
-- =====================================================

INSERT INTO lims_data_validation_rule (rule_id, rule_code, rule_name, rule_type, rule_expression, rule_params, error_level, error_message, is_enabled, status, create_by, create_time)
VALUES (1, 'VR-001', '结果范围校验', 'RANGE', 'value >= lowerLimit AND value <= upperLimit', '{"description":"校验报告值是否在标准范围内"}', 'ERROR', '检验结果超出标准范围', 1, 'ACTIVE', 'admin', sysdate());

INSERT INTO lims_data_validation_rule (rule_id, rule_code, rule_name, rule_type, rule_expression, rule_params, error_level, error_message, is_enabled, status, create_by, create_time)
VALUES (2, 'VR-002', '必填项校验', 'COMPLETENESS', 'value IS NOT NULL AND value != ""', '{"description":"校验必填字段是否已填写"}', 'ERROR', '必填字段未填写', 1, 'ACTIVE', 'admin', sysdate());

INSERT INTO lims_data_validation_rule (rule_id, rule_code, rule_name, rule_type, rule_expression, rule_params, error_level, error_message, is_enabled, status, create_by, create_time)
VALUES (3, 'VR-003', '重复性校验', 'REPEATABILITY', 'stdDev(values) <= maxStdDev', '{"maxStdDev":0.5,"description":"校验平行样标准差是否在允许范围内"}', 'WARNING', '平行样重复性超出允许范围', 1, 'ACTIVE', 'admin', sysdate());

INSERT INTO lims_data_validation_rule (rule_id, rule_code, rule_name, rule_type, rule_expression, rule_params, error_level, error_message, is_enabled, status, create_by, create_time)
VALUES (4, 'VR-004', '数值异常校验', 'OUTLIER', 'abs(value - mean) <= 3 * stdDev', '{"description":"Grubbs异常值检验"}', 'WARNING', '检测到可能的异常值', 1, 'ACTIVE', 'admin', sysdate());

INSERT INTO lims_data_validation_rule (rule_id, rule_code, rule_name, rule_type, rule_expression, rule_params, error_level, error_message, is_enabled, status, create_by, create_time)
VALUES (5, 'VR-005', '修约规则校验', 'CUSTOM', 'roundingRule(reportedValue, originalValue)', '{"description":"校验报告值是否按修约规则修约"}', 'WARNING', '报告值修约不符合规则', 1, 'ACTIVE', 'admin', sysdate());
