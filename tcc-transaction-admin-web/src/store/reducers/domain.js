import {Domain} from '../actions/domain';
// import {CommonAction} from 'app-common';

// export interface DomainState {
//   domainData: any;
//   currentDomain: string;
// }

const defaultState = {
  domainData: [],
  currentDomain: '',
  xidString: null,
  refresh: 0,
};

export default function domain(state = defaultState, {type, payload}) {
  switch (type) {
    case Domain.UPDATE_DOMAIN_DATA:
      return {...state, domainData: payload};
    case Domain.UPDATE_CURRENT_DOMAIN:
      // console.log("UPDATE_CURRENT_DOMAIN", state);
      return {...state, currentDomain: payload.domain, xidString: payload.xidString, refresh: state.refresh + 1};
    default:
      return state;
  }
}
