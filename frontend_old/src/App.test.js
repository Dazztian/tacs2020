import React from 'react';
import ReactDOM from 'react-dom';
import App from './themes/Shards-Dashboard-Lite-React-1.0.0/Source Files/src/App';

it('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<App />, div);
  ReactDOM.unmountComponentAtNode(div);
});
