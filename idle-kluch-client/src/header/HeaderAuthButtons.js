import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import * as appActions from '../app/actions';
import { getUser, isLoggedIn } from "../app/selectors";
import { Menu, MenuItem, Popover } from "material-ui";

import styles from './header-auth-buttons.css';
import avatar from './avatar_temp.png';
import HeaderAuthForm from "./HeaderAuthForm";

class HeaderAuthButtons extends Component {

  constructor(props) {
	super(props);
	this.state = {
	  userMenuAnchor: null,
	  userMenuOpen: false,
	};
  }

  getNameComponent = () => {
	const {
	  userMenuOpen,
	  userMenuAnchor,
	} = this.state;

	const {
	  user,
	  isLoggedIn,
	} = this.props;

	if (!isLoggedIn) {
	  return null;
	}

	const {
	  getLogoutComponent,
	} = this;

	return [
	  <div className={styles.name__container}
		   key={0}
		   onClick={(event) => {
			 event.preventDefault();
			 this.setState({ userMenuOpen: true, userMenuAnchor: event.currentTarget })
		   }}>
		<img src={avatar} className={styles.avatar}/>
		<div className={styles.name}>
		  {user.name}
		</div>
		<i className={'material-icons'}>arrow_drop_down</i>
	  </div>,
	  <Popover anchorOrigin={{ horizontal: 'middle', vertical: 'bottom' }}
			   open={userMenuOpen}
			   key={1}
			   onRequestClose={() => this.setState({userMenuOpen: false})}
			   anchorEl={userMenuAnchor}
	  >
		<Menu>
		  {getLogoutComponent()}
		</Menu>
	  </Popover>
	];
  };

  getLogoutComponent = () => {
	const {
	  isLoggedIn,
	  logout,
	} = this.props;

	if (!isLoggedIn) {
	  return null;
	}

	return (
	  <MenuItem value="Logout"
				primaryText={"Logout"}
				onClick={() => {
					this.setState({userMenuOpen: false})
					logout();
				}}/>
	);
  };

  getAuthFormComponent = () => {
	const {
	  isLoggedIn,
	} = this.props;

	if (isLoggedIn) {
	  return null;
	}

	return (
	  <HeaderAuthForm/>
	);
  };

  render() {
	const {
	  getNameComponent,
	  getAuthFormComponent,
	} = this;

	return (
	  <div className={styles.container}>
		{getNameComponent()}
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