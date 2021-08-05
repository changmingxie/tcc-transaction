import {Button, Space, Table} from 'antd';
import {columns as originColumns} from '../common/constants';
import {restore} from '../common/api';

const columns = originColumns.concat({
  title: 'Operation',
  key: 'operation',
  render: (text, record) => (
    <Space size="middle">
      <Button type="link" onClick={() => {
        restore({
          domain: record._domain,
          row: record._row,
          globalTxId: record.globalTxId,
          branchQualifier: record.branchQualifier
        })
      }}>restore</Button>
    </Space>
  )
})

const DeletedKey = ({ data }) => {
  return (
    <Table
      rowKey={record => `deleted_key_${record._index}`}
      columns={columns}
      dataSource={data}
    />
  )
}


export default DeletedKey;
