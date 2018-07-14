import React, { Component } from "react";
import { connect } from "react-redux";
import * as gameActions from "./actions";

import styles from "./game-container.css";
import GameMenu from "./menu/GameMenu";
import Toolbar from "./menu/Toolbar";
import { SelectedEntityInfo } from "./menu/selected-entity/SelectedEntityInfo";
import { getAttachSourceSlot, getEntityById, getResourceById, getSelectedEntityId, isConstructing } from "./selectors";

class GameContainer extends Component {
  componentDidMount = () => {
    this.props.connect();
  };

  getSelectedEntity = () => {
  	const { selectedEntityId, getEntityById } = this.props;
  	return getEntityById(selectedEntityId);
	};

	isDuringAction = () => {
		const { sourceSlot, constructing } = this.props;
		return sourceSlot || constructing;
	};

  render() {
    const {
      getResourceById,
      getEntityById,
      onAttachSourceClicked,
    } = this.props;

    const selectedEntity = this.getSelectedEntity();
    const duringAction = this.isDuringAction();

    return (
      <div className={`${styles.container} ${duringAction ? styles.during_action : ''}`}>
        <Toolbar/>
        <div className={styles[ 'menu-game-row' ]}>
          <div className={styles.menu}>
            <GameMenu/>
          </div>
          <div className={styles.game} id="game"/>
          <div className={styles.menu}>
            {selectedEntity &&
            <SelectedEntityInfo selectedEntity={selectedEntity}
                                getResourceById={getResourceById}
                                getEntityById={getEntityById}
                                onAttachSourceClicked={onAttachSourceClicked}
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
    selectedEntityId: getSelectedEntityId(state),
    getResourceById: (id) => getResourceById(state, id),
    getEntityById: (id) => getEntityById(state, id),
		sourceSlot: getAttachSourceSlot(state),
		constructing: isConstructing(state),
  };
};

export default connect(mapStateToProps, gameActions)(GameContainer);
