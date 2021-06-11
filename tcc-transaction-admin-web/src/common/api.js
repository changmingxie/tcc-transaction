import axios from 'axios';


export function getDomains() {
  return axios
    .get('/tcc-transaction-dashboard/api/domains')
    .then(res => res.data.reduce((prev, val) => {
      return prev.concat({
        ...val,
        value: val.label
      })
    }, []) || []);
}

export function getManageList({
  domain,
  row,
  isDeleted,
  pageNum,
  pageSize,
}) {
  return axios
    .get(`/tcc-transaction-dashboard/api/manage?domain=${domain}&row=${row}&isDeleted=${isDeleted}&pageNum=${pageNum}&pageSize=${pageSize}`)
    .then(res => res.data || {
      items: []
    });
}

export function reset(params) {
  let url = '/tcc-transaction-dashboard/api/reset?';
  Object.keys(params).forEach(key => {
    url += `${key}=${encodeURIComponent(params[key])}&`
  });
  url = url.substring(0, url.length - 1);
  return axios.put(url)
}

export function confirm(params) {
  let url = '/tcc-transaction-dashboard/api/confirm?';
  Object.keys(params).forEach(key => {
    url += `${key}=${encodeURIComponent(params[key])}&`
  });
  url = url.substring(0, url.length - 1);
  return axios.put(url)
}

export function cancel(params) {
  let url = '/tcc-transaction-dashboard/api/cancel?';
  Object.keys(params).forEach(key => {
    url += `${key}=${encodeURIComponent(params[key])}&`
  });
  url = url.substring(0, url.length - 1);
  return axios.put(url)
}

export function remove(params) {
  let url = '/tcc-transaction-dashboard/api/delete?';
  Object.keys(params).forEach(key => {
    url += `${key}=${encodeURIComponent(params[key])}&`
  });
  url = url.substring(0, url.length - 1);
  return axios.delete(url)
}

export function restore(params) {
  let url = '/tcc-transaction-dashboard/api/restore?';
  Object.keys(params).forEach(key => {
    url += `${key}=${encodeURIComponent(params[key])}&`
  });
  url = url.substring(0, url.length - 1);
  return axios.put(url)
}

export function getDegradeList() {
  return axios
    .get('/tcc-transaction-dashboard/api/degrade')
    .then(res => res.data.data);
}

export function degrade(domain, isDegrade) {
  const url = `/tcc-transaction-dashboard/api/degrade?domain=${domain}&degrade=${isDegrade}`;
  return axios.put(url);
}

export function deleteDomain(domainName) {
  const url = `/tcc-transaction-dashboard/api/domain?domain=${domainName}`;
  return axios.delete(url);
}
