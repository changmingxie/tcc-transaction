import React, {useEffect, useState} from 'react';
import {Button, Col, Form, Input, message, Modal, Popconfirm, Row, Select, Space, Table, Tooltip} from "antd";
import {domainAlertTest, domainCreate, domainDelete, domainModify, getAllDomains} from "../../../common/api";

const {Option} = Select;


const Page = (props) => {
  const [domainList, setDomainList] = useState([]);
  const [loadingStatus, setLoadingStatus] = useState(false); //加载数据
  const [addDomainModalVisible, setAddDomainModalVisible] = useState(false);
  const [modifyDomainModalVisible, setModifyDomainModalVisible] = useState(false);
  const [waitModifyDomain, setWaitModifyDomain] = useState(false);
  const [form] = Form.useForm();  //form实例
  const [modifyForm] = Form.useForm();  //form实例

  const showRender = (text, record) => {
    if (text && text.length > 20) {
      let shortText = String(text).substr(0, 20).concat("...");
      return (
        <div>
          <Tooltip placement="top" title={text}>
            <Button>{shortText}</Button>
          </Tooltip>
        </div>
      );
    }
    return text;
  };

  const columns = [
    {
      title: 'domain',
      key: 'domain',
      dataIndex: 'domain',
      width: 150
    },
    {
      title: '恢复任务最大重试次数',
      key: 'maxRetryCount',
      dataIndex: 'maxRetryCount',
      width: 150
    },
    {
      title: '恢复任务最大TPS',
      key: 'maxRecoveryRequestPerSecond',
      dataIndex: 'maxRecoveryRequestPerSecond',
      width: 150
    },
    {
      title: '手机号',
      key: 'phoneNumbers',
      dataIndex: 'phoneNumbers',
      width: 200,
      render: showRender
    },
    {
      title: '告警类型',
      key: 'alertType',
      dataIndex: 'alertType',
      width: 100,
      render: (text, record) => {
        if (text === 'DING') {
          return '钉钉';
        }
        if (text === 'SMS') {
          return '短信';
        }
        if (text === 'PHONE') {
          return '电话';
        }
        return 'UNKONW'
      }
    },
    {
      title: '告警阈值',
      key: 'threshold',
      dataIndex: 'threshold',
      width: 100
    },
    {
      title: '告警间隔(分钟)',
      key: 'intervalMinutes',
      dataIndex: 'intervalMinutes',
      width: 100
    },
    {
      title: '上次告警时间',
      key: 'lastAlertTime',
      dataIndex: 'lastAlertTime',
      width: 100
    },
    {
      title: '钉钉机器人',
      key: 'dingRobotUrl',
      dataIndex: 'dingRobotUrl',
      width: 250,
      render: showRender
    },
    {
      title: '创建时间',
      key: 'createTime',
      dataIndex: 'createTime',
      width: 150
    },
    {
      title: '修改时间',
      key: 'lastUpdateTime',
      dataIndex: 'lastUpdateTime',
      width: 150
    },
    {
      title: '版本号',
      key: 'version',
      dataIndex: 'version',
      width: 80
    },
    {
      title: '操作',
      key: 'operation',
      fixed: 'right',
      width: 180,
      render: (text, record) => (
        <Space>
          <Button
            size="small"
            type="primary"
            onClick={() => {
              showModifyDomainModal(record)
            }}>
            修改
          </Button>
          <Popconfirm
            title="是否执行"
            onConfirm={() => {
              alertTest(record)
            }}
            okText="是"
            cancelText="否">
            <Button
              size="small"
              type="primary"
              style={{backgroundColor: '#faad14', borderColor: '#faad14'}}
              danger>告警测试</Button>
          </Popconfirm>
          <Popconfirm
            title="是否删除"
            onConfirm={() => {
              doDeleteDomain(record)
            }}
            okText="是"
            cancelText="否">
            <Button
              size="small"
              type="primary"
              danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    }
  ];

  useEffect(() => {
    reLoadDomainList();
  }, []);

  useEffect(() => {
    modifyForm.resetFields();
  }, [waitModifyDomain]);

  const reLoadDomainList = () => {
    setLoadingStatus(true);
    getAllDomains().then(data => {
      setLoadingStatus(false);
      setDomainList(data);
    }).catch((res) => {
      setLoadingStatus(false);
    });
  }

  const doAddDomainCancel = () => {
    setAddDomainModalVisible(false);
  }
  const doAddDomainConfirm = async () => {
    const values = await form.validateFields();
    domainCreate(values).then(res => {
      setAddDomainModalVisible(false);
      reLoadDomainList();
    }).catch((res) => {
    });
  }

  const showAddDomainModel = () => {
    setAddDomainModalVisible(true)
  }

  const showModifyDomainModal = (record) => {
    setModifyDomainModalVisible(true)
    setWaitModifyDomain({
      ...record
    });
  }

  const doModifyDomainCancel = () => {
    setModifyDomainModalVisible(false);
  }
  const doModifyDomainConfirm = async () => {
    const values = await modifyForm.validateFields();
    domainModify(values).then(res => {
      setModifyDomainModalVisible(false);
      reLoadDomainList();
    })
  }

  const doDeleteDomain = (record) => {
    domainDelete(record).then(res => {
      reLoadDomainList();
    })
  }

  const alertTest = (record) => {
    domainAlertTest(record).then(res => {
      message.info("告警测试成功！")
    })
  }

  return (
    <>
      <Row style={{paddingBottom: '12px', paddingRight: '12px'}}>
        <Col span={1}>
          <Button style={{backgroundColor: '#faad14', borderColor: '#faad14'}}
                  type="primary"
                  size="small"
                  danger
                  onClick={() => showAddDomainModel()}>
            新增
          </Button>
        </Col>
        <Col span={22}>
          &nbsp;
        </Col>
        <Col span={1}>
          <Button
            type="primary"
            size="small"
            onClick={() => reLoadDomainList()}>
            刷新
          </Button>
        </Col>
      </Row>
      <Table rowKey={record => record.domain}
             columns={columns}
             loading={loadingStatus}
             dataSource={domainList}
             scroll={{x: 2000, y: 800}}/>

      <Modal title="新增Domain" visible={addDomainModalVisible}
             getContainer={false}
             forceRender
             closable={false}
             footer={[
               <Button key="cancel" onClick={doAddDomainCancel}>取消</Button>,
               <Button key="submit" type="primary" onClick={doAddDomainConfirm}>确认</Button>
             ]}
      >
        <Form
          name="basic"
          form={form}
          labelCol={{span: 8}}
          wrapperCol={{span: 16}}
          autoComplete="off"
        >
          <Form.Item
            label="domain"
            name="domain"
            rules={[{required: true, message: '请输入domain'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="恢复任务最大重试次数"
            name="maxRetryCount"
            initialValue={3}
            rules={[{required: true, message: '请输入恢复任务最大重试次数'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="恢复任务最大TPS"
            initialValue={100}
            name="maxRecoveryRequestPerSecond"
            rules={[{required: true, message: '请输入恢复任务最大TPS'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="手机号列表"
            name="phoneNumbers"
            rules={[{
              pattern: "[1-9][0-9]{10}(,[1-9][0-9]{10}){0,20}$",
              message: '手机号列表格式有误，形如：12345678901,12345678902'
            }]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="告警类型"
            initialValue={'DING'}
            name="alertType"
            rules={[{required: true, message: '告警类型必填'}]}>
            <Select
              style={{
                width: 80,
                margin: '0 8px',
              }}
            >
              <Option value="DING">钉钉</Option>
              <Option value="SMS" disabled>短信</Option>
              <Option value="PHONE" disabled>电话</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="告警阈值"
            name="threshold"
            rules={[{pattern: "[0-9]+", message: '必须为整数'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="告警间隔(分钟)"
            name="intervalMinutes"
            rules={[{pattern: "[0-9]+", message: '必须为整数'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="钉钉机器人地址"
            name="dingRobotUrl">
            <Input/>
          </Form.Item>
        </Form>
      </Modal>
      <Modal title="修改Domain" visible={modifyDomainModalVisible}
             getContainer={false}
             forceRender
             closable={false}
             footer={[
               <Button key="cancel" onClick={doModifyDomainCancel}>取消</Button>,
               <Button key="submit" type="primary" onClick={doModifyDomainConfirm}>确认</Button>
             ]}
      >
        <Form
          name="basic"
          form={modifyForm}
          labelCol={{span: 8}}
          wrapperCol={{span: 16}}
          autoComplete="off"
        >
          <Form.Item
            label="domain"
            name="domain"
            initialValue={waitModifyDomain.domain}
            rules={[{required: true, message: '请输入domain'}]}>
            <Input disabled/>
          </Form.Item>
          <Form.Item
            label="恢复任务最大重试次数"
            name="maxRetryCount"
            initialValue={waitModifyDomain.maxRetryCount}
            rules={[{required: true, message: '请输入恢复任务最大重试次数'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="恢复任务最大TPS"
            name="maxRecoveryRequestPerSecond"
            initialValue={waitModifyDomain.maxRecoveryRequestPerSecond}
            rules={[{required: true, message: '请输入恢复任务最大TPS'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="手机号列表"
            initialValue={waitModifyDomain.phoneNumbers}
            name="phoneNumbers"
            rules={[{
              pattern: "[1-9][0-9]{10}(,[1-9][0-9]{10}){0,20}$",
              message: '手机号列表格式有误，形如：12345678901,12345678902'
            }]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="告警类型"
            initialValue={waitModifyDomain.alertType}
            name="alertType"
            rules={[{required: true, message: '告警类型必填'}]}>
            <Select
              style={{
                width: 80,
                margin: '0 8px',
              }}
            >
              <Option value="DING">钉钉</Option>
              <Option value="SMS" disabled>短信</Option>
              <Option value="PHONE" disabled>电话</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="告警阈值"
            initialValue={String(waitModifyDomain.threshold)}
            name="threshold"
            rules={[{pattern: "[0-9]+", message: '必须为整数'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="告警间隔(分钟)"
            initialValue={String(waitModifyDomain.intervalMinutes)}
            name="intervalMinutes"
            rules={[{pattern: "[0-9]+", message: '必须为整数'}]}>
            <Input/>
          </Form.Item>
          <Form.Item
            label="钉钉机器人地址"
            initialValue={waitModifyDomain.dingRobotUrl}
            name="dingRobotUrl">
            <Input/>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
export default Page;
