import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import styles from './header.css';
import HeaderLinearProgress from "./HeaderLinearProgress";
import HeaderAuthButtons from './HeaderAuthButtons';
import * as appActions from '../app/actions';

class Header extends Component {

  constructor(props) {
	super(props);
  }

  render() {
	return (
	  <div>
		<div className={styles.container}>
		  <div className={styles.element}>
			<HeaderAuthButtons/>
		  </div>
		  <div className={[styles.element, styles.logo].join(' ')}>
			Idle Kluch
		  </div>
		  <div className={styles.element}>

		  </div>
		</div>
		<HeaderLinearProgress/>
	  </div>
	)
  }

}

const mapStateToProps = (state) => {
  return {};
};

export default connect(mapStateToProps, appActions)(Header);