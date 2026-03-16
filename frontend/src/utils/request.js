import axios from 'axios';

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 基础URL
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 输出请求日志
    console.log('Request:', {
      url: config.url,
      method: config.method,
      params: config.params,
      data: config.data
    });
    return config;
  },
  error => {
    // 输出错误日志
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 输出响应日志
    console.log('Response:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    });
    return response;
  },
  error => {
    // 输出错误日志
    console.error('Response Error:', {
      url: error.config ? error.config.url : 'Unknown URL',
      status: error.response ? error.response.status : 'No response',
      data: error.response ? error.response.data : error.message
    });
    return Promise.reject(error);
  }
);

export default request;
