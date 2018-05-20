export default class Node {
  constructor(components) {
    this.components = components;
  }

  static of(components) {
    components = Array.isArray(components) ? components: [ components ];
    return new Node(components);
  }

  getComponents = () => {
    return this.components;
  };
}
