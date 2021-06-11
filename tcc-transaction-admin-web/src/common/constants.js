import React from 'react';
import {Button, Modal} from 'antd';
import ReactJson from 'react-json-view';

export const columns = [
  {
    title: 'Domain',
    dataIndex: 'domain',
    key: 'domain',
  },
  {
    title: 'Global Tx Id',
    dataIndex: 'globalTxId',
    key: 'globalTxId',
  },
  {
    title: 'Status',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: 'Transaction Type',
    dataIndex: 'transactionType',
    key: 'transactionType',
  },
  {
    title: 'Retried Count',
    dataIndex: 'retriedCount',
    key: 'retriedCount',
  },
  {
    title: 'contentView',
    dataIndex: 'contentView',
    key: 'contentView',
    render: (text) => {
      return (
          <Button
              className="button"
              size="small"
              type="primary"
              onClick={() => {
                Modal.info({
                  content: <ReactJson src={JSON.parse(text)}/>,
                  width: '90%',
                });
              }}
          >
            查看详情
          </Button>
      );
    },
  },
  {
    title: 'Create Time',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: 'Last Update Time',
    dataIndex: 'lastUpdateTime',
    key: 'lastUpdateTime',
  },
];
