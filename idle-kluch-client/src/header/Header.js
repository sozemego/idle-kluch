import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import styles from './header.css';
import HeaderLinearProgress from "./HeaderLinearProgress";
import HeaderAuthButtons from './HeaderUserSection/HeaderUserSection';
import * as appActions from '../app/actions';
import { Divider } from "material-ui";
import HeaderKingdomSection from "./HeaderKingdomSection/HeaderKingdomSection";

class Header extends Component {

  constructor(props) {
	super(props);
  }

  render() {
	return (
	  <div className={styles.header}>
		<div className={styles.container}>
		  <div className={styles.element}>
			{/*<HeaderAuthButtons/>*/}
			<HeaderKingdomSection/>
		  </div>
		  <div className={[styles.element, styles.logo].join(' ')}>
			Idle Kluch
		  </div>
		  <div className={styles.element}>
			<HeaderAuthButtons/>
		  </div>
		</div>
		<Divider/>
		<HeaderLinearProgress/>
	  </div>
	)
  }

}

const mapStateToProps = (state) => {
  return {};
};

export default connect(mapStateToProps, appActions)(Header);