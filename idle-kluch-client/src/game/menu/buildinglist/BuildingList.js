import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import classNames from "classnames";
import _ from "lodash";
import {
  getConstructableBuildingsList,
  getKingdom,
  getSelectedConstructableBuilding,
} from "../../../kingdom/selectors";
import * as gameActions from "../../actions";


import styles from "./building-list.css";
import idleBuckImg from "../idle_buck_1.png";

class BuildingList extends Component {

  componentWillMount = () => {
    document.addEventListener("keyup", this.onKeyUp);
  };

  componentWillUnmount = () => {
    document.removeEventListener("keyup", this.onKeyUp);
  };

  onKeyUp = (event) => {
    if(event.keyCode === 27) {
      this.props.selectConstructableBuilding(null);
    }
  }

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

    return (
      <Fragment>
        <span>{building.name}</span>
        {getBuildingCost(building)}
      </Fragment>
    );
  };

  render() {
    const {
      getBuildingRowContent,
    } = this;

    const {
      constructableBuildings,
      selectConstructableBuilding,
      selectedConstructableBuilding,
      kingdom,
    } = this.props;

    const selectedConstructableBuildingId = _.get(selectedConstructableBuilding, "id", null,);

    return (
      <div>
        <div>Buildings</div>
        {constructableBuildings.map(building => {
          const canAfford = kingdom.idleBucks >= building.cost.idleBucks;
          return (
            <div key={building.id}
                 id={building.id}
                 className={classNames(
                   styles.building,
                   selectedConstructableBuildingId === building.id ? styles.building_selected : null,
                   canAfford ? null : styles.building_cant_afford,
                 )}
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
    kingdom: getKingdom(state),
  };
};

export default connect(mapStateToProps, gameActions)(BuildingList);