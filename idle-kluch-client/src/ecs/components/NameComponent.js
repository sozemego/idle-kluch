export class NameComponent {

  constructor(name) {
    this.name = name;
  }

  getName = () => {
    return this.name;
  };

  setName = (name) => {
    this.name = name;
  };

}
