import Node from "./Node";
import { PhysicsComponent } from "./components/PhysicsComponent";
import { NameComponent } from "./components/NameComponent";
import { GraphicsComponent } from "./components/GraphicsComponent";
import { ResourceHarvesterComponent } from "./components/ResourceHarvesterComponent";
import { ResourceStorageComponent } from "./components/ResourceStorageComponent";

export const NAME_RENDER_NODE = Node.of(PhysicsComponent, NameComponent);
export const GRAPHICS_RENDER_NODE = Node.of(PhysicsComponent, GraphicsComponent);
export const RESOURCE_HARVESTER_NODE = Node.of(PhysicsComponent, ResourceHarvesterComponent, ResourceStorageComponent);
export const STORAGE_NODE = Node.of(PhysicsComponent, ResourceStorageComponent);