import _ from 'lodash';
import Phaser from "phaser";
import Node from "./Node";
import { StaticOccupySpaceComponent } from "./components/StaticOccupySpaceComponent";
import { PhysicsComponent } from "./components/PhysicsComponent";

/**
 * Tries to find a component in an entity json.
 * This is not for use with Entity class, but a json representation send from backend.
 */
export const findComponent = (entity, componentType) => {
  const components = _.get(entity, 'components', []);

  return _.find(components, { componentType });
};

export const checkRectangleIntersectsCollidableEntities = (engine, rectangle) => {
  const staticOccupy = Node.of(StaticOccupySpaceComponent);
  const collidableEntities = engine.getEntitiesByNode(staticOccupy);

  for(let i = 0; i < collidableEntities.length; i++) {
    const target = collidableEntities[i];
    const targetPhysicsComponent = target.getComponent(PhysicsComponent);

    const rectangle2 = new Phaser.Rectangle(
      targetPhysicsComponent.getX(), targetPhysicsComponent.getY(),
      targetPhysicsComponent.getWidth(), targetPhysicsComponent.getHeight()
    );

    if(rectangle.intersects(rectangle2, 0)) {
      return true;
    }
  }

  return false;
};