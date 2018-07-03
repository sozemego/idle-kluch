import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { Entity } from "../../../ecs/Entity";
import { NameComponent } from "../../../ecs/components/NameComponent";
import style from "./selected-entity-info.css";
import { Divider, LinearProgress } from "@material-ui/core";
import { ResourceHarvesterComponent } from "../../../ecs/components/ResourceHarvesterComponent";
import { HARVESTING_STATE } from "../../../ecs/constants";

export class SelectedEntityInfo extends Component {

  componentDidMount = () => {
    this.interval = setInterval(() => this.setState({}), 16);
  };

  componentWillUnmount = () => {
    clearInterval(this.interval);
  };

  getNameComponent = () => {
    const { selectedEntity } = this.props;
    const nameComponent = selectedEntity.getComponent(NameComponent);
    return (
      <Fragment>
        <div className={style.name_container}>
          {nameComponent.getName()}
        </div>
        <Divider/>
      </Fragment>
    );
  };

  getHarvestingComponent = () => {
    const { selectedEntity, getResourceByName } = this.props;
    const harvester = selectedEntity.getComponent(ResourceHarvesterComponent);
    if(!harvester) {
      return null;
    }

    const resource = getResourceByName(harvester.getResource().name);
    const harvestingState = harvester.getState();
    const value = harvestingState === HARVESTING_STATE.WAITING ? 0 : harvester.getProgress() * 100;

    return (
      <Fragment>
        <div className={style.harvester_header}>
          <img className={style.resource_icon} src={`/resources/${resource.name}.png`}/>{harvestingState}
        </div>
        <div>
          <LinearProgress variant={"determinate"} value={value}/>
        </div>
        <Divider />
      </Fragment>
    )
  };

  render() {
    const {
      getNameComponent,
      getHarvestingComponent,
    } = this;

    return (
      <div>
        {getNameComponent()}
        {getHarvestingComponent()}
      </div>
    )
  }

}

SelectedEntityInfo.propTypes = {
  selectedEntity: PropTypes.instanceOf(Entity).isRequired,
  getResourceByName: PropTypes.func.isRequired,
};
