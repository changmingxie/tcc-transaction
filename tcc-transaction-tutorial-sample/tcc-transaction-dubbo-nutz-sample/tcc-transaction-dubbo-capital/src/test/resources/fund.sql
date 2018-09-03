
--drop table FOC_TRANSCODE_INFO;
--drop table FOC_NOBOOK_LOCK;
--drop table FOC_NOBOOK;
--drop table FOC_BATCH;
--drop table FOC_BATCH_DETAIL;
--drop table FOC_ERR_POLICY_CONFIG;
--drop table FOC_ERR_POLICY_MAST;
--drop table FOC_ERR_POLICY_XREF;
--drop table FOC_SEND_MSG;
--drop table FOC_DICT_XREF;
--drop table FOC_SEQUENCE;


/*==============================================================*/
/* Table: FOC_TRANSCODE_INFO                                    */
/*==============================================================*/
create table FOC_TRANSCODE_INFO 
(
   COD_TRANS_CODE       varchar(32)                    not null,
   COD_TRANS_NAME       varchar(40)                    null,
   COD_TRANS_INFO       CLOB                           null,
   TIMEOUT_STATUS       varchar(10)                    null,
   IGNORE_FLAG          varchar(1)                     null,
   REPEAT_MSG_FLAG      varchar(1)                     null,
   IMMEDIATELY_MSG_FLAG varchar(1)                     null,
   FLUSHES_FLAG         varchar(1)                     null,
   MSG_TEMPLATE         varchar(512)                   null,
   DEVELOPER            varchar(32)                    null,
   dat_create           timestamp                       not null ,
   cod_create_user      varchar(32)                    null,
   cod_create_org       varchar(32)                    null,
   dat_modify           timestamp                       not null ,
   cod_modify_user      varchar(32)                    null,
   cod_modify_org       varchar(32)                    null,
   ctr_update_srlno     int                            default 0,
   constraint PK_FOC_TRANSCODE_INFO primary key  (COD_TRANS_CODE)
);

comment on column FOC_TRANSCODE_INFO.COD_TRANS_CODE is 
'交易码';

comment on column FOC_TRANSCODE_INFO.COD_TRANS_NAME is 
'交易名称';

comment on column FOC_TRANSCODE_INFO.COD_TRANS_INFO is 
'交易描述';

comment on column FOC_TRANSCODE_INFO.TIMEOUT_STATUS is 
'超时状态:
01 超时成功,根据第三方返回结果，来判断是否需要重发
02 超时失败.根据第三方返回结果，来判断是否需要冲正
03 超时不做处理
';

comment on column FOC_TRANSCODE_INFO.IGNORE_FLAG is 
'标志位：y 表示不使用任何策略  n表示使用';

comment on column FOC_TRANSCODE_INFO.REPEAT_MSG_FLAG is 
'标志位：y 发短信  n 不发短信';

comment on column FOC_TRANSCODE_INFO.IMMEDIATELY_MSG_FLAG is 
'标志位：y 发短信  n 不发短信';

comment on column FOC_TRANSCODE_INFO.FLUSHES_FLAG is 
'标志位：y 冲正 n 不冲正';

comment on column FOC_TRANSCODE_INFO.MSG_TEMPLATE is 
'短信模板';

comment on column FOC_TRANSCODE_INFO.DEVELOPER is 
'开发者';

comment on column FOC_TRANSCODE_INFO.dat_create is 
'创建时间';

comment on column FOC_TRANSCODE_INFO.cod_create_user is 
'创建人员';

comment on column FOC_TRANSCODE_INFO.cod_create_org is 
'创建机构';

comment on column FOC_TRANSCODE_INFO.dat_modify is 
'维护时间';

comment on column FOC_TRANSCODE_INFO.cod_modify_user is 
'维护人员';

comment on column FOC_TRANSCODE_INFO.cod_modify_org is 
'维护机构';

comment on column FOC_TRANSCODE_INFO.ctr_update_srlno is 
'维护序号';


/*==============================================================*/
/* Table: FOC_NOBOOK_LOCK                                       */
/*==============================================================*/
create table FOC_NOBOOK_LOCK 
(
   COD_ETRAN_SEQ        varchar(64)                    not null,
   COD_TRAN_SEQ         varchar(64)                    not null,
   COD_TRAN_CODE        varchar(4)                     not null,
   SYSN_STATUS          char(1)                      default '0',
   DAT_CREATE           timestamp                      not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                      not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   constraint PK_FOC_NOBOOK_LOCK primary key  (COD_ETRAN_SEQ, COD_TRAN_CODE)
);

comment on table FOC_NOBOOK_LOCK is 
'流水锁表';

comment on column FOC_NOBOOK_LOCK.COD_ETRAN_SEQ is 
'外部流水号';

comment on column FOC_NOBOOK_LOCK.COD_TRAN_SEQ is 
'内部流水号';

comment on column FOC_NOBOOK_LOCK.COD_TRAN_CODE is 
'操作码';

comment on column FOC_NOBOOK_LOCK.SYSN_STATUS is 
'流水同步状态(0初始状态 1同步 2未同步 3未同步已发短信)';

comment on column FOC_NOBOOK_LOCK.DAT_CREATE is 
'创建时间';

comment on column FOC_NOBOOK_LOCK.COD_CREATE_USER is 
'创建人员';

comment on column FOC_NOBOOK_LOCK.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_NOBOOK_LOCK.DAT_MODIFY is 
'维护时间';

comment on column FOC_NOBOOK_LOCK.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_NOBOOK_LOCK.COD_MODIFY_ORG is 
'维护机构';

	  
/*==============================================================*/
/* Table: FOC_NOBOOK                                         */
/*==============================================================*/
create table FOC_NOBOOK 
(
   COD_TRAN_SEQ         VARCHAR(64)                    not null,
   COD_ETRAN_SEQ        VARCHAR(64)                    null,
   COD_TRAN_CODE        VARCHAR(4)                     not null,
   DAT_TRAN             date                           not null,
   DAT_TRAN_TIME        timestamp                           not null,
   COD_BATCH_NO         VARCHAR(32)                    null,
   COD_RESP_CODE        VARCHAR(6)                     null,
   COD_RESP_DESC        VARCHAR(120)                   null,
   TXT_REQ_DATA         CLOB                           null,
   TXT_RESP_DATA        CLOB                           null,
   CNT_QUERY_CUR        int                            null,
   CNT_RESEND_CUR       int                            null,
   FLG_ENABLE           char(1)                        null,
   COD_BAK1             VARCHAR(32)                    null,
   COD_BAK2             VARCHAR(32)                    null,
   COD_BAK3             VARCHAR(32)                    null,
   DAT_CREATE           timestamp                       not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                       not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   CTR_UPDATE_SRLNO     int                            null,
   constraint PK_FOC_NOBOOK primary key  (COD_TRAN_SEQ)
);

