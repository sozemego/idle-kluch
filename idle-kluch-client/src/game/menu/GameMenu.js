import React, { Component } from "react";
import PropTypes from "prop-types";
import _ from "lodash";
import { connect } from "react-redux";
import { getConstructableBuildings, getSelectedConstructableBuilding } from "../../kingdom/selectors";

import * as gameActions from "../actions";

import styles from "./game-menu.css";

class GameMenu extends Component {
  getBuildingsList = () => {
    const {
      constructableBuildings,
      selectConstructableBuilding,
      selectedConstructableBuilding
    } = this.props;

    const selectedConstructableBuildingId = _.get(
      selectedConstructableBuilding,
      "id",
      null
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

  render() {
    const { getBuildingsList } = this;

    return (
      <div>
        <div>Buildings</div>
        {getBuildingsList()}
      </div>
    );
  }
}

GameMenu.propTypes = {
  constructableBuildings: PropTypes.array.isRequired,
  selectConstructableBuilding: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {
    constructableBuildings: getConstructableBuildings(state),
    selectedConstructableBuilding: getSelectedConstructableBuilding(state)
  };
};

export default connect(mapStateToProps, gameActions)(GameMenu);
