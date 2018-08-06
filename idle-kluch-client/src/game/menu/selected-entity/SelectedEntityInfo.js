import React, { Component } from "react";
import PropTypes from "prop-types";
import { Entity } from "../../../ecs/Entity";
import { NameComponent } from "../../../ecs/components/NameComponent";
import style from "./selected-entity-info.css";
import { Divider, LinearProgress } from "@material-ui/core";
import { ResourceHarvesterComponent } from "../../../ecs/components/ResourceHarvesterComponent";
import { HARVESTING_STATE } from "../../../ecs/constants";
import { ResourceSourceComponent } from "../../../ecs/components/ResourceSourceComponent";
import { ResourceStorageComponent } from "../../../ecs/components/ResourceStorageComponent";
import { Avatar, Chip } from "@material-ui/core/es/index";
import { GraphicsComponent } from "../../../ecs/components/GraphicsComponent";
import { IMAGES, UPGRADE_TYPE } from "../../constants";
import { ResourceSellerComponent } from "../../../ecs/components/ResourceSellerComponent";
import { UpgradeButton } from "../../../components/UpgradeButton/UpgradeButton";

export class SelectedEntityInfo extends Component {

  componentDidMount = () => {
    this.interval = setInterval(() => this.setState({}), 250);
    document.addEventListener("keyup", this.onKeyUp);
    document.addEventListener("mouseup", this.onMouseUp);
  };

  componentWillUnmount = () => {
    clearInterval(this.interval);
    document.removeEventListener("keyup", this.onKeyUp);
    document.removeEventListener("mouseup", this.onMouseUp);
    this.props.onAttachSourceClicked(null);
  };

  onKeyUp = (event) => {
    if (event.keyCode === 27) {
      this.props.onAttachSourceClicked(null);
    }
  };

  onMouseUp = (event) => {
    if (event.button === 2) {
      this.props.onAttachSourceClicked(null);
    }
  };

  getNameComponent = () => {
    const { selectedEntity } = this.props;
    const nameComponent = selectedEntity.getComponent(NameComponent);
    return (
      <div className={style.section}>
        <div className={style.name_container}>
          {nameComponent.getName()}
        </div>
        <Divider className={style.divider}/>
      </div>
    );
  };

  getHarvestingComponent = () => {
    const { selectedEntity, getResourceById, onUpgradeComponentClicked, upgrades } = this.props
    const { getResourceSlotIcons } = this;
    const harvester = selectedEntity.getComponent(ResourceHarvesterComponent);
    if (!harvester) {
      return null;
    }

    const resource = getResourceById(harvester.getResource());
    const harvestingState = harvester.getState();
    const value = harvestingState === HARVESTING_STATE.WAITING ? 0 : harvester.getProgress() * 100;

    const harvestingStats = this.getHarvestingStats();
    const { unitsPerMinute, bonus, baseUnitsPerMinute } = harvestingStats;
    const speedLevel = harvester.speedLevel || 1;
    const upgrade = upgrades[UPGRADE_TYPE.HARVESTER_SPEED][speedLevel - 1];

    return (
      <div className={style.section}>
        <div className={style.harvester_header}>
          <span>Harvester</span>
          {getResourceSlotIcons(harvester.getSlots())}
        </div>
        <div className={style.harvester_state}>
          <Avatar>
            <img className={style.resource_icon}
                 src={`/resources/${resource.name}.png`}
                 alt={resource.name}
            />
          </Avatar>
          {harvestingState}
        </div>
        <LinearProgress variant={"determinate"} value={value}/>
        <div>
          {`${unitsPerMinute} per minute (base ${baseUnitsPerMinute})`}
        </div>
        <div>
          {bonus === 1 ? "No bonus" : `Bonus multiplier ${bonus}`}
        </div>
        <div>
          <div>{`Speed level ${speedLevel}`}</div>
          {upgrade && <UpgradeButton onClick={() => onUpgradeComponentClicked(selectedEntity.getId(), UPGRADE_TYPE.HARVESTER_SPEED, speedLevel)}
                                     cost={upgrade.cost}
                                     text={`Upgrade speed +${this.getUpgradeSpeedPercentage(upgrade)}%`}
          />}
        </div>
        <Divider className={style.divider}/>
      </div>
    );
  };

  getHarvestingStats = () => {
    const { selectedEntity } = this.props;

    const resourceHarvesterComponent = selectedEntity.getComponent(ResourceHarvesterComponent);
    const unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
    const bonus = this.getBonus(resourceHarvesterComponent.getSlots());

    return {
      baseUnitsPerMinute: unitsPerMinute,
      unitsPerMinute: (unitsPerMinute * bonus).toFixed(2),
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

  getResourceSlotIcons = (slots) => {
    const { getEntityById, onAttachSourceClicked } = this.props;
    const { getImageSrcByAsset } = this;
    return slots.map((slot, index) => {
      const entity = getEntityById(slot.sourceId);
      if (!entity) {
        return (
          <div>EMPTY BITCH</div>
        )
      };

      const graphicsComponent = entity.getComponent(GraphicsComponent);
      return (
        <img key={index}
             className={style.harvester_source_icon}
             src={getImageSrcByAsset(graphicsComponent.getAsset())}
             onClick={() => onAttachSourceClicked(++index)}
             alt={`source ${index + 1}`}
        />
      )
    });
  };

  getImageSrcByAsset = (asset) => IMAGES[asset];

  getUpgradeSpeedPercentage = (upgrade) => {
    const { data } = upgrade;
    const percentageIncrease = data - 1;
    return (percentageIncrease * 100).toFixed(0);
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
                key={resource.name}
                avatar={
                  <Avatar>
                    <img className={style.resource_icon}
                         src={`/resources/${resource.name}.png`}
                         alt={resource.name}
                    />
                  </Avatar>
                }
              />
            )
          })}
        </div>
        <Divider className={style.divider}/>
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

	getSellerComponent = () => {
		const { selectedEntity, getResourceById } = this.props;
		const seller = selectedEntity.getComponent(ResourceSellerComponent);
		if (!seller) {
			return null;
		}

		const resource = seller.getResourceBeingSold();
		const value = resource ? seller.getSellingProgress() * 100 : 0;
		const secondsPerUnit = seller.getSecondsPerUnit();

		return (
			<div className={style.section}>
				<div className={style.harvester_header}>
					<span>Seller</span>
				</div>
        <div className={style.harvester_state}>
          {resource &&
            <Avatar>
              <img className={style.resource_icon}
                   src={`/resources/${resource.name}.png`}
                   alt={resource.name}
              />
            </Avatar>
          }
          {resource ? "Selling (+" + resource.price + ")": "Waiting"}
				</div>
				<LinearProgress variant={"determinate"} value={value}/>
				<div>
					{`Selling ${60 / secondsPerUnit} per minute`}
				</div>
				<Divider className={style.divider}/>
			</div>
		);
  };

  render() {
    const {
      getNameComponent,
      getHarvestingComponent,
      getStorageComponent,
      getSellerComponent,
    } = this;

    return (
      <div>
        {getNameComponent()}
        {getHarvestingComponent()}
        {getStorageComponent()}
        {getSellerComponent()}
      </div>
    )
  }

}

SelectedEntityInfo.propTypes = {
  selectedEntity: PropTypes.instanceOf(Entity).isRequired,
  getResourceById: PropTypes.func.isRequired,
  getEntityById: PropTypes.func.isRequired,
  onAttachSourceClicked: PropTypes.func.isRequired,
  onUpgradeComponentClicked: PropTypes.func.isRequired,
	upgrades: PropTypes.object.isRequired,
};