comment on table FOC_NOBOOK is 
'流水表';

comment on column FOC_NOBOOK.COD_TRAN_SEQ is 
'交易流水号';

comment on column FOC_NOBOOK.COD_ETRAN_SEQ is 
'外部系统交易流水号';

comment on column FOC_NOBOOK.COD_TRAN_CODE is 
'交易码';

comment on column FOC_NOBOOK.DAT_TRAN is 
'交易日期';

comment on column FOC_NOBOOK.DAT_TRAN_TIME is 
'交易时间';

comment on column FOC_NOBOOK.COD_BATCH_NO is 
'批次号';

comment on column FOC_NOBOOK.COD_RESP_CODE is 
'响应码';

comment on column FOC_NOBOOK.COD_RESP_DESC is 
'响应描述';

comment on column FOC_NOBOOK.TXT_REQ_DATA is 
'请求报文';

comment on column FOC_NOBOOK.TXT_RESP_DATA is 
'响应报文';

comment on column FOC_NOBOOK.CNT_QUERY_CUR is 
'当前查询次数';

comment on column FOC_NOBOOK.CNT_RESEND_CUR is 
'当前重发次数';

comment on column FOC_NOBOOK.FLG_ENABLE is 
'是否可用（Y可用 N不可用  即是否仍可以查询或重发';

comment on column FOC_NOBOOK.COD_BAK1 is 
'备用字段1';

comment on column FOC_NOBOOK.COD_BAK2 is 
'备用字段2';

comment on column FOC_NOBOOK.COD_BAK3 is 
'备用字段3';

comment on column FOC_NOBOOK.DAT_CREATE is 
'创建时间';

comment on column FOC_NOBOOK.COD_CREATE_USER is 
'创建人员';

comment on column FOC_NOBOOK.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_NOBOOK.DAT_MODIFY is 
'维护时间';

comment on column FOC_NOBOOK.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_NOBOOK.COD_MODIFY_ORG is 
'维护机构';

comment on column FOC_NOBOOK.CTR_UPDATE_SRLNO is 
'维护序号';






/*==============================================================*/
/* Table: FOC_BATCH                                             */
/*==============================================================*/
create table FOC_BATCH 
(
   COD_BATCH_CODE       VARCHAR(64)                    not null,
   COD_TYPE             VARCHAR(2)                     not null,
   COD_STATUS           VARCHAR(6)                     not null,
   COD_TRAN_CODE        VARCHAR(4)                     not null,
   DAT_TRAN             date                           null,
   DAT_TRAN_TIME        timestamp                           null,
   CNT_QUERY_CUR        int                            null,
   CNT_RESEND_CUR       int                            null,
   FLG_ENABLE           char(1)                        null,
   COD_SEND_STATUS      char(1)                        default '0',
   COD_RESP_DESC        VARCHAR(120)                   null,
   COD_BAK1             VARCHAR(32)                    null,
   COD_BAK2             VARCHAR(32)                    null,
   COD_BAK3             VARCHAR(32)                    null,
   DAT_CREATE           timestamp                       not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                       not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   CTR_UPDATE_SRLNO     int                            null,
   constraint PK_FOC_BATCH primary key  (COD_BATCH_CODE, COD_TYPE)
);

comment on table FOC_BATCH is 
'批量流水表 ';

comment on column FOC_BATCH.COD_BATCH_CODE is 
'批次号、资金鉴别码';

comment on column FOC_BATCH.COD_TYPE is 
'批次类型：1资金鉴别码，2还款批次号，3放款批次号';

comment on column FOC_BATCH.COD_STATUS is 
'批次状态：0000处理成功，00002，处理中，00001处理失败';

comment on column FOC_BATCH.COD_TRAN_CODE is 
'交易码';

comment on column FOC_BATCH.DAT_TRAN is 
'交易日期';

comment on column FOC_BATCH.DAT_TRAN_TIME is 
'交易时间';

comment on column FOC_BATCH.CNT_QUERY_CUR is 
'当前查询次数';

comment on column FOC_BATCH.CNT_RESEND_CUR is 
'当前重发次数';

comment on column FOC_BATCH.FLG_ENABLE is 
'是否可用（Y可用 N不可用  即是否仍可以查询或重发';

comment on column FOC_BATCH.COD_SEND_STATUS is 
'发送状态(0未发送 1发送中 2已发送)';

comment on column FOC_BATCH.COD_RESP_DESC is 
'响应描述';

comment on column FOC_BATCH.COD_BAK1 is 
'备用字段1';

comment on column FOC_BATCH.COD_BAK2 is 
'备用字段2';

comment on column FOC_BATCH.COD_BAK3 is 
'备用字段3';

comment on column FOC_BATCH.DAT_CREATE is 
'创建时间';

comment on column FOC_BATCH.COD_CREATE_USER is 
'创建人员';

comment on column FOC_BATCH.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_BATCH.DAT_MODIFY is 
'维护时间';

comment on column FOC_BATCH.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_BATCH.COD_MODIFY_ORG is 
'维护机构';

comment on column FOC_BATCH.CTR_UPDATE_SRLNO is 
'维护序号';





/*==============================================================*/
/* Table: FOC_BATCH_DETAIL                                      */
/*==============================================================*/
create table FOC_BATCH_DETAIL 
(
   COD_BATCH_CODE       VARCHAR(64)                    not null,
   COD_TYPE             VARCHAR(2)                     not null,
   COD_TRAN_SEQ         VARCHAR(64)                    not null,
   COD_ETRAN_SEQ        VARCHAR(64)                    not null,
   COD_CUST_CODE        VARCHAR(32)                    null,
   COD_AMT              number(11,2)                   null,
   COD_INVEST_NO        VARCHAR(32)                    null,
   COD_AMT_CODE         VARCHAR(10)                    null,
   COD_FREEZENO         VARCHAR(32)                    null,
   DAT_CREATE           timestamp                       not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                       not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   CTR_UPDATE_SRLNO     int                            null,
   constraint PK_FOC_BATCH_DETAIL primary key  (COD_BATCH_CODE, COD_TYPE, COD_TRAN_SEQ, COD_ETRAN_SEQ)
);

