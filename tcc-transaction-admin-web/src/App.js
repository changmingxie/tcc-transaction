import {Provider} from 'react-redux';
import {HashRouter as Router, Redirect, Route, Switch} from 'react-router-dom';
import 'antd/dist/antd.css';
import './App.css';

import Login from './pages/tcc/login/index';
import Welcome from './pages/tcc/welcome/index';
import Domain from './pages/tcc/domain/index';
import Transaction from './pages/tcc/transaction/index';
import Task from "./pages/tcc/task/index";

import store from './store';
import TccLayout from "./layout/TccLayout";

function App(props) {
  return (
    <Provider store={store}>
      <Router forceRefresh={false}>
        <Switch>
          <Route key="login" path="/login" component={Login}></Route>
          <TccLayout routeList={
            <>
              <Route path="/welcome" component={Welcome}></Route>
              <Route path="/domain" component={Domain}></Route>
              <Route path="/transaction" component={Transaction}></Route>
              <Route path="/task" component={Task}></Route>
            </>
          }>
          </TccLayout>
          <Redirect to="/welcome" from="/"/>
        </Switch>
      </Router>
    </Provider>
  );

}

export default App;
