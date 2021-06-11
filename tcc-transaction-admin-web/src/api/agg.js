import axios from "axios";

/**
 * 获取所有domains
 */
export function getDomains() {
  return axios({
    url: `/business-aggregate-admin/api/domains`,
    method: "get",
  });
}

/**
 * 获取列表  domain=test&pagenum=1
 */
export function getList(data) {
  return axios({
    url: `/business-aggregate-admin/api/manage`,
    method: "get",
    params: data,
  });
}

/**
 * 重置
 */
export function handleToReset(data) {
  return axios({
    url: `/business-aggregate-admin/api/reset`,
    method: "put",
    data: data,
  });
}

/**
 * 新增
 */
export function handleToAdd(data) {
  return axios({
    url: `/business-aggregate-admin/api/domain`,
    method: "post",
    data: data,
  });
}

export function getDegradeList() {
  return axios
    .get('/business-aggregate-admin/api/degrade')
    .then(res => res.data.data);
}

export function degrade(domain, isDegrade) {
  const url = `/business-aggregate-admin/api/degrade?domain=${domain}&degrade=${isDegrade}`;
  return axios.put(url);
}