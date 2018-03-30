import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { hasKingdom, shouldShowCreateKingdomForm } from '../kingdom/selectors';
import CreateKingdomForm from '../kingdom/CreateKingdomForm';
import { isLoggedIn } from './selectors';
import * as kingdomActions from '../kingdom/actions';
// import styles from './content-container.css';
import GameContainer from '../game/GameContainer';

class ContentContainer extends Component {

	getComponent = () => {
		const {
			hasKingdom,
			isLoggedIn,
			showCreateKingdomForm,
		} = this.props;

		switch (true) {
			case showCreateKingdomForm && isLoggedIn:
				return <CreateKingdomForm/>;
			case hasKingdom && isLoggedIn:
				return <GameContainer/>;
			default:
				return null;
		}
	};

	render() {
		const {
			getComponent,
		} = this;

		return getComponent();
	}

}

ContentContainer.propTypes = {
	loadKingdom: PropTypes.func.isRequired,
};

const mapStateToProps = (state) => {
	return {
		isLoggedIn: isLoggedIn(state),
		hasKingdom: hasKingdom(state),
		showCreateKingdomForm: shouldShowCreateKingdomForm(state),
	};
};


export default connect(mapStateToProps, kingdomActions)(ContentContainer);