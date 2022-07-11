import {Button, Dropdown, Image, Layout, Menu, Space} from "antd";
import {DownOutlined} from "@ant-design/icons";
import React, {useState} from "react";
import {withRouter} from "react-router-dom";

const {Header} = Layout;


const Page = (props) => {
  const {history} = props;
  const [visible, setVisible] = useState(false);
  const loginedUserName = localStorage.getItem("username");

  const handleMenuClick = (e) => {
    if (e.key === 'logout') {
      logout();
    }
    setVisible(false);

  };

  const handleVisibleChange = (flag) => {
    setVisible(flag);
  };

  const logout = () => {
    localStorage.clear();
    history.push('/login');
  };

  const userMenu = (
    <Menu onClick={handleMenuClick} theme="dark">
      <Menu.Item key='logout'>登出</Menu.Item>
    </Menu>
  );

  const toLogin = () => {
    console.log("toLogin")
    history.push('/login');
  }

  return (
    <Header
      style={{
        backgroundColor: '#001529',
        padding: '0px',
      }}
    >
      <span style={{top: '0.6rem', position: 'relative', display: 'inline-block'}}>
        <Image
          width={30}
          preview={false}
          src="logo192.png"
        />
      </span>
      <span style={{color: '#fff', fontSize: 18, fontWeight: 'bold',}}>TCC管理后台</span>
      <span style={{float: 'right'}}>
        {
          !loginedUserName ?
            <Button onClick={() => toLogin()}>登录</Button>
            :
            <Dropdown overlay={userMenu} onVisibleChange={handleVisibleChange} visible={visible}>
              <Button type="primary" danger ghost>
                <Space>
                  {loginedUserName}
                  <DownOutlined/>
                </Space>
              </Button>
            </Dropdown>
        }
      </span>
    </Header>
  )
}

export default withRouter(Page);
