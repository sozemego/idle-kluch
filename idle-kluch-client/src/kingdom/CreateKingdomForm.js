import React, { Component } from 'react';
import { connect } from 'react-redux';

import styles from './create-kingdom-form.css';
import { Divider, RaisedButton, TextField } from 'material-ui';

import * as kingdomActions from './actions';
import { getKingdomNameRegistrationError } from './selectors';

class CreateKingdomForm extends Component {

  constructor(props) {
	super(props);
	this.state = {
	  kingdomName: '',
	};
  }

  onSubmit = () => {
	const {
	  kingdomName,
	} = this.state;

	return this.props.registerKingdom(kingdomName);
  };

  onKingdomNameChange = (e, kingdomName) => {
	this.setState({ kingdomName });
  };

  render() {
	const {
	  kingdomNameRegistrationError,
	} = this.props;

	const {
	  kingdomName,
	} = this.state;

	const {
	  onKingdomNameChange,
	  onSubmit,
	} = this;

	return (
	  <div className={styles.container}>
		<h2>Create new kingdom</h2>
		<Divider/>
		<div className={styles.input_row}>
		  <TextField value={kingdomName}
					 onChange={onKingdomNameChange}
					 floatingLabelFixed
					 floatingLabelText={'Kingdom name'}
					 errorText={kingdomNameRegistrationError}
		  />
		</div>
		<RaisedButton label={'Create kingdom!'}
					  onClick={onSubmit}
					  className={styles.submit_button}
		/>
	  </div>
	);
  }

}

const mapStateToProps = (state) => {
  return {
	kingdomNameRegistrationError: getKingdomNameRegistrationError(state),
  };
};

export default connect(mapStateToProps, kingdomActions)(CreateKingdomForm);