comment on table FOC_BATCH_DETAIL is 
'批量流水明细表 
';

comment on column FOC_BATCH_DETAIL.COD_BATCH_CODE is 
'批次号、资金鉴别码';

comment on column FOC_BATCH_DETAIL.COD_TYPE is 
'批次类型：1资金鉴别码，2还款批次号，3放款批次号';

comment on column FOC_BATCH_DETAIL.COD_TRAN_SEQ is 
'内部流水号';

comment on column FOC_BATCH_DETAIL.COD_ETRAN_SEQ is 
'外部流水号';

comment on column FOC_BATCH_DETAIL.COD_CUST_CODE is 
'客户编号';

comment on column FOC_BATCH_DETAIL.COD_AMT is 
'充值金额';

comment on column FOC_BATCH_DETAIL.COD_INVEST_NO is 
'投资编号';

comment on column FOC_BATCH_DETAIL.COD_AMT_CODE is 
'资金代码';

comment on column FOC_BATCH_DETAIL.COD_FREEZENO is 
'冻结编号';

comment on column FOC_BATCH_DETAIL.DAT_CREATE is 
'创建时间';

comment on column FOC_BATCH_DETAIL.COD_CREATE_USER is 
'创建人员';

comment on column FOC_BATCH_DETAIL.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_BATCH_DETAIL.DAT_MODIFY is 
'维护时间';

comment on column FOC_BATCH_DETAIL.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_BATCH_DETAIL.COD_MODIFY_ORG is 
'维护机构';

comment on column FOC_BATCH_DETAIL.CTR_UPDATE_SRLNO is 
'维护序号';



/*==============================================================*/
/* Table: FOC_ERR_POLICY_CONFIG                       */
/*==============================================================*/
create table FOC_ERR_POLICY_CONFIG 
(
   COD_TX_CODE          VARCHAR(32)                    not null,
   COD_REQUEST_VPD      VARCHAR(128)                   null,
   COD_RESPONSE_VPD     VARCHAR(128)                   null,
   COD_NT_CODE          VARCHAR(32)                    null,
   COD_ENTITY_VPD       VARCHAR(128)                   null,
   DAT_CREATE           timestamp                       not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                       not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   constraint PK_FOC_ERR_POLICY_CO primary key  (COD_TX_CODE)
);

comment on table FOC_ERR_POLICY_CONFIG is 
'错误重发策略接口配置表';

comment on column FOC_ERR_POLICY_CONFIG.COD_TX_CODE is 
'接口编号';

comment on column FOC_ERR_POLICY_CONFIG.COD_REQUEST_VPD is 
'请求实体编号';

comment on column FOC_ERR_POLICY_CONFIG.COD_RESPONSE_VPD is 
'返回实体编号';

comment on column FOC_ERR_POLICY_CONFIG.COD_NT_CODE is 
'发送短信模板编号';

comment on column FOC_ERR_POLICY_CONFIG.COD_ENTITY_VPD is 
'冲正实体编号';

comment on column FOC_ERR_POLICY_CONFIG.DAT_CREATE is 
'创建时间';

comment on column FOC_ERR_POLICY_CONFIG.COD_CREATE_USER is 
'创建人员';

comment on column FOC_ERR_POLICY_CONFIG.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_ERR_POLICY_CONFIG.DAT_MODIFY is 
'维护时间';

comment on column FOC_ERR_POLICY_CONFIG.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_ERR_POLICY_CONFIG.COD_MODIFY_ORG is 
'维护机构';



/*==============================================================*/
/* Table: FOC_ERR_POLICY_MAST                                   */
/*==============================================================*/
create table FOC_ERR_POLICY_MAST 
(
   COD_POLICY_ID        int                            not null,
   COD_ERROR            VARCHAR(32)                    null,
   COD_ENTITY_VPD       VARCHAR(256)                   null,
   CNT_QUERY_MAX        int                            null,
   CNT_RESEND_MAX       int                            null,
   FLG_SMS              char(1)                        null,
   FLG_RESEND           char(1)                        null,
   FLG_CORRECT          char(1)                        null,
   FLG_SYSTEM_DEFAULT   char(1)                        null,
   DAT_CREATE           timestamp                       not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                       not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   constraint PK_FOC_ERR_POLICY_MAST primary key  (COD_POLICY_ID)
);

comment on table FOC_ERR_POLICY_MAST is 
'错误重发机制主表';

comment on column FOC_ERR_POLICY_MAST.COD_POLICY_ID is 
'策略ID';

comment on column FOC_ERR_POLICY_MAST.COD_ERROR is 
'错误码';

comment on column FOC_ERR_POLICY_MAST.COD_ENTITY_VPD is 
'执行类实体编号';

comment on column FOC_ERR_POLICY_MAST.CNT_QUERY_MAX is 
'最大查询次数';

comment on column FOC_ERR_POLICY_MAST.CNT_RESEND_MAX is 
'最大重发次数';

comment on column FOC_ERR_POLICY_MAST.FLG_SMS is 
'是否发消息提醒(y 是,n 否)';

comment on column FOC_ERR_POLICY_MAST.FLG_RESEND is 
'是否重发请求(y 是,n 否)';

comment on column FOC_ERR_POLICY_MAST.FLG_CORRECT is 
'是否需要冲正(y 是,n 否)';

comment on column FOC_ERR_POLICY_MAST.FLG_SYSTEM_DEFAULT is 
'是否系统默认(y 是,n 否)';

comment on column FOC_ERR_POLICY_MAST.DAT_CREATE is 
'创建时间';

comment on column FOC_ERR_POLICY_MAST.COD_CREATE_USER is 
'创建人员';

comment on column FOC_ERR_POLICY_MAST.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_ERR_POLICY_MAST.DAT_MODIFY is 
'维护时间';

comment on column FOC_ERR_POLICY_MAST.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_ERR_POLICY_MAST.COD_MODIFY_ORG is 
'维护机构';



/*==============================================================*/
/* Table: FOC_ERR_POLICY_XREF                                   */
/*==============================================================*/
create table FOC_ERR_POLICY_XREF 
(
   COD_ID               int                            not null,
   COD_TX_CODE          VARCHAR(32)                    null,
   COD_ERROR            VARCHAR(32)                    null,
   COD_POLICY_ID        int                            null,
   DAT_CREATE           timestamp                       not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                       not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   constraint PK_FOC_ERR_POLICY_XREF primary key  (COD_ID)
);

