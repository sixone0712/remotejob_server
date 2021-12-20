package jp.co.canon.rss.logmanager.controller.examples;

public class HistoryExamples {
	public static final String RES_GET_BUILD_LOG_LIST = "[\n  {\n    \"name\": \"2021-06-09 14:21:44\",\n    \"id\": \"request_20210609_142144459100\",\n    \"status\": \"success\"\n  },\n  {\n    \"name\": \"2021-06-09 14:21:04\",\n    \"id\": \"request_20210609_142104230655\",\n    \"status\": \"failure\"\n  },\n  {\n    \"name\": \"2021-06-09 14:20:23\",\n    \"id\": \"request_20210609_142023956563\",\n    \"status\": \"canceled\"\n  }\n]";
	public static final String RES_GET_BUILD_LOG_TEXT = "2021-06-09 14:21:44: INFO: convert_process: converter-root=.convert/request_20210609_142144459100\n" +
			"2021-06-09 14:21:44: INFO: convert_process: get_download_list\n" +
			"2021-06-09 14:21:44: INFO: convert_process: download list ready. total 1 files\n" +
			"2021-06-09 14:21:44: INFO: convert_process: create converter process 0\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] rapid-downloader start\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] rapid-downloader start\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24085] converter start\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] download url=/rss/api/plans/storage/1234\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24085] converter start\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] download url=/rss/api/plans/storage/1234\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] download_file url=/rss/api/plans/storage/1234 dest=.convert/request_20210609_142144459100\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] download_file url=/rss/api/plans/storage/1234 dest=.convert/request_20210609_142144459100\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] get_machines\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] get_machines\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] input log files=9 standby\n" +
			"2021-06-09 14:21:44: INFO: local_logging: [24084] input log files=9 standby\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] converting start (status=running)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] converting start (status=running)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] convert_log OperationLog, 20200826234000_TYPE10_TYPE20_TYP30, 1\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] convert_log OperationLog, 20200826234000_TYPE10_TYPE20_TYP30, 1\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200826234000_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200826234000_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200826234000_TYPE10_TYPE20_TYP30(4 KByte)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200826234000_TYPE10_TYPE20_TYP30(4 KByte)\n" +
			"2021-06-09 14:21:45: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-26 02:43:19, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:45: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-26 02:43:19, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] 1 file converted (log=22608)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] 1 file converted (log=22608)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] job is working 1+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] job is working 1+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] convert_log OperationLog, 20200827231924_TYPE10_TYPE20_TYP30, 2\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] convert_log OperationLog, 20200827231924_TYPE10_TYPE20_TYP30, 2\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200827231924_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200827231924_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200827231924_TYPE10_TYPE20_TYP30(0 KByte)\n" +
			"2021-06-09 14:21:45: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200827231924_TYPE10_TYPE20_TYP30(0 KByte)\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-27 07:17:12, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-27 07:17:12, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22609)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22609)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 2+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 2+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] convert_log OperationLog, 20200828191415_TYPE10_TYPE20_TYP30, 3\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] convert_log OperationLog, 20200828191415_TYPE10_TYPE20_TYP30, 3\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200828191415_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200828191415_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200828191415_TYPE10_TYPE20_TYP30(2 KByte)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200828191415_TYPE10_TYPE20_TYP30(2 KByte)\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-28 03:09:09, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-28 03:09:09, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22610)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22610)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 3+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 3+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] convert_log OperationLog, 20200829223318_TYPE10_TYPE20_TYP30, 4\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] convert_log OperationLog, 20200829223318_TYPE10_TYPE20_TYP30, 4\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200829223318_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200829223318_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200829223318_TYPE10_TYPE20_TYP30(1 KByte)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200829223318_TYPE10_TYPE20_TYP30(1 KByte)\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-29 01:16:42, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-29 01:16:42, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22611)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22611)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 4+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 4+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] convert_log OperationLog, 20200830215712_TYPE10_TYPE20_TYP30, 5\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] convert_log OperationLog, 20200830215712_TYPE10_TYPE20_TYP30, 5\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200830215712_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200830215712_TYPE10_TYPE20_TYP30\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200830215712_TYPE10_TYPE20_TYP30(5 KByte)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20200830215712_TYPE10_TYPE20_TYP30(5 KByte)\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-30 00:00:41, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-08-30 00:00:41, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22612)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] 1 file converted (log=22612)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 5+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:46: INFO: local_logging: [24085] job is working 5+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201123220333_HEADER9, 6\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201123220333_HEADER9, 6\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201123220333_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201123220333_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201123220333_HEADER9(131 KByte)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201123220333_HEADER9(131 KByte)\n" +
			"2021-06-09 14:21:47: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-23 07:15:53, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:47: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-23 07:15:53, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 1 file converted (log=22613)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 1 file converted (log=22613)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] job is working 6+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] job is working 6+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201124215424_HEADER9, 7\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201124215424_HEADER9, 7\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201124215424_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201124215424_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201124215424_HEADER9(36 KByte)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201124215424_HEADER9(36 KByte)\n" +
			"2021-06-09 14:21:47: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-24 01:27:49, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:47: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-24 01:27:49, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 1 file converted (log=22614)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 1 file converted (log=22614)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] job is working 7+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] job is working 7+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201125181124_HEADER9, 8\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201125181124_HEADER9, 8\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201125181124_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201125181124_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201125181124_HEADER9(14 KByte)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201125181124_HEADER9(14 KByte)\n" +
			"2021-06-09 14:21:47: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-25 02:17:04, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:47: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-25 02:17:04, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 1 file converted (log=22615)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] 1 file converted (log=22615)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] job is working 8+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] job is working 8+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201126213828_HEADER9, 9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] convert_log OperationLog, 20201126213828_HEADER9, 9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201126213828_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [convert_log_file] log_name=OperationLog file_name=/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201126213828_HEADER9\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201126213828_HEADER9(22 KByte)\n" +
			"2021-06-09 14:21:47: INFO: local_logging: [24085] [OperationLog]/usr/local/crasapp/.convert/request_20210609_142144459100/OperationLog_s1_20210531_111132/SBPCN480/004_Operation_Log/20201126213828_HEADER9(22 KByte)\n" +
			"2021-06-09 14:21:48: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-26 05:32:57, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:48: ERROR: local_logging: [24085] duplicate key value violates unique constraint \"operation_log_pkey\"\n" +
			"DETAIL:  Key (equipment_name, log_time, log_idx)=(BSOT_s2_SBPCN480_G147, 2020-11-26 05:32:57, 0) already exists.\n" +
			"CONTEXT:  COPY operation_log, line 1\n" +
			"\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] 0 rows inserted rows in operation_log\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] 1 file converted (log=22616)\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] 1 file converted (log=22616)\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] job is working 9+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] job is working 9+0/9 (request_20210609_142144459100)\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] job success request_20210609_142144459100\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] job success request_20210609_142144459100\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] child_run done\n" +
			"2021-06-09 14:21:48: INFO: local_logging: [24085] child_run done\n";
}
