import {Button, Space, Table} from 'antd';
import {columns as originColumns} from '../common/constants';
import * as api from '../common/api';

const columns = originColumns.concat({
  title: 'Operation',
  key: 'operation',
  render: (text, record) => (
    <Space size="small">
      <Button type="link" onClick={() => {
        api.confirm({
          domain: record._domain,
          row: record._row,
          globalTxId: record.globalTxId,
          branchQualifier: record.branchQualifier
        })
      }}>confirm</Button>
      <Button type="link" onClick={() => {
        api.cancel({
          domain: record._domain,
          row: record._row,
          globalTxId: record.globalTxId,
          branchQualifier: record.branchQualifier
        })
      }}>cancel</Button>
      <Button type="link" onClick={() => {
        api.reset({
          domain: record._domain,
          row: record._row,
          globalTxId: record.globalTxId,
          branchQualifier: record.branchQualifier
        })
      }}>reset</Button>
      <Button type="link" danger onClick={() => {
        api.remove({
          domain: record._domain,
          row: record._row,
          globalTxId: record.globalTxId,
          branchQualifier: record.branchQualifier
        })
      }}>remove</Button>
    </Space>
  )
})

const Normal = ({ data }) => {
  return (
    <Table columns={columns} dataSource={data} />
  )
}

export default Normal
