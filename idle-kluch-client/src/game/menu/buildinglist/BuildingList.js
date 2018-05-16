import React, { Component } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import _ from "lodash";
import { getConstructableBuildingsList, getSelectedConstructableBuilding } from "../../../kingdom/selectors";
import * as gameActions from "../../actions";


import styles from "./building-list.css";
import idleBuckImg from "../idle_buck_1.png";

class BuildingList extends Component {

  getBuildingCost = (building) => {
    const {cost} = building;
    if(!cost) {
      return null;
    }

    return (
      <div className={styles.building_cost}>
        <img src={idleBuckImg} className={styles.building_cost_icon} alt={"Idle bucks coin"}/>
        {cost.idleBucks}
      </div>
    )
  }

  getBuildingRowContent = (building) => {
    const {
      getBuildingCost,
    } = this;

    return [
      <span>{building.name}</span>,
      getBuildingCost(building),
    ];
  };

  render() {
    const {
      getBuildingRowContent,
    } = this;

    const {
      constructableBuildings,
      selectConstructableBuilding,
      selectedConstructableBuilding,
    } = this.props;

    const selectedConstructableBuildingId = _.get(selectedConstructableBuilding, "id", null,);

    return (
      <div>
        <div>Buildings</div>
        {constructableBuildings.map(building => {
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
              {getBuildingRowContent(building)}
            </div>
          );
        })}
      </div>
    );
  }

}

BuildingList.propTypes = {
  constructableBuildings: PropTypes.array.isRequired,
  selectConstructableBuilding: PropTypes.func.isRequired,
};

const mapStateToProps = (state) => {
  return {
    constructableBuildings: getConstructableBuildingsList(state),
    selectedConstructableBuilding: getSelectedConstructableBuilding(state),
  };
};

export default connect(mapStateToProps, gameActions)(BuildingList);