export class ComponentContainer {
  constructor() {
    this.components = {};
    this.nodeCache = {};
  }

  addComponent = (entityId, component) => {
    const components = this.getEntityComponents(entityId);
    const componentAdded = !components[ component.constructor ];
    components[ component.constructor ] = component;
    return componentAdded;
  };

  getComponent = (entityId, clazz) => {
    const components = this.getEntityComponents(entityId);
    return components[ clazz ] || null;
  };

  removeComponent = (entityId, clazz) => {
    const components = this.getEntityComponents(entityId);
    delete components[ clazz ];
  };

  removeEntityComponents = entityId => {
    delete this.components[ entityId ];
  };

  getEntitiesByNode = node => {
    return Object.entries(this.components)
      .filter(([ entityId, components ]) => {
        let hasAllComponents = true;
        node.getComponents().forEach(comp => {
          if (!components[ comp ]) {
            hasAllComponents = false;
          }
        });
        return hasAllComponents;
      })
      .map(([ entityId ]) => entityId);
  };

  getEntityComponents = entityId => {
    this.components[ entityId ] = this.components[ entityId ] || {};
    return this.components[ entityId ];
  };

  destroy = () => {
    this.components = {};
    this.nodeCache = {};
  };
}