comment on table FOC_ERR_POLICY_XREF is 
'错误重发策略扩展表';

comment on column FOC_ERR_POLICY_XREF.COD_ID is 
'主键ID';

comment on column FOC_ERR_POLICY_XREF.COD_TX_CODE is 
'接口编号';

comment on column FOC_ERR_POLICY_XREF.COD_ERROR is 
'错误码';

comment on column FOC_ERR_POLICY_XREF.COD_POLICY_ID is 
'策略ID';

comment on column FOC_ERR_POLICY_XREF.DAT_CREATE is 
'创建时间';

comment on column FOC_ERR_POLICY_XREF.COD_CREATE_USER is 
'创建人员';

comment on column FOC_ERR_POLICY_XREF.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_ERR_POLICY_XREF.DAT_MODIFY is 
'维护时间';

comment on column FOC_ERR_POLICY_XREF.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_ERR_POLICY_XREF.COD_MODIFY_ORG is 
'维护机构';



/*==============================================================*/
/* Table: FOC_SEND_MSG                                          */
/*==============================================================*/
create table FOC_SEND_MSG 
(
   COD_MSG_ID           int                            not null,
   CON_NT_CODE          VARCHAR(32)                    not null,
   COD_SND_TYPE         char(1)                        not null,
   MSG_SND_STS          char(1)                        not null,
   TXT_PARAMS           VARCHAR(256)                   null,
   MSG_KEY            VARCHAR(256)                   null,
   DAT_CREATE           timestamp                       not null,
   COD_CREATE_USER      VARCHAR(32)                    null,
   COD_CREATE_ORG       VARCHAR(32)                    null,
   DAT_MODIFY           timestamp                       not null,
   COD_MODIFY_USER      VARCHAR(32)                    null,
   COD_MODIFY_ORG       VARCHAR(32)                    null,
   constraint PK_FOC_SEND_MSG primary key  (COD_MSG_ID)
);

comment on table FOC_SEND_MSG is 
'消息发送表';

comment on column FOC_SEND_MSG.COD_MSG_ID is 
'信息编号';

comment on column FOC_SEND_MSG.CON_NT_CODE is 
'信息模板编号';

comment on column FOC_SEND_MSG.COD_SND_TYPE is 
'发送类型 1-手机发送，2-站内消息 3-微信模板消息 4-电话语音发送';

comment on column FOC_SEND_MSG.MSG_SND_STS is 
'发送状态 0-未发送 1-已发送';

comment on column FOC_SEND_MSG.TXT_PARAMS is 
'短信参数';

comment on column FOC_SEND_MSG.MSG_KEY is 
'短信key,定义了该key，用来区分短信是那种类型';

comment on column FOC_SEND_MSG.DAT_CREATE is 
'创建时间';

comment on column FOC_SEND_MSG.COD_CREATE_USER is 
'创建人员';

comment on column FOC_SEND_MSG.COD_CREATE_ORG is 
'创建机构';

comment on column FOC_SEND_MSG.DAT_MODIFY is 
'维护时间';

comment on column FOC_SEND_MSG.COD_MODIFY_USER is 
'维护人员';

comment on column FOC_SEND_MSG.COD_MODIFY_ORG is 
'维护机构';



/*==============================================================*/
/* Table: FOC_DICT_XREF                                         */
/*==============================================================*/
create table FOC_DICT_XREF 
(
   COD_FIELD            VARCHAR(32)                    not null,
   COD_REMOTE_VALUE        VARCHAR(64)                    not null,
   COD_LOCAL_VALUE      VARCHAR(64)                    not null,
   COD_VALUE_DESC       VARCHAR(64)                    not null,
   COD_FIELD_DESC       VARCHAR(64)                    null,
   constraint PK_FOC_DICT_XREF primary key  (COD_FIELD, COD_REMOTE_VALUE)
);

comment on table FOC_DICT_XREF is 
'数据字典映射表';

comment on column FOC_DICT_XREF.COD_FIELD is 
'字段';

comment on column FOC_DICT_XREF.COD_REMOTE_VALUE is 
'第三方值';

comment on column FOC_DICT_XREF.COD_LOCAL_VALUE is 
'本地值';

comment on column FOC_DICT_XREF.COD_VALUE_DESC is 
'字段值描述';

comment on column FOC_DICT_XREF.COD_FIELD_DESC is 
'字段描述';



/*==============================================================*/
/* Table: FOC_SEQUENCE                                          */
/*==============================================================*/
create table FOC_SEQUENCE 
(
   COD_TRANS_CODE       varchar(32)                    not null,
   COD_SEQ              varchar(20)                    not null,
   COD_SEQ_NAME         varchar(64)                    null,
   SEQ_STRATEGY         varchar(20)                    null,
   SEQ_USE              varchar(1)                     null,
   seq_start     int                                       default 0,
   seq_increment      int                                  default 1,
   ctr_update_srlno     int                            default 0,
   seq_curr_date         varchar(10)                    null,
   dat_create           timestamp                       not null,
   cod_create_user      varchar(32)                    null,
   cod_create_org       varchar(32)                    null,
   dat_modify           timestamp                       not null ,
   cod_modify_user      varchar(32)                    null,
   cod_modify_org       varchar(32)                    null,
   constraint PK_FOC_SEQUENCE primary key  (COD_TRANS_CODE, COD_SEQ)
);

comment on column FOC_SEQUENCE.COD_TRANS_CODE is 
'交易码';

comment on column FOC_SEQUENCE.COD_SEQ is 
'序列名';

comment on column FOC_SEQUENCE.COD_SEQ_NAME is 
'序列名称';

comment on column FOC_SEQUENCE.SEQ_USE is 
'使用状态: y-使用 n-不使用';

comment on column FOC_SEQUENCE.SEQ_STRATEGY is 
'策略：1,顺序递增    2,按天重新产生序列号';

comment on column FOC_SEQUENCE.seq_start is 
'起点值';

comment on column FOC_SEQUENCE.seq_increment is 
'步长';

comment on column FOC_SEQUENCE.ctr_update_srlno is 
'维护序号';

comment on column FOC_SEQUENCE.seq_curr_date is 
'序列日期(yyyy-MM-dd)';

comment on column FOC_SEQUENCE.dat_create is 
'创建时间';

comment on column FOC_SEQUENCE.cod_create_user is 
'创建人员';

comment on column FOC_SEQUENCE.cod_create_org is 
'创建机构';

comment on column FOC_SEQUENCE.dat_modify is 
'维护时间';

