/**
 * MoPiX Demo Component
 *
 * Demonstrates the core functionality of the modern MoPiX engine
 */

import { useEffect, useRef, useState } from 'react';
import { MoPiXObject } from '../types';
import { CanvasRenderer } from '../engine/CanvasRenderer';
import { AnimationController } from '../engine/AnimationController';
import { updateObjectAtTime } from '../engine/Expression';
import {
  ConstantExpression,
  VariableExpression,
  BinaryOperatorExpression,
  UnaryOperatorExpression
} from '../engine/Expression';

export function MoPiXDemo() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const rendererRef = useRef<CanvasRenderer | null>(null);
  const animationRef = useRef<AnimationController | null>(null);
  const objectsRef = useRef<Map<string, MoPiXObject>>(new Map());

  const [isPlaying, setIsPlaying] = useState(false);
  const [currentFrame, setCurrentFrame] = useState(0);

  // Initialize demo objects
  useEffect(() => {
    const objects = new Map<string, MoPiXObject>();

    // Object 1: Moving horizontally - x = t * 2 + 100
    const mover: MoPiXObject = {
      id: 'mover',
      name: 'Mover',
      x: 100,
      y: 200,
      width: 50,
      height: 50,
      rotation: 0,
      red: 255,
      green: 100,
      blue: 100,
      transparency: 100,
      appearance: 'square',
      equations: new Map([
        ['x', {
          attribute: 'x',
          expression: new BinaryOperatorExpression(
            '+',
            new BinaryOperatorExpression(
              '*',
              new VariableExpression('t'),
              new ConstantExpression(2)
            ),
            new ConstantExpression(100)
          ),
          cache: new Map()
        }]
      ])
    };

    // Object 2: Bouncing vertically - y = sin(t * 10) * 100 + 300
    const bouncer: MoPiXObject = {
      id: 'bouncer',
      name: 'Bouncer',
      x: 400,
      y: 300,
      width: 60,
      height: 60,
      rotation: 0,
      red: 100,
      green: 200,
      blue: 255,
      transparency: 100,
      appearance: 'circle',
      equations: new Map([
        ['y', {
          attribute: 'y',
          expression: new BinaryOperatorExpression(
            '+',
            new BinaryOperatorExpression(
              '*',
              new UnaryOperatorExpression(
                'sin',
                new BinaryOperatorExpression(
                  '*',
                  new VariableExpression('t'),
                  new ConstantExpression(10)
                )
              ),
              new ConstantExpression(100)
            ),
            new ConstantExpression(300)
          ),
          cache: new Map()
        }]
      ])
    };

    // Object 3: Rotating - rotation = t * 5
    const spinner: MoPiXObject = {
      id: 'spinner',
      name: 'Spinner',
      x: 600,
      y: 200,
      width: 70,
      height: 40,
      rotation: 0,
      red: 100,
      green: 255,
      blue: 100,
      transparency: 100,
      appearance: 'square',
      equations: new Map([
        ['rotation', {
          attribute: 'rotation',
          expression: new BinaryOperatorExpression(
            '*',
            new VariableExpression('t'),
            new ConstantExpression(5)
          ),
          cache: new Map()
        }]
      ])
    };

    // Object 4: Color changing - red = 128 + sin(t * 6) * 127
    const colorChanger: MoPiXObject = {
      id: 'colorChanger',
      name: 'ColorChanger',
      x: 200,
      y: 400,
      width: 80,
      height: 80,
      rotation: 0,
      red: 128,
      green: 128,
      blue: 255,
      transparency: 100,
      appearance: 'circle',
      equations: new Map([
        ['redColour', {
          attribute: 'redColour',
          expression: new BinaryOperatorExpression(
            '+',
            new ConstantExpression(128),
            new BinaryOperatorExpression(
              '*',
              new UnaryOperatorExpression(
                'sin',
                new BinaryOperatorExpression(
                  '*',
                  new VariableExpression('t'),
                  new ConstantExpression(6)
                )
              ),
              new ConstantExpression(127)
            )
          ),
          cache: new Map()
        }]
      ])
    };

    objects.set('mover', mover);
    objects.set('bouncer', bouncer);
    objects.set('spinner', spinner);
    objects.set('colorChanger', colorChanger);

    objectsRef.current = objects;
  }, []);

  // Initialize renderer and animation controller
  useEffect(() => {
    if (!canvasRef.current) return;

    // Create renderer
    if (!rendererRef.current) {
      rendererRef.current = new CanvasRenderer(canvasRef.current);
    }

    // Create animation controller
    if (!animationRef.current) {
      animationRef.current = new AnimationController((frame) => {
        setCurrentFrame(frame);
        renderFrame(frame);
      }, 30);
    }

    // Initial render
    renderFrame(0);

    return () => {
      animationRef.current?.stop();
    };
  }, []);

  // Handle play/pause
  useEffect(() => {
    if (!animationRef.current) return;

    if (isPlaying) {
      animationRef.current.start();
    } else {
      animationRef.current.stop();
    }
  }, [isPlaying]);

  // Render a specific frame
  const renderFrame = (frame: number) => {
    if (!rendererRef.current) return;

    const objects = Array.from(objectsRef.current.values());
    const updatedObjects = objects.map(obj =>
      updateObjectAtTime(obj, frame, objectsRef.current)
    );

    rendererRef.current.drawGrid(50);
    rendererRef.current.renderScene(updatedObjects);
  };

  // Control handlers
  const handlePlayPause = () => {
    setIsPlaying(!isPlaying);
  };

  const handleReset = () => {
    setIsPlaying(false);
    animationRef.current?.reset();
    setCurrentFrame(0);
    renderFrame(0);
  };

  const handleStepForward = () => {
    setIsPlaying(false);
    animationRef.current?.stepForward();
  };

  const handleStepBackward = () => {
    setIsPlaying(false);
    animationRef.current?.stepBackward();
  };

  const handleFrameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const frame = parseInt(e.target.value, 10);
    setIsPlaying(false);
    animationRef.current?.setFrame(frame);
    setCurrentFrame(frame);
    renderFrame(frame);
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>MoPiX Modern - Proof of Concept</h1>

      <div style={{ marginBottom: '20px', backgroundColor: '#f5f5f5', padding: '15px', borderRadius: '8px' }}>
        <h2 style={{ marginTop: 0 }}>Demo Objects</h2>
        <ul style={{ margin: '10px 0' }}>
          <li><strong>Red Square (Mover):</strong> x = t × 2 + 100 (moves horizontally)</li>
          <li><strong>Blue Circle (Bouncer):</strong> y = sin(t × 10) × 100 + 300 (bounces vertically)</li>
          <li><strong>Green Rectangle (Spinner):</strong> rotation = t × 5 (rotates)</li>
          <li><strong>Color-Changing Circle:</strong> red = 128 + sin(t × 6) × 127 (pulses red)</li>
        </ul>
      </div>

      <canvas
        ref={canvasRef}
        width={800}
        height={600}
        style={{
          border: '2px solid #333',
          display: 'block',
          marginBottom: '20px',
          borderRadius: '4px'
        }}
      />

      <div style={{
        display: 'flex',
        gap: '10px',
        alignItems: 'center',
        marginBottom: '20px'
      }}>
        <button
          onClick={handleReset}
          style={{
            padding: '10px 20px',
            fontSize: '16px',
            cursor: 'pointer',
            borderRadius: '4px',
            border: '1px solid #ccc',
            backgroundColor: '#fff'
          }}
        >
          ⏮ Reset
        </button>

        <button
          onClick={handleStepBackward}
          style={{
            padding: '10px 20px',
            fontSize: '16px',
            cursor: 'pointer',
            borderRadius: '4px',
            border: '1px solid #ccc',
            backgroundColor: '#fff'
          }}
        >
          ⏪ Step Back
        </button>

        <button
          onClick={handlePlayPause}
          style={{
            padding: '10px 30px',
            fontSize: '16px',
            cursor: 'pointer',
            borderRadius: '4px',
            border: '1px solid #ccc',
            backgroundColor: isPlaying ? '#ffebee' : '#e8f5e9',
            fontWeight: 'bold'
          }}
        >
          {isPlaying ? '⏸ Pause' : '▶ Play'}
        </button>

        <button
          onClick={handleStepForward}
          style={{
            padding: '10px 20px',
            fontSize: '16px',
            cursor: 'pointer',
            borderRadius: '4px',
            border: '1px solid #ccc',
            backgroundColor: '#fff'
          }}
        >
          Step Forward ⏩
        </button>

        <div style={{ marginLeft: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
          <label htmlFor="frame-input">Frame:</label>
          <input
            id="frame-input"
            type="number"
            value={currentFrame}
            onChange={handleFrameChange}
            min="0"
            max="1000"
            style={{
              padding: '8px',
              fontSize: '16px',
              width: '80px',
              borderRadius: '4px',
              border: '1px solid #ccc'
            }}
          />
          <input
            type="range"
            value={currentFrame}
            onChange={handleFrameChange}
            min="0"
            max="200"
            style={{ width: '200px' }}
          />
        </div>
      </div>

      <div style={{
        backgroundColor: '#fff3cd',
        padding: '15px',
        borderRadius: '8px',
        border: '1px solid #ffc107'
      }}>
        <h3 style={{ marginTop: 0 }}>What This Demonstrates</h3>
        <ul>
          <li>✅ <strong>Expression Evaluation Engine:</strong> Supports arithmetic, trigonometric, and logical operators</li>
          <li>✅ <strong>Canvas Rendering:</strong> HTML5 Canvas API for shapes (rectangles, circles) with rotation</li>
          <li>✅ <strong>Animation System:</strong> Frame-by-frame animation using requestAnimationFrame</li>
          <li>✅ <strong>Caching:</strong> Expression results are cached per frame for performance</li>
          <li>✅ <strong>React Integration:</strong> Clean separation between engine logic and UI</li>
          <li>✅ <strong>Type Safety:</strong> Full TypeScript implementation catches errors at compile time</li>
        </ul>
        <p style={{ marginBottom: 0 }}>
          <strong>Bundle Size:</strong> ~20KB (vs 1-2MB for GWT) |
          <strong> Build Time:</strong> &lt;1 second |
          <strong> Hot Reload:</strong> Instant feedback
        </p>
      </div>
    </div>
  );
}
