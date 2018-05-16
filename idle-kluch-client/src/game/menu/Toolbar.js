import React, { Component } from "react";
import { connect } from "react-redux";

import styles from './toolbar.css';
import idleBuck from './idle_buck_1.png';
import { getKingdom } from "../../kingdom/selectors";

class Toolbar extends Component {


  render() {
    const { kingdom } = this.props;

    const {
      idleBucks,
    } = kingdom;

    return (
      <div className={styles.container}>
        <div className={styles.idle_bucks_container}>
          <img src={idleBuck} className={styles.idle_buck_img} alt={"Idle bucks coin"}/>
          <span className={styles.idle_buck_amount}>{idleBucks}</span>
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
  };
};

export default connect(mapStateToProps, null)(Toolbar);
