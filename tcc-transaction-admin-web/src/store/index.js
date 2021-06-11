import {combineReducers, createStore} from 'redux';
import {composeWithDevTools} from 'redux-devtools-extension';
import domain from './reducers/domain';

const store = createStore(
  combineReducers({domain}),
  composeWithDevTools(),
);

export default store;
