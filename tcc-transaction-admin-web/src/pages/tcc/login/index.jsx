import React, {useState} from 'react';
import {Button, Card, Form, Input, Spin} from 'antd';
import {userLogin} from "../../../common/api";
import {LockOutlined, UserOutlined} from '@ant-design/icons';


const Page = (props) => {
  // const {history} = props;
  const {history} = props;
  const [loginLoading, setLoginLoading] = useState(false);


  const [form] = Form.useForm();  //form实例

  const handleSubmit = async () => {
    const values = await form.validateFields();
    setLoginLoading(true)
    userLogin(values).then(res => {
      localStorage.setItem('tcc-token', res.token);
      localStorage.setItem('username', res.username);
      localStorage.setItem('connectionMode', res.connectionMode);
      history.push('/welcome');
      setLoginLoading(false);
    }).catch(res => {
      setLoginLoading(false);
    });
  }

  return (
    <div className="login-container">
      <Spin spinning={loginLoading} size="large" delay={500}>
        <Card title="TCC管理后台" style={{width: 500, margin: '15rem auto'}} hoverable>
          <Form
            name="basic"
            form={form}
            autoComplete="off"
          >
            <Form.Item
              name="username"
              rules={[
                {
                  required: true,
                  message: '请输入用户名!',
                },
              ]}
            >
              <Input size="large" placeholder="用户名" prefix={<UserOutlined/>}/>
            </Form.Item>

            <Form.Item
              name="password"
              rules={[
                {
                  required: true,
                  message: '请输入密码!',
                },
              ]}
            >
              <Input.Password size="large" placeholder="密码" prefix={<LockOutlined/>}/>
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit" onClick={handleSubmit} block>
                登录
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </Spin>
    </div>
  );
};

export default Page;
