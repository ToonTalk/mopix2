/**
 * Canvas Renderer
 *
 * Renders MoPiX objects using HTML5 Canvas API
 */

import { MoPiXObject } from '../types';

export class CanvasRenderer {
  private ctx: CanvasRenderingContext2D;

  constructor(canvas: HTMLCanvasElement) {
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      throw new Error('Could not get 2D context');
    }
    this.ctx = ctx;
  }

  /**
   * Clear the entire canvas
   */
  clear(): void {
    const { width, height } = this.ctx.canvas;
    this.ctx.clearRect(0, 0, width, height);
  }

  /**
   * Render a single MoPiX object
   */
  renderObject(obj: MoPiXObject): void {
    this.ctx.save();

    // Translate to object position
    this.ctx.translate(obj.x, obj.y);

    // Rotate around center
    this.ctx.rotate((obj.rotation * Math.PI) / 180);

    // Set fill color with transparency
    const alpha = obj.transparency / 100;
    this.ctx.fillStyle = `rgba(${obj.red}, ${obj.green}, ${obj.blue}, ${alpha})`;

    // Draw shape based on appearance
    switch (obj.appearance) {
      case 'square':
        this.ctx.fillRect(
          -obj.width / 2,
          -obj.height / 2,
          obj.width,
          obj.height
        );
        break;

      case 'circle':
        this.ctx.beginPath();
        this.ctx.ellipse(
          0,
          0,
          obj.width / 2,
          obj.height / 2,
          0,
          0,
          2 * Math.PI
        );
        this.ctx.fill();
        break;
    }

    this.ctx.restore();
  }

  /**
   * Render all objects in the scene
   */
  renderScene(objects: MoPiXObject[]): void {
    this.clear();
    objects.forEach(obj => this.renderObject(obj));
  }

  /**
   * Draw a grid for reference (optional)
   */
  drawGrid(spacing: number = 50, color: string = '#e0e0e0'): void {
    const { width, height } = this.ctx.canvas;

    this.ctx.save();
    this.ctx.strokeStyle = color;
    this.ctx.lineWidth = 1;

    // Vertical lines
    for (let x = 0; x <= width; x += spacing) {
      this.ctx.beginPath();
      this.ctx.moveTo(x, 0);
      this.ctx.lineTo(x, height);
      this.ctx.stroke();
    }

    // Horizontal lines
    for (let y = 0; y <= height; y += spacing) {
      this.ctx.beginPath();
      this.ctx.moveTo(0, y);
      this.ctx.lineTo(width, y);
      this.ctx.stroke();
    }

    this.ctx.restore();
  }
}
