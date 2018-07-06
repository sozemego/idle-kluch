import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { Entity } from "../../../ecs/Entity";
import { NameComponent } from "../../../ecs/components/NameComponent";
import style from "./selected-entity-info.css";
import { Divider, LinearProgress } from "@material-ui/core";
import { ResourceHarvesterComponent } from "../../../ecs/components/ResourceHarvesterComponent";
import { HARVESTING_STATE } from "../../../ecs/constants";
import { ResourceSourceComponent } from "../../../ecs/components/ResourceSourceComponent";

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
    if (!harvester) {
      return null;
    }

    const resource = getResourceByName(harvester.getResource().name);
    const harvestingState = harvester.getState();
    const value = harvestingState === HARVESTING_STATE.WAITING ? 0 : harvester.getProgress() * 100;

    const harvestingStats = this.getHarvestingStats();
    const { unitsPerMinute, bonus, baseUnitsPerMinute } = harvestingStats;

    return (
      <Fragment>
        <div className={style.harvester_header}>
          <img className={style.resource_icon} src={`/resources/${resource.name}.png`}/>{harvestingState}
        </div>
        <div>
          <LinearProgress variant={"determinate"} value={value}/>
        </div>
        <div>
          {`${unitsPerMinute} per minute (base ${baseUnitsPerMinute})`}
        </div>
        <div>
          {bonus === 1 ? "No bonus" : `Bonus multiplier ${bonus}`}
        </div>
        <Divider/>
      </Fragment>
    )
  };

  getHarvestingStats = () => {
    const { selectedEntity } = this.props;

    const resourceHarvesterComponent = selectedEntity.getComponent(ResourceHarvesterComponent);
    const unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
    const bonus = this.getBonus(resourceHarvesterComponent.getSources());

    return {
      baseUnitsPerMinute: unitsPerMinute,
      unitsPerMinute: unitsPerMinute * bonus,
      bonus,
    };
  };

  getBonus = (sources) => {
    const { getEntityById } = this.props;
    return sources
      .map(source => getEntityById(source.id))
      .map(entity => {
        const resourceSource = entity.getComponent(ResourceSourceComponent);
        return resourceSource.getBonus();
      })
      .reduce((prev, next) => prev * next, 1);
  }

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
  getEntityById: PropTypes.func.isRequired,
};
