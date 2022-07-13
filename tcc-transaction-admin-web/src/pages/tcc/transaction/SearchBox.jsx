import React from 'react';
import {Button, Form, Input, Select} from 'antd';
import {useDispatch, useSelector} from 'react-redux';
import {Domain} from '../../../store/actions/domain';

const SearchBox = (props) => {
  const {domainData} = useSelector(({domain}) => domain);
  const dispatch = useDispatch();
  const onSearch = (values) => {
    dispatch({type: Domain.UPDATE_CURRENT_DOMAIN, payload: values});
  };
  return (
    <div className="search-panel" style={{margin: '30px 0'}}>
      <Form layout="inline" onFinish={onSearch}>
        <Form.Item
          label="domain"
          name="domain"
          initialValue={props.domain}
          rules={[
            {
              required: true,
              message: '请输入',
            },
          ]}
        >
          <Select
            style={{width: 400}}
            placeholder="请选择"
            showSearch>
            {domainData.map((val, idx) => {
              return (
                <Select.Option key={idx} value={val}>{val}</Select.Option>
              );
            })}
          </Select>
        </Form.Item>

        <Form.Item
          label="xid"
          name="xidString"
        >
          <Input allowClear style={{width: 400}}/>
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
          &nbsp;&nbsp;&nbsp;
        </Form.Item>
      </Form>
    </div>
  );
};

export default SearchBox;
