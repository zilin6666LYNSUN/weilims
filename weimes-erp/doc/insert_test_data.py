"""
往LIMS数据库插入完整测试数据 - 基于实际表结构
"""
import pymysql
from datetime import datetime, timedelta

conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()
now = datetime.now()

# 先清理已有数据（按外键依赖顺序）
print("0. 清理已有数据...")
for t in ['lims_electronic_signature','lims_audit_trail','lims_oos_phase_record','lims_oos_investigation',
          'lims_stability_test','lims_stability_protocol','lims_change_control','lims_capa','lims_deviation',
          'lims_sample_status_log','lims_sample_dispose','lims_retain_sample',
          'lims_test_result','lims_sample_test_item','lims_test_order',
          'lims_micro_test_record','lims_sample',
          'lims_test_template_item','lims_test_template',
          'lims_specification_line','lims_specification',
          'lims_test_method',
          'lims_reagent_stock_log','lims_culture_collection','lims_reference_standard','lims_reagent',
          'lims_instrument_reservation','lims_instrument_maintenance','lims_instrument_calibration','lims_instrument',
          'lims_instrument_type']:
    try:
        cur.execute(f'DELETE FROM {t}')
    except:
        pass
conn.commit()
print("  清理完成")

# ========== 1. 仪器类型 ==========
print("1. 插入仪器类型...")
instrument_types = [
    ('IT001', 'HPLC', 'CHROMATOGRAPHY', None),
    ('IT002', 'GC', 'CHROMATOGRAPHY', None),
    ('IT003', 'UV-Vis', 'SPECTROPHOTOMETER', None),
    ('IT005', '分析天平', 'BALANCE', None),
    ('IT006', '溶出仪', 'DISSOLUTION', None),
    ('IT007', 'pH计', 'ELECTROCHEMICAL', None),
    ('IT008', '微生物培养箱', 'MICROBIOLOGY', None),
]
cur.executemany('INSERT INTO lims_instrument_type(type_code, type_name, category, parent_id) VALUES(%s,%s,%s,%s)', instrument_types)
print(f"  插入 {len(instrument_types)} 条")

