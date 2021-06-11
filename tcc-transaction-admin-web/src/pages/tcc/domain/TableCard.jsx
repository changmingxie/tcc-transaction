import React, {useEffect, useState} from 'react';
import {Button, Card, message, Space, Table} from 'antd';
import {columns} from '../../../common/constants';
import {useSelector} from 'react-redux';
import * as api from '../../../common/api';

const TableCard = (props) => {
  const {row, activeTabKey} = props;
  const {currentDomain: domain, refresh} = useSelector(({domain}) => domain);
  const [datasource, setdatasource] = useState({}); //列表数据
  const [loadingStatus, setloadingStatus] = useState(false); //加载数据
  const isDeleted = activeTabKey === 'deletedKeys';
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 1,
  });
  const handleTableChange = (pagination) => {
    setPagination({
      current: pagination.current,
      pageSize: pagination.pageSize,
      total: pagination.total,
    });
  };
  //获取列表数据
  const getListData = () => {
    setloadingStatus(true);
    setdatasource([]);
    let data = {
      domain,
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      row,
      isDeleted,
    };
    api
      .getManageList(data)
      .then((res) => {
        setloadingStatus(false);
        setdatasource(res);
        setPagination({
          current: res.pageNum,
          pageSize: res.pageSize,
          total: res.total,
        })
      })
      .catch((res) => {
        setloadingStatus(false);
        message.error('服务异常，请稍后再试');
      });
  };

  const reloadHandler = (promise) => {
    promise
      .then((res) => {
        if (+res.data.code === 200) {
          getListData();
          message.success('操作成功');
        } else {
          message.error(res.data.message);
        }
      })
      .catch(e => {
        message.error(e.message || '服务异常，请稍后再试');
      });
  };

  useEffect(() => {
    getListData();
  }, [activeTabKey, refresh, pagination.current, pagination.pageSize]);

  return isDeleted
    ? (
      <Card title={domain.concat(row)}>
        <Table
          rowKey="key"
          columns={columns.concat({
            title: 'Operation',
            key: 'operation',
            render: (text, record) => (
              <Space size="middle">
                <Button type="link" onClick={() => {
                  reloadHandler(api.restore({
                    domain,
                    row,
                    globalTxId: record.globalTxId,
                    branchQualifier: record.branchQualifier,
                  }));
                }}>restore</Button>
              </Space>
            ),
          })}
          dataSource={datasource.items}
          size="small"
          bordered
          loading={loadingStatus}
          pagination={pagination}
          onChange={handleTableChange}
        />
      </Card>
    )
    : (
      <Card title={domain.concat(row)}>
        <Table
          rowKey="key"
          columns={columns.concat({
            title: 'Operation',
            key: 'operation',
            render: (text, record) => (
              <Space size="small">
                <Button type="link" onClick={() => {
                  reloadHandler(api.confirm({
                    domain,
                    row,
                    globalTxId: record.globalTxId,
                    branchQualifier: record.branchQualifier,
                  }));
                }}>confirm</Button>
                <Button type="link" onClick={() => {
                  reloadHandler(api.cancel({
                    domain,
                    row,
                    globalTxId: record.globalTxId,
                    branchQualifier: record.branchQualifier,
                  }));
                }}>cancel</Button>
                <Button type="link" onClick={() => {
                  reloadHandler(api.reset({
                    domain,
                    row,
                    globalTxId: record.globalTxId,
                    branchQualifier: record.branchQualifier,
                  }));
                }}>reset</Button>
                <Button type="link" danger onClick={() => {
                  reloadHandler(api.remove({
                    domain,
                    row,
                    globalTxId: record.globalTxId,
                    branchQualifier: record.branchQualifier,
                  }));
                }}>remove</Button>
              </Space>
            ),
          })
          }
          dataSource={datasource.items}
          size="small"
          bordered
          loading={loadingStatus}
          pagination={pagination}
          onChange={handleTableChange}
        />
      </Card>
    );
};

export default TableCard;
