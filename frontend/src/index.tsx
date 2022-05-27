import { EuiProvider } from '@elastic/eui';
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';
import createCache from '@emotion/cache';

const cache = createCache({
  key: 'biblivre',
  container: document.querySelector('meta[name="emotion-styles"]') as HTMLElement,
});

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <EuiProvider cache={cache} >
    <App />
  </EuiProvider>,
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
