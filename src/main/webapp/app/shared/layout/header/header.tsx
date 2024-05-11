import './header.scss';
import React from 'react';
import { Link } from 'react-router-dom';
// tslint:disable-next-line
const close = require('../../../../content/images/close.png');
// tslint:disable-next-line
const lock = require('../../../../content/images/lock.png');
// tslint:disable-next-line
const usFlag = require('../../../../content/images/us_flag_small.png');
// tslint:disable-next-line
const iconDot = require('../../../../content/images/icon-dot-gov.svg');
// tslint:disable-next-line
const iconHttps = require('../../../../content/images/icon-https.svg');
// tslint:disable-next-line
const irsLogo = require('../../../../content/images/irs-logo.png');

const Header = () => {
  return (
    <div style={{ background: '#ffffff' }}>
      <section className="usa-banner" aria-label="Official government website">
        <div className="usa-accordion">
          <header className="usa-banner__header">
            <div className="usa-banner__inner">
              <div className="grid-col-auto">
                <img className="usa-banner__header-flag" src={usFlag} alt="U.S. flag" />
              </div>
              <div className="grid-col-fill tablet:grid-col-auto">
                <p className="usa-banner__header-text">An official website of the United States Government</p>
                <p className="usa-banner__header-action" aria-hidden="true">Here’s how you know</p>
              </div>
              <button className="usa-accordion__button usa-banner__button" aria-expanded="false" aria-controls="gov-banner">
                <span className="usa-banner__button-text">Here’s how you know</span>
              </button>
            </div>
          </header>
          <div className="usa-banner__content usa-accordion__content" id="gov-banner" hidden>
            <div className="grid-row grid-gap-lg">
              <div className="usa-banner__guidance tablet:grid-col-6">
                <img className="usa-banner__icon usa-media-block__img" src={iconDot} alt="Dot gov" />
                <div className="usa-media-block__body">
                  <p>
                    <strong>
                      Official websites use .gov
                            </strong>
                    <br />
                        A <strong>.gov</strong> website belongs to an official government organization in the United States.

                        </p>
                </div>
              </div>
              <div className="usa-banner__guidance tablet:grid-col-6">
                <img className="usa-banner__icon usa-media-block__img" src={iconHttps} alt="Https" />
                <div className="usa-media-block__body">
                  <p>
                    <strong>
                      Secure .gov websites use HTTPS
                            </strong>
                    <br />
                        A <strong>lock</strong> (
                            <span />
                    <img src={lock} alt="A locked padlock" />
                            ) or <strong>https://</strong> means you’ve safely connected to the .gov website. Share sensitive information only on official, secure websites.

                        </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <header className="usa-nav-container" style={{ background: '#ffffff' }}>
        <div className="usa-nav-container" >
          <div className="usa-navbar desktop:maxw-card height-auto flex-align-right">
            <div className="logo">
              <Link to="/">
                <img src={irsLogo} alt="IRS logo" />
              </Link>
              <span className="usa-sr-only">Home</span>
            </div>

          </div>
          {/*<nav aria-label="Primary navigation" className="usa-nav" role="navigation">*/}
          {/*  <button className="usa-nav__close">*/}
          {/*    <img src={close} alt="close" />*/}
          {/*  </button>*/}
          {/*  <ul className="usa-nav__primary usa-accordion">*/}
          {/*    <li className="usa-nav__primary-item">*/}
          {/*      <NavLink exact activeClassName="usa-current" to="/">*/}
          {/*        <span>Home</span>*/}
          {/*      </NavLink>*/}
          {/*    </li>*/}
          {/*    <li className="usa-nav__primary-item">*/}
          {/*      <button className="usa-accordion__button usa-nav__link" aria-expanded="false" aria-controls="nav-2">*/}
          {/*        <span>Forms</span>*/}
          {/*      </button>*/}
          {/*      <ul id="nav-2" className="usa-nav__submenu">*/}
          {/*        <li className="usa-nav__submenu-item">*/}
          {/*          <Link to="/fheo">*/}
          {/*            <span>FHEO</span>*/}
          {/*          </Link>*/}
          {/*        </li>*/}
          {/*        <li className="usa-nav__submenu-item">*/}
          {/*          <Link to="/">*/}
          {/*            <span>MTW</span>*/}
          {/*          </Link>*/}
          {/*        </li>*/}
          {/*      </ul>*/}
          {/*    </li>*/}
          {/*    <li className="usa-nav__primary-item">*/}
          {/*      <a href="/" ><span>Dashboard</span></a>*/}
          {/*    </li>*/}
          {/*    <li className="usa-nav__primary-item">*/}
          {/*      <NavLink className="usa-nav__link" exact activeClassName="usa-current" to="/admin">*/}
          {/*        <span>Admin</span>*/}
          {/*      </NavLink>*/}
          {/*    </li>*/}
          {/*  </ul>*/}
          {/*</nav>*/}
        </div>
      </header>
    </div>
  );
};

export default Header;
