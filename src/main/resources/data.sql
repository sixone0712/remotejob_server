INSERT INTO cras.cras_option select optionlist.*
    from (
            (SELECT 1, 'createlist', 'operations', 'max') union all
            (SELECT 2, 'createlist', 'operations', 'min') union all
            (SELECT 3, 'createlist', 'operations', 'stddev') union all
            (SELECT 4, 'createlist', 'operations', 'variance') union all
            (SELECT 5, 'createlist', 'operations', 'average') union all
            (SELECT 6, 'createlist', 'operations', 'range') union all
            (SELECT 7, 'createlist', 'operations', 'absmax') union all
            (SELECT 8, 'createlist', 'operations', 'absmin') union all
            (SELECT 9, 'createlist', 'operations', 'stddevp') union all
            (SELECT 10, 'createlist', 'operations', 'variancep') union all
            (SELECT 11, 'createlist', 'operations', 'free') union all
            (SELECT 12, 'createlist', 'operations', 'nop') union all
            (SELECT 13, 'createlist', 'calPeriodUnit', 'day') union all
            (SELECT 14, 'createlist', 'calPeriodUnit', 'job') union all
            (SELECT 15, 'createlist', 'calPeriodUnit', 'lot') union all
            (SELECT 16, 'createlist', 'calResultType', 'integer') union all
            (SELECT 17, 'createlist', 'calResultType', 'float') union all
            (SELECT 18, 'createlist', 'calResultType', 'time') union all
            (SELECT 19, 'createlist', 'calResultType', 'etc') union all
            (SELECT 20, 'judgelist', 'calCondition', 'Sum') union all
            (SELECT 21, 'judgelist', 'calCondition', 'Ave.') union all
            (SELECT 22, 'judgelist', 'calCondition', 'Diff') union all
            (SELECT 23, 'judgelist', 'calCondition', 'Rate') union all
            (SELECT 24, 'judgelist', 'calCondition', 'Range') union all
            (SELECT 25, 'judgelist', 'calCondition', 'Coef') union all
            (SELECT 26, 'judgelist', 'compareValueToThreshold', 'Over') union all
            (SELECT 27, 'judgelist', 'compareValueToThreshold', 'Under') union all
            (SELECT 28, 'judgelist', 'compareValueToThreshold', 'AbsOver') union all
            (SELECT 29, 'judgelist', 'compareValueToThreshold', 'AbsUnder')
    ) optionlist
WHERE NOT EXISTS (SELECT * FROM cras.cras_option);

with selected as (select username from log_manager.user where username='Administrator')
insert into log_manager.user (
    username,
    password,
    roles
)
select 'Administrator', '5f4dcc3b5aa765d61d8327deb882cf99', '{ROLE_JOB,ROLE_CONFIGURE,ROLE_RULES,ROLE_ADDRESS,ROLE_ACCOUNT}'
where not exists (select * from selected);