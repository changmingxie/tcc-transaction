import React from 'react';
import { Layout, Menu } from 'antd';
import { Switch, Route, Link } from 'react-router-dom';
import Normal from './Normal';
import Degrade from './Degrade';

const { Sider, Content } = Layout;

const Page = () => (
  <Layout>
    <Sider width={200} style={{ background: '#fff'}}>
      <Menu mode="inline">
        <Menu.Item>
          <Link to="/agg/normal">Normal</Link>
        </Menu.Item>
        <Menu.Item >
          <Link to="/agg/degrade">降级管理</Link>
        </Menu.Item>
      </Menu>
    </Sider>
    <Content style={{ padding: '0 24px', minHeight: 320 }}>
      <Switch>
        <Route path="/agg/normal">
          <Normal />
        </Route>
        <Route path="/agg/degrade">
          <Degrade />
        </Route>
      </Switch>
    </Content>
  </Layout>
)

export default Page;