comment on column FOC_SEQUENCE.cod_modify_user is 
'维护人员';

comment on column FOC_SEQUENCE.cod_modify_org is 
'维护机构';



-- 恢复sequence到初始值
CREATE OR REPLACE PROCEDURE P2P_HJ003_111222.p_reset_sequence (in_seq VARCHAR2,in_start NUMBER,in_increase NUMBER)
AS
   n   NUMBER (10);
BEGIN
   EXECUTE IMMEDIATE 'select ' || in_seq || '.nextval from dual' INTO n;
   n := - (n - 1);
   IF n <= 0 THEN
      return;
   END IF;
   EXECUTE IMMEDIATE 'alter sequence   ' || in_seq || ' increment by ' || n;
   EXECUTE IMMEDIATE 'select   ' || in_seq || '.nextval from dual' INTO n;
   EXECUTE IMMEDIATE 'alter sequence  ' || in_seq || ' increment by ' || (in_start - 2);
   EXECUTE IMMEDIATE 'select ' || in_seq || '.nextval from dual' INTO n;
   EXECUTE IMMEDIATE 'alter sequence  ' || in_seq || ' increment by ' || in_increase;
END p_reset_sequence;
/





-- sequence 初始化
INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '1087','FUND_CUST_1087_SEQ','开户','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

-- SEQUENCE
-- 开户1087
CREATE SEQUENCE FUND_CUST_1087_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;
  


-- sequence 初始化
INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '2280','FUND_CH_2280_SEQ','普通提现','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

-- 普通提现
CREATE SEQUENCE FUND_CH_2280_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;
  

-- sequence 初始化
INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '2290','FUND_CH_2290_SEQ','实时提现','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

-- 实时提现
CREATE SEQUENCE FUND_CH_2290_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;
  
-- sequence 初始化
INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '2080','FUND_CUST_2080_SEQ','充值','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );


