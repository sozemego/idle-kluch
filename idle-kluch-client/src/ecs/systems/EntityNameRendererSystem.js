import { PhysicsComponent } from "../components/PhysicsComponent";
import { IN_GAME_FONT_SIZE } from "../../game/constants";
import { NameComponent } from "../components/NameComponent";
import { Engine as Entity } from "../Engine";
import { NAME_RENDER_NODE } from "../nodes";

export class EntityNameRendererSystem {

  constructor(engine, textGroup, textFactory, getSelectedEntity) {
    this.engine = engine;
    this.node = NAME_RENDER_NODE;
    this.textGroup = textGroup;
    this.textFactory = textFactory;
    this.texts = {};
    this.getSelectedEntity = getSelectedEntity;
  }

  getEngine() {
    return this.engine;
  }

  shouldUpdate = () => true;

  update = (delta) => {
    const selectedEntity = this.getSelectedEntity() || Entity.nullInstance();

    this.getEngine()
      .getEntitiesByNode(this.node)
      .map(entity => {
        const text = this.getText(entity.getId());
        text.kill();
        return entity;
      })
      .filter(entity => entity.getId() === selectedEntity.getId())
      .forEach((entity) => this.updateEntity(entity, delta));
  }

  updateEntity = (entity, delta) => {
    const text = this.getText(entity.getId());
    text.revive();
    const physicsComponent = entity.getComponent(PhysicsComponent);
    const nameComponent = entity.getComponent(NameComponent);

    text.x = physicsComponent.getX() + physicsComponent.getWidth();
    text.y = physicsComponent.getY();
    text.text = nameComponent.getName();

  }

  getText = (id) => {
    let text = this.texts[ id ];
    if (!text) {
      text = this.textFactory.text(0, 0, "", { font: `${IN_GAME_FONT_SIZE}px Arial`, fill: "rgba(255, 255, 255, 0.9)", stroke: "black" });
      this.textGroup.add(text);
      this.texts[ id ] = text;
    }
    return text;
  }

}