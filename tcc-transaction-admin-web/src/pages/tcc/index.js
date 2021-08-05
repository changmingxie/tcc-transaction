import {Layout, Menu} from 'antd';
import {Link, Route, Switch} from 'react-router-dom';
// @ts-ignore
import Domain from './domain';
import Degrade from './Degrade';

const { Sider, Content } = Layout;

const Page = () => (
  <Layout>
    <Sider width={200} style={{ background: '#fff'}}>
      <Menu mode="inline">
        <Menu.Item>
          <Link to="/tcc/normal">Normal</Link>
          </Menu.Item>
        <Menu.Item>
          <Link to="/tcc/degrade">降级配置</Link>
        </Menu.Item>
      </Menu>
    </Sider>
    <Content style={{ padding: '0 24px', minHeight: 320 }}>
      <Switch>
        <Route path="/tcc/normal">
          <Domain />
        </Route>
        <Route path="/tcc/degrade">
          <Degrade />
        </Route>
      </Switch>
    </Content>
  </Layout>
)

export default Page;
