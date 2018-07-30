import React, { Component } from "react";
import { connect } from "react-redux";

import styles from './toolbar.css';
import idleBuck from './idle_buck_1.png';
import { getCashHistory, getKingdom } from "../../kingdom/selectors";

class Toolbar extends Component {


	render() {
		const { kingdom, cashHistory } = this.props;

		const sum = cashHistory
			.map(event => event.cash)
			.reduce((prev, next) => {
				return prev + next;
			}, 0);

		const average = sum ? (sum / 10) : 0;

		const {
			idleBucks,
		} = kingdom;

		return (
			<div className={styles.container}>
				<div className={styles.idle_bucks_container}>
					<img src={idleBuck} className={styles.idle_buck_img} alt={"Idle bucks coin"}/>
					<span className={styles.idle_buck_amount}>{idleBucks}</span>
					<span>{` (${average}/s)`}</span>
				</div>
			</div>
		);
	}
}

Toolbar.propTypes = {

};

const mapStateToProps = state => {
  return {
    kingdom: getKingdom(state),
    cashHistory: getCashHistory(state),
  };
};

export default connect(mapStateToProps, null)(Toolbar);
