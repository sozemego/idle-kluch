export class GraphicsComponent {

  constructor(sprite) {
    this.sprite = sprite;
  }

  setSpritePosition = (x, y) => {
    this.sprite.x = x;
    this.sprite.y = y;
  };

  getSprite = () => this.sprite;

}
