import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import { Entity } from "../../../ecs/Entity";
import { NameComponent } from "../../../ecs/components/NameComponent";
import style from "./selected-entity-info.css";
import { Divider } from "material-ui";


export class SelectedEntityInfo extends Component {

  getNameComponent = () => {
    const {selectedEntity} = this.props;
    const nameComponent = selectedEntity.getComponent(NameComponent);
    return (
      <Fragment>
        <div className={style.name_container}>
          {nameComponent.getName()}
        </div>
        <Divider />
      </Fragment>
    )
  };

  render() {
    const {
      getNameComponent,
    } = this;

    return (
      <div>
        {getNameComponent()}
      </div>
    )
  }

}

SelectedEntityInfo.propTypes = {
  selectedEntity: PropTypes.instanceOf(Entity).isRequired,
};
