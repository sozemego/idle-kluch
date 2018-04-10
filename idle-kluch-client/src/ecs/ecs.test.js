import { ComponentContainer } from "./ComponentContainer";
import { Node } from './Node';
import { Engine } from "./Engine";
import { EntitySystem } from "./EntitySystem";

const { describe, it, beforeEach, expect } = global;


describe('component container', () => {

  let container = null;

  beforeEach(() => {
	container = new ComponentContainer();
  });

  it('should add a component', () => {
	expect(container.addComponent(1, "STRING")).toBe(true);
  });

  it('should not add same component twice', () => {
	expect(container.addComponent(1, "String")).toBe(true);
	expect(container.addComponent(1, "String")).toBe(false);
  });

  it('should return component', () => {
	expect(container.addComponent(1, "String")).toBe(true);
	expect(container.getComponent(1, String)).toBe("String");
  });

  it('should not return a non existent component', () => {
	expect(container.getComponent(1, String)).toBe(null);
  });

  it('should remove component', () => {
    expect(container.addComponent(1, "String")).toBe(true);
    container.removeComponent(1, String);
    expect(container.getComponent(1, String)).toBe(null);
  });

});

describe('engine', () => {

  let engine = null;

  beforeEach(() => {
    engine = new Engine();
  });

  it('should add system', () => {
    const updates = [];
    const system = new EntitySystem(engine);
    system.update = () => {
      updates.push('one');
	};

	engine.addSystem(system);
	engine.update(0);
	expect(updates.length).toBe(1);
  });

  it('should add two systems', () => {
    const system1 = new EntitySystem(engine);
    const system2 = new EntitySystem(engine);
    const updates = [];

    system1.update = () => updates.push('one');
    system2.update = () => updates.push('two');

    engine.addSystem(system1);
    engine.addSystem(system2);
    engine.update();
    expect(updates.length).toBe(2);
  });

  it('should return system', () => {
    const system = new EntitySystem(engine);
    engine.addSystem(system);
    expect(engine.getSystem(EntitySystem)).toBe(system);
  });

  it('should remove system', () => {
	const system = new EntitySystem(engine);
	engine.addSystem(system);
	expect(engine.getSystem(EntitySystem)).toBe(system);
	engine.removeSystem(system);
	expect(engine.getSystem(EntitySystem)).toBe(null);
  });

  it('should add entity', () => {
  	const factory = engine.getEntityFactory();
  	const entity = factory.createEntity();
  	engine.addEntity(entity);
  	expect(engine.getAllEntities().length).toBe(1);
  });

  it('should add five entities', () => {
    const factory = engine.getEntityFactory();
    factory.createEntityAndAddToEngine();
    factory.createEntityAndAddToEngine();
    factory.createEntityAndAddToEngine();
    factory.createEntityAndAddToEngine();
    factory.createEntityAndAddToEngine();
	expect(engine.getAllEntities().length).toBe(5);
  });

  it('should not add already added entity', () => {
	const factory = engine.getEntityFactory();
	const entity = factory.createEntity();
	engine.addEntity(entity);
	expect(() => engine.addEntity(entity)).toThrow();
  });

  it('should not update already updating system', () => {
      const system = new EntitySystem(engine);
      system.update = () => engine.update();
      engine.addSystem(system);
      expect(() => engine.update()).toThrow();
  });

  it('should not remove non-existent entity', () => {
    expect(() => engine.removeEntity(25)).toThrow();
  });

  it('should not add entity while updating', () => {
  	const system = new EntitySystem(engine);
  	system.update = () => {
  	  engine.addEntity(engine.getEntityFactory().createEntity());
  	  expect(engine.getAllEntities().length).toBe(0);
	};
  	engine.addSystem(system);
  	engine.update();
	expect(engine.getAllEntities().length).toBe(1);
  });

  it('should throw when adding same entity twice while updating', () => {
    expect.assertions = 1;
	const system = new EntitySystem(engine);
	system.update = () => {
	  const entity = engine.getEntityFactory().createEntity();
	  engine.addEntity(entity);
	  expect(() => engine.addEntity(entity)).toThrow();
	};
	engine.addSystem(system);
	engine.update();
  });

  it('should not update entities added while updating', () => {
    const entity = engine.getEntityFactory().createEntity();
    const system = new EntitySystem(engine);
    const counts = [];
    system.update = () => {
      counts.push(engine.getAllEntities().length);
	};
    engine.addSystem(system);
    expect(engine.getAllEntities().length).toBe(0);
    engine.update();
    expect(counts[0]).toBe(0);

    engine.addEntity(entity);
    engine.update();
	expect(counts[1]).toBe(1);

  });

});