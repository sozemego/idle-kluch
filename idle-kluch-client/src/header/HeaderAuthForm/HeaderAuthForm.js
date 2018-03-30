import React, { Component } from 'react';
import { connect } from 'react-redux';

import * as appActions from '../../app/actions';
import { getPasswordError, getUsernameError } from '../../app/selectors';

import styles from './header-auth-form.css';

class HeaderAuthForm extends Component {

  constructor(props) {
	super(props);
	this.state = {
	  username: '',
	  password: '',
	};
  }

  register = () => {
	const {
	  register,
	} = this.props;

	return register(this.state.username, this.state.password);
  };

  login = () => {
	const {
	  login,
	} = this.props;

	return login(this.state.username, this.state.password);
  };

  render() {
	const {
	  usernameError,
	  passwordError,
	} = this.props;

	const {
	  register,
	  login,
	} = this;

	return (
	  <div className={styles.container}>
		<div className={styles.input__row}>
		  <label className={styles.input__label}>Username</label>
		  <input type={'text'}
				 placeholder={'Username'}
				 onChange={e => this.setState({ username: e.target.value })}
				 autoComplete={'username'}
				 tabIndex={1}
		  />
		  <button className={styles.button} onClick={register}>Register</button>
		  <div className={styles.error}>{usernameError}</div>
		</div>
		<div className={styles.input__row}>
		  <label className={styles.input__label}>Password</label>
		  <input type={'password'}
				 placeholder={'Password'}
				 autoComplete={'current-password'}
				 onChange={e => this.setState({ password: e.target.value })}
				 tabIndex={2}
		  />
		  <button className={styles.button} onClick={login}>Login</button>
		  <div className={styles.error}>{passwordError}</div>
		</div>
	  </div>
	);
  }

}

const mapStateToProps = (state) => {
  return {
	usernameError: getUsernameError(state),
	passwordError: getPasswordError(state),
  };
};


export default connect(mapStateToProps, appActions)(HeaderAuthForm);