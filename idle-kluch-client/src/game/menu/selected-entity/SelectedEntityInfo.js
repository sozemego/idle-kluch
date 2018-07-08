import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { Entity } from "../../../ecs/Entity";
import { NameComponent } from "../../../ecs/components/NameComponent";
import style from "./selected-entity-info.css";
import { Divider, LinearProgress } from "@material-ui/core";
import { ResourceHarvesterComponent } from "../../../ecs/components/ResourceHarvesterComponent";
import { HARVESTING_STATE } from "../../../ecs/constants";
import { ResourceSourceComponent } from "../../../ecs/components/ResourceSourceComponent";
import { ResourceStorageComponent } from "../../../ecs/components/ResourceStorageComponent";
import { Chip } from "@material-ui/core/es/index";

export class SelectedEntityInfo extends Component {

  componentDidMount = () => {
    this.interval = setInterval(() => this.setState({}), 250);
  };

  componentWillUnmount = () => {
    clearInterval(this.interval);
  };

  getNameComponent = () => {
    const { selectedEntity } = this.props;
    const nameComponent = selectedEntity.getComponent(NameComponent);
    return (
      <div className={style.section}>
        <div className={style.name_container}>
          {nameComponent.getName()}
        </div>
        <Divider/>
      </div>
    );
  };

  getHarvestingComponent = () => {
    const { selectedEntity } = this.props;
    const harvester = selectedEntity.getComponent(ResourceHarvesterComponent);
    if (!harvester) {
      return null;
    }

    const resource = harvester.getResource();
    const harvestingState = harvester.getState();
    const value = harvestingState === HARVESTING_STATE.WAITING ? 0 : harvester.getProgress() * 100;

    const harvestingStats = this.getHarvestingStats();
    const { unitsPerMinute, bonus, baseUnitsPerMinute } = harvestingStats;

    return (
      <div className={style.section}>
        <span>Harvesting</span>
        <div className={style.harvester_header}>
          <img className={style.resource_icon} src={`/resources/${resource.name}.png`}/>{harvestingState}
        </div>
        <LinearProgress variant={"determinate"} value={value}/>
        <div>
          {`${unitsPerMinute} per minute (base ${baseUnitsPerMinute})`}
        </div>
        <div>
          {bonus === 1 ? "No bonus" : `Bonus multiplier ${bonus}`}
        </div>
        <Divider/>
      </div>
    )
  };

  getHarvestingStats = () => {
    const { selectedEntity } = this.props;

    const resourceHarvesterComponent = selectedEntity.getComponent(ResourceHarvesterComponent);
    const unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
    const bonus = this.getBonus(resourceHarvesterComponent.getSlots());

    return {
      baseUnitsPerMinute: unitsPerMinute,
      unitsPerMinute: unitsPerMinute * bonus,
      bonus,
    };
  };

  getBonus = (slots) => {
    const { getEntityById } = this.props;
    return slots
      .map(slots => getEntityById(slots.sourceId))
      .map(entity => {
        const resourceSource = entity.getComponent(ResourceSourceComponent);
        return resourceSource.getBonus();
      })
      .reduce((prev, next) => prev * next, 1);
  };

  getStorageComponent = () => {
    const { selectedEntity } = this.props;
    const storageComponent = selectedEntity.getComponent(ResourceStorageComponent);
    if (!storageComponent) {
      return null;
    }

    const resourceCounts = this.getResourceCounts();

    return (
      <div className={style.section}>
        <span>Storage</span>
        <div className={style.storage_container}>
          {resourceCounts.map(resource => {
            return (
              <Chip
                label={resource.count}
                avatar={
                  <img className={style.resource_icon} src={`/resources/${resource.name}.png`}/>
                }
              />
            )
          })}
        </div>
        <Divider />
      </div>
    );
  };

  getResourceCounts = () => {
    const { selectedEntity } = this.props;
    const storageComponent = selectedEntity.getComponent(ResourceStorageComponent);
    const resources = storageComponent.getResources();

    const resourceMap = resources.reduce((prev, curr) => {
      if(!prev[curr.name]) {
        prev[curr.name] = {
          count: 0,
          name: curr.name,
        }
      }
      prev[curr.name].count++;
      return prev;
    }, {});

    return Object.values(resourceMap);
  };

  render() {
    const {
      getNameComponent,
      getHarvestingComponent,
      getStorageComponent,
    } = this;

    return (
      <div>
        {getNameComponent()}
        {getHarvestingComponent()}
        {getStorageComponent()}
      </div>
    )
  }

}

SelectedEntityInfo.propTypes = {
  selectedEntity: PropTypes.instanceOf(Entity).isRequired,
  getResourceByName: PropTypes.func.isRequired,
  getEntityById: PropTypes.func.isRequired,
};
