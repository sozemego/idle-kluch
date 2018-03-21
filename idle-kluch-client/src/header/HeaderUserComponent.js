import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import * as appActions from '../app/actions';
import { getUser, isLoggedIn } from "../app/selectors";
import { Menu, MenuItem, Popover } from "material-ui";

import avatar from './avatar_temp.png';
import styles from './header-user.css';

class HeaderUserComponent extends Component {

  constructor(props) {
	super(props);
	this.state = {
	  userMenuAnchor: null,
	  userMenuOpen: false,
	};
  }

  onContainerClicked = (event) => {
	event.preventDefault();
	this.setState({ userMenuOpen: true, userMenuAnchor: event.currentTarget });
  };

  render() {
	const {
	  userMenuOpen,
	  userMenuAnchor,
	} = this.state;

	const {
	  user,
	  isLoggedIn,
	  logout,
	} = this.props;

	if (!isLoggedIn) {
	  return null;
	}

	return [
	  <div className={styles.name__container}
		   key={0}
		   onClick={this.onContainerClicked}>
		<img src={avatar} className={styles.avatar}/>
		<div className={styles.name}>
		  {user.name}
		</div>
		<i className={'material-icons'}>arrow_drop_down</i>
	  </div>,
	  <Popover anchorOrigin={{ horizontal: 'middle', vertical: 'bottom' }}
			   open={userMenuOpen}
			   key={1}
			   onRequestClose={() => this.setState({ userMenuOpen: false })}
			   anchorEl={userMenuAnchor}
	  >
		<Menu>
		  <MenuItem value="Logout"
					primaryText={"Logout"}
					onClick={() => {
					  this.setState({ userMenuOpen: false })
					  logout();
					}}
		  />
		</Menu>
	  </Popover>
	];
  }

}

HeaderUserComponent.propTypes = {
  user: PropTypes.object.isRequired,
  isLoggedIn: PropTypes.bool.isRequired,
};

const mapStateToProps = (state) => {
  return {
	user: getUser(state),
	isLoggedIn: isLoggedIn(state),
  };
};

export default connect(mapStateToProps, appActions)(HeaderUserComponent);

