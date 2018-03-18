import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import * as appActions from '../app/actions';
import { getUser, isLoggedIn } from "../app/selectors";
import { FlatButton } from "material-ui";

import styles from './header-auth-buttons.css';
import HeaderAuthForm from "./HeaderAuthForm";

class HeaderAuthButtons extends Component {

  constructor(props) {
    super(props);
  }

  getNameComponent = () => {
	const {
	  user,
	  isLoggedIn,
	} = this.props;

	if(!isLoggedIn) {
	  return null;
	}

	return (
	  <div>
		{user.name}
	  </div>
	);
  };

  getLogoutComponent = () => {
	const {
	  user,
	  isLoggedIn,
	} = this.props;

	if(!isLoggedIn) {
	  return null;
	}

	return (
	  <FlatButton label={"Logout"}/>
	);
  };

  getAuthFormComponent = () => {
	const {
	  user,
	  isLoggedIn,
	} = this.props;

	if(isLoggedIn) {
	  return null;
	}

	return (
	  <HeaderAuthForm />
	);
  };

  render() {
    const {
      user,
	  isLoggedIn,
	} = this.props;

    const {
	  getNameComponent,
	  getLogoutComponent,
	  getAuthFormComponent,
	} = this;

    return(
      <div className={styles.container}>
		{getNameComponent()}
		{getLogoutComponent()}
		{getAuthFormComponent()}
	  </div>
	);
  }

}

const mapStateToProps = (state) => {
  return {
    user: getUser(state),
	isLoggedIn: isLoggedIn(state),
  };
};

export default connect(mapStateToProps, appActions)(HeaderAuthButtons);

HeaderAuthButtons.propTypes = {
  user: PropTypes.object.isRequired,
  isLoggedIn: PropTypes.bool.isRequired,
};