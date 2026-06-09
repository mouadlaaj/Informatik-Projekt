import React, { Suspense, useEffect } from 'react';
import { ToastContainer } from 'react-toastify';
import { Notifications } from 'react-push-notification';
import 'react-toastify/dist/ReactToastify.css';
import Root from './root';
import AOS from 'aos';
import 'aos/dist/aos.css';

function App() {
  useEffect(() => {
    AOS.init({ duration: 700 });
  }, []);
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <div className="App" style={{ backgroundColor: '#F9FAFB', overflow: 'visible' }}>
        <Notifications />
        <Root />
        <ToastContainer 
          position="bottom-right"
          autoClose={2000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light" 
        />
      </div>
    </Suspense>
  );
}

export default App;
