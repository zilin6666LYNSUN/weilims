import pymysql

conn = pymysql.connect(host='120.48.96.152', port=3306, user='mes', password='stl123456789', database='mes', charset='utf8mb4')
cursor = conn.cursor()

sqls = [
    """CREATE TABLE IF NOT EXISTS lims_test_template (
        template_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '模板ID',
        template_code    VARCHAR(50)  NOT NULL COMMENT '模板编码',
        template_name    VARCHAR(200) NOT NULL COMMENT '模板名称',
        template_type    VARCHAR(50)  DEFAULT NULL COMMENT '模板类型',
        specification_id BIGINT       DEFAULT NULL COMMENT '关联规格ID',
        specification_name VARCHAR(200) DEFAULT NULL COMMENT '规格名称',
        status           CHAR(1)      DEFAULT '0' COMMENT '状态(0正常 1停用)',
        version          VARCHAR(20)  DEFAULT NULL COMMENT '版本号',
        create_by        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
        create_time      DATETIME     DEFAULT NULL COMMENT '创建时间',
        update_by        VARCHAR(64)  DEFAULT '' COMMENT '更新者',
        update_time      DATETIME     DEFAULT NULL COMMENT '更新时间',
        remark           VARCHAR(500) DEFAULT NULL COMMENT '备注',
        PRIMARY KEY (template_id),
        UNIQUE KEY uk_template_code (template_code)
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='检验模板表'""",

    """CREATE TABLE IF NOT EXISTS lims_test_template_item (
        id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
        template_id      BIGINT       NOT NULL COMMENT '模板ID',
        test_item_name   VARCHAR(200) NOT NULL COMMENT '检验项目名称',
        test_item_code   VARCHAR(50)  DEFAULT NULL COMMENT '检验项目编码',
        method_id        BIGINT       DEFAULT NULL COMMENT '检验方法ID',
        method_name      VARCHAR(200) DEFAULT NULL COMMENT '检验方法名称',
        spec_line_id     BIGINT       DEFAULT NULL COMMENT '规格行ID',
        test_item_type   VARCHAR(50)  DEFAULT NULL COMMENT '检验项目类型',
        is_required      CHAR(1)      DEFAULT '1' COMMENT '是否必检(0否 1是)',
        sort_order       INT          DEFAULT 0 COMMENT '排序',
        create_by        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
        create_time      DATETIME     DEFAULT NULL COMMENT '创建时间',
        remark           VARCHAR(500) DEFAULT NULL COMMENT '备注',
        PRIMARY KEY (id),
        KEY idx_template_id (template_id)
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='检验模板项目表'""",

    """CREATE TABLE IF NOT EXISTS lims_oos_phase_record (
        phase_record_id  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '阶段记录ID',
        investigation_id BIGINT       NOT NULL COMMENT '调查ID',
        phase            VARCHAR(50)  DEFAULT NULL COMMENT '调查阶段',
        hypothesis       TEXT         DEFAULT NULL COMMENT '假设',
        experiment_method TEXT        DEFAULT NULL COMMENT '实验方法',
        experiment_result TEXT        DEFAULT NULL COMMENT '实验结果',
        conclusion       TEXT         DEFAULT NULL COMMENT '结论',
        operator_id      BIGINT       DEFAULT NULL COMMENT '操作人ID',
        operator_name    VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',
        create_by        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
        create_time      DATETIME     DEFAULT NULL COMMENT '创建时间',
        PRIMARY KEY (phase_record_id),
        KEY idx_investigation_id (investigation_id)
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='OOS调查阶段记录表'""",

    """CREATE TABLE IF NOT EXISTS lims_reagent_stock_log (
        stock_log_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '库存日志ID',
        reagent_id       BIGINT       NOT NULL COMMENT '试剂ID',
        operation_type   VARCHAR(30)  NOT NULL COMMENT '操作类型(IN入库/OUT出库/RETURN退库)',
        quantity         DECIMAL(12,2) DEFAULT NULL COMMENT '数量',
        remain_qty       DECIMAL(12,2) DEFAULT NULL COMMENT '剩余数量',
        related_order_no VARCHAR(50)  DEFAULT NULL COMMENT '关联单号',
        purpose          VARCHAR(500) DEFAULT NULL COMMENT '用途',
        operator_id      BIGINT       DEFAULT NULL COMMENT '操作人ID',
        operator_name    VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',
        operation_time   DATETIME     DEFAULT NULL COMMENT '操作时间',
        create_by        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
        create_time      DATETIME     DEFAULT NULL COMMENT '创建时间',
        PRIMARY KEY (stock_log_id),
        KEY idx_reagent_id (reagent_id)
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='试剂库存流水表'"""
]

for sql in sqls:
    table_name = sql.split('EXISTS ')[1].split(' ')[0]
    try:
        cursor.execute(sql)
        print(f"Created table: {table_name}")
    except Exception as e:
        print(f"Error creating {table_name}: {e}")

conn.commit()
cursor.close()
conn.close()
print("Done!")
