import React, { Component } from "react";
import { connect } from "react-redux";
import * as gameActions from "./actions";

import styles from "./game-container.css";
import GameMenu from "./menu/GameMenu";
import Toolbar from "./menu/Toolbar";

class GameContainer extends Component {
  componentDidMount = () => {
    this.props.connect();
  };

  render() {
    return (
      <div className={styles.container}>
        <Toolbar />
        <div className={styles['menu-game-row']}>
          <div className={styles.menu}>
            <GameMenu/>
          </div>
          <div className={styles.game} id="game"/>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {};
};

export default connect(mapStateToProps, gameActions)(GameContainer);
