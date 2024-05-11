import axios from 'axios';
import * as React from 'react';
import { toast } from 'react-toastify';

// let showModal = false;
const TIMEOUT = 1 * 90 * 1000;
const title = <div style={{ color: 'red' }}>Unexpected internal system error has occurred</div>;
const message = <div>Unable to process your request at the moment.</div>;

const errorMessage = <div>Internal server error occured !.</div>;
axios.defaults.timeout = TIMEOUT;
axios.defaults.headers = { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' };
// if (process.env.NODE_ENV === 'development') {
//   axios.defaults.baseURL = 'http://localhost:8181/faas/';
// }
// axios.defaults.baseURL = 'http://localhost:8080/faas/';
axios.interceptors.response.use(null, error => {
  console.error('Interceptors Error ', error);
  const expectedError = error.response && error.response.status && error.response.status === 500;
  if (expectedError) {
    console.error('Internal server error occurred', error, error.response.status);
    toast.error(errorMessage, {
      position: 'top-center',
      autoClose: 12000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true
    });
  }
  return Promise.reject(error);
});
export default {
  get: axios.get,
  post: axios.post,
  put: axios.put,
  delete: axios.delete
};
