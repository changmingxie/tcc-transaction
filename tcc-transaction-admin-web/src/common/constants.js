import {Button, message} from 'antd';

// createTime: "2022-05-18T01:09:22.000+0000"
const dateRender = (text, record) => {
  if (text) {
    let arr = String(text).split(".")
    return arr[0];
  }
  return text;
};
const xidRender = (text, record) => {
  let newText = text;
  if (text && text.length > 40) {
    newText = String(text).substr(0, 40).concat("...");
  }
  return (
    <div>
      {newText}
      <Button
        className="button"
        size="small"
        type="primary"
        style={{backgroundColor: '#03cee0', borderColor: '#03cee0'}}
        onClick={() => {
          let oInput = document.createElement('input')
          oInput.value = text;
          document.body.appendChild(oInput)
          oInput.select() // 选择对象
          document.execCommand("Copy") // 执行浏览器复制命令
          message.success('复制成功');
          oInput.remove()
        }}>
        复制
      </Button>
    </div>
  );
};
export const columns = [
  {
    title: 'domain',
    dataIndex: 'domain',
    key: 'domain',
    width: 150
  },
  {
    title: 'xid',
    dataIndex: 'xidString',
    key: 'xidString',
    width: 200,
    render: xidRender
  },
  {
    title: 'rootDomain',
    dataIndex: 'rootDomain',
    key: 'rootDomain',
    width: 150
  },
  {
    title: 'rootXid',
    dataIndex: 'rootXidString',
    key: 'rootXidString',
    width: 200,
    render: xidRender
  },
  {
    title: '状态',
    dataIndex: 'statusId',
    key: 'status',
    width: 120,
    render: (text, record) => {
      if (text === 1) {
        return 'TRYING';
      }
      if (text === 2) {
        return 'CONFIRMING';
      }
      if (text === 3) {
        return 'CANCELLING';
      }
      if (text === 11) {
        return 'TRY_SUCCESS';
      }
      if (text === 12) {
        return 'TRY_FAILED';
      }
      return 'UNKONW'
    }
  },
  {
    title: '事件类型',
    dataIndex: 'transactionTypeId',
    key: 'transactionType',
    width: 100,
    render: (text, record) => {
      if (text === 1) {
        return 'ROOT';
      }
      if (text === 2) {
        return 'BRANCH';
      }
      return 'UNKONW'
    }
  },
  {
    title: '已重试数',
    dataIndex: 'retriedCount',
    key: 'retriedCount',
    width: 120
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    render: dateRender
  },
  {
    title: '修改时间',
    dataIndex: 'lastUpdateTime',
    key: 'lastUpdateTime',
    width: 180,
    render: dateRender
  },
  {
    title: '版本号',
    dataIndex: 'version',
    key: 'version',
    width: 80
  },
];
