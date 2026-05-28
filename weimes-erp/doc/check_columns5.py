import pymysql
conn = pymysql.connect(host='sh-cdb-l0c3gbtu.sql.tencentcdb.com', port=22700, user='root', password='Stl123456789', database='weilims', charset='utf8mb4')
cur = conn.cursor()
for t in ['lims_instrument_calibration', 'lims_electronic_signature', 'lims_deviation', 'lims_capa', 'lims_sample_status_log', 'lims_oos_phase_record']:
    try:
        cur.execute(f"SHOW COLUMNS FROM {t}")
        columns = [r[0] for r in cur.fetchall()]
        print(f"{t} ({len(columns)}列):")
        print(f"  {columns}")
    except Exception as e:
        print(f"{t}: ERROR - {e}")
conn.close()
