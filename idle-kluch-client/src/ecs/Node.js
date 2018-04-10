export class Node {

  constructor(components) {
    this.components = components;
  }

  static of(components) {
    return new Node(components);
  }

  getComponents = () => {
    return this.components;
  };

}