import _ from 'lodash';
import Phaser from "phaser";
import Node from "./Node";
import { StaticOccupySpaceComponent } from "./components/StaticOccupySpaceComponent";
import { PhysicsComponent } from "./components/PhysicsComponent";
import { ResourceSourceComponent } from "./components/ResourceSourceComponent";
import { COMPONENT_TYPES } from "../game/constants";

/**
 * Tries to find a component in an entity json.
 * This is not for use with Entity class, but a json representation sent from backend.
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

export const checkEntityInRangeOfResource = (engine, entity) => {
  const harvesterComponent = findComponent(entity, COMPONENT_TYPES.RESOURCE_HARVESTER);
  const radius = harvesterComponent.radius;
  const resource = harvesterComponent.resourceId;

  const physicsComponent = findComponent(entity, COMPONENT_TYPES.PHYSICS);
  const point = { x: physicsComponent.x, y: physicsComponent.y };

  const resourceSource = Node.of(ResourceSourceComponent);
  const resourceSources = engine.getEntitiesByNode(resourceSource).filter(entity => {
    const resourceSource = entity.getComponent(ResourceSourceComponent);
    return resourceSource.getResourceId() === resource;
  });

  for(let i = 0; i < resourceSources.length; i++) {
    const source = resourceSources[i];
    const sourcePhysicsComponent = source.getComponent(PhysicsComponent);
    if(Math.hypot(point.x - sourcePhysicsComponent.getX(), point.y - sourcePhysicsComponent.getY()) <= radius) {
      return true;
    }
  }

  return false;
};

/**
 * Checks if this point is within physical bounds of a given entity.
 * @param {Entity} entity
 * @param {{x, y}} point
 */
export const doesContain = (entity, point) => {
  const physicsComponent = entity.getComponent(PhysicsComponent);

  const rectangle = new Phaser.Rectangle(
    physicsComponent.getX(), physicsComponent.getY(),
    physicsComponent.getWidth(), physicsComponent.getHeight()
  );

  return rectangle.contains(point.x, point.y);
};

export const distance = (entity1, entity2) => {
  const center1 = getCenter(entity1);
  const center2 = getCenter(entity2);
  return distanceBetweenPoints(center1, center2);
};

export const getCenter = (entity) => {
	const physicsComponent = entity.getComponent(PhysicsComponent);
	return {
		x: physicsComponent.getX() + (physicsComponent.getWidth() / 2),
		y: physicsComponent.getY() + (physicsComponent.getHeight() / 2),
	}
};

export const distanceBetweenPoints = (point1, point2) => {
	return Math.hypot(
		point1.x - point2.x,
		point1.y - point2.y,
	);
};