-- ============================================================
-- WEIMES LIMS 初始化脚本
-- 创建86张LIMS新表
-- 执行顺序: 先执行 lims-cleanup.sql，再执行本脚本
-- ============================================================

-- ============================================================
-- 一、合规基础层（2张）
-- ============================================================

-- 1. 审计追踪表
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

-- 2. 电子签名表
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

-- ============================================================
-- 二、工作流引擎（2张）
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

-- ============================================================
-- 三、样品管理（5张）
-- ============================================================

CREATE TABLE lims_sample (
    sample_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_code       VARCHAR(50) NOT NULL UNIQUE COMMENT '样品编号(自动生成)',
    sample_name       VARCHAR(200) COMMENT '样品名称',
    sample_type       VARCHAR(30) NOT NULL COMMENT '原料/中间体/成品/稳定性/环境/水系统/临床',
    sample_source     VARCHAR(30) COMMENT '供应商/生产车间/研发/客户委托/临床',
    batch_no          VARCHAR(50) COMMENT '批号',
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
    storage_condition VARCHAR(100) COMMENT '存储条件',
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

CREATE TABLE lims_sample_status_log (
    log_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    sample_id         BIGINT NOT NULL,
    from_status       VARCHAR(20),
    to_status         VARCHAR(20),
    changed_by        BIGINT,
    changed_time      DATETIME,
    reason            VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='样品状态变更日志';

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

-- ============================================================
-- 四、检验管理（5张）
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

CREATE TABLE lims_specification_line (
    line_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    spec_id           BIGINT NOT NULL,
    test_item_id      BIGINT COMMENT '检测项ID',
    test_item_name    VARCHAR(200) COMMENT '检测项名称',
    method_id         BIGINT COMMENT '检验方法',
    specification_val VARCHAR(200) COMMENT '规格值',
    lower_limit       DECIMAL(18,6) COMMENT '下限',
    upper_limit       DECIMAL(18,6) COMMENT '上限',
    unit              VARCHAR(20) COMMENT '单位',
    sequence          INT COMMENT '排序',
    is_mandatory      TINYINT DEFAULT 1 COMMENT '是否必检',
    rounding_rule     VARCHAR(50) COMMENT '修约规则'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量标准行';

CREATE TABLE lims_test_method (
    method_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    method_code       VARCHAR(50) NOT NULL COMMENT '方法编号',
    method_name       VARCHAR(200) NOT NULL COMMENT '方法名称',
    method_version    VARCHAR(20) COMMENT '版本号',
    pharmacopoeia_ref VARCHAR(200) COMMENT '药典引用',
    method_type       VARCHAR(50) COMMENT '理化/微生物/仪器分析',
    method_category   VARCHAR(50) COMMENT '鉴别/检查/含量测定/微生物',
    validation_status VARCHAR(20) COMMENT '验证状态',
    validated_date    DATE COMMENT '验证日期',
    approved_by       BIGINT COMMENT '批准人',
    status            VARCHAR(20) COMMENT '草稿/生效/停用',
    effective_date    DATE,
    expiry_date       DATE,
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验方法';

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

CREATE TABLE lims_test_result (
    result_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_order_id     BIGINT NOT NULL COMMENT '检验订单',
    spec_line_id      BIGINT COMMENT '质量标准行',
    test_item_name    VARCHAR(200) COMMENT '检测项名称',
    method_id         BIGINT COMMENT '检验方法',
    test_item_type    VARCHAR(30) COMMENT '定性/定量/描述',
    original_value    VARCHAR(200) COMMENT '原始值',
    instrument_value  VARCHAR(200) COMMENT '仪器采集值',
    calculated_value  DECIMAL(18,6) COMMENT '计算值',
    reported_value    VARCHAR(200) COMMENT '报告值(修约后)',
    rounding_rule     VARCHAR(50) COMMENT '修约规则',
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
    test_condition    TEXT COMMENT '检测条件',
    remark            TEXT,
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验结果';

CREATE TABLE lims_test_template (
    template_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code     VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编号',
    template_name     VARCHAR(200) NOT NULL COMMENT '模板名称',
    product_id        BIGINT COMMENT '产品ID',
    product_name      VARCHAR(200) COMMENT '产品名称',
    specification_id  BIGINT COMMENT '质量标准ID',
    test_type         VARCHAR(30) COMMENT '检验类型',
    status            VARCHAR(20) DEFAULT 'ENABLED' COMMENT '启用/停用',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验模板';

CREATE TABLE lims_test_template_item (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id       BIGINT NOT NULL COMMENT '模板ID',
    test_item_name    VARCHAR(200) COMMENT '检测项名称',
    test_item_code    VARCHAR(50) COMMENT '检测项编码',
    method_id         BIGINT COMMENT '检验方法ID',
    method_name       VARCHAR(200) COMMENT '检验方法名称',
    spec_line_id      BIGINT COMMENT '质量标准行ID',
    test_item_type    VARCHAR(30) COMMENT '定性/定量/描述',
    is_required       TINYINT DEFAULT 1 COMMENT '是否必检',
    sort_order        INT DEFAULT 0 COMMENT '排序',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验模板检测项';

-- ============================================================
-- 五、质量管理（10张）
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

CREATE TABLE lims_oos_phase_record (
    phase_record_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    investigation_id  BIGINT NOT NULL COMMENT '关联调查ID',
    phase             VARCHAR(20) COMMENT '阶段: PHASE_I/PHASE_II/PHASE_III',
    hypothesis        TEXT COMMENT '假设',
    experiment_method TEXT COMMENT '实验方法',
    experiment_result TEXT COMMENT '实验结果',
    conclusion        TEXT COMMENT '调查结论',
    operator_id       BIGINT COMMENT '操作人ID',
    operator_name     VARCHAR(64) COMMENT '操作人姓名',
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OOS调查阶段记录';

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

CREATE TABLE lims_deviation_impact (
    impact_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    deviation_id       BIGINT NOT NULL,
    impact_area        VARCHAR(50) COMMENT '产品质量/验证状态/注册/安全性',
    impact_description TEXT,
    impact_level       VARCHAR(20) COMMENT '无影响/轻微/中等/严重',
    assessed_by        BIGINT,
    assessed_date       DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='偏差影响评估';

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

CREATE TABLE lims_capa_task (
    task_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    capa_id            BIGINT NOT NULL,
    task_description   TEXT NOT NULL,
    assignee_id        BIGINT,
    due_date           DATE,
    completion_date    DATE,
    status             VARCHAR(20) COMMENT '待执行/执行中/已完成'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CAPA任务';

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
    regulatory_impact  VARCHAR(20) COMMENT '注册影响',
    requester_id       BIGINT,
    approver_id        BIGINT,
    effective_date     DATE,
    status             VARCHAR(20) COMMENT '申请中/评估中/审批中/执行中/效果确认/已关闭',
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更控制';

CREATE TABLE lims_change_impact (
    impact_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    change_id          BIGINT NOT NULL,
    impact_area        VARCHAR(50) COMMENT '产品/验证/注册/文件/培训/设备',
    impact_detail      TEXT,
    risk_level         VARCHAR(20) COMMENT '高/中/低',
    mitigation_plan    TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更影响评估';

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

CREATE TABLE lims_batch_release_check (
    check_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    release_id         BIGINT NOT NULL,
    check_item         VARCHAR(200),
    check_result       VARCHAR(20) COMMENT '通过/不通过/N/A',
    check_remark       TEXT,
    checked_by         BIGINT,
    checked_date       DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批放行检查清单';

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
-- 六、仪器管理（5张）
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
    calibration_status VARCHAR(20) COMMENT '校准状态',
    maintenance_cycle  INT COMMENT '维护周期(天)',
    last_maintenance   DATE COMMENT '上次维护日期',
    next_maintenance   DATE COMMENT '下次维护日期',
    interface_type     VARCHAR(30) COMMENT '接口类型',
    status             VARCHAR(20) COMMENT '在用/停用/报废/维修中',
    create_by          VARCHAR(64),
    create_time        DATETIME,
    update_by          VARCHAR(64),
    update_time        DATETIME,
    remark             TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器台账';

CREATE TABLE lims_instrument_type (
    type_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_code          VARCHAR(50) NOT NULL,
    type_name          VARCHAR(200) NOT NULL,
    category           VARCHAR(30) COMMENT '理化/微生物/分析天平/色谱/光谱/其他',
    parent_id          BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器类型';

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

CREATE TABLE lims_instrument_maintenance (
    maintenance_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    instrument_id      BIGINT NOT NULL,
    maintenance_type   VARCHAR(20) COMMENT '预防性维护/纠正性维护',
    maintenance_date   DATE,
    performed_by       BIGINT,
    description        TEXT,
    status             VARCHAR(20) COMMENT '计划中/执行中/完成'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器维护保养';

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

-- ============================================================
-- 七、试剂/标准品/菌种/培养基（7张）
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

CREATE TABLE lims_reagent_stock_log (
    stock_log_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    reagent_id        BIGINT NOT NULL COMMENT '试剂ID',
    operation_type    VARCHAR(20) COMMENT '入库/出库/退库/报废',
    quantity          DECIMAL(18,4) COMMENT '操作数量',
    remain_qty        DECIMAL(18,4) COMMENT '剩余数量',
    related_order_no  VARCHAR(50) COMMENT '关联单号',
    purpose           VARCHAR(500) COMMENT '用途',
    operator_id       BIGINT COMMENT '操作人ID',
    operator_name     VARCHAR(64) COMMENT '操作人姓名',
    operation_time    DATETIME COMMENT '操作时间',
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试剂库存流水';

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

CREATE TABLE lims_standard_usage (
    usage_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    standard_id        BIGINT NOT NULL,
    used_date          DATE,
    used_by            BIGINT,
    test_order_id      BIGINT,
    quantity_used      DECIMAL(18,4),
    remaining_after    DECIMAL(18,4)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标准品使用记录';

CREATE TABLE lims_culture_collection (
    culture_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    culture_code       VARCHAR(50) NOT NULL,
    culture_name       VARCHAR(200) COMMENT '菌种名称',
    strain_number      VARCHAR(100) COMMENT '菌株编号',
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

-- ============================================================
-- 八、人员资质与培训（6张）
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

-- ============================================================
-- 九、专项监测（7张）
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
    test_points        VARCHAR(500) COMMENT '检测时间点',
    specification_id   BIGINT COMMENT '质量标准',
    test_template_id   BIGINT COMMENT '检验模板',
    status             VARCHAR(20),
    proc_inst_id       VARCHAR(64),
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='稳定性方案';

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

-- ============================================================
-- 十、供应商质量管理（3张）
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

-- ============================================================
-- 十一、文档与记录管理（7张）
-- ============================================================

CREATE TABLE lims_doc_category (
    category_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_code      VARCHAR(50),
    category_name      VARCHAR(200),
    parent_id          BIGINT,
    sort_order         INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档分类';

CREATE TABLE lims_document (
    document_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code      VARCHAR(50) NOT NULL COMMENT '文件编号',
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

CREATE TABLE lims_document_read_confirm (
    confirm_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id        BIGINT NOT NULL,
    user_id            BIGINT NOT NULL,
    read_time          DATETIME,
    esignature_id      BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档阅读确认';

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

CREATE TABLE lims_eln_template (
    template_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code      VARCHAR(50) NOT NULL,
    template_name      VARCHAR(200),
    template_type      VARCHAR(30) COMMENT '理化/微生物/仪器分析/稳定性',
    template_content   LONGTEXT COMMENT 'JSON: 模板结构',
    version_number     VARCHAR(20),
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELN模板';

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

-- ============================================================
-- 十二、报告模板可配置化（6张）
-- ============================================================

CREATE TABLE lims_report_template (
    template_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code      VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编号',
    template_name      VARCHAR(200) NOT NULL COMMENT '模板名称',
    template_type      VARCHAR(30) NOT NULL COMMENT 'COA/TEST_REPORT/STABILITY/ENV_MONITOR/OOS/WATER/RELEASE/APQR',
    template_category  VARCHAR(30) COMMENT '原料/成品/稳定性/环境/水系统',
    description        VARCHAR(500) COMMENT '模板描述',
    version_number     VARCHAR(20) DEFAULT 'V1.0' COMMENT '版本号',
    paper_size        VARCHAR(20) DEFAULT 'A4' COMMENT '纸张大小',
    orientation       VARCHAR(20) DEFAULT 'PORTRAIT' COMMENT '方向',
    margin_top        DECIMAL(5,2) DEFAULT 2.0 COMMENT '上边距(cm)',
    margin_bottom     DECIMAL(5,2) DEFAULT 2.0 COMMENT '下边距(cm)',
    margin_left       DECIMAL(5,2) DEFAULT 2.5 COMMENT '左边距(cm)',
    margin_right      DECIMAL(5,2) DEFAULT 2.5 COMMENT '右边距(cm)',
    header_content    TEXT COMMENT 'JSON: 页眉内容',
    footer_content    TEXT COMMENT 'JSON: 页脚内容',
    watermark         VARCHAR(200) COMMENT '水印',
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

CREATE TABLE lims_report_template_section (
    section_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id        BIGINT NOT NULL COMMENT '关联模板',
    section_code       VARCHAR(50) COMMENT '区域编码',
    section_name       VARCHAR(200) COMMENT '区域名称',
    section_type       VARCHAR(30) COMMENT 'HEADER/TITLE/SAMPLE_INFO/RESULT_TABLE/SIGNATURE/FOOTER/TEXT/IMAGE/CHART',
    sequence           INT COMMENT '排序',
    is_repeatable      TINYINT DEFAULT 0 COMMENT '是否可重复',
    repeat_source      VARCHAR(100) COMMENT '重复数据源',
    section_config     LONGTEXT COMMENT 'JSON: 区域配置',
    visibility_condition VARCHAR(500) COMMENT '显示条件',
    parent_section_id  BIGINT COMMENT '父区域ID',
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板-区域';

CREATE TABLE lims_report_template_field (
    field_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_id         BIGINT NOT NULL COMMENT '关联区域',
    field_code         VARCHAR(100) NOT NULL COMMENT '字段编码',
    field_label        VARCHAR(200) COMMENT '字段标签',
    field_type         VARCHAR(30) COMMENT 'TEXT/NUMBER/DATE/IMAGE/SIGNATURE/BARCODE/FORMULA',
    data_source        VARCHAR(100) COMMENT '数据源',
    data_format        VARCHAR(50) COMMENT '格式',
    formula            VARCHAR(500) COMMENT '计算公式',
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

CREATE TABLE lims_report_template_style (
    style_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id        BIGINT NOT NULL,
    style_name         VARCHAR(100) COMMENT '样式名称',
    style_type         VARCHAR(30) COMMENT 'TABLE/CELL/TEXT/IMAGE',
    style_config       TEXT COMMENT 'JSON: CSS-like样式',
    create_by          VARCHAR(64),
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板-样式';

CREATE TABLE lims_report_template_product (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id        BIGINT NOT NULL,
    product_id         BIGINT COMMENT '产品ID(NULL表示通用)',
    product_code       VARCHAR(50),
    product_name       VARCHAR(200),
    is_default         TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板-产品关联';

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
    pdf_hash           VARCHAR(128) COMMENT 'PDF哈希',
    generated_by       BIGINT,
    generated_time     DATETIME,
    reviewed_by        BIGINT,
    reviewed_time      DATETIME,
    review_comment     VARCHAR(500) COMMENT '复核意见',
    approved_by        BIGINT,
    approved_time      DATETIME,
    approve_comment    VARCHAR(500) COMMENT '批准意见',
    released_by        BIGINT COMMENT '放行人',
    released_time      DATETIME COMMENT '放行时间',
    printed_count      INT DEFAULT 0 COMMENT '打印次数',
    last_printed_time  DATETIME,
    status             VARCHAR(20) COMMENT 'DRAFT/GENERATED/REVIEWING/APPROVING/RELEASED/VOID',
    proc_inst_id       VARCHAR(64),
    conclusion         VARCHAR(500) COMMENT '报告结论',
    void_reason        VARCHAR(500) COMMENT '作废原因',
    version            INT COMMENT '版本号',
    parent_report_id   BIGINT COMMENT '原报告ID(重新生成时关联)',
    create_by          VARCHAR(64),
    create_time        DATETIME,
    update_by          VARCHAR(64) COMMENT '更新者',
    update_time        DATETIME COMMENT '更新时间',
    remark             VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='已生成报告';

-- 报告签章记录表 (21 CFR Part 11合规)
CREATE TABLE lims_report_signature (
    signature_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_id         BIGINT NOT NULL COMMENT '报告ID',
    signature_type    VARCHAR(30) NOT NULL COMMENT '签章类型: PREPARE/REVIEW/APPROVE/RELEASE',
    signer_id         BIGINT NOT NULL COMMENT '签章人ID',
    signer_name       VARCHAR(50) COMMENT '签章人姓名',
    signature_time    DATETIME NOT NULL COMMENT '签章时间',
    signature_meaning VARCHAR(100) COMMENT '签章含义',
    is_valid          TINYINT DEFAULT 1 COMMENT '是否有效',
    ip_address        VARCHAR(50) COMMENT 'IP地址',
    reason            VARCHAR(500) COMMENT '签章原因',
    create_by         VARCHAR(64) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         VARCHAR(64) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    remark            VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_report_signature_type (report_id, signature_type),
    INDEX idx_report_id (report_id),
    INDEX idx_signature_type (signature_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告签章记录';

-- 数据校验规则表
CREATE TABLE lims_data_validation_rule (
    rule_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_code         VARCHAR(64) NOT NULL UNIQUE COMMENT '规则编号',
    rule_name         VARCHAR(200) NOT NULL COMMENT '规则名称',
    rule_type         VARCHAR(32) NOT NULL COMMENT '规则类型: RANGE/OUTLIER/REPEATABILITY/ACCURACY/COMPLETENESS/CUSTOM',
    spec_line_id      BIGINT COMMENT '关联标准行ID',
    test_item_name    VARCHAR(200) COMMENT '检测项名称',
    rule_expression   VARCHAR(1000) COMMENT '校验表达式',
    rule_params       JSON COMMENT '校验参数(JSON)',
    error_level       VARCHAR(32) DEFAULT 'ERROR' COMMENT '错误级别: WARNING/ERROR/CRITICAL',
    error_message     VARCHAR(500) COMMENT '不符合时的提示信息',
    is_enabled        TINYINT DEFAULT 1 COMMENT '是否启用: 0-否 1-是',
    status            VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/INACTIVE',
    remark            VARCHAR(500) COMMENT '备注',
    create_by         VARCHAR(64) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         VARCHAR(64) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    INDEX idx_rule_type (rule_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据校验规则';

-- 数据校验日志表
CREATE TABLE lims_data_validation_log (
    log_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_order_id     BIGINT NOT NULL COMMENT '检验订单ID',
    result_id         BIGINT COMMENT '检验结果ID',
    rule_id           BIGINT COMMENT '校验规则ID',
    rule_code         VARCHAR(64) COMMENT '规则编号',
    validation_status VARCHAR(32) NOT NULL COMMENT '校验状态: PASS/FAIL/WARNING',
    actual_value      VARCHAR(500) COMMENT '实际值',
    expected_value    VARCHAR(500) COMMENT '期望值',
    error_message     VARCHAR(500) COMMENT '错误信息',
    validated_by      VARCHAR(64) COMMENT '校验执行人',
    validated_at      DATETIME COMMENT '校验时间',
    create_by         VARCHAR(64) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         VARCHAR(64) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    remark            VARCHAR(500) COMMENT '备注',
    INDEX idx_test_order_id (test_order_id),
    INDEX idx_rule_id (rule_id),
    INDEX idx_validation_status (validation_status),
    INDEX idx_result_id (result_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据校验日志';

-- 仪器数据采集配置表
CREATE TABLE lims_instrument_data_acquisition (
    acquisition_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    instrument_id     BIGINT NOT NULL COMMENT '仪器ID',
    instrument_code   VARCHAR(64) COMMENT '仪器编号',
    interface_type    VARCHAR(32) NOT NULL COMMENT '接口类型: RS232/LAN/USB/FILE/OPC/MQTT',
    connection_config JSON COMMENT '连接参数(JSON)',
    data_protocol     VARCHAR(32) DEFAULT 'ASCII' COMMENT '数据协议: ASCII/BINARY/HL7/ASTM/JSON',
    parsing_rules     JSON COMMENT '数据解析规则(JSON)',
    field_mappings    JSON COMMENT '采集字段与检验结果字段映射(JSON)',
    auto_import       TINYINT DEFAULT 0 COMMENT '是否自动导入: 0-否 1-是',
    poll_interval     INT DEFAULT 0 COMMENT '轮询间隔(秒)',
    status            VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/INACTIVE',
    remark            VARCHAR(500) COMMENT '备注',
    create_by         VARCHAR(64) COMMENT '创建者',
    create_time       DATETIME COMMENT '创建时间',
    update_by         VARCHAR(64) COMMENT '更新者',
    update_time       DATETIME COMMENT '更新时间',
    INDEX idx_instrument_id (instrument_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪器数据采集配置';

-- ============================================================
-- 十三、GxP合规（13张）
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
    create_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='伦理审查记录';

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

CREATE TABLE lims_adverse_event (
    ae_id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    ae_code            VARCHAR(50) NOT NULL UNIQUE COMMENT 'AE编号',
    trial_id           BIGINT COMMENT '关联临床试验',
    subject_id         VARCHAR(50) COMMENT '受试者编号',
    event_type         VARCHAR(30) COMMENT 'AE/SAE/SUSAR',
    event_term         VARCHAR(200) COMMENT '事件术语(MedDRA编码)',
    event_date         DATE COMMENT '发生日期',
    severity           VARCHAR(20) COMMENT '1级/2级/3级/4级/5级(死亡)',
    seriousness_criteria VARCHAR(100) COMMENT '严重性标准',
    causality_assessment VARCHAR(30) COMMENT '因果评估',
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

CREATE TABLE lims_pharmacopoeia (
    pharmacopoeia_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    pharmacopoeia_type VARCHAR(20) COMMENT 'ChP/USP/EP/BP/JP',
    edition            VARCHAR(20) COMMENT '版次',
    monograph_number   VARCHAR(50) COMMENT '各论编号',
    monograph_name     VARCHAR(200) COMMENT '各论名称',
    monograph_content  LONGTEXT COMMENT '各论内容',
    effective_date     DATE,
    status             VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药典标准库';

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

-- ============================================================
-- 十四、统计与集成（5张）
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
    rule_violated      VARCHAR(50) COMMENT '违反规则'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPC控制图数据';

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

-- ============================================================
-- 十五、附加表：召回 + APQR + 采样计划（3张）
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

-- ============================================================
-- 十六、sys_menu 菜单初始化（LIMS模块动态路由与权限）
-- 菜单ID从3000开始，避免与现有MES菜单冲突
-- menu_type: M=目录, C=菜单页面, F=按钮权限
-- module_type: 1=PC端, 2=移动端
-- ============================================================

-- 一级目录: LIMS系统 (parent_id=0 顶级)
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `module_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_user_id`, `create_time`, `update_by`, `update_user_id`, `update_time`, `remark`, `deleted`, `deleteAt`, `version`) VALUES (3000, 'LIMS系统', 0, 5, 'lims', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'education', 'admin', NULL, NOW(), '', NULL, NULL, 'LIMS系统目录', 0, '1000-01-01 00:00:00', 1);

-- 二级目录: 样品管理
INSERT INTO `sys_menu` VALUES (3100, '样品管理', 3000, 1, 'sample', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'peoples', 'admin', NULL, NOW(), '', NULL, NULL, '样品管理目录', 0, '1000-01-01 00:00:00', 1);
-- 二级目录: 检验管理
INSERT INTO `sys_menu` VALUES (3200, '检验管理', 3000, 2, 'test', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'documentation', 'admin', NULL, NOW(), '', NULL, NULL, '检验管理目录', 0, '1000-01-01 00:00:00', 1);
-- 二级目录: 仪器管理
INSERT INTO `sys_menu` VALUES (3300, '仪器管理', 3000, 3, 'instrument', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'component', 'admin', NULL, NOW(), '', NULL, NULL, '仪器管理目录', 0, '1000-01-01 00:00:00', 1);
-- 二级目录: 试剂管理
INSERT INTO `sys_menu` VALUES (3400, '试剂管理', 3000, 4, 'reagent', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'shopping', 'admin', NULL, NOW(), '', NULL, NULL, '试剂管理目录', 0, '1000-01-01 00:00:00', 1);
-- 二级目录: 质量管理
INSERT INTO `sys_menu` VALUES (3500, '质量管理', 3000, 5, 'quality', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'quality', 'admin', NULL, NOW(), '', NULL, NULL, '质量管理目录', 0, '1000-01-01 00:00:00', 1);
-- 二级目录: 合规管理
INSERT INTO `sys_menu` VALUES (3600, '合规管理', 3000, 6, 'compliance', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'lock', 'admin', NULL, NOW(), '', NULL, NULL, '合规管理目录', 0, '1000-01-01 00:00:00', 1);
-- 二级目录: 报告管理 (统一主菜单：原 doc/lims_report_tables.sql 菜单并入本脚本，menu_id 使用 3700 系列避免冲突)
INSERT INTO `sys_menu` VALUES (3700, '报告管理', 3000, 7, 'report', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'documentation', 'admin', NULL, NOW(), '', NULL, NULL, '报告管理目录', 0, '1000-01-01 00:00:00', 1);

-- ============================
-- 报告管理 菜单与权限
-- ============================
-- C: 报告模板
INSERT INTO `sys_menu` VALUES (3701, '报告模板', 3700, 1, 'template', 'lims/report/template', '', 1, 0, 'C', '1', '0', '0', 'lims:report:template:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '报告模板菜单', 0, '1000-01-01 00:00:00', 1);
-- C: 检验报告
INSERT INTO `sys_menu` VALUES (3702, '检验报告', 3700, 2, 'testReport', 'lims/report/testReport', '', 1, 0, 'C', '1', '0', '0', 'lims:report:test:list', 'documentation', 'admin', NULL, NOW(), '', NULL, NULL, '检验报告菜单', 0, '1000-01-01 00:00:00', 1);
-- C: 数据校验
INSERT INTO `sys_menu` VALUES (3703, '数据校验', 3700, 3, 'validation', 'lims/report/validation', '', 1, 0, 'C', '1', '0', '0', 'lims:report:validation:list', 'checkbox', 'admin', NULL, NOW(), '', NULL, NULL, '数据校验菜单', 0, '1000-01-01 00:00:00', 1);
-- C: 采集配置 (归入仪器管理)
INSERT INTO `sys_menu` VALUES (3704, '采集配置', 3300, 5, 'acquisition', 'lims/instrument/acquisition', '', 1, 0, 'C', '1', '0', '0', 'lims:instrument:acquisition:list', 'upload', 'admin', NULL, NOW(), '', NULL, NULL, '仪器采集配置菜单', 0, '1000-01-01 00:00:00', 1);
-- F: 报告模板按钮权限
INSERT INTO `sys_menu` VALUES (3711, '报告模板查询', 3701, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:template:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3712, '报告模板新增', 3701, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:template:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3713, '报告模板修改', 3701, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:template:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3714, '报告模板删除', 3701, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:template:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- F: 检验报告按钮权限
INSERT INTO `sys_menu` VALUES (3721, '检验报告查询', 3702, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:test:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3722, '检验报告新增', 3702, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:test:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3723, '检验报告修改', 3702, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:test:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3724, '检验报告删除', 3702, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:test:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- F: 数据校验按钮权限
INSERT INTO `sys_menu` VALUES (3731, '数据校验查询', 3703, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:validation:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3732, '数据校验新增', 3703, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:validation:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3733, '数据校验修改', 3703, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:validation:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3734, '数据校验删除', 3703, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:report:validation:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- F: 采集配置按钮权限
INSERT INTO `sys_menu` VALUES (3741, '采集配置查询', 3704, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:acquisition:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3742, '采集配置新增', 3704, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:acquisition:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3743, '采集配置修改', 3704, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:acquisition:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3744, '采集配置删除', 3704, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:acquisition:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- 管理员角色(role_id=1)授权报告管理菜单与按钮
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3700);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3701);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3702);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3703);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3704);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3711);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3712);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3713);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3714);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3721);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3722);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3723);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3724);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3731);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3732);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3733);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3734);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3741);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3742);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3743);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3744);

-- ============================
-- 样品管理 菜单与权限
-- ============================
-- C: 样品登记
INSERT INTO `sys_menu` VALUES (3101, '样品登记', 3100, 1, 'index', 'lims/sample/index', '', 1, 0, 'C', '1', '0', '0', 'lims:sample:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '样品登记菜单', 0, '1000-01-01 00:00:00', 1);
-- F: 样品权限按钮
INSERT INTO `sys_menu` VALUES (3102, '样品查询', 3101, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3103, '样品新增', 3101, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3104, '样品修改', 3101, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3105, '样品删除', 3101, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3106, '样品接收', 3101, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:receive', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3107, '样品导出', 3101, 6, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:export', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3108, '样品复核', 3101, 7, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:review', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3109, '留样处置', 3101, 8, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:dispose', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3110, '处置审批', 3101, 9, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:sample:approve', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- ============================
-- 检验管理 菜单与权限
-- ============================
-- C: 检验方法
INSERT INTO `sys_menu` VALUES (3201, '检验方法', 3200, 1, 'method', 'lims/test/method', '', 1, 0, 'C', '1', '0', '0', 'lims:test:method:list', 'skill', 'admin', NULL, NOW(), '', NULL, NULL, '检验方法菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3202, '方法查询', 3201, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:method:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3203, '方法新增', 3201, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:method:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3204, '方法修改', 3201, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:method:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3205, '方法删除', 3201, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:method:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 质量标准
INSERT INTO `sys_menu` VALUES (3210, '质量标准', 3200, 2, 'specification', 'lims/test/specification', '', 1, 0, 'C', '1', '0', '0', 'lims:test:spec:list', 'clipboard', 'admin', NULL, NOW(), '', NULL, NULL, '质量标准菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3211, '标准查询', 3210, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:spec:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3212, '标准新增', 3210, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:spec:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3213, '标准修改', 3210, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:spec:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3214, '标准删除', 3210, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:spec:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 检验模板
INSERT INTO `sys_menu` VALUES (3220, '检验模板', 3200, 3, 'template', 'lims/test/template', '', 1, 0, 'C', '1', '0', '0', 'lims:test:template:list', 'build', 'admin', NULL, NOW(), '', NULL, NULL, '检验模板菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3221, '模板查询', 3220, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:template:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3222, '模板新增', 3220, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:template:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3223, '模板修改', 3220, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:template:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3224, '模板删除', 3220, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:template:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 检验单管理
INSERT INTO `sys_menu` VALUES (3230, '检验单管理', 3200, 4, 'order', 'lims/test/order', '', 1, 0, 'C', '1', '0', '0', 'lims:test:order:list', 'list', 'admin', NULL, NOW(), '', NULL, NULL, '检验单管理菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3231, '检验单查询', 3230, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3232, '检验单新增', 3230, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3233, '检验单修改', 3230, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3234, '检验单删除', 3230, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3235, '检验单提交', 3230, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:submit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3236, '检验单复核', 3230, 6, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:review', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3237, '检验单批准', 3230, 7, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:approve', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3238, '检验单导出', 3230, 8, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:order:export', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3239, '检验结果查看', 3230, 9, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:result:list', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- ============================
-- 仪器管理 菜单与权限
-- ============================
-- C: 仪器台账
INSERT INTO `sys_menu` VALUES (3301, '仪器台账', 3300, 1, 'index', 'lims/instrument/index', '', 1, 0, 'C', '1', '0', '0', 'lims:instrument:list', 'component', 'admin', NULL, NOW(), '', NULL, NULL, '仪器台账菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3302, '仪器查询', 3301, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3303, '仪器新增', 3301, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3304, '仪器修改', 3301, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3305, '仪器删除', 3301, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3306, '仪器导出', 3301, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:instrument:export', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 仪器分类
INSERT INTO `sys_menu` VALUES (3310, '仪器分类', 3300, 2, 'type', 'lims/instrument/type', '', 1, 0, 'C', '1', '0', '0', 'lims:instrument:list', 'tree', 'admin', NULL, NOW(), '', NULL, NULL, '仪器分类菜单', 0, '1000-01-01 00:00:00', 1);

-- C: 仪器校准
INSERT INTO `sys_menu` VALUES (3320, '仪器校准', 3300, 3, 'calibration', 'lims/instrument/calibration', '', 1, 0, 'C', '1', '0', '0', 'lims:instrument:list', 'date', 'admin', NULL, NOW(), '', NULL, NULL, '仪器校准菜单', 0, '1000-01-01 00:00:00', 1);

-- C: 仪器维护
INSERT INTO `sys_menu` VALUES (3330, '仪器维护', 3300, 4, 'maintenance', 'lims/instrument/maintenance', '', 1, 0, 'C', '1', '0', '0', 'lims:instrument:list', 'tool', 'admin', NULL, NOW(), '', NULL, NULL, '仪器维护菜单', 0, '1000-01-01 00:00:00', 1);

-- C: 仪器预约
INSERT INTO `sys_menu` VALUES (3340, '仪器预约', 3300, 5, 'reservation', 'lims/instrument/reservation', '', 1, 0, 'C', '1', '0', '0', 'lims:instrument:list', 'time', 'admin', NULL, NOW(), '', NULL, NULL, '仪器预约菜单', 0, '1000-01-01 00:00:00', 1);

-- ============================
-- 试剂管理 菜单与权限
-- ============================
-- C: 试剂台账
INSERT INTO `sys_menu` VALUES (3401, '试剂台账', 3400, 1, 'index', 'lims/reagent/index', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:list', 'shopping', 'admin', NULL, NOW(), '', NULL, NULL, '试剂台账菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3402, '试剂查询', 3401, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3403, '试剂新增', 3401, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3404, '试剂修改', 3401, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3405, '试剂删除', 3401, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 标准品管理
INSERT INTO `sys_menu` VALUES (3410, '标准品管理', 3400, 2, 'standard', 'lims/reagent/standard', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:list', 'money', 'admin', NULL, NOW(), '', NULL, NULL, '标准品管理菜单', 0, '1000-01-01 00:00:00', 1);

-- C: 菌种管理
INSERT INTO `sys_menu` VALUES (3420, '菌种管理', 3400, 3, 'culture', 'lims/reagent/culture', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:list', 'bug', 'admin', NULL, NOW(), '', NULL, NULL, '菌种管理菜单', 0, '1000-01-01 00:00:00', 1);

-- ============================
-- 质量管理 菜单与权限
-- ============================
-- C: OOS/OOT调查
INSERT INTO `sys_menu` VALUES (3501, 'OOS/OOT调查', 3500, 1, 'oos', 'lims/quality/oos', '', 1, 0, 'C', '1', '0', '0', 'lims:oos:list', 'warning', 'admin', NULL, NOW(), '', NULL, NULL, 'OOS/OOT调查菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3502, 'OOS查询', 3501, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:oos:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3503, 'OOS新增', 3501, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:oos:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3504, 'OOS修改', 3501, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:oos:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3505, 'OOS删除', 3501, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:oos:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3506, 'OOS审核', 3501, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:oos:review', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3507, 'OOS批准', 3501, 6, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:oos:approve', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 偏差管理
INSERT INTO `sys_menu` VALUES (3510, '偏差管理', 3500, 2, 'deviation', 'lims/quality/deviation', '', 1, 0, 'C', '1', '0', '0', 'lims:deviation:list', 'edit', 'admin', NULL, NOW(), '', NULL, NULL, '偏差管理菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3511, '偏差查询', 3510, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:deviation:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3512, '偏差新增', 3510, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:deviation:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3513, '偏差修改', 3510, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:deviation:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3514, '偏差删除', 3510, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:deviation:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3515, '偏差审批', 3510, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:deviation:approve', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3516, '偏差关闭', 3510, 6, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:deviation:close', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: CAPA管理
INSERT INTO `sys_menu` VALUES (3520, 'CAPA管理', 3500, 3, 'capa', 'lims/quality/capa', '', 1, 0, 'C', '1', '0', '0', 'lims:capa:list', 'checkbox', 'admin', NULL, NOW(), '', NULL, NULL, 'CAPA管理菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3521, 'CAPA查询', 3520, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:capa:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3522, 'CAPA新增', 3520, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:capa:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3523, 'CAPA修改', 3520, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:capa:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3524, 'CAPA删除', 3520, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:capa:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3525, 'CAPA审批', 3520, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:capa:approve', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3526, 'CAPA关闭', 3520, 6, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:capa:close', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 变更控制
INSERT INTO `sys_menu` VALUES (3530, '变更控制', 3500, 4, 'change', 'lims/quality/change', '', 1, 0, 'C', '1', '0', '0', 'lims:change:list', 'switch', 'admin', NULL, NOW(), '', NULL, NULL, '变更控制菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3531, '变更查询', 3530, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:change:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3532, '变更新增', 3530, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:change:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3533, '变更修改', 3530, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:change:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3534, '变更删除', 3530, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:change:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3535, '变更审批', 3530, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:change:approve', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3536, '变更执行', 3530, 6, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:change:implement', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3537, '变更完成', 3530, 7, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:change:complete', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 稳定性考察
INSERT INTO `sys_menu` VALUES (3540, '稳定性考察', 3500, 5, 'stability', 'lims/quality/stability', '', 1, 0, 'C', '1', '0', '0', 'lims:stability:list', 'time', 'admin', NULL, NOW(), '', NULL, NULL, '稳定性考察菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3541, '稳定性查询', 3540, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:stability:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3542, '稳定性新增', 3540, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:stability:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3543, '稳定性修改', 3540, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:stability:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3544, '稳定性删除', 3540, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:stability:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- ============================
-- 合规管理 菜单与权限
-- ============================
-- C: 审计追踪
INSERT INTO `sys_menu` VALUES (3601, '审计追踪', 3600, 1, 'audit', 'lims/compliance/audit', '', 1, 0, 'C', '1', '0', '0', 'lims:esign:audit:list', 'log', 'admin', NULL, NOW(), '', NULL, NULL, '审计追踪菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3602, '审计追踪查询', 3601, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:esign:audit:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- C: 电子签名记录
INSERT INTO `sys_menu` VALUES (3610, '电子签名记录', 3600, 2, 'esign', 'lims/compliance/esign', '', 1, 0, 'C', '1', '0', '0', 'lims:esign:list', 'password', 'admin', NULL, NOW(), '', NULL, NULL, '电子签名记录菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3611, '签名查询', 3610, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:esign:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3612, '签名签发', 3610, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:esign:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3613, '签名验证', 3610, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:esign:verify', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- ============================================================
-- 初始化完成，共创建 86 张新表 + LIMS菜单权限数据
-- ============================================================

-- ============================================================
-- P1 可配置审批工作流：流程实例表（支撑 lims_process_definition /
-- lims_process_approval 的运行时状态存储，原两表无实例运行态字段）
-- ============================================================
CREATE TABLE IF NOT EXISTS lims_process_instance (
    proc_inst_id       VARCHAR(64) PRIMARY KEY COMMENT '流程实例ID(引擎生成 PI+时间+序号)',
    proc_def_id        BIGINT COMMENT '流程定义ID',
    proc_key           VARCHAR(100) COMMENT '流程KEY',
    business_type      VARCHAR(50) COMMENT '业务类型: REPORT/DEVIATION/CHANGE/CAPA等',
    business_id        BIGINT COMMENT '业务ID',
    current_node_id    VARCHAR(64) COMMENT '当前节点ID',
    status             VARCHAR(20) DEFAULT 'RUNNING' COMMENT 'RUNNING/COMPLETED/TERMINATED',
    variables          LONGTEXT COMMENT '流程变量JSON',
    current_approvals  TEXT COMMENT '会签节点审批进度JSON',
    start_time         DATETIME,
    end_time           DATETIME,
    create_by          VARCHAR(64),
    create_time        DATETIME,
    update_by          VARCHAR(64),
    update_time        DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例表';

-- ============================================================
-- 流程管理 菜单与权限（P1 可配置审批工作流）
-- ============================================================
INSERT INTO `sys_menu` VALUES (3800, '流程管理', 3000, 8, 'process', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'workflow', 'admin', NULL, NOW(), '', NULL, NULL, '流程管理目录', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3801, '流程定义', 3800, 1, 'definition', 'lims/process/definition', '', 1, 0, 'C', '1', '0', '0', 'lims:process:definition:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '流程定义菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3802, '流程实例', 3800, 2, 'instance', 'lims/process/instance', '', 1, 0, 'C', '1', '0', '0', 'lims:process:instance:list', 'tree-table', 'admin', NULL, NOW(), '', NULL, NULL, '流程实例菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3803, '我的待办', 3800, 3, 'todo', 'lims/process/todo', '', 1, 0, 'C', '1', '0', '0', 'lims:process:instance:list', 'checkbox', 'admin', NULL, NOW(), '', NULL, NULL, '我的待办菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3810, '流程定义新增', 3801, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:process:definition:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3811, '流程定义修改', 3801, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:process:definition:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3812, '流程定义删除', 3801, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:process:definition:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3813, '流程定义查询', 3801, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:process:definition:query', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3800);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3801);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3802);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3803);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3810);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3811);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3812);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3813);

-- ============================================================
-- 十二、委托与客户管理（新增 4 张）
-- ============================================================

CREATE TABLE lims_customer (
    customer_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code     VARCHAR(50) NOT NULL COMMENT '客户编码',
    customer_name     VARCHAR(200) NOT NULL COMMENT '客户名称',
    customer_type     VARCHAR(30) COMMENT '生产企业/经营企业/研发机构/医院/其他',
    contact_person    VARCHAR(50) COMMENT '联系人',
    contact_phone     VARCHAR(30) COMMENT '联系电话',
    contact_email     VARCHAR(100) COMMENT '邮箱',
    address           VARCHAR(300) COMMENT '地址',
    credit_level      VARCHAR(20) COMMENT '信用等级 A/B/C',
    status            VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户档案';

CREATE TABLE lims_entrust_template (
    template_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code     VARCHAR(50) NOT NULL COMMENT '模板编码',
    template_name     VARCHAR(200) NOT NULL COMMENT '模板名称',
    template_fields   TEXT COMMENT '自定义字段定义(JSON)',
    default_remark    VARCHAR(500) COMMENT '默认备注',
    status            VARCHAR(20) DEFAULT 'ENABLED' COMMENT 'ENABLED/DISABLED',
    create_by         VARCHAR(64),
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托单模板';

CREATE TABLE lims_entrust (
    entrust_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    entrust_code      VARCHAR(50) NOT NULL COMMENT '委托单号',
    customer_id       BIGINT NOT NULL COMMENT '客户ID',
    template_id       BIGINT COMMENT '模板ID',
    entrust_date      DATE COMMENT '委托日期',
    sample_name       VARCHAR(200) NOT NULL COMMENT '样品名称',
    specification     VARCHAR(200) COMMENT '规格型号',
    batch_no          VARCHAR(50) COMMENT '批号',
    quantity          DECIMAL(18,4) COMMENT '数量',
    unit_of_measure   VARCHAR(20) COMMENT '单位',
    delivery_date     DATE COMMENT '交付日期',
    report_required   TINYINT DEFAULT 1 COMMENT '是否需要报告',
    report_format     VARCHAR(30) COMMENT '报告格式',
    priority          VARCHAR(20) DEFAULT 'NORMAL' COMMENT 'NORMAL/URGENT/EMERGENCY',
    status            VARCHAR(20) DEFAULT 'DRAFT' COMMENT '草稿/受理评审/评审通过/评审驳回/已登记样品/完成',
    review_by         BIGINT COMMENT '评审人',
    review_time       DATETIME COMMENT '评审时间',
    review_comment    VARCHAR(500) COMMENT '评审意见',
    sample_id         BIGINT COMMENT '贯通生成的样品ID',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托单';

CREATE TABLE lims_entrust_item (
    item_id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    entrust_id        BIGINT NOT NULL COMMENT '委托单ID',
    test_item_name    VARCHAR(200) NOT NULL COMMENT '检验项目名称',
    test_method       VARCHAR(200) COMMENT '检验方法',
    standard_ref      VARCHAR(200) COMMENT '标准依据',
    quantity          DECIMAL(10,2) COMMENT '数量',
    unit_price        DECIMAL(12,2) COMMENT '单价',
    status            VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/DONE',
    create_time       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托检验项目';

-- ============================================================
-- 十三、人员档案（新增 1 张）
-- ============================================================

CREATE TABLE lims_staff (
    staff_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    staff_code        VARCHAR(50) NOT NULL COMMENT '人员工号',
    staff_name        VARCHAR(50) NOT NULL COMMENT '姓名',
    dept_name         VARCHAR(100) COMMENT '部门',
    position          VARCHAR(50) COMMENT '岗位',
    phone             VARCHAR(30) COMMENT '电话',
    email             VARCHAR(100) COMMENT '邮箱',
    hire_date         DATE COMMENT '入职日期',
    status            VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员档案';

-- ============================================================
-- 十四、供应商管理（新增 2 张）
-- ============================================================

CREATE TABLE lims_supplier (
    vendor_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_code       VARCHAR(50) NOT NULL COMMENT '供应商编码',
    vendor_name       VARCHAR(200) NOT NULL COMMENT '供应商名称',
    vendor_type       VARCHAR(30) COMMENT '生产商/经销商/服务商',
    contact_person    VARCHAR(50) COMMENT '联系人',
    contact_phone     VARCHAR(30) COMMENT '联系电话',
    email             VARCHAR(100) COMMENT '邮箱',
    address           VARCHAR(300) COMMENT '地址',
    grade             VARCHAR(20) COMMENT '评估分级 A/B/C',
    approved          TINYINT DEFAULT 0 COMMENT '是否合格名录',
    status            VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    update_by         VARCHAR(64),
    update_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商档案';

CREATE TABLE lims_supplier_purchase (
    purchase_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_id         BIGINT NOT NULL COMMENT '供应商ID',
    purchase_no       VARCHAR(50) COMMENT '采购单号',
    purchase_date     DATE COMMENT '采购日期',
    material_type     VARCHAR(30) COMMENT '试剂/物料/耗材',
    material_name     VARCHAR(200) NOT NULL COMMENT '物料名称',
    reagent_id        BIGINT COMMENT '关联试剂ID',
    quantity          DECIMAL(18,4) COMMENT '数量',
    unit_of_measure   VARCHAR(20) COMMENT '单位',
    unit_price        DECIMAL(12,2) COMMENT '单价',
    total_amount      DECIMAL(14,2) COMMENT '总金额',
    status            VARCHAR(20) DEFAULT 'ORDERED' COMMENT 'ORDERED/RECEIVED',
    create_by         VARCHAR(64),
    create_time       DATETIME,
    remark            TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商采购记录';

-- ============================================================
-- 菜单与权限 SQL（P1 补强：新模块 40xx/41xx/42xx + 补旧 9 页面）
-- ============================================================

-- 二级目录: 委托与客户管理
INSERT INTO `sys_menu` VALUES (4000, '委托与客户管理', 3000, 8, 'entrust', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'phone', 'admin', NULL, NOW(), '', NULL, NULL, '委托与客户管理目录', 0, '1000-01-01 00:00:00', 1);
-- C: 客户档案
INSERT INTO `sys_menu` VALUES (4001, '客户档案', 4000, 1, 'customer', 'lims/entrust/customer', '', 1, 0, 'C', '1', '0', '0', 'lims:customer:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '客户档案菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4002, '客户新增', 4001, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:customer:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4003, '客户修改', 4001, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:customer:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4004, '客户删除', 4001, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:customer:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 委托单模板
INSERT INTO `sys_menu` VALUES (4005, '委托单模板', 4000, 2, 'template', 'lims/entrust/template', '', 1, 0, 'C', '1', '0', '0', 'lims:entrust:template:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '委托单模板菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4006, '模板新增', 4005, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:template:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4007, '模板修改', 4005, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:template:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4008, '模板删除', 4005, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:template:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 委托单管理
INSERT INTO `sys_menu` VALUES (4009, '委托单管理', 4000, 3, 'index', 'lims/entrust/index', '', 1, 0, 'C', '1', '0', '0', 'lims:entrust:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '委托单管理菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4010, '委托新增', 4009, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4011, '委托修改', 4009, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4012, '委托删除', 4009, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4013, '提交受理', 4009, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:submit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4014, '受理评审', 4009, 5, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:review', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4015, '登记样品', 4009, 6, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:entrust:register', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- 二级目录: 培训与资质
INSERT INTO `sys_menu` VALUES (4100, '培训与资质', 3000, 9, 'training', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'education', 'admin', NULL, NOW(), '', NULL, NULL, '培训与资质目录', 0, '1000-01-01 00:00:00', 1);
-- C: 人员档案
INSERT INTO `sys_menu` VALUES (4101, '人员档案', 4100, 1, 'staff', 'lims/training/staff', '', 1, 0, 'C', '1', '0', '0', 'lims:staff:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '人员档案菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4102, '人员新增', 4101, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:staff:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4103, '人员修改', 4101, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:staff:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4104, '人员删除', 4101, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:staff:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 培训计划
INSERT INTO `sys_menu` VALUES (4105, '培训计划', 4100, 2, 'plan', 'lims/training/plan', '', 1, 0, 'C', '1', '0', '0', 'lims:training:plan:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '培训计划菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4106, '计划新增', 4105, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:training:plan:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4107, '计划修改', 4105, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:training:plan:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4108, '计划删除', 4105, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:training:plan:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 培训记录
INSERT INTO `sys_menu` VALUES (4109, '培训记录', 4100, 3, 'record', 'lims/training/record', '', 1, 0, 'C', '1', '0', '0', 'lims:training:record:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '培训记录菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4110, '记录新增', 4109, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:training:record:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4111, '记录修改', 4109, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:training:record:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4112, '记录删除', 4109, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:training:record:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4113, '参训人员', 4109, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:training:record:participant', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 上岗资质
INSERT INTO `sys_menu` VALUES (4114, '上岗资质', 4100, 4, 'qualification', 'lims/training/qualification', '', 1, 0, 'C', '1', '0', '0', 'lims:qualification:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '上岗资质菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4115, '资质新增', 4114, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:qualification:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4116, '资质修改', 4114, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:qualification:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4117, '资质删除', 4114, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:qualification:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4118, '到期提醒', 4114, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:qualification:expire', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- 二级目录: 供应商管理
INSERT INTO `sys_menu` VALUES (4200, '供应商管理', 3000, 10, 'supplier', NULL, '', 1, 0, 'M', '1', '0', '0', '', 'shopping', 'admin', NULL, NOW(), '', NULL, NULL, '供应商管理目录', 0, '1000-01-01 00:00:00', 1);
-- C: 供应商档案
INSERT INTO `sys_menu` VALUES (4201, '供应商档案', 4200, 1, 'index', 'lims/supplier/index', '', 1, 0, 'C', '1', '0', '0', 'lims:supplier:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '供应商档案菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4202, '供应商新增', 4201, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4203, '供应商修改', 4201, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4204, '供应商删除', 4201, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 资质附件
INSERT INTO `sys_menu` VALUES (4205, '资质附件', 4200, 2, 'qualification', 'lims/supplier/qualification', '', 1, 0, 'C', '1', '0', '0', 'lims:supplier:qualification:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '供应商资质菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4206, '资质新增', 4205, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:qualification:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4207, '资质修改', 4205, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:qualification:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4208, '资质删除', 4205, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:qualification:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 供应商审计
INSERT INTO `sys_menu` VALUES (4209, '供应商审计', 4200, 3, 'audit', 'lims/supplier/audit', '', 1, 0, 'C', '1', '0', '0', 'lims:supplier:audit:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '供应商审计菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4210, '审计新增', 4209, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:audit:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4211, '审计修改', 4209, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:audit:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4212, '审计删除', 4209, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:audit:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 合格名录
INSERT INTO `sys_menu` VALUES (4213, '合格名录', 4200, 4, 'approval', 'lims/supplier/approval', '', 1, 0, 'C', '1', '0', '0', 'lims:supplier:approval:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '合格名录菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4214, '名录新增', 4213, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:approval:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4215, '名录修改', 4213, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:approval:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4216, '名录删除', 4213, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:approval:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 采购记录
INSERT INTO `sys_menu` VALUES (4217, '采购记录', 4200, 5, 'purchase', 'lims/supplier/purchase', '', 1, 0, 'C', '1', '0', '0', 'lims:supplier:purchase:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '采购记录菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4218, '采购新增', 4217, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:purchase:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4219, '采购修改', 4217, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:purchase:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (4220, '采购删除', 4217, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:supplier:purchase:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- ============================================================
-- 补旧 9 页面菜单（试剂/质量/检验）
-- ============================================================
-- C: 试剂批次效期 (reagent/lot)
INSERT INTO `sys_menu` VALUES (3415, '试剂批次效期', 3400, 5, 'lot', 'lims/reagent/lot', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:lot:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '试剂批次效期菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3416, '批次新增', 3415, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:lot:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3417, '批次修改', 3415, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:lot:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3418, '批次删除', 3415, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:lot:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 标准品资质 (reagent/standardQualification)
INSERT INTO `sys_menu` VALUES (3425, '标准品资质', 3400, 6, 'standardQualification', 'lims/reagent/standardQualification', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:qualification:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '标准品资质菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3426, '资质新增', 3425, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:qualification:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3427, '资质修改', 3425, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:qualification:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3428, '资质删除', 3425, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:qualification:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 标准品使用 (reagent/standardUsage)
INSERT INTO `sys_menu` VALUES (3429, '标准品使用', 3400, 7, 'standardUsage', 'lims/reagent/standardUsage', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:usage:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '标准品使用菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3430, '使用新增', 3429, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:usage:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3431, '使用删除', 3429, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:usage:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 培养基管理 (reagent/cultureMedia)
INSERT INTO `sys_menu` VALUES (3432, '培养基管理', 3400, 8, 'cultureMedia', 'lims/reagent/cultureMedia', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:media:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '培养基管理菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3433, '培养基新增', 3432, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:media:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3434, '培养基修改', 3432, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:media:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3435, '培养基删除', 3432, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:media:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 菌种流转 (reagent/cultureCollection)
INSERT INTO `sys_menu` VALUES (3436, '菌种流转', 3400, 9, 'cultureCollection', 'lims/reagent/cultureCollection', '', 1, 0, 'C', '1', '0', '0', 'lims:reagent:culture:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '菌种流转菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3437, '菌种新增', 3436, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:culture:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3438, '菌种修改', 3436, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:culture:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3439, '菌种删除', 3436, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:reagent:culture:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 偏差影响评估 (quality/deviationImpact)
INSERT INTO `sys_menu` VALUES (3545, '偏差影响评估', 3500, 7, 'deviationImpact', 'lims/quality/deviationImpact', '', 1, 0, 'C', '1', '0', '0', 'lims:quality:deviationImpact:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '偏差影响评估菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3546, '评估新增', 3545, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:deviationImpact:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3547, '评估修改', 3545, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:deviationImpact:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3548, '评估删除', 3545, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:deviationImpact:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 变更影响评估 (quality/changeImpact)
INSERT INTO `sys_menu` VALUES (3549, '变更影响评估', 3500, 8, 'changeImpact', 'lims/quality/changeImpact', '', 1, 0, 'C', '1', '0', '0', 'lims:quality:changeImpact:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '变更影响评估菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3550, '评估新增', 3549, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:changeImpact:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3551, '评估修改', 3549, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:changeImpact:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3552, '评估删除', 3549, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:changeImpact:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: CAPA任务 (quality/capaTask)
INSERT INTO `sys_menu` VALUES (3553, 'CAPA任务', 3500, 9, 'capaTask', 'lims/quality/capaTask', '', 1, 0, 'C', '1', '0', '0', 'lims:quality:capaTask:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, 'CAPA任务菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3554, '任务新增', 3553, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:capaTask:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3555, '任务修改', 3553, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:capaTask:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3556, '任务删除', 3553, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:capaTask:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3557, '任务完成', 3553, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:quality:capaTask:complete', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
-- C: 微生物检测 (test/micro)
INSERT INTO `sys_menu` VALUES (3240, '微生物检测', 3200, 8, 'micro', 'lims/test/micro', '', 1, 0, 'C', '1', '0', '0', 'lims:test:micro:list', 'form', 'admin', NULL, NOW(), '', NULL, NULL, '微生物检测菜单', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3241, '检测新增', 3240, 1, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:micro:add', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3242, '检测修改', 3240, 2, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:micro:edit', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3243, '结果录入', 3240, 3, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:micro:result', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);
INSERT INTO `sys_menu` VALUES (3244, '检测删除', 3240, 4, '#', '', NULL, 1, 0, 'F', '1', '0', '0', 'lims:test:micro:remove', '#', 'admin', NULL, NOW(), '', NULL, NULL, '', 0, '1000-01-01 00:00:00', 1);

-- 管理员授权 role_id=1
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4000);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4001);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4002);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4003);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4004);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4005);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4006);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4007);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4008);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4009);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4010);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4011);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4012);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4013);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4014);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4015);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4100);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4101);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4102);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4103);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4104);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4105);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4106);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4107);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4108);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4109);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4110);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4111);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4112);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4113);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4114);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4115);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4116);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4117);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4118);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4200);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4201);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4202);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4203);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4204);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4205);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4206);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4207);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4208);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4209);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4210);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4211);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4212);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4213);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4214);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4215);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4216);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4217);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4218);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4219);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4220);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3415);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3416);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3417);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3418);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3425);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3426);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3427);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3428);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3429);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3430);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3431);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3432);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3433);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3434);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3435);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3436);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3437);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3438);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3439);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3545);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3546);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3547);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3548);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3549);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3550);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3551);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3552);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3553);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3554);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3555);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3556);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3557);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3240);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3241);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3242);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3243);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3244);
