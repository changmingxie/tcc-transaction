/* eslint-disable */
const {createProxyMiddleware} = require('http-proxy-middleware');

module.exports = function (app) {
  let env = '';

  switch (process.env.REACT_APP_ENV) {
    case 'test':
    case 'uat':
      env = process.env.REACT_APP_ENV;
      break;
    default:
      break;
  }

  // app.use(
  //   createProxyMiddleware(
  //     [
  //       '/tcc-transaction-dashboard',
  //     ],
  //     {
  //       target: 'http://localhost:8888/',
  //       changeOrigin: true,
  //     }
  //   )
  // );
};
