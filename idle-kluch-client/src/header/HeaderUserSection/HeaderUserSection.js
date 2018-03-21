import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import * as appActions from '../../app/actions';
import { getUser, isLoggedIn } from "../../app/selectors";

import styles from './header-user.css';
import HeaderAuthForm from "../HeaderAuthForm/HeaderAuthForm";
import HeaderUserComponent from "./HeaderUserComponent";

class HeaderUserSection extends Component {

  getComponent = () => {
	const {
	  isLoggedIn,
	} = this.props;

    switch (true) {
	  case !isLoggedIn: return <HeaderAuthForm />;
	  case isLoggedIn: return <HeaderUserComponent />;
	  default: return null;
	}
  };

  render() {
	const {
	  getComponent,
	} = this;

	return (
	  <div className={styles.container}>
		{getComponent()}
	  </div>
	);
  }

}

HeaderUserSection.propTypes = {
  user: PropTypes.object.isRequired,
  isLoggedIn: PropTypes.bool.isRequired,
};

const mapStateToProps = (state) => {
  return {
	user: getUser(state),
	isLoggedIn: isLoggedIn(state),
  };
};

export default connect(mapStateToProps, appActions)(HeaderUserSection);

