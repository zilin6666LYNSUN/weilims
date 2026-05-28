"""
往LIMS数据库插入测试数据
"""
import pymysql
from datetime import datetime, timedelta
import random

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()

# 先检查现有数据量
tables = [
    'lims_instrument_type', 'lims_instrument', 'lims_instrument_calibration', 'lims_instrument_maintenance', 'lims_instrument_reservation',
    'lims_reagent', 'lims_reference_standard', 'lims_culture_collection', 'lims_reagent_stock_log',
    'lims_specification', 'lims_specification_line', 'lims_test_method', 'lims_test_template', 'lims_test_template_item',
    'lims_sample', 'lims_sample_test_item', 'lims_test_order', 'lims_test_result',
    'lims_retain_sample', 'lims_sample_dispose', 'lims_sample_status_log',
    'lims_oos_investigation', 'lims_oos_phase_record',
    'lims_deviation', 'lims_capa', 'lims_change_control',
    'lims_stability_protocol', 'lims_stability_test',
    'lims_audit_trail', 'lims_electronic_signature', 'lims_micro_test_record',
]

print("=== 当前数据量 ===")
for t in tables:
    try:
        cur.execute(f'SELECT COUNT(*) FROM {t}')
        cnt = cur.fetchone()[0]
        if cnt > 0:
            print(f'  {t}: {cnt} 条')
    except Exception as e:
        print(f'  {t}: ERROR - {e}')

conn.close()
