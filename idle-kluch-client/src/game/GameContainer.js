import React, { Component } from "react";
import { connect } from "react-redux";
import * as gameActions from "./actions";

import styles from "./game-container.css";
import GameMenu from "./menu/GameMenu";
import Toolbar from "./menu/Toolbar";
import { SelectedEntityInfo } from "./menu/selected-entity/SelectedEntityInfo";
import { getEntityById, getResourceByName, getSelectedEntity } from "./selectors";

class GameContainer extends Component {
  componentDidMount = () => {
    this.props.connect();
  };

  render() {
    const {
      selectedEntity,
      getResourceByName,
      getEntityById,
    } = this.props;

    return (
      <div className={styles.container}>
        <Toolbar/>
        <div className={styles[ 'menu-game-row' ]}>
          <div className={styles.menu}>
            <GameMenu/>
          </div>
          <div className={styles.game} id="game"/>
          <div className={styles.menu}>
            {selectedEntity &&
              <SelectedEntityInfo selectedEntity={selectedEntity}
                                  getResourceByName={getResourceByName}
                                  getEntityById={getEntityById}
              />
            }
          </div>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    selectedEntity: getSelectedEntity(state),
    getResourceByName: (name) => getResourceByName(state, name),
    getEntityById: (id) => getEntityById(state, id),
  };
};

export default connect(mapStateToProps, gameActions)(GameContainer);
