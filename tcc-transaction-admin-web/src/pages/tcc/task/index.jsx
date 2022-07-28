import React, {useEffect, useState} from 'react';
import {getAllTask, taskModifyCron, taskPause, taskResume} from '../../../common/api';
import {Button, Col, Form, Input, message, Modal, Popconfirm, Row, Space, Table} from 'antd';


const Page = () => {
  const [taskList, setTaskList] = useState([]);
  const [loadingStatus, setloadingStatus] = useState(false); //加载数据
  const [modifyModalVisible, setModifyModalVisible] = useState(false);
  const [waitModifyTask, setWaitModifyTask] = useState({});
  const [form] = Form.useForm();  //form实例

  useEffect(() => {
    reLoadAllTaskList();
  }, []);
  useEffect(() => {
    form.resetFields();
  }, [waitModifyTask]);

  const reLoadAllTaskList = () => {
    setloadingStatus(true);
    getAllTask().then(data => {
      setloadingStatus(false);
      setTaskList(data);
    }).catch((res) => {
      setloadingStatus(false);
    });
  }
  const columns = [
    {
      title: 'domain',
      dataIndex: 'domain',
      key: 'domain',
    },
    {
      title: 'job组',
      dataIndex: 'jobGroup',
      key: 'jobGroup',
    },
    {
      title: 'job名称',
      dataIndex: 'jobName',
      key: 'jobName',
    },
    {
      title: 'cron表达式',
      dataIndex: 'cronExpression',
      key: 'cronExpression',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: '操作',
      key: 'operation',
      render: (text, record) => (
        <Space>
          {
            record.status === 'NORMAL' ?
              <Popconfirm
                title="是否执行"
                onConfirm={() => {
                  taskPause(record.domain).then(resp => {
                    reLoadAllTaskList()
                  })
                }}
                okText="是"
                cancelText="否">
                <Button
                  size="small"
                  type="primary"
                  danger>停止</Button>
              </Popconfirm>
              : <></>
          }
          {
            record.status !== 'NORMAL' ?
              <Popconfirm
                title="是否执行"
                onConfirm={() => {
                  taskResume(record.domain).then(resp => {
                    reLoadAllTaskList()
                  })
                }}
                okText="是"
                cancelText="否">
                <Button
                  size="small"
                  type="primary"
                  style={{backgroundColor: '#faad14', borderColor: '#faad14'}}>恢复</Button>
              </Popconfirm>
              : <></>
          }
          <Button
            size="small"
            type="primary"
            onClick={() => {
              showModifyModal(record)
            }}>
            修改
          </Button>
          {/*<Popconfirm*/}
          {/*  title="是否执行"*/}
          {/*  onConfirm={() => {*/}
          {/*    taskDelete(record.domain).then(resp => {*/}
          {/*      reLoadAllTaskList()*/}
          {/*    })*/}
          {/*  }}*/}
          {/*  okText="是"*/}
          {/*  cancelText="否">*/}
          {/*  <Button*/}
          {/*    size="small"*/}
          {/*    type="primary"*/}
          {/*    danger>*/}
          {/*    删除*/}
          {/*  </Button>*/}
          {/*</Popconfirm>*/}

        </Space>
      ),
    }
  ];
  const showModifyModal = (record) => {
    setWaitModifyTask({
      ...record
    })
    setModifyModalVisible(true);
  };

  const handleCancel = () => {
    setModifyModalVisible(false);
  };


  const onFinish = async () => {
    const values = await form.validateFields();
    taskModifyCron(values).then(res => {
      setModifyModalVisible(false);
      reLoadAllTaskList();
    }).catch((res) => {
    });
  };
  return (
    <div>

      <Row style={{padding: '12px'}}>
        <Col span={23}>
          &nbsp;
        </Col>
        <Col span={1}>
          <Button type="primary"
                  size="small"
                  onClick={() => reLoadAllTaskList()}>
            刷新
          </Button>
        </Col>
      </Row>
      <Table rowKey={record => record.domain}
             dataSource={taskList}
             columns={columns}
             pagination={false}
             loading={loadingStatus}/>
      <Modal title="修改" visible={modifyModalVisible}
             getContainer={false}
             forceRender
             closable={false}
             footer={[
               <Button key="cancel" onClick={handleCancel}>取消</Button>,
               <Button key="submit" type="primary" onClick={onFinish}>确认</Button>
             ]}
      >
        <Form
          name="basic"
          form={form}
          labelCol={{span: 6}}
          wrapperCol={{span: 16}}
          onFinish={onFinish}
          autoComplete="off"
        >
          <Form.Item
            label="domain"
            name="domain"
            initialValue={waitModifyTask.domain}>
            <Input disabled/>
          </Form.Item>
          <Form.Item
            label="cron表达式"
            name="cronExpression"
            initialValue={waitModifyTask.cronExpression}
            rules={[{required: true, message: '请输入cronExpression'}]}>
            <Input/>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default Page;
