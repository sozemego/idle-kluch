import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from "react-redux";
import * as gameActions from './actions';

import styles from './game-container.css';
import GameMenu from "./menu/GameMenu";

class GameContainer extends Component {

  constructor(props) {
	super(props);
  }

  componentDidMount = () => {
	this.props.connect();
  };

  render() {
	return (
	  <div className={styles.container}>
		<div className={styles.menu}>
		  <GameMenu/>
		</div>
		<div className={styles.game} id='game'></div>
	  </div>
	);
  }

}

const mapStateToProps = (state) => {
  return {};
};

export default connect(mapStateToProps, gameActions)(GameContainer);