insert into foc_sequence (COD_TRANS_CODE, COD_SEQ, COD_SEQ_NAME, SEQ_STRATEGY, SEQ_USE, SEQ_START, SEQ_INCREMENT, CTR_UPDATE_SRLNO, SEQ_CURR_DATE, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG)
values ('1088', 'FUND_CUST_1088_SEQ', '换卡', '2', 'y', 1, 1, 0, '2017-12-09', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001');

insert into foc_sequence (COD_TRANS_CODE, COD_SEQ, COD_SEQ_NAME, SEQ_STRATEGY, SEQ_USE, SEQ_START, SEQ_INCREMENT, CTR_UPDATE_SRLNO, SEQ_CURR_DATE, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG)
values ('3003', 'FUND_CUST_3003_SEQ', '产品份额查询', '2', 'y', 1, 1, 0, '2017-12-09', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001');

insert into foc_sequence (COD_TRANS_CODE, COD_SEQ, COD_SEQ_NAME, SEQ_STRATEGY, SEQ_USE, SEQ_START, SEQ_INCREMENT, CTR_UPDATE_SRLNO, SEQ_CURR_DATE, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG)
values ('3010', 'FUND_CUST_3010_SEQ', '短信验证码上送', '2', 'y', 1, 1, 0, '2017-12-09', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001');

-- SEQUENCE
-- 货基代扣充值  FUND_CUST_2080_SEQ
CREATE SEQUENCE FUND_CUST_2080_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;
  
  
insert into FOC_SEQUENCE (COD_TRANS_CODE, COD_SEQ, COD_SEQ_NAME, SEQ_STRATEGY, SEQ_USE, SEQ_START, SEQ_INCREMENT, CTR_UPDATE_SRLNO, SEQ_CURR_DATE, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG)
values ('3001', 'FUND_TRANS_3001_SEQ', '交易结果查询', '2', 'y', 1, 1, 0, '2017-12-12', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001');


-- 表基础数据录入
-- 初始化交易码信息表信息  begin 2018/01/22 add by lqf
insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('1087', '开户申请', '开户申请', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('2080', '充值', '充值', '01', 'n', 'y', 'y', 'n', '', 'DENGXQ', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('2280', '普通提现', '普通提现', '01', 'n', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('2290', '实时提现', '实时提现', '01', 'n', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('1088', '换卡', '换卡', '03', 'y', 'n', 'n', 'n', '', 'xiexd', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('3003', '产品份额查询', '产品份额查询', '03', 'y', 'n', 'n', 'n', '', 'xiexd', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('3010', '短信验证码上送', '短信验证码上送', '03', 'y', 'n', 'n', 'n', '', 'xiexd', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('2085', '货基购买', '货基购买', '03', 'y', 'n', 'n', 'n', '', 'xiexd', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9010', '对账文件下载', '对账文件下载', '03', 'y', 'n', 'n', 'n', '', 'liangcz',sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9020', '资金文件下载', '资金文件下载', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9030', '确认文件下载', '确认文件下载', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9040', '行情文件下载', '行情文件下载', '03', 'y', 'n', 'n', 'n', '', 'liangcz',sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9050', '分红文件下载', '分红文件下载', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9060', '份额确认文件下载', '份额确认文件下载', '03', 'y', 'n', 'n', 'n', '', 'liangcz',sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9070', '批量开户(文件上传)', '批量开户(文件上传)', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9080', '批量开户回盘(文件下载)', '批量开户回盘(文件下载)', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9110', '批量还款(文件上传)', '批量还款(文件上传)', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

insert into foc_transcode_info (COD_TRANS_CODE, COD_TRANS_NAME, COD_TRANS_INFO, TIMEOUT_STATUS, IGNORE_FLAG, REPEAT_MSG_FLAG, IMMEDIATELY_MSG_FLAG, FLUSHES_FLAG, MSG_TEMPLATE, DEVELOPER, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG, CTR_UPDATE_SRLNO)
values ('9120', '批量还款回盘(文件下载)', '批量还款回盘(文件下载)', '03', 'y', 'n', 'n', 'n', '', 'liangcz', sysdate, 'ADMIN', '1001', sysdate, 'ADMIN', '1001', 0);

-- 初始化交易码信息表信息  end 2018/01/22 add by lqf

-- Create sequence 
create sequence FUND_TRANS_3001_SEQ
minvalue 1
maxvalue 999999
start with 1
increment by 1
nocache
cycle
order;

-- Create sequence 
create sequence FUND_FOC_SEND_MSG_SEQ
minvalue 1
maxvalue 999999
start with 1
increment by 1
nocache
cycle
order;

create sequence FUND_CUST_1088_SEQ
minvalue 1
maxvalue 999999
start with 1
increment by 1
nocache
cycle
order;

create sequence FUND_CUST_3003_SEQ
minvalue 1
maxvalue 999999
start with 1
increment by 1
nocache
cycle
order;

create sequence FUND_CUST_3010_SEQ
minvalue 1
maxvalue 999999
start with 10000
increment by 1
nocache
cycle
order;



create sequence FUND_PURCHASE_2085_SEQ
minvalue 1
maxvalue 999999
start with 1
increment by 1
nocache
cycle
order;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9010','FUND_CUST_9010_SEQ','对账文件下载','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9010_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9020','FUND_CUST_9020_SEQ','资金文件下载','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9020_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9030','FUND_CUST_9030_SEQ','确认文件下载','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9030_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9040','FUND_CUST_9040_SEQ','行情文件下载','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9040_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9050','FUND_CUST_9050_SEQ','分红文件下载','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9050_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9060','FUND_CUST_9060_SEQ','份额确认文件下载','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9060_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9070','FUND_CUST_9070_SEQ','批量开户(文件上传)','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9070_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9080','FUND_CUST_9080_SEQ','批量开户回盘(文件下载)','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9080_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9110','FUND_CUST_9110_SEQ','批量还款(文件上传)','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9110_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;


INSERT INTO FOC_SEQUENCE ( COD_TRANS_CODE,COD_SEQ,COD_SEQ_NAME,SEQ_USE, seq_strategy,SEQ_START,SEQ_INCREMENT,DAT_CREATE,COD_CREATE_USER,COD_CREATE_ORG,DAT_MODIFY,COD_MODIFY_USER,COD_MODIFY_ORG ) VALUES( '9120','FUND_CUST_9120_SEQ','批量还款回盘(文件下载)','y',2,1,1,sysdate,'ADMIN','1001',sysdate,'ADMIN','1001' );

CREATE SEQUENCE FUND_CUST_9120_SEQ
  START WITH 1
  MAXVALUE 999999
  MINVALUE 1
  CYCLE
  NOCACHE
  ORDER;

INSERT INTO FOC_ERR_POLICY_MAST VALUES (1, '00002', 'handleWaitingParentManager', 10, 3, 'y', 'y', NULL,'y', sysdate, 'system', '1001', sysdate, 'system', '1001');
INSERT INTO FOC_ERR_POLICY_MAST VALUES (2, 'E0005', 'handleNoOrderParentManager', 10, 3, 'y', 'y', NULL, 'y', sysdate, 'system', '1001', sysdate, 'system', '1001');
INSERT INTO FOC_ERR_POLICY_MAST VALUES (3, '90015', 'handleTimeOutSuccessParentManager', 10, 3, 'y', 'y', NULL, 'y',sysdate, 'system', '1001', sysdate, 'system', '1001');
INSERT INTO FOC_ERR_POLICY_MAST VALUES (4, '90016', 'handleTimeOutFailParentManager', 10, 3, 'y', 'n', NULL, 'y',sysdate, 'system', '1001', sysdate, 'system', '1001');


-- 初始化接口配置信息  begin 2018/01/22 add by lqf
insert into FOC_ERR_POLICY_CONFIG (COD_TX_CODE, COD_REQUEST_VPD, COD_RESPONSE_VPD, COD_NT_CODE, COD_ENTITY_VPD, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG)
values ('2280', 'com.ifs.fund.ch.entity.xml.WithdrawRequest', 'com.ifs.fund.sys.adapter.entity.BaseResponse', 'HJ1001', '', sysdate, 'system', '1001', sysdate, 'system', '1001');

insert into FOC_ERR_POLICY_CONFIG (COD_TX_CODE, COD_REQUEST_VPD, COD_RESPONSE_VPD, COD_NT_CODE, COD_ENTITY_VPD, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG)
values ('2290', 'com.ifs.fund.ch.entity.xml.WithdrawRequest', 'com.ifs.fund.sys.adapter.entity.BaseResponse', 'HJ1001', '', sysdate, 'system', '1001', sysdate, 'system', '1001');

insert into FOC_ERR_POLICY_CONFIG (COD_TX_CODE, COD_REQUEST_VPD, COD_RESPONSE_VPD, COD_NT_CODE, COD_ENTITY_VPD, DAT_CREATE, COD_CREATE_USER, COD_CREATE_ORG, DAT_MODIFY, COD_MODIFY_USER, COD_MODIFY_ORG)
values ('2080', 'com.ifs.fund.ch.entity.xml.DepositRequest', 'com.ifs.fund.ch.entity.xml.DepositResponse', 'HJ1001', '', sysdate, 'system', '1001', sysdate, 'system', '1001');
-- 初始化接口配置信息  end 2018/01/22 add by lqf

-- 添加字典项映射
INSERT INTO foc_dict_xref VALUES ('bank_code', '01000000', '403', '中国邮政储蓄银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '01020000', '102', '中国工商银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '01030000', '103', '中国农业银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '01040000', '104', '中国银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '01050000', '105', '中国建设银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03010000', '301', '交通银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03020000', '302', '中信银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03030000', '303', '中国光大银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03040000', '304', '华夏银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03050000', '305', '中国民生银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03060000', '306', '广发银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03080000', '308', '招商银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03090000', '309', '兴业银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '03100000', '310', '上海浦东发展银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '04012900', '4012900', '上海银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '04031000', '04031000', '北京银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('bank_code', '04105840', '307', '平安银行', '银行编码');
INSERT INTO foc_dict_xref VALUES ('id_type', '01', '01', '身份证', '证件类型');
INSERT INTO foc_dict_xref VALUES ('id_type', '11', '02', '(港、澳)回乡证、通行证', '证件类型');
INSERT INTO foc_dict_xref VALUES ('acct_type', '1', '00', '借记卡', '账户类型类型');
INSERT INTO foc_dict_xref VALUES ('acct_type', '3', '01', '存折', '账户类型类型');
INSERT INTO foc_dict_xref VALUES ('acct_type', '2', '02', '贷记卡', '账户类型类型');




drop table foc_file_policy;


CREATE TABLE foc_file_policy
(
   cod_tran_code         VARCHAR2 (8) NOT NULL,
   ctr_max_retry         INTEGER NULL,
   signle_cycle	         INTEGER NULL,
   ctr_retry_interval    INTEGER NOT NULL,
   ctr_timeout           INTEGER,
   cod_alert_type        char(1) NOT NULL,
   flg_transfer_enable   char(1) NOT NULL,
   flg_allow_succ_repeat        char(1)  NOT NULL,
   cod_nofatal_error  VARCHAR2 (512) NULL,
    cod_ignore_error  VARCHAR2 (512) NULL,
   tim_allow_begin       VARCHAR2 (12),
   tim_allow_end         VARCHAR2 (12),
   CONSTRAINT pk_foc_file_policy PRIMARY KEY (cod_tran_code)
);

COMMENT ON TABLE foc_file_policy IS '文件策略表';

COMMENT ON COLUMN foc_file_policy.cod_tran_code IS '交易码';

COMMENT ON COLUMN foc_file_policy.signle_cycle IS
   '单笔重试次数(非致命错误)';

COMMENT ON COLUMN foc_file_policy.ctr_max_retry IS
   '最大重试次数(网络错误)';

COMMENT ON COLUMN foc_file_policy.ctr_retry_interval IS
   '重试间隔时间(s)';

COMMENT ON COLUMN foc_file_policy.ctr_timeout IS '超时时间(ms)';

COMMENT ON COLUMN foc_file_policy.cod_alert_type IS
   '预警方式(0-无 1-短信提醒)';

COMMENT ON COLUMN foc_file_policy.flg_transfer_enable IS
   '传输允许标识(y-允许，n-不允许)';

   COMMENT ON COLUMN foc_file_policy.flg_allow_succ_repeat IS
   '允许传输成功后再次传输(y-允许，n-不允许)';

   COMMENT ON COLUMN foc_file_policy.cod_nofatal_error IS
   '非致命错误码，允许重传(逗号隔开)';
   COMMENT ON COLUMN foc_file_policy.cod_ignore_error IS
   '可忽略错误(逗号隔开，没有策略控制)';
  COMMENT ON COLUMN foc_file_policy.tim_allow_begin IS
   '允许传输最早时间(HH:mm)';

COMMENT ON COLUMN foc_file_policy.tim_allow_end IS
   '允许传输最晚时间(HH:mm)';

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error,cod_ignore_error ) VALUES
( '9010',2,1,30,'1','y','00:00','03:00','y','90001,90002,90003,90004,90005,90006,90007','93009' );

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error,cod_ignore_error ) VALUES
( '9020',null,1,30,'1','y','00:00','23:59','y','90001,90002,90003,90004,90005,90006,90007','93009' );

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error,cod_ignore_error ) VALUES
( '9030',3,1,30,'1','y','12:00','16:00','y','90001,90002,90003,90004,90005,90006,90007','93009' );

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error,cod_ignore_error ) VALUES
( '9040',2,1,30,'1','y','10:00','13:00','y','90001,90002,90003,90004,90005,90006,90007','93009');

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error,cod_ignore_error ) VALUES
( '9050',3,1,30,'1','y','12:00','16:00','y','90001,90002,90003,90004,90005,90006,90007','93009' );

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error,cod_ignore_error ) VALUES
( '9060',null,1,30,'1','y','00:00','23:59','y','90001,90002,90003,90004,90005,90006,90007','93009' );

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error ) VALUES
( '9110',null,1,30,'1','y','00:00','23:59','y','90001,90002,90003,90004,90005,90006,90007' );

 INSERT INTO FOC_FILE_POLICY ( COD_TRAN_CODE,CTR_MAX_RETRY,signle_cycle,CTR_RETRY_INTERVAL,COD_ALERT_TYPE,FLG_TRANSFER_ENABLE,TIM_ALLOW_BEGIN,TIM_ALLOW_END,flg_allow_succ_repeat,cod_nofatal_error ) VALUES
( '9120',null,1,30,'1','y','00:00','23:59','y','90001,90002,90003,90004,90005,90006,90007' );


CREATE TABLE foc_file_transfer_jrn
(
   cod_tran_seq        VARCHAR2 (64) NOT NULL,
   cod_tran_code       VARCHAR2 (8) ,
   cod_batch_no        VARCHAR2 (32) ,
   txt_tran_desc       VARCHAR2 (512) ,
   tim_tran_begin     TIMESTAMP NOT NULL,
   tim_tran_end        TIMESTAMP NOT NULL,
   tim_deal            INTEGER NOT NULL,
   result              VARCHAR2 (1),
   cod_error_code      VARCHAR2 (60),
   txt_errog_msg       VARCHAR2 (512),
   file_transfer       VARCHAR2 (512),
   file_reps_decrypt   VARCHAR2 (512),
   dat_create          TIMESTAMP NOT NULL,
   dat_modify          TIMESTAMP NULL,
   CONSTRAINT pk_foc_file_transfer_jrn PRIMARY KEY (cod_tran_seq)
);

COMMENT ON TABLE foc_file_transfer_jrn IS '文件传输日志表';

COMMENT ON COLUMN foc_file_transfer_jrn.cod_tran_seq IS '交易流水号';

COMMENT ON COLUMN foc_file_transfer_jrn.cod_tran_code IS '交易码';

COMMENT ON COLUMN foc_file_transfer_jrn.cod_batch_no IS '批次号';

COMMENT ON COLUMN foc_file_transfer_jrn.txt_tran_desc IS
   '重试间隔时间(ms)';

COMMENT ON COLUMN foc_file_transfer_jrn.tim_tran_begin IS '开始时间';

COMMENT ON COLUMN foc_file_transfer_jrn.tim_tran_end IS '结束时间';

COMMENT ON COLUMN foc_file_transfer_jrn.tim_deal IS '处理时间(ms)';

COMMENT ON COLUMN foc_file_transfer_jrn.result IS
   '交易结果(0-成功 1-交易中 2-失败)';

COMMENT ON COLUMN foc_file_transfer_jrn.cod_error_code IS '错误码';

COMMENT ON COLUMN foc_file_transfer_jrn.txt_errog_msg IS '错误信息';

COMMENT ON COLUMN foc_file_transfer_jrn.file_transfer IS '传输源文件';

COMMENT ON COLUMN foc_file_transfer_jrn.file_reps_decrypt IS
   '响应解密文件';

COMMENT ON COLUMN foc_file_transfer_jrn.dat_create IS '创建时间';

COMMENT ON COLUMN foc_file_transfer_jrn.dat_modify IS '维护时间';




create table FOC_BATCH_OPEN_ACCT ( 
COD_CUST_CODE VARCHAR2(16) not null, 
COD_BANK VARCHAR2(32) not null, 
COD_ACCT_TYPE CHAR(1) not null, 
COD_ACCT VARCHAR2(32) not null, 
NAM_ACCT VARCHAR2(64), 
COD_CUST_ID_TYPE VARCHAR2(2), 
COD_CUST_ID_NO VARCHAR2(32), 
COD_CUST_PHONE VARCHAR2(32), 
FLG_DEAL char(1), 
DAT_CREATE TIMESTAMP not null, 
USR_CREATE VARCHAR2(32), 
ORG_CREATE VARCHAR2(32), 
DAT_MODIFY TIMESTAMP not null, 
USR_MODIFY VARCHAR2(32), 
ORG_MODIFY VARCHAR2(32), 
constraint PK_FOC_BATCH_OPEN_ACCT primary key (COD_CUST_CODE) 
); 

comment on table FOC_BATCH_OPEN_ACCT is 
'批量开户信息表'; 

comment on column FOC_BATCH_OPEN_ACCT.COD_CUST_CODE is 
'客户编号'; 

comment on column FOC_BATCH_OPEN_ACCT.COD_BANK is 
'银行编码'; 

comment on column FOC_BATCH_OPEN_ACCT.COD_ACCT_TYPE is 
'账户类型 1-借记卡'; 

comment on column FOC_BATCH_OPEN_ACCT.COD_ACCT is 
'账号'; 

comment on column FOC_BATCH_OPEN_ACCT.NAM_ACCT is 
'户名'; 

comment on column FOC_BATCH_OPEN_ACCT.COD_CUST_ID_TYPE is 
'证件类型 01-大陆身份证 02-港澳台'; 

comment on column FOC_BATCH_OPEN_ACCT.COD_CUST_ID_NO is 
'证件号码'; 

comment on column FOC_BATCH_OPEN_ACCT.COD_CUST_PHONE is 
'电话号码'; 

comment on column FOC_BATCH_OPEN_ACCT.FLG_DEAL is 
'处理标识 0-未处理 1-处理中 2-处理成功 3-处理失败'; 

comment on column FOC_BATCH_OPEN_ACCT.DAT_CREATE is 
'创建时间'; 

comment on column FOC_BATCH_OPEN_ACCT.USR_CREATE is 
'创建人员'; 

comment on column FOC_BATCH_OPEN_ACCT.ORG_CREATE is 
'创建机构'; 

comment on column FOC_BATCH_OPEN_ACCT.DAT_MODIFY is 
'维护时间'; 

comment on column FOC_BATCH_OPEN_ACCT.USR_MODIFY is 
'维护人员'; 

comment on column FOC_BATCH_OPEN_ACCT.ORG_MODIFY is 
'维护机构'; 




/*==============================================================*/ 
/* Table: FOC_CUST_XREF */ 
/*==============================================================*/ 
create table FOC_CUST_XREF ( 
COD_CUST_CODE VARCHAR2(16) not null, 
SIGN_NUM VARCHAR2(32) not null, 
COD_SIGN_TYPE CHAR(1) not null, 
COD_OPEN_MODE CHAR(1) not null, 
COD_RSL_SYNCHRO CHAR(1) not null,
DAT_CREATE TIMESTAMP not null, 
USR_CREATE VARCHAR2(32), 
ORG_CREATE VARCHAR2(32), 
DAT_MODIFY TIMESTAMP not null, 
USR_MODIFY VARCHAR2(32), 
ORG_MODIFY VARCHAR2(32), 
constraint PK_FOC_CUST_XREF primary key (COD_CUST_CODE) 
); 

comment on table FOC_CUST_XREF is 
'通联会员号映射表'; 

comment on column FOC_CUST_XREF.COD_CUST_CODE is 
'客户编号'; 

comment on column FOC_CUST_XREF.SIGN_NUM is 
'通联会员号'; 

comment on column FOC_CUST_XREF.COD_SIGN_TYPE is 
'签约类型：1.网页；2.快捷；3.信任；4.供应商开户；5.合作签约；6:企业开户'; 

comment on column FOC_CUST_XREF.COD_OPEN_MODE is 
'开户方式 0-联机 1-批量'; 

comment on column FOC_CUST_XREF.COD_RSL_SYNCHRO is 
'同步状态('同步结果:0-未同步 1-已同步')'; 

comment on column FOC_CUST_XREF.DAT_CREATE is 
'创建时间'; 

comment on column FOC_CUST_XREF.USR_CREATE is 
'创建人员'; 

comment on column FOC_CUST_XREF.ORG_CREATE is 
'创建机构'; 

comment on column FOC_CUST_XREF.DAT_MODIFY is 
'维护时间'; 

comment on column FOC_CUST_XREF.USR_MODIFY is 
'维护人员'; 

comment on column FOC_CUST_XREF.ORG_MODIFY is 
'维护机构'; 



CREATE TABLE foc_batch_open_acct_result
(
   cod_batch_no     VARCHAR2 (32) NOT NULL,
   cod_cust_code    VARCHAR2 (16) NOT NULL,
   txt_line    VARCHAR2 (512),
   result           VARCHAR2 (1),
   cod_error_code   VARCHAR2 (60),
   txt_errog_msg    VARCHAR2 (512),
   DAT_CREATE TIMESTAMP not null, 
   USR_CREATE VARCHAR2(32), 
   ORG_CREATE VARCHAR2(32), 
   DAT_MODIFY TIMESTAMP not null, 
   USR_MODIFY VARCHAR2(32), 
   ORG_MODIFY VARCHAR2(32), 
   CONSTRAINT foc_batch_open_acct_result PRIMARY KEY (cod_batch_no, cod_cust_code)
);

comment on table foc_batch_open_acct_result is  '批量开户结果表'; 
comment on column foc_batch_open_acct_result.cod_batch_no is '批次号'; 
comment on column foc_batch_open_acct_result.COD_CUST_CODE is '客户编号'; 
comment on column foc_batch_open_acct_result.txt_line is '行数据'; 
COMMENT ON COLUMN foc_batch_open_acct_result.result IS '交易结果(0-成功 1-交易中 2-失败)';
COMMENT ON COLUMN foc_batch_open_acct_result.cod_error_code IS '错误码';
COMMENT ON COLUMN foc_batch_open_acct_result.txt_errog_msg IS '错误信息';
COMMENT ON COLUMN foc_batch_open_acct_result.dat_create IS '创建时间';
COMMENT ON COLUMN foc_batch_open_acct_result.dat_modify IS '维护时间';