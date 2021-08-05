import {useEffect, useState} from 'react';
import {Button, Cascader, Form, message, Modal, Table} from 'antd';
import ReactJson from 'react-json-view';
import * as api from '../../api/agg';
import CreateModal from './modal';

const columns = [
  {
    title: "Domain",
    key: "Domain",
    dataIndex: "Domain",
    render: (text) => (
      <span>{ text }</span>
    ),
  },
  {
    title: "ID",
    key: "key",
    dataIndex: "key",
  },
  {
    title: "Status",
    key: "status",
    dataIndex: "status",
  },
  {
    title: "Transaction Type",
    key: "type",
    dataIndex: "type",
  },
  {
    title: "Retried Count",
    key: "retried",
    dataIndex: "retried",
  },
  {
    title: "Content",
    key: "content",
    dataIndex: "content",
    render: (text) => {
      return (
        <Button
          className = "button"
          size = "small"
          type = "primary"
          onClick = {
            () => {
              Modal.info({
                content: (<ReactJson src={ JSON.parse(text) } />),
                width: "90%",
              });
            }
          }
        >查看详情</Button>
      );
    },
  },
  {
    title: "Create Time",
    key: "createTime",
    dataIndex: "createTime",
  },
  {
    title: "Last Update Time",
    key: "lastUpdateTime",
    dataIndex: "lastUpdateTime",
  },
]

const Normal = () => {
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [domain, setDomain] = useState('');
  const [row, setRow] = useState('');
  const [dataSource, setDataSource] = useState({});
  const [domainData, setDomainData] = useState([]);
  const [addVisible, setAddVisible] = useState(false);
  const [loadingStatus, setLoadingStatus] = useState(false);

  const onSelectChange = rowKeys => {
    setSelectedRowKeys(rowKeys);
  };

  const getDomainList = () => {
    api.getDomains().then(res => {
      setDomainData(res.data);
    })
  }

  const getListData = (pageNum=1, d=domain, r=row) => {
    setLoadingStatus(true);
    setDataSource([]);
    api.getList({
      domain: d,
      pageNum,
      row: r
    }).then(res => {
      setLoadingStatus(false);
      setDataSource(res.data);
    }).catch(err => {
      setLoadingStatus(false);
      if (err.response.status === 500) {
        message.error('服务异常，请稍后再试');
      }
    });
  }

  const handleReset = () => {
    api.handleToReset({
      domain,
      row,
      keys: selectedRowKeys
    }).then(res => {
      if (res.data.code === 200) {
        getListData();
        setSelectedRowKeys([]);
        message.success('重置成功');
        return;
      }

      message.error(res.data.message);
    })
  }

  const handleSearch = values => {
    setDomain(values.domain[0]);
    setRow(values.domain[1]);
    setSelectedRowKeys([]);
    getListData(1, values.domain[0], values.domain[1]);
  }

  useEffect(() => {
    getDomainList();
  }, []);

  return (
    <>
      <div>
        <Form layout="inline" onFinish={handleSearch}>
          <Form.Item
            label="DOMAIN"
            name="domain"
            rules={[
              {
                required: true,
                message: '请输入'
              }
            ]}
          >
            <Cascader
              style={{ width: 600 }}
              placeholder="请选择"
              options={domainData}
              changeOnSelect
            />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">查询</Button>
          </Form.Item>
          <Form.Item>
            <Button type="primary" disabled onClick={() => setAddVisible(true)}>添加</Button>
          </Form.Item>
        </Form>
      </div>
      <div>
        <div style={{ margin: '30px 0' }}>
          <Button
            type="primary"
            disabled={!selectedRowKeys.length}
            onClick={handleReset}
          >重置</Button>
        </div>
        <Table
          rowKey="key"
          columns={columns}
          dataSource={dataSource.items}
          size="small"
          bordered
          rowSelection={{
            selectedRowKeys,
            onChange: onSelectChange
          }}
          loading={loadingStatus}
          pagination={{
            pageSize: +dataSource.pageSize,
            total: +dataSource.total,
            current: +dataSource.pageNum,
            size: 'default',
            showSizeChanger: false,
            onChange: page => getListData(+page)
          }}
        />
      </div>
      {
        addVisible && (
          <CreateModal
            handleCancel={() => setAddVisible(false)}
            getList={() => getListData()}
          />
        )
      }
    </>
  );
}

export default Normal;
