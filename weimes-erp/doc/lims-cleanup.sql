-- ============================================================
-- WEIMES ERP → LIMS 数据清理脚本
-- 删除MES专用表（共55张）
-- 执行前请备份！
-- ============================================================

-- ============================================================
-- 1. 生产管理模块 (pro_) — 全部删除（16张）
-- ============================================================
DROP TABLE IF EXISTS pro_workorder;
DROP TABLE IF EXISTS pro_workorder_bom;
DROP TABLE IF EXISTS pro_task;
DROP TABLE IF EXISTS pro_task_issue;
DROP TABLE IF EXISTS pro_feedback;
DROP TABLE IF EXISTS pro_process;
DROP TABLE IF EXISTS pro_process_content;
DROP TABLE IF EXISTS pro_route;
DROP TABLE IF EXISTS pro_route_process;
DROP TABLE IF EXISTS pro_route_product;
DROP TABLE IF EXISTS pro_route_product_bom;
DROP TABLE IF EXISTS pro_client_order;
DROP TABLE IF EXISTS pro_client_order_item;
DROP TABLE IF EXISTS pro_trans_order;
DROP TABLE IF EXISTS pro_user_workstation;
DROP TABLE IF EXISTS pro_workrecord;

-- ============================================================
-- 2. 排班日历模块 (cal_) — 全部删除（7张）
-- ============================================================
DROP TABLE IF EXISTS cal_holiday;
DROP TABLE IF EXISTS cal_plan;
DROP TABLE IF EXISTS cal_plan_team;
DROP TABLE IF EXISTS cal_shift;
DROP TABLE IF EXISTS cal_team;
DROP TABLE IF EXISTS cal_team_member;
DROP TABLE IF EXISTS cal_teamshift;

-- ============================================================
-- 3. 工装管理模块 (tm_) — 全部删除（2张）
-- ============================================================
DROP TABLE IF EXISTS tm_tool;
DROP TABLE IF EXISTS tm_tool_type;

-- ============================================================
-- 4. 会员模块 (huiyuan_) — 全部删除（8张）
-- ============================================================
DROP TABLE IF EXISTS huiyuan_age;
DROP TABLE IF EXISTS huiyuan_fengongsi;
DROP TABLE IF EXISTS huiyuan_huoyuedu;
DROP TABLE IF EXISTS huiyuan_sex;
DROP TABLE IF EXISTS huiyuan_work;
DROP TABLE IF EXISTS huiyuan_wxtl;
DROP TABLE IF EXISTS huiyuan_wxtlshuliang;
DROP TABLE IF EXISTS huiyuan_xueli;

-- ============================================================
-- 5. 主数据模块 (md_) — 删除MES专用表（9张）
-- ============================================================
DROP TABLE IF EXISTS md_workstation;
DROP TABLE IF EXISTS md_workstation_machine;
DROP TABLE IF EXISTS md_workstation_tool;
DROP TABLE IF EXISTS md_workstation_worker;
DROP TABLE IF EXISTS md_product_bom;
DROP TABLE IF EXISTS md_product_sop;
DROP TABLE IF EXISTS md_product_sip;
DROP TABLE IF EXISTS md_product_color;
DROP TABLE IF EXISTS md_produt_sop;

-- ============================================================
-- 6. 仓库模块 (wm_) — 删除MES专用表（8张）
-- ============================================================
DROP TABLE IF EXISTS wm_rt_salse;
DROP TABLE IF EXISTS wm_rt_salse_line;
DROP TABLE IF EXISTS wm_package;
DROP TABLE IF EXISTS wm_package_line;
DROP TABLE IF EXISTS wm_product_produce;
DROP TABLE IF EXISTS wm_product_produce_line;
DROP TABLE IF EXISTS wm_item_consume;
DROP TABLE IF EXISTS wm_item_consume_line;

-- ============================================================
-- 7. HRM模块 (hrm_) — 删除不适用表（5张）
-- ============================================================
DROP TABLE IF EXISTS hrm_interview_record;
DROP TABLE IF EXISTS hrm_attendance_records;
DROP TABLE IF EXISTS hrm_attendance_rules;
DROP TABLE IF EXISTS hrm_leave_records;
DROP TABLE IF EXISTS hrm_overtime_records;

-- ============================================================
-- 清理完成，共删除 55 张表
-- ============================================================