# ========== 2. 仪器 ==========
print("2. 插入仪器...")
instruments = [
    ('INST001', 'Agilent 1260 HPLC', 1, 'IT001', 'HPLC', 'Agilent', '1260 Infinity II', 'SN-HPLC-001', 'AN-001', 'Agilent Technologies', 1, '理化实验室', 'A区-01', '2024-01-15', 'PASS', 'PASS', 'PASS', 365, '2025-05-01', '2026-05-01', 'VALID', 180, '2025-06-01', '2025-12-01', 'RS232', 'ACTIVE', 'admin', now),
    ('INST002', 'Agilent 7890B GC', 2, 'IT002', 'GC', 'Agilent', '7890B', 'SN-GC-001', 'AN-002', 'Agilent Technologies', 1, '理化实验室', 'A区-02', '2024-02-20', 'PASS', 'PASS', 'PASS', 365, '2025-04-15', '2026-04-15', 'VALID', 180, '2025-05-15', '2025-11-15', 'LAN', 'ACTIVE', 'admin', now),
    ('INST003', 'Shimadzu UV-2600i', 3, 'IT003', 'UV-Vis', 'Shimadzu', 'UV-2600i', 'SN-UV-001', 'AN-003', 'Shimadzu', 1, '理化实验室', 'A区-03', '2024-03-10', 'PASS', 'PASS', 'PASS', 365, '2025-06-01', '2026-06-01', 'VALID', 180, '2025-07-01', '2026-01-01', 'USB', 'ACTIVE', 'admin', now),
    ('INST004', 'Mettler XS205', 4, 'IT005', '分析天平', 'Mettler Toledo', 'XS205', 'SN-BAL-001', 'AN-004', 'Mettler Toledo', 1, '天平室', 'B区-01', '2024-01-05', 'PASS', 'PASS', 'PASS', 365, '2025-05-10', '2026-05-10', 'VALID', 90, '2025-08-01', '2025-11-01', 'RS232', 'ACTIVE', 'admin', now),
    ('INST005', 'Sotax AT7', 5, 'IT006', '溶出仪', 'Sotax', 'AT7smart', 'SN-DIS-001', 'AN-005', 'Sotax', 2, '溶出室', 'C区-01', '2024-04-01', 'PASS', 'PASS', 'PASS', 365, '2025-03-01', '2026-03-01', 'VALID', 180, '2025-06-15', '2025-12-15', 'LAN', 'ACTIVE', 'admin', now),
    ('INST006', 'Mettler SevenExcellence', 6, 'IT007', 'pH计', 'Mettler Toledo', 'SevenExcellence', 'SN-PH-001', 'AN-006', 'Mettler Toledo', 2, '理化实验室', 'A区-04', '2024-05-01', 'PASS', 'PASS', 'PASS', 180, '2025-07-01', '2026-01-01', 'VALID', 90, '2025-08-15', '2025-11-15', 'USB', 'ACTIVE', 'admin', now),
    ('INST007', 'Thermo Heratherm', 7, 'IT008', '微生物培养箱', 'Thermo Fisher', 'IMC18', 'SN-INC-001', 'AN-007', 'Thermo Fisher', 3, '微生物室', 'D区-01', '2024-06-01', 'PASS', 'PASS', 'PASS', 365, '2025-04-01', '2026-04-01', 'VALID', 180, '2025-05-01', '2025-11-01', 'LAN', 'ACTIVE', 'admin', now),
]
cur.executemany('''INSERT INTO lims_instrument(instrument_code, instrument_name, instrument_type_id, instrument_type_code, instrument_type_name,
    brand, model, serial_number, asset_number, manufacturer, lab_id, lab_name, location, purchase_date,
    iq_status, oq_status, pq_status, calibration_cycle, last_calibration, next_calibration, calibration_status,
    maintenance_cycle, last_maintenance, next_maintenance, interface_type, status, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', instruments)
print(f"  插入 {len(instruments)} 条")

# ========== 3. 仪器校准 ==========
print("3. 插入仪器校准记录...")
# calibration_id, instrument_id, calibration_code, calibration_type, calibration_org, calibration_date, next_calibration, calibration_result, certificate_no, certificate_file, calibrated_by, approved_by, status
calibrations = [
    (1, 'CAL-2025-001', 'ANNUAL', '省计量院', '2025-05-01', '2026-05-01', 'PASS', 'CERT-2025-001', None, 1, 1, 'ACTIVE'),
    (2, 'CAL-2025-002', 'ANNUAL', '省计量院', '2025-04-15', '2026-04-15', 'PASS', 'CERT-2025-002', None, 1, 1, 'ACTIVE'),
    (3, 'CAL-2025-003', 'ANNUAL', '省计量院', '2025-06-01', '2026-06-01', 'PASS', 'CERT-2025-003', None, 1, 1, 'ACTIVE'),
    (4, 'CAL-2025-004', 'ANNUAL', '省计量院', '2025-05-10', '2026-05-10', 'PASS', 'CERT-2025-004', None, 1, 1, 'ACTIVE'),
    (5, 'CAL-2025-005', 'ANNUAL', '省计量院', '2025-03-01', '2026-03-01', 'PASS', 'CERT-2025-005', None, 1, 1, 'ACTIVE'),
]
cur.executemany('''INSERT INTO lims_instrument_calibration(instrument_id, calibration_code, calibration_type, calibration_org,
    calibration_date, next_calibration, calibration_result, certificate_no, certificate_file, calibrated_by, approved_by, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', calibrations)
print(f"  插入 {len(calibrations)} 条")

# ========== 4. 仪器维护 ==========
print("4. 插入仪器维护记录...")
maintenances = [
    (1, 'PREVENTIVE', '2025-06-01', 1, '年度预防性维护，更换密封圈', 'COMPLETED'),
    (2, 'PREVENTIVE', '2025-05-15', 1, '年度预防性维护，清洗进样器', 'COMPLETED'),
    (4, 'CORRECTIVE', '2025-07-10', 1, '校准偏差修正', 'COMPLETED'),
    (5, 'PREVENTIVE', '2025-06-15', 1, '更换桨叶', 'COMPLETED'),
]
cur.executemany('''INSERT INTO lims_instrument_maintenance(instrument_id, maintenance_type, maintenance_date, performed_by, description, status)
    VALUES(%s,%s,%s,%s,%s,%s)''', maintenances)
print(f"  插入 {len(maintenances)} 条")

# ========== 5. 仪器预约 ==========
print("5. 插入仪器预约记录...")
reservations = [
    (1, None, 1, '2025-05-27', '09:00:00', '17:00:00', '原料阿司匹林含量测定', 'COMPLETED'),
    (2, None, 1, '2025-05-28', '09:00:00', '17:00:00', '原料残留溶剂检测', 'COMPLETED'),
    (3, None, 1, '2025-05-29', '09:00:00', '12:00:00', '成品吸光度检查', 'ACTIVE'),
]
cur.executemany('''INSERT INTO lims_instrument_reservation(instrument_id, test_order_id, tester_id, reserved_date, start_time, end_time, purpose, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s)''', reservations)
print(f"  插入 {len(reservations)} 条")

# ========== 6. 试剂 ==========
print("6. 插入试剂...")
# reagent_id, reagent_code, reagent_name, reagent_type, cas_number, manufacturer, hazard_level, storage_condition, status
reagents = [
    ('RGT001', '甲醇', 'SOLVENT', '67-56-1', 'Merck', 'FLAMMABLE', 'ROOM_TEMP', 'ACTIVE'),
    ('RGT002', '乙腈', 'SOLVENT', '75-05-8', 'Merck', 'FLAMMABLE', 'ROOM_TEMP', 'ACTIVE'),
    ('RGT003', '磷酸二氢钾', 'REAGENT', '7778-77-0', '国药集团', 'NONE', 'ROOM_TEMP', 'ACTIVE'),
    ('RGT004', '氢氧化钠', 'REAGENT', '1310-73-2', '国药集团', 'CORROSIVE', 'ROOM_TEMP', 'ACTIVE'),
    ('RGT005', '盐酸', 'REAGENT', '7647-01-0', '国药集团', 'CORROSIVE', 'ROOM_TEMP', 'ACTIVE'),
    ('RGT006', '纯化水', 'SOLVENT', '7732-18-5', '自制', 'NONE', 'ROOM_TEMP', 'ACTIVE'),
    ('RGT007', '胰酪大豆胨液体培养基', 'MEDIUM', None, '北京陆桥', 'NONE', 'ROOM_TEMP', 'ACTIVE'),
]
cur.executemany('''INSERT INTO lims_reagent(reagent_code, reagent_name, reagent_type, cas_number, manufacturer, hazard_level, storage_condition, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s)''', reagents)
print(f"  插入 {len(reagents)} 条")

# ========== 7. 标准品 ==========
print("7. 插入标准品...")
standards = [
    ('STD001', '阿司匹林对照品', 'CHEMICAL', '中国食品药品检定研究院', '100113-202010', '20200501', 99.8, '%', '2024-01-15', None, '2027-01-15', 'ROOM_TEMP', '标准品柜A-01', 100, 'mg', 'QUALIFIED', '2025-06-01', '2026-06-01', None, None, '631-57-2', 'ACTIVE'),
    ('STD002', '对乙酰氨基酚对照品', 'CHEMICAL', '中国食品药品检定研究院', '100018-202010', '20200301', 99.9, '%', '2024-02-20', None, '2027-02-20', 'ROOM_TEMP', '标准品柜A-02', 80, 'mg', 'QUALIFIED', '2025-05-15', '2026-05-15', None, None, '103-90-2', 'ACTIVE'),
    ('STD003', '维生素C对照品', 'CHEMICAL', '中国食品药品检定研究院', '100425-202010', '20200801', 99.7, '%', '2024-03-10', None, '2027-03-10', 'COLD_2_8', '冰箱B-01', 60, 'mg', 'QUALIFIED', '2025-04-01', '2026-04-01', None, None, '50-81-7', 'ACTIVE'),
]
cur.executemany('''INSERT INTO lims_reference_standard(standard_code, standard_name, standard_type, source, catalog_number, lot_number,
    potency, potency_unit, received_date, opened_date, expire_date, storage_condition, storage_location,
    remaining_quantity, unit, qualification_status, qualification_date, next_qualification,
    coa_file, structure_file, cas_number, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', standards)
print(f"  插入 {len(standards)} 条")

# ========== 8. 菌种 ==========
print("8. 插入菌种...")
cultures = [
    ('CC001', '大肠埃希菌', 'ATCC 25922', 'ATCC', '冷冻干燥', '冰箱C-01', 5, 15, '2024-01-10', '2027-01-10', '2025-05-01', 'VIABLE', 'ACTIVE'),
    ('CC002', '金黄色葡萄球菌', 'ATCC 6538', 'ATCC', '冷冻干燥', '冰箱C-02', 3, 15, '2024-02-15', '2027-02-15', '2025-06-01', 'VIABLE', 'ACTIVE'),
    ('CC003', '枯草芽孢杆菌', 'ATCC 6633', 'ATCC', '冷冻干燥', '冰箱C-03', 4, 15, '2024-03-20', '2027-03-20', '2025-07-01', 'VIABLE', 'ACTIVE'),
]
cur.executemany('''INSERT INTO lims_culture_collection(culture_code, culture_name, strain_number, source, storage_method, storage_location,
    passage_count, max_passage, received_date, expire_date, viability_test_date, viability_result, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', cultures)
print(f"  插入 {len(cultures)} 条")

# ========== 9. 试剂出入库 ==========
print("9. 插入试剂出入库记录...")
stocklogs = [
    (1, 'IN', 50.0, 50.0, None, '采购入库', 1, 'admin', now, 'admin', now),
    (1, 'OUT', 10.0, 40.0, None, '日常消耗', 1, 'admin', now, 'admin', now),
    (2, 'IN', 40.0, 40.0, None, '采购入库', 1, 'admin', now, 'admin', now),
    (2, 'OUT', 5.0, 35.0, None, '日常消耗', 1, 'admin', now, 'admin', now),
    (6, 'IN', 100.0, 100.0, None, '制备入库', 1, 'admin', now, 'admin', now),
]
cur.executemany('''INSERT INTO lims_reagent_stock_log(reagent_id, operation_type, quantity, remain_qty, related_order_no, purpose, operator_id, operator_name, operation_time, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', stocklogs)
print(f"  插入 {len(stocklogs)} 条")

# ========== 10. 检验方法 ==========
print("10. 插入检验方法...")
methods = [
    ('M001', '阿司匹林含量测定-HPLC法', 'V1.0', 'ChP2020 二部', 'HPLC', 'ASSAY', 'VALIDATED', '2024-06-01', 1, 'ACTIVE', '2024-06-01', '2029-06-01', 'admin', now),
    ('M002', '残留溶剂测定-GC法', 'V1.0', 'ChP2020 通则0861', 'GC', 'LIMIT', 'VALIDATED', '2024-07-01', 1, 'ACTIVE', '2024-07-01', '2029-07-01', 'admin', now),
    ('M003', '吸光度检查-UV法', 'V1.0', 'ChP2020 通则0401', 'UV', 'IDENTITY', 'VALIDATED', '2024-08-01', 1, 'ACTIVE', '2024-08-01', '2029-08-01', 'admin', now),
    ('M004', 'pH值测定', 'V1.0', 'ChP2020 通则0631', 'PH', 'GENERAL', 'VALIDATED', '2024-05-01', 1, 'ACTIVE', '2024-05-01', '2029-05-01', 'admin', now),
    ('M005', '溶出度测定', 'V1.0', 'ChP2020 通则0931', 'DISSOLUTION', 'ASSAY', 'VALIDATED', '2024-09-01', 1, 'ACTIVE', '2024-09-01', '2029-09-01', 'admin', now),
    ('M006', '微生物限度检查', 'V1.0', 'ChP2020 通则1105/1106', 'MICRO', 'MICROBIOLOGY', 'VALIDATED', '2024-10-01', 1, 'ACTIVE', '2024-10-01', '2029-10-01', 'admin', now),
]
cur.executemany('''INSERT INTO lims_test_method(method_code, method_name, method_version, pharmacopoeia_ref, method_type, method_category,
    validation_status, validated_date, approved_by, status, effective_date, expiry_date, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', methods)
print(f"  插入 {len(methods)} 条")

# ========== 11. 质量标准 ==========
print("11. 插入质量标准...")
specifications = [
    ('SPEC001', '阿司匹林原料质量标准', 1, 'RAW', 'V1.0', 'ChP2020', '2024-06-01', '2029-06-01', 'ACTIVE', 'admin', now),
    ('SPEC002', '阿司匹林片成品质量标准', 1, 'FINISHED', 'V1.0', 'ChP2020', '2024-06-01', '2029-06-01', 'ACTIVE', 'admin', now),
    ('SPEC003', '对乙酰氨基酚片成品质量标准', 1, 'FINISHED', 'V1.0', 'ChP2020', '2024-07-01', '2029-07-01', 'ACTIVE', 'admin', now),
]
cur.executemany('''INSERT INTO lims_specification(spec_code, spec_name, product_id, spec_type, spec_version, pharmacopoeia_ref,
    effective_date, expiry_date, status, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', specifications)
print(f"  插入 {len(specifications)} 条")

# ========== 12. 标准行 ==========
print("12. 插入标准行...")
spec_lines = [
    (1, 1, '含量', 1, '98.0%~102.0%', 98.0, 102.0, '%', 1, 1, '0.1'),
    (1, 2, '有关物质', 1, '≤0.5%', 0, 0.5, '%', 2, 1, '0.01'),
    (1, 3, '残留溶剂-甲醇', 2, '≤3000ppm', 0, 3000, 'ppm', 3, 1, '1'),
    (1, 4, '残留溶剂-乙腈', 2, '≤410ppm', 0, 410, 'ppm', 4, 1, '1'),
    (1, 5, '干燥失重', 4, '≤0.5%', 0, 0.5, '%', 5, 1, '0.01'),
    (2, 6, '含量', 1, '95.0%~105.0%', 95.0, 105.0, '%', 1, 1, '0.1'),
    (2, 7, '溶出度', 5, '≥80%', 80, 100, '%', 2, 1, '0.1'),
    (2, 8, '微生物限度', 6, '需氧菌≤10³cfu/g', 0, 1000, 'cfu/g', 3, 1, '1'),
    (3, 9, '含量', 1, '95.0%~105.0%', 95.0, 105.0, '%', 1, 1, '0.1'),
    (3, 10, '溶出度', 5, '≥75%', 75, 100, '%', 2, 1, '0.1'),
]
cur.executemany('''INSERT INTO lims_specification_line(spec_id, test_item_id, test_item_name, method_id,
    specification_val, lower_limit, upper_limit, unit, sequence, is_mandatory, rounding_rule)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', spec_lines)
print(f"  插入 {len(spec_lines)} 条")

# ========== 13. 检验模板 ==========
print("13. 插入检验模板...")
templates = [
    ('TPL001', '阿司匹林原料全检模板', 1, '阿司匹林', 1, 'RAW', 'ACTIVE', 'admin', now),
    ('TPL002', '阿司匹林片成品全检模板', 1, '阿司匹林片', 2, 'FINISHED', 'ACTIVE', 'admin', now),
    ('TPL003', '对乙酰氨基酚片成品全检模板', 1, '对乙酰氨基酚片', 3, 'FINISHED', 'ACTIVE', 'admin', now),
]
cur.executemany('''INSERT INTO lims_test_template(template_code, template_name, product_id, product_name, specification_id, test_type, status, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s)''', templates)
print(f"  插入 {len(templates)} 条")

# ========== 14. 模板检测项 ==========
print("14. 插入模板检测项...")
template_items = [
    (1, '含量', 'TI001', 1, '阿司匹林含量测定-HPLC法', 1, 'QUANTITATIVE', 1, 1, 'admin', now),
    (1, '有关物质', 'TI002', 1, '阿司匹林含量测定-HPLC法', 2, 'QUANTITATIVE', 1, 2, 'admin', now),
    (1, '残留溶剂', 'TI003', 2, '残留溶剂测定-GC法', 3, 'QUANTITATIVE', 1, 3, 'admin', now),
    (1, '干燥失重', 'TI005', 4, 'pH值测定', 5, 'QUANTITATIVE', 1, 4, 'admin', now),
    (2, '含量', 'TI006', 1, '阿司匹林含量测定-HPLC法', 6, 'QUANTITATIVE', 1, 1, 'admin', now),
    (2, '溶出度', 'TI007', 5, '溶出度测定', 7, 'QUANTITATIVE', 1, 2, 'admin', now),
    (2, '微生物限度', 'TI008', 6, '微生物限度检查', 8, 'QUALITATIVE', 1, 3, 'admin', now),
    (3, '含量', 'TI009', 1, '阿司匹林含量测定-HPLC法', 9, 'QUANTITATIVE', 1, 1, 'admin', now),
    (3, '溶出度', 'TI010', 5, '溶出度测定', 10, 'QUANTITATIVE', 1, 2, 'admin', now),
]
cur.executemany('''INSERT INTO lims_test_template_item(template_id, test_item_name, test_item_code, method_id, method_name,
    spec_line_id, test_item_type, is_required, sort_order, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', template_items)
print(f"  插入 {len(template_items)} 条")

# ========== 15. 样品 ==========
print("15. 插入样品...")
samples = [
    ('SMP202505001', '阿司匹林原料-批号B20250501', 'RAW', 'WORKSHOP', 'B20250501', 1, 'P001', '阿司匹林', None, None, None, None, '500g/kg', 2.0, 2.0, 'kg', '2027-05-01', 'ROOM_TEMP', '原料仓库A-01', 'URGENT', now, 1, 1, 1, 1, 'TESTING', None, 'admin', now),
    ('SMP202505002', '阿司匹林片-批号P20250501', 'FINISHED', 'WORKSHOP', 'P20250501', 1, 'P002', '阿司匹林片', None, None, None, None, '0.3g/片', 1.0, 1.0, '盒', '2027-05-01', 'ROOM_TEMP', '成品仓库B-01', 'NORMAL', now, 1, 1, 2, 2, 'TESTING', None, 'admin', now),
    ('SMP202505003', '对乙酰氨基酚片-批号P20250502', 'FINISHED', 'WORKSHOP', 'P20250502', 1, 'P003', '对乙酰氨基酚片', None, None, None, None, '0.5g/片', 1.0, 1.0, '盒', '2027-06-01', 'ROOM_TEMP', '成品仓库B-02', 'NORMAL', now, 1, 1, 3, 3, 'PENDING', None, 'admin', now),
    ('SMP202505004', '阿司匹林原料-批号B20250502', 'RAW', 'VENDOR', 'B20250502', 1, 'P001', '阿司匹林', 1, 'V001', '供应商A', 'VB20250502', '500g/kg', 3.0, 3.0, 'kg', '2027-06-01', 'ROOM_TEMP', '原料仓库A-02', 'NORMAL', now, 1, 1, 1, 1, 'PENDING', None, 'admin', now),
]
cur.executemany('''INSERT INTO lims_sample(sample_code, sample_name, sample_type, sample_source, batch_no,
    product_id, product_code, product_name, vendor_id, vendor_code, vendor_name, vendor_batch,
    specification, quantity_received, quantity_remaining, unit_of_measure, expire_date,
    storage_condition, storage_location, priority, receive_date, sampler_id, inspector_id,
    specification_id, test_template_id, status, proc_inst_id, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', samples)
print(f"  插入 {len(samples)} 条")

# ========== 16. 样品检测项 ==========
print("16. 插入样品检测项...")
sample_items = [
    (1, 1, 'TI001', '含量', 1, 1, 1, 'PENDING'),
    (1, 2, 'TI002', '有关物质', 1, 2, 1, 'PENDING'),
    (1, 3, 'TI003', '残留溶剂-甲醇', 2, 3, 1, 'PENDING'),
    (2, 6, 'TI006', '含量', 1, 6, 1, 'PENDING'),
    (2, 7, 'TI007', '溶出度', 5, 7, 1, 'PENDING'),
    (2, 8, 'TI008', '微生物限度', 6, 8, 1, 'PENDING'),
    (3, 9, 'TI009', '含量', 1, 9, 1, 'PENDING'),
    (3, 10, 'TI010', '溶出度', 5, 10, 1, 'PENDING'),
]
cur.executemany('''INSERT INTO lims_sample_test_item(sample_id, test_item_id, test_item_code, test_item_name, method_id, specification_line_id, is_mandatory, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s)''', sample_items)
print(f"  插入 {len(sample_items)} 条")

# ========== 17. 检验订单 ==========
print("17. 插入检验订单...")
# test_order_id, test_order_code, test_order_name, sample_id, sample_code, specification_id, test_template_id,
# source_doc_type, source_doc_id, source_doc_code, test_type, priority,
# planned_start_date, planned_end_date, actual_start_date, actual_end_date,
# tester_id, reviewer_id, approver_id, check_result, conclusion, report_no, proc_inst_id, status, create_by, create_time
orders = [
    ('TO202505001', '阿司匹林原料全检', 1, 'SMP202505001', 1, 1, None, None, None, 'ROUTINE', 'URGENT', now, now + timedelta(days=7), now, None, 1, None, None, None, None, None, None, 'TESTING', 'admin', now),
    ('TO202505002', '阿司匹林片成品全检', 2, 'SMP202505002', 2, 2, None, None, None, 'ROUTINE', 'NORMAL', now, now + timedelta(days=7), now, None, 1, None, None, None, None, None, None, 'TESTING', 'admin', now),
    ('TO202505003', '对乙酰氨基酚片成品全检', 3, 'SMP202505003', 3, 3, None, None, None, 'ROUTINE', 'NORMAL', now, now + timedelta(days=7), None, None, None, None, None, None, None, None, None, 'PENDING', 'admin', now),
]
cur.executemany('''INSERT INTO lims_test_order(test_order_code, test_order_name, sample_id, sample_code, specification_id, test_template_id,
    source_doc_type, source_doc_id, source_doc_code, test_type, priority,
    planned_start_date, planned_end_date, actual_start_date, actual_end_date,
    tester_id, reviewer_id, approver_id, check_result, conclusion, report_no, proc_inst_id, status, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', orders)
print(f"  插入 {len(orders)} 条")

# ========== 18. 检验结果 ==========
print("18. 插入检验结果...")
results = [
    (1, 1, '含量', 1, 'QUANTITATIVE', '99.52', None, '99.52', '99.5', 'ROUND_HALF_UP', None, 98.0, 102.0, '98.0%~102.0%', 'PASS', 0, 0, 1, now, None, None, 1, None, '20200501', None, None, 'admin', now),
    (1, 2, '有关物质', 1, 'QUANTITATIVE', '0.12', None, '0.12', '0.1', 'ROUND_HALF_UP', None, 0, 0.5, '≤0.5%', 'PASS', 0, 0, 1, now, None, None, 1, None, None, None, None, 'admin', now),
    (1, 3, '残留溶剂-甲醇', 2, 'QUANTITATIVE', '523', None, '523', '523', 'ROUND_HALF_UP', None, 0, 3000, '≤3000ppm', 'PASS', 0, 0, 1, now, None, None, 2, None, None, None, None, 'admin', now),
    (2, 6, '含量', 1, 'QUANTITATIVE', '98.76', None, '98.76', '98.8', 'ROUND_HALF_UP', None, 95.0, 105.0, '95.0%~105.0%', 'PASS', 0, 0, 1, now, None, None, 1, None, '20200501', None, None, 'admin', now),
    (2, 7, '溶出度', 5, 'QUANTITATIVE', '92.3', None, '92.3', '92', 'ROUND_HALF_UP', None, 80, 100, '≥80%', 'PASS', 0, 0, 1, now, None, None, 5, None, None, None, None, 'admin', now),
]
cur.executemany('''INSERT INTO lims_test_result(test_order_id, spec_line_id, test_item_name, method_id, test_item_type,
    original_value, instrument_value, calculated_value, reported_value, rounding_rule, formula,
    lower_limit, upper_limit, specification_val, result_conclusion, is_oos, is_oot,
    tester_id, test_date, reviewer_id, review_date, instrument_id, reagent_lot, standard_lot, test_condition, remark,
    create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', results)
print(f"  插入 {len(results)} 条")

# ========== 19. 留样 ==========
print("19. 插入留样...")
retains = [
    ('RET001', 1, '阿司匹林', 'B20250501', 'LONG_TERM', 500.0, 480.0, '留样室A-01', 'ROOM_TEMP', now, '2027-05-01', '90', None, now + timedelta(days=90), None, None, None, None, 'ACTIVE'),
    ('RET002', 2, '阿司匹林片', 'P20250501', 'LONG_TERM', 200.0, 190.0, '留样室B-01', 'ROOM_TEMP', now, '2027-05-01', '90', None, now + timedelta(days=90), None, None, None, None, 'ACTIVE'),
    ('RET003', 3, '对乙酰氨基酚片', 'P20250502', 'STABILITY', 300.0, 300.0, '稳定性考察室C-01', 'ROOM_TEMP', now, '2027-06-01', '30', None, now + timedelta(days=30), None, None, None, None, 'ACTIVE'),
]
cur.executemany('''INSERT INTO lims_retain_sample(retain_code, sample_id, product_name, batch_no, retain_type,
    retain_quantity, remain_quantity, storage_location, storage_condition, retain_date, expire_date,
    observation_cycle, last_observation, next_observation, observation_result, dispose_type, dispose_date, dispose_approver, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', retains)
print(f"  插入 {len(retains)} 条")

# ========== 20. 样品处置 ==========
print("20. 插入样品处置...")
disposes = [
    (1, 'DESTROY', 0.2, now, 1, 1, '检验完成，剩余样品处置'),
]
cur.executemany('''INSERT INTO lims_sample_dispose(sample_id, dispose_type, dispose_quantity, dispose_date, disposed_by, approved_by, reason)
    VALUES(%s,%s,%s,%s,%s,%s,%s)''', disposes)
print(f"  插入 {len(disposes)} 条")

# ========== 21. OOS调查 ==========
print("21. 插入OOS调查...")
oos_investigations = [
    ('OOS202505001', 1, 2, 'OOS', 'LAB_INVESTIGATION', '92.3', '95.0~105.0', '初步调查：检查仪器、试剂、操作过程', 0, None, None, None, None, None, None, None, None, 1, 1, None, 'IN_PROGRESS', None, 'admin', now),
]
cur.executemany('''INSERT INTO lims_oos_investigation(investigation_no, test_result_id, test_order_id, type, phase,
    oos_value, specification_limit, lab_investigation, is_lab_cause, lab_error_type,
    full_investigation, root_cause, capa_ids, corrective_action,
    original_result_valid, retest_result, conclusion,
    investigator_id, approver_id, close_date, status, proc_inst_id, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', oos_investigations)
print(f"  插入 {len(oos_investigations)} 条")

# ========== 22. 偏差 ==========
print("22. 插入偏差...")
# deviation_id, deviation_code, deviation_title, deviation_type, deviation_category, severity_level,
# discovery_date, discovery_source, affected_product, affected_batch, description, immediate_action,
# is_capa_needed, investigation_result, root_cause, capa_ids, effectiveness_review,
# owner_id, due_date, close_date, proc_inst_id, status, create_by, create_time
deviations = [
    ('DEV202505001', 'HPLC基线漂移偏差', 'PROCESS', 'ANALYTICAL', 'MINOR', now, 'LAB_TEST', '阿司匹林', 'B20250501', 'HPLC基线异常漂移', '暂停检验，检查仪器', 1, '调查中', '实验室温度波动', None, None, 1, now + timedelta(days=30), None, None, 'IN_PROGRESS', 'admin', now),
]
cur.executemany('''INSERT INTO lims_deviation(deviation_code, deviation_title, deviation_type, deviation_category, severity_level,
    discovery_date, discovery_source, affected_product, affected_batch, description, immediate_action,
    is_capa_needed, investigation_result, root_cause, capa_ids, effectiveness_review,
    owner_id, due_date, close_date, proc_inst_id, status, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', deviations)
print(f"  插入 {len(deviations)} 条")

# ========== 23. CAPA ==========
print("23. 插入CAPA...")
# capa_id, capa_code, capa_type, source_type, source_code, description, root_cause,
# corrective_action, preventive_action, responsible_id, due_date, completion_date,
# effectiveness_result, effectiveness_date, status, proc_inst_id, create_by, create_time
capas = [
    ('CAPA202505001', 'CORRECTIVE', 'DEVIATION', 'DEV202505001', '安装空调恒温系统，定期检查柱温箱', '实验室温度波动', '安装恒温系统', '制定温控SOP', 1, now + timedelta(days=30), None, None, None, 'IN_PROGRESS', None, 'admin', now),
]
cur.executemany('''INSERT INTO lims_capa(capa_code, capa_type, source_type, source_code, description, root_cause,
    corrective_action, preventive_action, responsible_id, due_date, completion_date,
    effectiveness_result, effectiveness_date, status, proc_inst_id, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', capas)
print(f"  插入 {len(capas)} 条")

# ========== 24. 变更控制 ==========
print("24. 插入变更控制...")
changes = [
    ('CC202505001', '阿司匹林含量测定方法变更', 'MINOR', 'ANALYTICAL', '将含量测定方法由滴定法变更为HPLC法', 'CURRENT', '采用HPLC法替代滴定法', '提高检测准确度和精密度', '低风险', '不影响现有产品质量', '按新方法验证报告执行', 0, 1, 'NONE', 1, 1, '2025-07-01', 'APPROVED', None, 'admin', now),
]
cur.executemany('''INSERT INTO lims_change_control(change_code, change_title, change_type, change_category, change_description,
    current_state, proposed_change, change_reason, risk_assessment, impact_analysis,
    implementation_plan, training_needed, revalidation_needed, regulatory_impact,
    requester_id, approver_id, effective_date, status, proc_inst_id, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', changes)
print(f"  插入 {len(changes)} 条")

# ========== 25. 稳定性考察 ==========
print("25. 插入稳定性考察...")
stability_protocols = [
    ('STB202505001', 1, '阿司匹林', 'B20250501', 'LONG_TERM', '25°C±2°C/60%RH±5%', '2025-05-01', '2027-05-01', '0,3,6,9,12,18,24', 1, 2, 'ACTIVE', None, 'admin', now),
]
cur.executemany('''INSERT INTO lims_stability_protocol(protocol_code, product_id, product_name, batch_no, study_type,
    storage_condition, start_date, end_date, test_points, specification_id, test_template_id,
    status, proc_inst_id, create_by, create_time)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', stability_protocols)
print(f"  插入 {len(stability_protocols)} 条")

stability_tests = [
    (1, '0月', '2025-05-01', '2025-05-02', None, '所有项目符合规定', 0, 'COMPLETED'),
    (1, '3月', '2025-08-01', '2025-08-02', None, '所有项目符合规定', 0, 'COMPLETED'),
    (1, '6月', '2025-11-01', None, None, None, 0, 'PENDING'),
]
cur.executemany('''INSERT INTO lims_stability_test(protocol_id, test_point, planned_date, actual_date, test_order_id, test_result_summary, is_oot, status)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s)''', stability_tests)
print(f"  插入 {len(stability_tests)} 条")

# ========== 26. 审计追踪 ==========
print("26. 插入审计追踪...")
audit_trails = [
    ('lims_sample', 1, 'status', 'PENDING', 'TESTING', 'UPDATE', 1, 'admin', now, '开始检验', None, '127.0.0.1', 'LIMS', 'session001'),
    ('lims_test_result', 1, 'reported_value', None, '99.5', 'INSERT', 1, 'admin', now, '录入检验结果', None, '127.0.0.1', 'LIMS', 'session001'),
    ('lims_test_result', 1, 'result_conclusion', None, 'PASS', 'INSERT', 1, 'admin', now, '判定结果', None, '127.0.0.1', 'LIMS', 'session001'),
]
cur.executemany('''INSERT INTO lims_audit_trail(table_name, record_id, field_name, old_value, new_value,
    operation_type, operator_id, operator_name, operation_time, reason, signature_id, ip_address, application_name, session_id)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', audit_trails)
print(f"  插入 {len(audit_trails)} 条")

# ========== 27. 电子签名 ==========
print("27. 插入电子签名...")
# signature_id, record_type, record_id, signer_id, signer_name, signature_meaning, signature_time, reason, is_valid, ip_address, session_id
esignatures = [
    ('TEST_RESULT', 1, 1, 'admin', 'APPROVE', now, '确认检验结果', 1, '127.0.0.1', 'session001'),
]
cur.executemany('''INSERT INTO lims_electronic_signature(record_type, record_id, signer_id, signer_name, signature_meaning, signature_time, reason, is_valid, ip_address, session_id)
    VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)''', esignatures)
print(f"  插入 {len(esignatures)} 条")

conn.commit()
conn.close()

print("\n=== 测试数据插入完成! ===")
