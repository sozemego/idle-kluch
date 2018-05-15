import React, { Component } from "react";
import PropTypes from "prop-types";
import _ from "lodash";
import { connect } from "react-redux";
import { getConstructableBuildingsList, getSelectedConstructableBuilding } from "../../kingdom/selectors";

import * as gameActions from "../actions";

import styles from "./game-menu.css";
import { Dialog } from "material-ui";
import { isAlreadyConnected } from "../../app/selectors";

class GameMenu extends Component {

  getBuildingsItems = () => {
    const {
      constructableBuildings,
      selectConstructableBuilding,
      selectedConstructableBuilding,
    } = this.props;

    const selectedConstructableBuildingId = _.get(
      selectedConstructableBuilding,
      "id",
      null,
    );

    return constructableBuildings.map(building => {
      return (
        <div key={building.id}
             id={building.id}
             className={`${styles.building} ${
               selectedConstructableBuildingId === building.id
                 ? styles[ "building-selected" ]
                 : ""
               }`}
             onClick={() => selectConstructableBuilding(building.id)}
        >
          {building.name}
        </div>
      );
    });
  };

  getBuildingsList = () => {
    const {
      getBuildingsItems,
    } = this;

    return [
      <div key={"Buildings"}>Buildings</div>,
      ...getBuildingsItems(),
    ];
  };

  getAlreadyConnectedDialog = () => {
    const {
      alreadyConnected,
    } = this.props;

    if(!alreadyConnected) {
      return null;
    }

    return (
      <Dialog open={true}>
        You are already connected to the game in another tab or browser. Please close this tab.
        This message will later be expanded to account for users with your username logged in the same browser (another tab)
        or another browser altogether.
      </Dialog>
    );
  };

  render() {
    const { getBuildingsList, getAlreadyConnectedDialog } = this;

    return [
      ...getBuildingsList(),
      getAlreadyConnectedDialog(),
    ];
  }
}

GameMenu.propTypes = {
  constructableBuildings: PropTypes.array.isRequired,
  selectConstructableBuilding: PropTypes.func.isRequired,
};

const mapStateToProps = state => {
  return {
    constructableBuildings: getConstructableBuildingsList(state),
    selectedConstructableBuilding: getSelectedConstructableBuilding(state),
    alreadyConnected: isAlreadyConnected(state),
  };
};

export default connect(mapStateToProps, gameActions)(GameMenu);
