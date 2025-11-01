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
import { sampleModels } from '../data/sampleModels';

export function MoPiXDemo() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const rendererRef = useRef<CanvasRenderer | null>(null);
  const animationRef = useRef<AnimationController | null>(null);
  const objectsRef = useRef<Map<string, MoPiXObject>>(new Map());

  const [isPlaying, setIsPlaying] = useState(false);
  const [currentFrame, setCurrentFrame] = useState(0);
  const [selectedObjectInfo, setSelectedObjectInfo] = useState<string | null>(null);
  const [showSamplesMenu, setShowSamplesMenu] = useState(false);
  const [, setObjectsVersion] = useState(0); // Force re-render when objects change

  // Store initial demo objects for reset
  const createDefaultObjects = () => {
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
      ]),
      info: 'This square moves horizontally across the screen.\n\nEquation: x = t × 2 + 100\n\nThe x-position is calculated by multiplying time (t) by 2 and adding 100. This creates steady horizontal motion, starting at position 100 and moving 2 pixels per frame to the right.'
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
      ]),
      info: 'This circle bounces up and down smoothly.\n\nEquation: y = sin(t × 10) × 100 + 300\n\nThe sine function creates smooth oscillation between -1 and 1. Multiplying by 10 speeds up the oscillation, multiplying by 100 sets the bounce height (amplitude), and adding 300 centers it at y-position 300.'
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
      ]),
      info: 'This rectangle rotates continuously.\n\nEquation: rotation = t × 5\n\nThe rotation angle is simply time multiplied by 5. This creates a steady rotation of 5 degrees per frame. As time increases, the rectangle keeps spinning.'
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
      ]),
      info: 'This circle pulses between purple and magenta.\n\nEquation: red = 128 + sin(t × 6) × 127\n\nThe red color channel oscillates using a sine wave. Since sin() ranges from -1 to 1, multiplying by 127 gives -127 to 127. Adding 128 centers this at 128, giving a final range of 1 to 255 (the valid color range).'
    };

    objects.set('mover', mover);
    objects.set('bouncer', bouncer);
    objects.set('spinner', spinner);
    objects.set('colorChanger', colorChanger);

    return objects;
  };

  // Initialize demo objects
  useEffect(() => {
    objectsRef.current = createDefaultObjects();
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

  const loadDefaultDemo = () => {
    // Stop animation and reset
    setIsPlaying(false);
    animationRef.current?.reset();
    setCurrentFrame(0);

    // Load default demo objects
    objectsRef.current = createDefaultObjects();

    // Force component to re-render to show new objects
    setObjectsVersion((v: number) => v + 1);

    // Re-render
    renderFrame(0);
    setShowSamplesMenu(false);
  };

  const loadSampleModel = (modelId: string) => {
    const model = sampleModels.find(m => m.id === modelId);
    if (!model) return;

    // Stop animation and reset
    setIsPlaying(false);
    animationRef.current?.reset();
    setCurrentFrame(0);

    // Load the sample model objects
    objectsRef.current = new Map(model.objects);

    // Force component to re-render to show new objects
    setObjectsVersion((v: number) => v + 1);

    // Re-render
    renderFrame(0);
    setShowSamplesMenu(false);
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1 style={{ margin: 0 }}>MoPiX Modern - Proof of Concept</h1>
        <button
          onClick={() => setShowSamplesMenu(true)}
          style={{
            padding: '10px 20px',
            fontSize: '16px',
            cursor: 'pointer',
            borderRadius: '4px',
            border: '1px solid #4CAF50',
            backgroundColor: '#E8F5E9',
            color: '#2E7D32',
            fontWeight: 'bold'
          }}
        >
          📚 Load Sample Model
        </button>
      </div>

      {showSamplesMenu && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }} onClick={() => setShowSamplesMenu(false)}>
          <div style={{
            backgroundColor: 'white',
            padding: '30px',
            borderRadius: '8px',
            maxWidth: '700px',
            maxHeight: '80vh',
            overflow: 'auto',
            boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
          }} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginTop: 0 }}>Sample Models</h2>
            <p style={{ color: '#666', marginBottom: '20px' }}>
              Choose a sample model to load. These are recreations of the original MoPiX examples.
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div
                style={{
                  padding: '15px',
                  border: '2px solid #4CAF50',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  transition: 'background-color 0.2s',
                  backgroundColor: '#E8F5E9'
                }}
                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#C8E6C9'}
                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#E8F5E9'}
                onClick={() => loadDefaultDemo()}
              >
                <h3 style={{ margin: '0 0 8px 0', color: '#2E7D32' }}>🏠 Default Demo</h3>
                <p style={{ margin: 0, color: '#1B5E20' }}>Four objects demonstrating basic movement, rotation, bouncing, and color changes.</p>
              </div>
              {sampleModels.map(model => (
                <div
                  key={model.id}
                  style={{
                    padding: '15px',
                    border: '1px solid #ddd',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    transition: 'background-color 0.2s',
                    backgroundColor: '#fff'
                  }}
                  onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f5f5f5'}
                  onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#fff'}
                  onClick={() => loadSampleModel(model.id)}
                >
                  <h3 style={{ margin: '0 0 8px 0', color: '#1976D2' }}>{model.name}</h3>
                  <p style={{ margin: 0, color: '#666' }}>{model.description}</p>
                </div>
              ))}
            </div>
            <button
              onClick={() => setShowSamplesMenu(false)}
              style={{
                marginTop: '20px',
                padding: '10px 20px',
                fontSize: '16px',
                cursor: 'pointer',
                borderRadius: '4px',
                border: '1px solid #ccc',
                backgroundColor: '#f5f5f5'
              }}
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      <div style={{ marginBottom: '20px', backgroundColor: '#f5f5f5', padding: '15px', borderRadius: '8px' }}>
        <h2 style={{ marginTop: 0 }}>Demo Objects</h2>
        <ul style={{ margin: '10px 0', listStyleType: 'none', paddingLeft: 0 }}>
          {Array.from(objectsRef.current.values()).map(obj => (
            <li key={obj.id} style={{ marginBottom: '10px', display: 'flex', alignItems: 'center', gap: '10px' }}>
              <span style={{ flex: 1 }}>
                <strong>{obj.name}:</strong> {
                  obj.equations.size > 0
                    ? Array.from(obj.equations.values()).map(eq => eq.expression.toString()).join(', ')
                    : 'Static object'
                }
              </span>
              {obj.info && (
                <button
                  onClick={() => setSelectedObjectInfo(obj.info || null)}
                  style={{
                    padding: '5px 10px',
                    fontSize: '14px',
                    cursor: 'pointer',
                    borderRadius: '4px',
                    border: '1px solid #2196F3',
                    backgroundColor: '#E3F2FD',
                    color: '#1976D2'
                  }}
                  title="Show information about this object"
                >
                  ℹ Info
                </button>
              )}
            </li>
          ))}
        </ul>
      </div>

      {selectedObjectInfo && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }} onClick={() => setSelectedObjectInfo(null)}>
          <div style={{
            backgroundColor: 'white',
            padding: '30px',
            borderRadius: '8px',
            maxWidth: '600px',
            maxHeight: '80vh',
            overflow: 'auto',
            boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
          }} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginTop: 0 }}>Object Information</h2>
            <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.6' }}>
              {selectedObjectInfo}
            </div>
            <button
              onClick={() => setSelectedObjectInfo(null)}
              style={{
                marginTop: '20px',
                padding: '10px 20px',
                fontSize: '16px',
                cursor: 'pointer',
                borderRadius: '4px',
                border: '1px solid #ccc',
                backgroundColor: '#2196F3',
                color: 'white'
              }}
            >
              Close
            </button>
          </div>
        </div>
      )}

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
