import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { Entity } from "../../../ecs/Entity";
import { NameComponent } from "../../../ecs/components/NameComponent";
import style from "./selected-entity-info.css";
import { Divider, LinearProgress } from "@material-ui/core";
import { ResourceHarvesterComponent } from "../../../ecs/components/ResourceHarvesterComponent";

export class SelectedEntityInfo extends Component {

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
    )
  };

  getHarvestingComponent = () => {
    const { selectedEntity } = this.props;
    const harvester = selectedEntity.getComponent(ResourceHarvesterComponent);
    if(!harvester) {
      return null;
    }

    const value = harvester.getProgress() * 100;
    const buffer = value + 10;

    return (
      <Fragment>
        <div>
          <LinearProgress variant={"buffer"} value={value} valueBuffer={buffer}/>
        </div>
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
};
