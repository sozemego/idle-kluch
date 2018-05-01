export class OwnershipComponent {

  constructor(ownerId) {
    this.ownerId = ownerId;
  }

  getOwnerId = () => {
    return this.ownerId;
  };

  setOwnerId = ownerId => {
    this.ownerId = ownerId;
  };

}
