export class GraphicsComponent {

  constructor(asset, sprite) {
    this.asset = asset;
    this.sprite = sprite;
  }

  setSpritePosition = (x, y) => {
    this.sprite.x = x;
    this.sprite.y = y;
  };

  getSprite = () => this.sprite;

  getAsset = () => this.asset;

}
