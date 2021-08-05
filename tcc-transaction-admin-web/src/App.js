import {Provider} from 'react-redux';
import {Layout, Menu} from 'antd';
import {BrowserRouter as Router, Link, Route, Switch} from 'react-router-dom';
import 'antd/dist/antd.css';
import './App.css';
import Domain from './pages/tcc/domain/index';
import Degrade from './pages/tcc/Degrade';
import store from './store';

const {Header, Content} = Layout;

function App() {
  return (
    <Provider store={store}>
      <Router basename="/gatekeeper/tcc-transaction-web">
        <Layout className="layout">
          <Header
            style={{
              backgroundColor: '#fff',
              fontSize: 18,
              fontWeight: 'bold',
            }}
          >
            <div style={{float: 'left', marginRight: 80}}>TCC TRANSACTION管理后台</div>
            <Menu mode="horizontal">
              <Menu.Item>
                <Link to="/normal">常规</Link>
              </Menu.Item>
              <Menu.Item>
                <Link to="/degrade">降级配置</Link>
              </Menu.Item>
            </Menu>
          </Header>
          <Content>
            <div className="site-layout-content">
              <Switch>
                <Route path="/normal">
                  <Domain/>
                </Route>
                <Route path="/degrade">
                  <Degrade/>
                </Route>
              </Switch>
            </div>
          </Content>
        </Layout>
      </Router>
    </Provider>
  );

}

export default App;
