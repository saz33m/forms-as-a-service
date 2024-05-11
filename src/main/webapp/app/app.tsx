import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import React, { Fragment, useEffect } from 'react';
import { connect } from 'react-redux';
import { HashRouter as Router } from 'react-router-dom';
import { ToastContainer, toast, ToastPosition } from 'react-toastify';
import { IRootState } from '../app/shared/reducers';
import Header from '../app/shared/layout/header/header';
import Footer from '../app/shared/layout/footer/footer';
import { hasAnyAuthority } from '../app/shared/auth/private-route';
import ErrorBoundary from '../app/shared/error/error-boundary';
import { AUTHORITIES } from '../app/config/constants';
import AppRoutes from '../app/routes';

const baseHref = document
  .querySelector('base')
  .getAttribute('href')
  .replace(/\/$/, '');

export const App = () => {
  return (
    <Router>
      <Fragment>
        <ToastContainer
          position={toast.POSITION.TOP_LEFT as ToastPosition}
          className="toastify-container"
          toastClassName="toastify-toast"
        />
        <Header />
        <main className="main-content  background" id="main-content" aria-label="Content">
          <div>
            <div className="grid-container">
              <div className="grid-row">
                <div className="grid-col-12 card">
                  <ErrorBoundary>
                    <AppRoutes />
                  </ErrorBoundary>
                </div>
              </div>
            </div>
          </div>
        </main>
        <Footer />
      </Fragment>
    </Router>
  );
};
export default App;
