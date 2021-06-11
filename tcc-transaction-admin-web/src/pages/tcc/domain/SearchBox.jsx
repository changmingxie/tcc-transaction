import React, {useState} from 'react';
import {Button, Form, Select, Drawer} from 'antd';
import {useDispatch, useSelector} from 'react-redux';
import {Domain} from '../../../store/actions/domain';
import DrawerDomainList from './DrawerDomainList';

const SearchBox = (props) => {
  const [addVisible, setAddVisible] = useState(false); //新增弹窗
  const [showDrawer, setShowDrawer] = useState(false); //抽屉显示
  const {domainData} = useSelector(({domain}) => domain);
  const dispatch = useDispatch();
  const onSearch = (values) => {
    // todo add
    dispatch({type: Domain.UPDATE_CURRENT_DOMAIN, payload: values?.domain});
  };
  return (
    <div className="search-panel" style={{margin: '30px 0'}}>
      <Form layout="inline" onFinish={onSearch}>
        <Form.Item
          label="DOMAIN"
          name="domain"
          rules={[
            {
              required: true,
              message: '请输入',
            },
          ]}
        >
          <Select
            style={{width: 600}}
            placeholder="请选择"
            showSearch
          >
            {domainData.map((val, idx) => {
              return (
                <Select.Option key={idx} value={val.value}>{val.label}</Select.Option>
              );
            })}
          </Select>
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
          &nbsp;&nbsp;&nbsp;
          <Button
            type="primary"
            disabled={true}
            onClick={() => setAddVisible(true)}
          >
            添加
          </Button>
          &nbsp;&nbsp;&nbsp;
          <Button
            danger
            onClick={() => setShowDrawer(true)}
          >
            删除Domain
          </Button>
        </Form.Item>
      </Form>
      <Drawer
        title="Domain List"
        placement='right'
        closable={false}
        onClose={() => setShowDrawer(false)}
        visible={showDrawer}
        key='right'
        width="30%"
      >
        <DrawerDomainList />
      </Drawer>
      {/*{addVisible ? (*/}
      {/*  <CreateModal*/}
      {/*    handleCancel={() => {*/}
      {/*      setAddVisible(false);*/}
      {/*    }}*/}
      {/*    getList={onSearch}*/}
      {/*  />*/}
      {/*) : null}*/}
    </div>
  );
};

export default SearchBox;
