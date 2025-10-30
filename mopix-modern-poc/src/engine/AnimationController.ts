/**
 * Animation Controller
 *
 * Manages frame-by-frame animation using requestAnimationFrame
 */

export class AnimationController {
  private animationFrameId: number | null = null;
  private lastFrameTime: number = 0;
  private frameDuration: number;
  private currentFrame: number = 0;

  constructor(
    private onFrame: (frameNumber: number) => void,
    private fps: number = 30
  ) {
    this.frameDuration = 1000 / fps;
  }

  /**
   * Start the animation loop
   */
  start(): void {
    if (this.animationFrameId !== null) return; // Already running

    this.lastFrameTime = performance.now();
    this.animationFrameId = requestAnimationFrame(this.tick);
  }

  /**
   * Stop the animation loop
   */
  stop(): void {
    if (this.animationFrameId !== null) {
      cancelAnimationFrame(this.animationFrameId);
      this.animationFrameId = null;
    }
  }

  /**
   * Reset to frame 0
   */
  reset(): void {
    this.currentFrame = 0;
    this.lastFrameTime = performance.now();
  }

  /**
   * Set the current frame number
   */
  setFrame(frame: number): void {
    this.currentFrame = frame;
    this.onFrame(this.currentFrame);
  }

  /**
   * Get current frame number
   */
  getFrame(): number {
    return this.currentFrame;
  }

  /**
   * Step forward one frame
   */
  stepForward(): void {
    this.currentFrame++;
    this.onFrame(this.currentFrame);
  }

  /**
   * Step backward one frame
   */
  stepBackward(): void {
    this.currentFrame = Math.max(0, this.currentFrame - 1);
    this.onFrame(this.currentFrame);
  }

  /**
   * Animation loop tick - called by requestAnimationFrame
   */
  private tick = (currentTime: number): void => {
    const elapsed = currentTime - this.lastFrameTime;

    // Only advance if enough time has passed
    if (elapsed >= this.frameDuration) {
      this.currentFrame++;
      this.onFrame(this.currentFrame);
      this.lastFrameTime = currentTime;
    }

    this.animationFrameId = requestAnimationFrame(this.tick);
  }

  /**
   * Check if animation is currently running
   */
  isRunning(): boolean {
    return this.animationFrameId !== null;
  }
}
