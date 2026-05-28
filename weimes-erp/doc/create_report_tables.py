import pymysql

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# 1. 数据校验规则表
cur.execute("""
CREATE TABLE IF NOT EXISTS `lims_data_validation_rule` (
  `rule_id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_code` varchar(64) NOT NULL COMMENT '规则编号',
  `rule_name` varchar(200) NOT NULL COMMENT '规则名称',
  `rule_type` varchar(32) NOT NULL COMMENT '规则类型: RANGE/OUTLIER/REPEATABILITY/ACCURACY/COMPLETENESS/CUSTOM',
  `spec_line_id` bigint DEFAULT NULL COMMENT '关联标准行ID',
  `test_item_name` varchar(200) DEFAULT NULL COMMENT '检测项名称',
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='数据校验规则'
""")
conn.commit()
print("OK: lims_data_validation_rule")

# 2. 数据校验日志表
cur.execute("""
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='数据校验日志'
""")
conn.commit()
print("OK: lims_data_validation_log")

# 3. 仪器数据采集配置表
cur.execute("""
CREATE TABLE IF NOT EXISTS `lims_instrument_data_acquisition` (
  `acquisition_id` bigint NOT NULL AUTO_INCREMENT COMMENT '采集配置ID',
  `instrument_id` bigint NOT NULL COMMENT '仪器ID',
  `instrument_code` varchar(64) DEFAULT NULL COMMENT '仪器编号',
  `interface_type` varchar(32) NOT NULL COMMENT '接口类型: RS232/LAN/USB/FILE/OPC/MQTT',
  `connection_config` json DEFAULT NULL COMMENT '连接参数(JSON)',
  `data_protocol` varchar(32) DEFAULT 'ASCII' COMMENT '数据协议: ASCII/BINARY/HL7/ASTM/JSON',
  `parsing_rules` json DEFAULT NULL COMMENT '数据解析规则(JSON)',
  `field_mappings` json DEFAULT NULL COMMENT '采集字段与检验结果字段映射(JSON)',
  `auto_import` tinyint DEFAULT 0 COMMENT '是否自动导入: 0-否 1-是',
  `poll_interval` int DEFAULT 0 COMMENT '轮询间隔(秒)',
  `status` varchar(32) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/INACTIVE',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`acquisition_id`),
  KEY `idx_instrument_id` (`instrument_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='仪器数据采集配置'
""")
conn.commit()
print("OK: lims_instrument_data_acquisition")

# 4. 报告签章记录表
cur.execute("""
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='报告签章记录'
""")
conn.commit()
print("OK: lims_report_signature")

# 5. 为lims_report表添加缺失的GMP审批字段
try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN released_by bigint DEFAULT NULL COMMENT '放行人ID' AFTER approved_time")
    conn.commit()
    print("OK: lims_report add released_by")
except Exception as e:
    print(f"SKIP released_by: {e}")

try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN released_time datetime DEFAULT NULL COMMENT '放行时间' AFTER released_by")
    conn.commit()
    print("OK: lims_report add released_time")
except Exception as e:
    print(f"SKIP released_time: {e}")

try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN review_comment varchar(500) DEFAULT NULL COMMENT '复核意见' AFTER reviewed_time")
    conn.commit()
    print("OK: lims_report add review_comment")
except Exception as e:
    print(f"SKIP review_comment: {e}")

try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN approve_comment varchar(500) DEFAULT NULL COMMENT '批准意见' AFTER approved_time")
    conn.commit()
    print("OK: lims_report add approve_comment")
except Exception as e:
    print(f"SKIP approve_comment: {e}")

try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN void_reason varchar(500) DEFAULT NULL COMMENT '作废原因'")
    conn.commit()
    print("OK: lims_report add void_reason")
except Exception as e:
    print(f"SKIP void_reason: {e}")

try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN version int DEFAULT 1 COMMENT '报告版本'")
    conn.commit()
    print("OK: lims_report add version")
except Exception as e:
    print(f"SKIP version: {e}")

try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN parent_report_id bigint DEFAULT NULL COMMENT '原始报告ID'")
    conn.commit()
    print("OK: lims_report add parent_report_id")
except Exception as e:
    print(f"SKIP parent_report_id: {e}")

try:
    cur.execute("ALTER TABLE lims_report ADD COLUMN conclusion varchar(500) DEFAULT NULL COMMENT '总结论'")
    conn.commit()
    print("OK: lims_report add conclusion")
except Exception as e:
    print(f"SKIP conclusion: {e}")

# 6. 插入初始校验规则数据
rules = [
    (1, 'VR-001', '结果范围校验', 'RANGE', 'value >= lowerLimit AND value <= upperLimit',
     '{"description":"校验报告值是否在标准范围内"}', 'ERROR', '检验结果超出标准范围', 1, 'ACTIVE', 'admin'),
    (2, 'VR-002', '必填项校验', 'COMPLETENESS', 'value IS NOT NULL AND value != ""',
     '{"description":"校验必填字段是否已填写"}', 'ERROR', '必填字段未填写', 1, 'ACTIVE', 'admin'),
    (3, 'VR-003', '重复性校验', 'REPEATABILITY', 'stdDev(values) <= maxStdDev',
     '{"maxStdDev":0.5,"description":"校验平行样标准差是否在允许范围内"}', 'WARNING', '平行样重复性超出允许范围', 1, 'ACTIVE', 'admin'),
    (4, 'VR-004', '数值异常校验', 'OUTLIER', 'abs(value - mean) <= 3 * stdDev',
     '{"description":"Grubbs异常值检验"}', 'WARNING', '检测到可能的异常值', 1, 'ACTIVE', 'admin'),
    (5, 'VR-005', '修约规则校验', 'CUSTOM', 'roundingRule(reportedValue, originalValue)',
     '{"description":"校验报告值是否按修约规则修约"}', 'WARNING', '报告值修约不符合规则', 1, 'ACTIVE', 'admin'),
]

for r in rules:
    try:
        cur.execute("""INSERT INTO lims_data_validation_rule
            (rule_id, rule_code, rule_name, rule_type, rule_expression, rule_params, error_level, error_message, is_enabled, status, create_by, create_time)
            VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,now())""", r)
        conn.commit()
        print(f"OK: Insert rule {r[1]}")
    except Exception as e:
        print(f"SKIP rule {r[1]}: {e}")

cur.close()
conn.close()
print("\nAll done!")
