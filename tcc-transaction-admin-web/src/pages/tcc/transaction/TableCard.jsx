import React, {useEffect, useState} from 'react';
import {Button, Card, message, Modal, Popconfirm, Space, Table, Tag} from 'antd';
import {columns} from '../../../common/constants';
import {useSelector} from 'react-redux';
import * as api from '../../../common/api';
import ReactJson from "react-json-view";
import {InfoCircleOutlined} from '@ant-design/icons';


const TableCard = (props) => {
  const {activeTabKey} = props;
  const deleted = activeTabKey === 'deletedKeys';
  const {currentDomain: domain, xidString, refresh} = useSelector(({domain}) => domain);
  const [datasource, setdatasource] = useState([]); //列表数据
  const [total, setTotal] = useState(0); //总数
  const [loadingStatus, setloadingStatus] = useState(false); //加载数据
  const [selectedRows, setSelectedRows] = useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const onSelectChange = (selectedRowKeys, selectedRows) => {
    console.log('selectedRows changed: ', selectedRows);
    setSelectedRows(selectedRows);
    setSelectedRowKeys(selectedRowKeys);
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: onSelectChange,
  };
  // 一次获取1000条，再是前端分页
  const fetchNum = 1000;
  const initPagination = {
    offset: null,
    pageSize: fetchNum,
    forward: true
  };
  const pagination = {
    ...initPagination
  };

  const pageInit = () => {
    setdatasource([])
    setSelectedRows([])
    setSelectedRowKeys([])
    getListData(initPagination);
  }
  //获取列表数据
  const getListData = (pagination) => {
    if (!domain) {
      return;
    }
    setloadingStatus(true);
    let data = {
      domain,
      xidString,
      offset: pagination.offset,
      pageSize: pagination.pageSize,
      deleted,
    };
    api
      .getManageList(data)
      .then((res) => {
        setloadingStatus(false);
        setdatasource(res.items);
        setTotal(res.total);
        if (res.items && res.items.length <= 0) {
          message.success('没有数据了😁');
        }
      })
      .catch((res) => {
        setloadingStatus(false);
      });
  };

  const reloadHandler = (promises) => {
    promises
      .then((resultList) => {// TODO 需要进行异常测试
        let failCount = 0;
        let sucCount = resultList.length;
        if (failCount == 0) {
          message.success('批量操作成功');
        } else {
          message.warn("批量操作：成功数(" + sucCount + "),失败数(" + failCount + ")")
        }
        if (sucCount > 0) {
          pageInit()
        }
      })
      .catch(e => {
        message.error(e.message || '服务异常，请稍后再试');
      });
  };
  useEffect(() => {
    pageInit();
  }, [refresh, activeTabKey]);


  const batchHandle = (handle) => {
    if (!selectedRows || selectedRows.length <= 0) {
      message.warn("请至少选择一条事件");
      return;
    }
    let resultPromiseList = new Array();
    for (let i = 0; i < selectedRows.length; i++) {
      resultPromiseList.push(
        handle({
          ...selectedRows[i]
        }));
    }
    reloadHandler(Promise.all(resultPromiseList));
  };

  const openTransactionDetail = (record) => {
    api.detail(record).then(res => {
      let detailContent;
      let isJsonParseSuccess = true;
      try {
        console.log("contentFormat", res);
        detailContent = JSON.parse(res.content)
      } catch (e) {
        detailContent = [res.content];
        isJsonParseSuccess = false;
      }

      Modal.info({
        icon: isJsonParseSuccess === true ? <InfoCircleOutlined/> :
          <InfoCircleOutlined style={{color: 'red'}}/>,
        content:
          <>
            {
              isJsonParseSuccess === true ? <p></p> : <p style={{color: 'red'}}>事件详情解析异常，降级为base64展示，可复制内容去源服务进行人工解析</p>
            }
            <ReactJson collapseStringsAfterLength={100} src={detailContent}/>
          </>
        ,
        width: '90%',
      });
    })

  }

  return (
    <Card>
      {
        deleted ?
          <Space size="small" style={{float: "left", marginBottom: '1rem'}}>
            <Popconfirm
              title="是否执行"
              onConfirm={() => batchHandle(api.restore)}
              okText="是"
              cancelText="否">
              <Button
                size="small"
                type="primary"
                style={{backgroundColor: '#faad14', borderColor: '#faad14'}}>恢复</Button>
            </Popconfirm>
            <Popconfirm
              title="是否执行"
              onConfirm={() => batchHandle(api.transactionDelete)}
              okText="是"
              cancelText="否">
              <Button
                size="small"
                type="primary"
                danger>彻底删除</Button>
            </Popconfirm>
          </Space>
          :
          <Space size="small" style={{float: "left", marginBottom: '1rem'}}>
            <Popconfirm
              title="是否执行"
              onConfirm={() => batchHandle(api.confirm)}
              okText="是"
              cancelText="否">
              <Button
                size="small"
                type="primary"
                style={{backgroundColor: '#faad14', borderColor: '#faad14'}}>确认</Button>
            </Popconfirm>
            <Popconfirm
              title="是否执行"
              onConfirm={() => batchHandle(api.cancel)}
              okText="是"
              cancelText="否">
              <Button
                size="small"
                type="primary"
                style={{backgroundColor: '#faad14', borderColor: '#faad14'}}>取消</Button>
            </Popconfirm>
            <Popconfirm
              title="是否执行"
              onConfirm={() => batchHandle(api.reset)}
              okText="是"
              cancelText="否">
              <Button
                size="small"
                type="primary"
                style={{backgroundColor: '#faad14', borderColor: '#faad14'}}>重置</Button>
            </Popconfirm>
            <Popconfirm
              title="是否执行"
              onConfirm={() => batchHandle(api.remove)}
              okText="是"
              cancelText="否">
              <Button
                size="small"
                type="primary"
                danger>删除</Button>
            </Popconfirm>
          </Space>
      }

      <Space size="small" style={{float: "right"}}>
        <Tag color="#f50">总数:{total > fetchNum ? total + ', 仅显示' + fetchNum + '条' : total}</Tag>
      </Space>
      <Table
        rowKey={record => record.xidString}
        rowSelection={rowSelection}
        columns={columns.concat({
          title: '事件详情',
          key: 'detail',
          fixed: 'right',
          width: 80,
          render: (text, record) => (
            <Space>
              <Button
                className="button"
                size="small"
                type="primary"
                onClick={() => {
                  openTransactionDetail(record)
                }}
              >
                详情
              </Button>
            </Space>
          ),
        })
        }
        dataSource={datasource}
        size="small"
        bordered
        loading={loadingStatus}
        pagination={{
          defaultPageSize: 10,
          pageSizeOptions: [10,50,100,200,500,1000],
          showSizeChanger: true,
        }}
        scroll={{x: 1500, y: 800}}
      />
    </Card>
  );
};

export default TableCard;
