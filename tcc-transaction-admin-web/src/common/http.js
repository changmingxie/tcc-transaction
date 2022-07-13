import axios from 'axios'
import {message} from "antd";

// create an axios instance
const http = axios.create({
  baseURL: process.env.REACT_APP_ENV === 'test' ? "http://localhost:22332" : "",
  timeout: 20000 // request timeout
})

function goLogin() {
  const url = window.location.href;
  localStorage.removeItem('token');
  const base_url = url.split('#')[0];
  window.location.href = `${base_url}#/login`;
}

// request interceptor
http.interceptors.request.use(
  config => {
    // console.log("http.interceptors.request", config)

    const {url} = config;
    if (!url.includes('/api/user/login')) {
      let tccToken = localStorage.getItem('tcc-token');
      if (tccToken) {
        config.headers['Authorization'] = tccToken
      }
    }
    return config
  },
  error => {
    throw new Error(error)
    console.error('"http.interceptors.request"', error)
    return Promise.reject(error)
  }
)

const RESPONSE_DEFAULT_ERROR_MESSAGE = '服务异常，请稍后再试!';


// response interceptor
http.interceptors.response.use(
  response => {
    console.log("http.interceptors.response", response); // 验证完  删除 TODO
    let errorMessage = null;
    let businessResponse = response.data;
    if (!response.status || response.status !== 200) {
      errorMessage = RESPONSE_DEFAULT_ERROR_MESSAGE;
    } else if (response.status === 401) {
      errorMessage = '请先登录';
    } else if (businessResponse.code && businessResponse.code !== 200 && businessResponse.code !== '200') {
      errorMessage = businessResponse.code + '-' + businessResponse.message || 'Error'
    }
    if (errorMessage != null) {
      message.error(errorMessage);
      return Promise.reject(errorMessage)
    } else {
      return businessResponse.data
    }
  },
  error => {
    console.log('http.interceptors.response err', error) // for debug
    if (error.response) {
      const {data = {}, status} = error.response;
      let errorMessage = `HTTP ERROR: ${status}`;
      if (typeof data === 'string') {
        errorMessage = data;
      } else if (typeof data === 'object') {
        errorMessage = data.message;
      }

      if (status === 401) {
        goLogin();
        message.error("请先登录");
      } else {
        message.error(errorMessage);
      }
      return Promise.reject(error.response);
    }
    message.error(RESPONSE_DEFAULT_ERROR_MESSAGE)
    return Promise.reject(error)
  }
)

export default http
