# MoPiX 2 - Modern Web Stack Migration Guide

## Executive Summary

This document outlines a complete rewrite of MoPiX 2 using modern client-side web technologies. Rather than upgrading the legacy GWT 1.7.0 framework, this approach creates a new repository with contemporary JavaScript/TypeScript tools while preserving the educational functionality and user experience.

**Estimated Effort**: 4-6 months with 2 developers
**Recommended Stack**: React + TypeScript + Canvas API
**Code Reduction**: ~10,000 LOC Java → ~5,000-7,000 LOC TypeScript

---

## Table of Contents

1. [Technology Stack Recommendations](#technology-stack-recommendations)
2. [Architecture Overview](#architecture-overview)
3. [Core Components to Build](#core-components-to-build)
4. [Migration Strategy](#migration-strategy)
5. [Effort Estimates](#effort-estimates)
6. [Repository Structure](#repository-structure)
7. [Implementation Roadmap](#implementation-roadmap)
8. [Testing Strategy](#testing-strategy)
9. [Deployment Options](#deployment-options)
10. [Risk Assessment](#risk-assessment)

---

## 1. Technology Stack Recommendations

### Option A: React + TypeScript (Recommended)

**Frontend Framework**: React 18+
- Large ecosystem and community
- Excellent TypeScript support
- Rich component libraries available
- Good Canvas integration (react-konva, fabric.js)

**Language**: TypeScript 5+
- Type safety for complex expression system
- Better IDE support
- Easier refactoring
- Prevents runtime errors

**Build Tool**: Vite
- Fast development server
- Optimized production builds
- Native ESM support
- Great TypeScript integration

**State Management**: Zustand or Redux Toolkit
- Simple, hooks-based API (Zustand)
- Time-travel debugging (Redux DevTools)
- Undo/redo support built-in

**Canvas Rendering**: Native Canvas API or Konva.js
- Direct Canvas for performance
- Konva.js for easier object manipulation

**Math Rendering**: KaTeX or MathJax
- Render MathML/LaTeX
- Fast rendering (KaTeX)
- Good browser compatibility

**Testing**: Vitest + React Testing Library
- Fast test execution
- Component testing
- Integration testing

**Package Manager**: pnpm or npm
- Fast, efficient dependency management

---

### Option B: Vue 3 + TypeScript

**Frontend Framework**: Vue 3 with Composition API
- Simpler learning curve
- Excellent TypeScript support
- Good performance
- Similar ecosystem to React

**Differences from Option A**:
- Smaller bundle size
- More opinionated structure
- Slightly smaller ecosystem

---

### Option C: Svelte + TypeScript

**Frontend Framework**: Svelte + SvelteKit
- Compiles to vanilla JS (no runtime)
- Smallest bundle size
- Excellent performance
- Built-in reactive state management

**Considerations**:
- Smaller ecosystem
- Fewer third-party libraries
- Newer framework (less battle-tested)

---

### Recommended Choice: Option A (React + TypeScript)

**Rationale**:
1. **Largest ecosystem** - More libraries for math, canvas, UI components
2. **Best hiring pool** - Easier to find React developers
3. **Mature tooling** - Excellent dev tools and debugging
4. **Educational use** - Many schools teach React
5. **Long-term support** - Facebook-backed, stable roadmap

---

## 2. Architecture Overview

### 2.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────┐
│                   React App (UI Layer)               │
│  ┌───────────────┐  ┌────────────────┐             │
│  │  Canvas View  │  │ Equation Editor│             │
│  └───────┬───────┘  └────────┬───────┘             │
└──────────┼──────────────────┼─────────────────────┘
           │                   │
┌──────────┼──────────────────┼─────────────────────┐
│          │   Application State (Zustand)          │
│          ▼                   ▼                      │
│  ┌─────────────────┐ ┌─────────────────┐          │
│  │  Scene Objects  │ │   Equations     │          │
│  └─────────────────┘ └─────────────────┘          │
└─────────────┬────────────────┬────────────────────┘
              │                │
┌─────────────┼────────────────┼────────────────────┐
│             │  Core Engine Layer                   │
│   ┌─────────▼─────────┐  ┌──▼──────────────────┐ │
│   │ Expression Engine │  │  Rendering Engine   │ │
│   │  (Pure Functions) │  │  (Canvas API)       │ │
│   └───────────────────┘  └─────────────────────┘ │
│   ┌──────────────────────────────────────────────┐│
│   │     Animation System (requestAnimationFrame) ││
│   └──────────────────────────────────────────────┘│
│   ┌──────────────────────────────────────────────┐│
│   │  Serialization (XML/JSON)                    ││
│   └──────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────┘
```

### 2.2 Module Structure

```typescript
src/
├── components/              # React UI components
│   ├── Canvas/             # Canvas view component
│   ├── EquationEditor/     # Equation editing UI
│   ├── Timeline/           # Temporal controls
│   ├── ObjectInspector/    # Property inspector
│   └── Toolbar/            # Main toolbar
├── engine/                 # Core logic (framework-agnostic)
│   ├── expression/         # Expression evaluation
│   ├── animation/          # Animation loop
│   ├── rendering/          # Canvas rendering
│   └── geometry/           # Shape mathematics
├── store/                  # State management
│   ├── sceneStore.ts      # Object state
│   ├── historyStore.ts    # Undo/redo
│   └── uiStore.ts         # UI state
├── models/                 # TypeScript types/interfaces
│   ├── MoPiXObject.ts
│   ├── Expression.ts
│   └── Equation.ts
├── utils/                  # Utilities
│   ├── serialization.ts   # Save/load
│   ├── mathml.ts          # MathML parsing
│   └── colors.ts          # Color conversions
└── tests/                  # Unit and integration tests
```

---

## 3. Core Components to Build

### 3.1 Expression Evaluation Engine

**File**: `src/engine/expression/`

**Requirements**:
- Parse MathML/XML to expression tree
- Evaluate expressions recursively
- Support 29+ operators
- Cache evaluation results
- Detect circular dependencies

**TypeScript Implementation**:

```typescript
// Expression.ts
export abstract class Expression {
  abstract evaluate(context: EvaluationContext): number;
  abstract toMathML(): string;
  abstract clone(): Expression;
}

export interface EvaluationContext {
  object: MoPiXObject;
  time: number;
  allObjects: Map<string, MoPiXObject>;
  constants: Map<string, number>;
}

// Concrete implementations
export class ConstantExpression extends Expression {
  constructor(private value: number) { super(); }

  evaluate(context: EvaluationContext): number {
    return this.value;
  }
}

export class BinaryOperatorExpression extends Expression {
  constructor(
    private operator: '+' | '-' | '*' | '/' | '^',
    private left: Expression,
    private right: Expression
  ) { super(); }

  evaluate(context: EvaluationContext): number {
    const leftValue = this.left.evaluate(context);
    const rightValue = this.right.evaluate(context);

    switch (this.operator) {
      case '+': return leftValue + rightValue;
      case '-': return leftValue - rightValue;
      case '*': return leftValue * rightValue;
      case '/': return leftValue / rightValue;
      case '^': return Math.pow(leftValue, rightValue);
    }
  }
}

// Parser
export class ExpressionParser {
  static fromMathML(xml: string): Expression {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xml, 'text/xml');
    return this.parseNode(doc.documentElement);
  }

  private static parseNode(node: Element): Expression {
    if (node.tagName === 'cn') {
      return new ConstantExpression(parseFloat(node.textContent!));
    }
    if (node.tagName === 'ci') {
      return new VariableExpression(node.textContent!);
    }
    if (node.tagName === 'apply') {
      const operator = node.firstElementChild!.tagName;
      const args = Array.from(node.children).slice(1).map(n => this.parseNode(n));
      return this.createOperatorExpression(operator, args);
    }
    throw new Error(`Unknown node type: ${node.tagName}`);
  }
}
```

**Key Classes to Implement**:
- `Expression` (base class)
- `ConstantExpression`
- `VariableExpression`
- `BinaryOperatorExpression` (+, -, *, /, ^, and, or, ==, <, >, etc.)
- `UnaryOperatorExpression` (-, abs, sin, cos, tan, etc.)
- `AttributeExpression` (access object property)
- `ExpressionParser` (MathML → Expression tree)

**Caching Strategy**:
```typescript
export class Equation {
  private cache: Map<number, number> = new Map();

  evaluate(context: EvaluationContext): number {
    const cacheKey = context.time;

    if (this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey)!;
    }

    const result = this.expression.evaluate(context);
    this.cache.set(cacheKey, result);
    return result;
  }

  clearCache(): void {
    this.cache.clear();
  }
}
```

**Estimated Lines**: 1,500-2,000 LOC
**Estimated Time**: 3-4 weeks

---

### 3.2 Object Model & State Management

**File**: `src/models/MoPiXObject.ts`

```typescript
export interface MoPiXObject {
  id: string;
  name: string;

  // Visual properties
  x: number;
  y: number;
  width: number;
  height: number;
  rotation: number;

  // Color
  red: number;
  green: number;
  blue: number;
  transparency: number;

  // Pen/trail
  penDown: boolean;
  penThickness: number;
  penColor: { r: number; g: number; b: number };
  penTransparency: number;

  // Shape type
  appearance: 'square' | 'circle' | 'text';

  // Equations defining behavior
  equations: Map<string, Equation[]>;

  // Cached trail points
  trail: TrailPoint[];
}

export interface TrailPoint {
  time: number;
  x: number;
  y: number;
}

export interface Equation {
  attribute: string;        // e.g., "x", "width"
  timeSpec: number | 't';   // Specific frame or 't' for all times
  expression: Expression;
  cache: Map<number, number>;
}
```

**State Management with Zustand**:

```typescript
// src/store/sceneStore.ts
import { create } from 'zustand';

interface SceneState {
  objects: Map<string, MoPiXObject>;
  currentTime: number;
  isPlaying: boolean;
  selectedObjectId: string | null;

  // Actions
  addObject: (object: MoPiXObject) => void;
  removeObject: (id: string) => void;
  updateObject: (id: string, updates: Partial<MoPiXObject>) => void;
  setTime: (time: number) => void;
  play: () => void;
  pause: () => void;
}

export const useSceneStore = create<SceneState>((set) => ({
  objects: new Map(),
  currentTime: 0,
  isPlaying: false,
  selectedObjectId: null,

  addObject: (object) => set((state) => {
    const newObjects = new Map(state.objects);
    newObjects.set(object.id, object);
    return { objects: newObjects };
  }),

  removeObject: (id) => set((state) => {
    const newObjects = new Map(state.objects);
    newObjects.delete(id);
    return { objects: newObjects };
  }),

  updateObject: (id, updates) => set((state) => {
    const newObjects = new Map(state.objects);
    const existing = newObjects.get(id);
    if (existing) {
      newObjects.set(id, { ...existing, ...updates });
    }
    return { objects: newObjects };
  }),

  setTime: (time) => set({ currentTime: time }),
  play: () => set({ isPlaying: true }),
  pause: () => set({ isPlaying: false }),
}));
```

**Estimated Lines**: 500-800 LOC
**Estimated Time**: 1-2 weeks

---

### 3.3 Animation System

**File**: `src/engine/animation/AnimationController.ts`

```typescript
export class AnimationController {
  private animationFrameId: number | null = null;
  private lastFrameTime: number = 0;
  private frameDuration: number = 1000 / 30; // 30 fps

  constructor(
    private onFrame: (time: number) => void,
    private fps: number = 30
  ) {
    this.frameDuration = 1000 / fps;
  }

  start(): void {
    if (this.animationFrameId !== null) return;

    this.lastFrameTime = performance.now();
    this.animationFrameId = requestAnimationFrame(this.tick);
  }

  stop(): void {
    if (this.animationFrameId !== null) {
      cancelAnimationFrame(this.animationFrameId);
      this.animationFrameId = null;
    }
  }

  private tick = (currentTime: number): void => {
    const elapsed = currentTime - this.lastFrameTime;

    if (elapsed >= this.frameDuration) {
      this.onFrame(Math.floor(currentTime / this.frameDuration));
      this.lastFrameTime = currentTime;
    }

    this.animationFrameId = requestAnimationFrame(this.tick);
  }
}

// Usage in React component
export function useAnimation() {
  const { currentTime, isPlaying, setTime } = useSceneStore();
  const controllerRef = useRef<AnimationController | null>(null);

  useEffect(() => {
    if (!controllerRef.current) {
      controllerRef.current = new AnimationController(
        (frameTime) => setTime(frameTime),
        30
      );
    }

    const controller = controllerRef.current;

    if (isPlaying) {
      controller.start();
    } else {
      controller.stop();
    }

    return () => controller.stop();
  }, [isPlaying, setTime]);
}
```

**Features**:
- requestAnimationFrame for smooth animation
- Configurable frame rate
- Play/pause/step controls
- Frame-accurate timing

**Estimated Lines**: 200-300 LOC
**Estimated Time**: 1 week

---

### 3.4 Canvas Rendering Engine

**File**: `src/engine/rendering/CanvasRenderer.ts`

```typescript
export class CanvasRenderer {
  private ctx: CanvasRenderingContext2D;

  constructor(canvas: HTMLCanvasElement) {
    this.ctx = canvas.getContext('2d')!;
  }

  clear(): void {
    const { width, height } = this.ctx.canvas;
    this.ctx.clearRect(0, 0, width, height);
  }

  renderObject(obj: MoPiXObject): void {
    this.ctx.save();

    // Transform to object position
    this.ctx.translate(obj.x, obj.y);
    this.ctx.rotate((obj.rotation * Math.PI) / 180);

    // Set fill style
    const alpha = obj.transparency / 100;
    this.ctx.fillStyle = `rgba(${obj.red}, ${obj.green}, ${obj.blue}, ${alpha})`;

    // Draw shape
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
        this.ctx.ellipse(0, 0, obj.width / 2, obj.height / 2, 0, 0, 2 * Math.PI);
        this.ctx.fill();
        break;

      case 'text':
        this.ctx.font = `${obj.height}px Arial`;
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.fillText(obj.name, 0, 0);
        break;
    }

    this.ctx.restore();
  }

  renderTrail(trail: TrailPoint[], penColor: { r: number; g: number; b: number }, thickness: number): void {
    if (trail.length < 2) return;

    this.ctx.beginPath();
    this.ctx.moveTo(trail[0].x, trail[0].y);

    for (let i = 1; i < trail.length; i++) {
      this.ctx.lineTo(trail[i].x, trail[i].y);
    }

    this.ctx.strokeStyle = `rgb(${penColor.r}, ${penColor.g}, ${penColor.b})`;
    this.ctx.lineWidth = thickness;
    this.ctx.stroke();
  }

  renderScene(objects: MoPiXObject[]): void {
    this.clear();

    // Render trails first (behind objects)
    objects.forEach(obj => {
      if (obj.penDown && obj.trail.length > 0) {
        this.renderTrail(obj.trail, obj.penColor, obj.penThickness);
      }
    });

    // Render objects
    objects.forEach(obj => this.renderObject(obj));
  }
}
```

**React Component**:

```typescript
// src/components/Canvas/CanvasView.tsx
export function CanvasView() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const rendererRef = useRef<CanvasRenderer | null>(null);
  const objects = useSceneStore(state => Array.from(state.objects.values()));
  const currentTime = useSceneStore(state => state.currentTime);

  useEffect(() => {
    if (!canvasRef.current) return;

    if (!rendererRef.current) {
      rendererRef.current = new CanvasRenderer(canvasRef.current);
    }

    // Update all objects for current time
    const updatedObjects = objects.map(obj => updateObjectAtTime(obj, currentTime));

    // Render
    rendererRef.current.renderScene(updatedObjects);
  }, [objects, currentTime]);

  return (
    <canvas
      ref={canvasRef}
      width={800}
      height={600}
      className="border border-gray-300"
    />
  );
}
```

**Estimated Lines**: 500-700 LOC
**Estimated Time**: 2 weeks

---

### 3.5 Equation Editor UI

**File**: `src/components/EquationEditor/`

**Requirements**:
- Visual equation builder
- MathML/LaTeX display
- Operator palette
- Variable selection
- Property targeting

**React Component Structure**:

```typescript
export function EquationEditor() {
  const [expression, setExpression] = useState<Expression | null>(null);
  const [targetAttribute, setTargetAttribute] = useState<string>('x');
  const [targetObject, setTargetObject] = useState<string | null>(null);

  return (
    <div className="equation-editor">
      <div className="operator-palette">
        <button onClick={() => addOperator('+')}>+</button>
        <button onClick={() => addOperator('-')}>-</button>
        <button onClick={() => addOperator('*')}>×</button>
        <button onClick={() => addOperator('/')}>÷</button>
        {/* More operators */}
      </div>

      <div className="expression-display">
        {expression && <MathMLRenderer expression={expression} />}
      </div>

      <div className="target-selection">
        <select value={targetAttribute} onChange={e => setTargetAttribute(e.target.value)}>
          <option value="x">X Position</option>
          <option value="y">Y Position</option>
          <option value="width">Width</option>
          <option value="height">Height</option>
          <option value="rotation">Rotation</option>
          {/* More attributes */}
        </select>

        <ObjectSelector value={targetObject} onChange={setTargetObject} />
      </div>

      <button onClick={applyEquation}>Apply Equation</button>
    </div>
  );
}
```

**MathML Rendering with KaTeX**:

```typescript
import katex from 'katex';

export function MathMLRenderer({ expression }: { expression: Expression }) {
  const mathml = expression.toMathML();
  const latex = convertMathMLToLaTeX(mathml);

  return (
    <div
      dangerouslySetInnerHTML={{
        __html: katex.renderToString(latex, { throwOnError: false })
      }}
    />
  );
}
```

**Estimated Lines**: 800-1,200 LOC
**Estimated Time**: 3 weeks

---

### 3.6 Serialization & Persistence

**File**: `src/utils/serialization.ts`

**Requirements**:
- Save/load to XML (backward compatibility)
- Save/load to JSON (modern format)
- Export to shareable format

**XML Serialization**:

```typescript
export class XMLSerializer {
  static serialize(objects: MoPiXObject[]): string {
    const doc = document.implementation.createDocument('', '', null);
    const root = doc.createElement('apply');
    const and = doc.createElement('and');
    root.appendChild(and);

    objects.forEach(obj => {
      obj.equations.forEach((equations, attribute) => {
        equations.forEach(eq => {
          const eqNode = this.serializeEquation(doc, obj, attribute, eq);
          root.appendChild(eqNode);
        });
      });
    });

    const serializer = new XMLSerializer();
    return serializer.serializeToString(doc);
  }

  private static serializeEquation(
    doc: Document,
    obj: MoPiXObject,
    attribute: string,
    equation: Equation
  ): Element {
    const apply = doc.createElement('apply');
    const eq = doc.createElement('eq');
    apply.appendChild(eq);

    // Left side: attribute(object, time)
    const leftApply = doc.createElement('apply');
    const mo = doc.createElement('mo');
    mo.textContent = attribute;
    leftApply.appendChild(mo);

    const ci = doc.createElement('ci');
    ci.textContent = obj.name;
    leftApply.appendChild(ci);

    const time = doc.createElement('ci');
    time.textContent = 't';
    leftApply.appendChild(time);

    apply.appendChild(leftApply);

    // Right side: expression
    const rightNode = equation.expression.toMathMLNode(doc);
    apply.appendChild(rightNode);

    return apply;
  }

  static deserialize(xml: string): MoPiXObject[] {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xml, 'text/xml');
    // Parse and recreate objects
    return this.parseObjects(doc);
  }
}
```

**JSON Format (Modern)**:

```typescript
interface SerializedScene {
  version: '2.0',
  objects: {
    id: string;
    name: string;
    properties: {
      x: number;
      y: number;
      // ... other properties
    };
    equations: {
      attribute: string;
      timeSpec: number | 't';
      expression: string; // MathML or JSON
    }[];
  }[];
}

export class JSONSerializer {
  static serialize(objects: MoPiXObject[]): string {
    const scene: SerializedScene = {
      version: '2.0',
      objects: objects.map(obj => ({
        id: obj.id,
        name: obj.name,
        properties: {
          x: obj.x,
          y: obj.y,
          width: obj.width,
          height: obj.height,
          // ... all properties
        },
        equations: Array.from(obj.equations.entries()).flatMap(([attr, eqs]) =>
          eqs.map(eq => ({
            attribute: attr,
            timeSpec: eq.timeSpec,
            expression: eq.expression.toMathML()
          }))
        )
      }))
    };

    return JSON.stringify(scene, null, 2);
  }

  static deserialize(json: string): MoPiXObject[] {
    const scene: SerializedScene = JSON.parse(json);
    return scene.objects.map(this.deserializeObject);
  }
}
```

**Estimated Lines**: 400-600 LOC
**Estimated Time**: 1-2 weeks

---

### 3.7 Undo/Redo System

**File**: `src/store/historyStore.ts`

```typescript
interface HistoryState {
  past: SceneSnapshot[];
  present: SceneSnapshot;
  future: SceneSnapshot[];

  undo: () => void;
  redo: () => void;
  record: (snapshot: SceneSnapshot) => void;
}

export const useHistoryStore = create<HistoryState>((set) => ({
  past: [],
  present: createEmptySnapshot(),
  future: [],

  undo: () => set((state) => {
    if (state.past.length === 0) return state;

    const previous = state.past[state.past.length - 1];
    const newPast = state.past.slice(0, -1);

    return {
      past: newPast,
      present: previous,
      future: [state.present, ...state.future]
    };
  }),

  redo: () => set((state) => {
    if (state.future.length === 0) return state;

    const next = state.future[0];
    const newFuture = state.future.slice(1);

    return {
      past: [...state.past, state.present],
      present: next,
      future: newFuture
    };
  }),

  record: (snapshot) => set((state) => ({
    past: [...state.past, state.present],
    present: snapshot,
    future: [] // Clear redo stack
  }))
}));
```

**Estimated Lines**: 200-300 LOC
**Estimated Time**: 1 week

---

## 4. Migration Strategy

### 4.1 Approach: Clean Rewrite

**Why not port/translate?**
1. GWT-specific patterns don't translate well
2. Opportunity to improve architecture
3. Cleaner, more maintainable code
4. Remove accumulated technical debt

**What to preserve**:
- Core algorithms (expression evaluation, shape rendering)
- User workflows and UX patterns
- File format compatibility (XML/MathML)
- Educational concepts and metaphors

### 4.2 Phased Migration

**Phase 1: Core Engine (Weeks 1-6)**
- Expression evaluation engine
- Object model
- Basic rendering

**Phase 2: UI Components (Weeks 7-12)**
- Canvas view
- Timeline controls
- Object inspector
- Equation editor

**Phase 3: Advanced Features (Weeks 13-16)**
- Serialization (load old files)
- Undo/redo
- Direct manipulation
- Trail drawing

**Phase 4: Polish & Testing (Weeks 17-20)**
- Comprehensive testing
- Performance optimization
- Documentation
- User testing

**Phase 5: Migration Tools (Weeks 21-24)**
- Import old MoPiX files
- Export to various formats
- Migration guide for users

---

## 5. Effort Estimates

### 5.1 Component-by-Component Breakdown

| Component | Lines of Code | Developer Weeks | Priority |
|-----------|---------------|-----------------|----------|
| Expression Engine | 1,500-2,000 | 3-4 | Critical |
| Object Model & State | 500-800 | 1-2 | Critical |
| Animation System | 200-300 | 1 | Critical |
| Canvas Renderer | 500-700 | 2 | Critical |
| Equation Editor UI | 800-1,200 | 3 | High |
| Timeline Controls | 300-400 | 1 | High |
| Object Inspector | 400-600 | 1-2 | High |
| Serialization | 400-600 | 1-2 | High |
| Undo/Redo | 200-300 | 1 | Medium |
| Direct Manipulation | 600-800 | 2-3 | Medium |
| Testing | 1,000-1,500 | 3-4 | High |
| Documentation | - | 2 | Medium |
| **TOTAL** | **~5,000-7,000** | **20-28 weeks** | - |

### 5.2 Team Size Scenarios

**1 Developer**: 6-7 months
**2 Developers**: 4-5 months (realistic with good coordination)
**3 Developers**: 3-4 months (diminishing returns, coordination overhead)

**Recommended**: 2 developers
- Developer A: Core engine, rendering, animation
- Developer B: UI components, state management, serialization

---

## 6. Repository Structure

### 6.1 Project Setup

```bash
# Create new repository
npx create-vite@latest mopix-modern --template react-ts
cd mopix-modern

# Install dependencies
npm install zustand katex mathml-to-latex
npm install -D vitest @testing-library/react @testing-library/jest-dom
npm install -D @types/katex
```

### 6.2 Directory Structure

```
mopix-modern/
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── deploy.yml
├── public/
│   ├── examples/          # Sample MoPiX files
│   └── assets/           # Images, icons
├── src/
│   ├── components/
│   │   ├── Canvas/
│   │   │   ├── CanvasView.tsx
│   │   │   ├── CanvasControls.tsx
│   │   │   └── CanvasView.test.tsx
│   │   ├── EquationEditor/
│   │   │   ├── EquationEditor.tsx
│   │   │   ├── OperatorPalette.tsx
│   │   │   ├── MathMLRenderer.tsx
│   │   │   └── EquationEditor.test.tsx
│   │   ├── Timeline/
│   │   │   ├── TimelineControls.tsx
│   │   │   ├── TimelineSlider.tsx
│   │   │   └── Timeline.test.tsx
│   │   ├── ObjectInspector/
│   │   │   ├── ObjectInspector.tsx
│   │   │   ├── PropertyList.tsx
│   │   │   └── EquationList.tsx
│   │   └── Toolbar/
│   │       ├── Toolbar.tsx
│   │       └── MenuBar.tsx
│   ├── engine/
│   │   ├── expression/
│   │   │   ├── Expression.ts
│   │   │   ├── operators/
│   │   │   │   ├── BinaryOperators.ts
│   │   │   │   ├── UnaryOperators.ts
│   │   │   │   └── LogicalOperators.ts
│   │   │   ├── ExpressionParser.ts
│   │   │   └── Expression.test.ts
│   │   ├── animation/
│   │   │   ├── AnimationController.ts
│   │   │   └── AnimationController.test.ts
│   │   ├── rendering/
│   │   │   ├── CanvasRenderer.ts
│   │   │   ├── ShapeRenderer.ts
│   │   │   └── CanvasRenderer.test.ts
│   │   └── geometry/
│   │       ├── shapes.ts
│   │       ├── transformations.ts
│   │       └── perimeter.ts
│   ├── store/
│   │   ├── sceneStore.ts
│   │   ├── historyStore.ts
│   │   ├── uiStore.ts
│   │   └── store.test.ts
│   ├── models/
│   │   ├── MoPiXObject.ts
│   │   ├── Expression.ts
│   │   ├── Equation.ts
│   │   └── types.ts
│   ├── utils/
│   │   ├── serialization.ts
│   │   ├── mathml.ts
│   │   ├── colors.ts
│   │   ├── validation.ts
│   │   └── serialization.test.ts
│   ├── App.tsx
│   ├── main.tsx
│   └── vite-env.d.ts
├── docs/
│   ├── API.md
│   ├── ARCHITECTURE.md
│   ├── MIGRATION.md
│   └── USER_GUIDE.md
├── .gitignore
├── package.json
├── tsconfig.json
├── vite.config.ts
├── vitest.config.ts
├── README.md
└── LICENSE
```

### 6.3 Package.json

```json
{
  "name": "mopix-modern",
  "version": "3.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage",
    "lint": "eslint src --ext ts,tsx",
    "format": "prettier --write \"src/**/*.{ts,tsx}\""
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "zustand": "^4.4.0",
    "katex": "^0.16.9",
    "mathml-to-latex": "^1.3.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "@types/katex": "^0.16.0",
    "@typescript-eslint/eslint-plugin": "^6.0.0",
    "@typescript-eslint/parser": "^6.0.0",
    "@vitejs/plugin-react": "^4.0.0",
    "@vitest/ui": "^0.34.0",
    "@testing-library/react": "^14.0.0",
    "@testing-library/jest-dom": "^6.0.0",
    "eslint": "^8.45.0",
    "prettier": "^3.0.0",
    "typescript": "^5.0.0",
    "vite": "^4.4.0",
    "vitest": "^0.34.0"
  }
}
```

---

## 7. Implementation Roadmap

### Month 1: Foundation

**Week 1-2: Project Setup**
- [ ] Initialize repository
- [ ] Set up build system (Vite)
- [ ] Configure TypeScript
- [ ] Set up testing (Vitest)
- [ ] Create basic project structure
- [ ] Set up CI/CD pipeline

**Week 3-4: Expression Engine**
- [ ] Implement Expression base class
- [ ] Implement all operator classes
- [ ] Build ExpressionParser (MathML → Expression tree)
- [ ] Write comprehensive unit tests
- [ ] Benchmark performance

### Month 2: Core Engine

**Week 5-6: Object Model & State**
- [ ] Define TypeScript interfaces
- [ ] Implement Zustand stores
- [ ] Build object update system
- [ ] Implement caching strategy
- [ ] Write state management tests

**Week 7-8: Rendering & Animation**
- [ ] Build CanvasRenderer
- [ ] Implement shape rendering
- [ ] Create AnimationController
- [ ] Implement trail drawing
- [ ] Performance optimization

### Month 3: UI Components

**Week 9-10: Canvas View**
- [ ] Build Canvas React component
- [ ] Implement mouse interaction
- [ ] Add zoom/pan controls
- [ ] Selection system
- [ ] Visual feedback

**Week 11-12: Timeline & Controls**
- [ ] Build timeline component
- [ ] Play/pause/step buttons
- [ ] Time slider
- [ ] Frame display
- [ ] Keyboard shortcuts

### Month 4: Advanced Features

**Week 13-14: Equation Editor**
- [ ] Operator palette UI
- [ ] Expression builder
- [ ] MathML rendering (KaTeX)
- [ ] Variable/object selection
- [ ] Apply equation logic

**Week 15-16: Object Inspector & Tools**
- [ ] Object property inspector
- [ ] Equation list view
- [ ] Edit/delete equations
- [ ] Object toolbar
- [ ] Context menus

### Month 5: Integration & Polish

**Week 17-18: Serialization**
- [ ] XML serialization (MathML)
- [ ] JSON serialization
- [ ] Load old MoPiX files
- [ ] Save/load dialogs
- [ ] File format validation

**Week 19-20: Undo/Redo & History**
- [ ] History store implementation
- [ ] Undo/redo UI
- [ ] Keyboard shortcuts (Ctrl+Z, Ctrl+Y)
- [ ] History limit management
- [ ] Performance optimization

### Month 6: Testing & Documentation

**Week 21-22: Comprehensive Testing**
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests
- [ ] End-to-end tests
- [ ] Performance benchmarks
- [ ] Browser compatibility testing

**Week 23-24: Documentation & Release**
- [ ] API documentation
- [ ] User guide
- [ ] Migration guide (from MoPiX 2)
- [ ] Example projects
- [ ] Release v3.0.0

---

## 8. Testing Strategy

### 8.1 Unit Tests

**Expression Engine Tests**:
```typescript
describe('Expression evaluation', () => {
  it('evaluates constant expressions', () => {
    const expr = new ConstantExpression(42);
    const context = createMockContext();
    expect(expr.evaluate(context)).toBe(42);
  });

  it('evaluates binary operations', () => {
    const expr = new BinaryOperatorExpression(
      '+',
      new ConstantExpression(10),
      new ConstantExpression(5)
    );
    expect(expr.evaluate(createMockContext())).toBe(15);
  });

  it('detects circular dependencies', () => {
    // Create circular reference
    const obj = createMockObject();
    obj.equations.set('x', [
      { attribute: 'x', timeSpec: 't', expression: attrExpr('y', obj) }
    ]);
    obj.equations.set('y', [
      { attribute: 'y', timeSpec: 't', expression: attrExpr('x', obj) }
    ]);

    expect(() => evaluateAttribute(obj, 'x', 0)).toThrow('Circular dependency');
  });
});
```

**Rendering Tests**:
```typescript
describe('CanvasRenderer', () => {
  let canvas: HTMLCanvasElement;
  let renderer: CanvasRenderer;

  beforeEach(() => {
    canvas = document.createElement('canvas');
    renderer = new CanvasRenderer(canvas);
  });

  it('renders a square object', () => {
    const obj: MoPiXObject = {
      id: '1',
      name: 'Square1',
      x: 100,
      y: 100,
      width: 50,
      height: 50,
      rotation: 0,
      red: 255,
      green: 0,
      blue: 0,
      transparency: 100,
      appearance: 'square',
      // ... other properties
    };

    renderer.renderObject(obj);

    // Verify canvas operations (spy on ctx methods)
    expect(renderer.ctx.fillRect).toHaveBeenCalled();
  });
});
```

### 8.2 Integration Tests

```typescript
describe('Object animation', () => {
  it('updates object position over time', () => {
    const store = useSceneStore.getState();

    // Create object with x = t equation
    const obj = createObject({
      name: 'Mover',
      equations: new Map([
        ['x', [{ attribute: 'x', timeSpec: 't', expression: timeVarExpr() }]]
      ])
    });

    store.addObject(obj);

    // Advance time
    store.setTime(0);
    expect(getObjectX(obj, 0)).toBe(0);

    store.setTime(10);
    expect(getObjectX(obj, 10)).toBe(10);

    store.setTime(50);
    expect(getObjectX(obj, 50)).toBe(50);
  });
});
```

### 8.3 E2E Tests (Playwright/Cypress)

```typescript
test('create and animate object', async ({ page }) => {
  await page.goto('http://localhost:5173');

  // Create new object
  await page.click('[data-testid="add-object"]');
  await page.fill('[data-testid="object-name"]', 'Ball');

  // Add equation: x = t
  await page.click('[data-testid="add-equation"]');
  await page.selectOption('[data-testid="attribute"]', 'x');
  // Build expression...
  await page.click('[data-testid="apply-equation"]');

  // Play animation
  await page.click('[data-testid="play-button"]');

  // Verify object moves
  await page.waitForTimeout(1000);
  const position = await page.locator('[data-testid="Ball"]').boundingBox();
  expect(position?.x).toBeGreaterThan(0);
});
```

**Coverage Goals**:
- Unit tests: 80%+ coverage
- Integration tests: Key workflows
- E2E tests: Critical user paths

---

## 9. Deployment Options

### 9.1 Static Hosting (Recommended)

**Platforms**:
- **Vercel** (recommended): Zero-config, preview deployments
- **Netlify**: Similar to Vercel, good CI/CD
- **GitHub Pages**: Free, simple
- **Cloudflare Pages**: Fast, global CDN

**Benefits**:
- No server required (pure client-side)
- Cheap/free hosting
- Auto-scaling
- CDN distribution
- Simple deployment

**Setup** (Vercel):
```bash
npm install -g vercel
vercel login
vercel
```

### 9.2 Containerized Deployment

**Docker**:
```dockerfile
# Dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Deploy to**:
- Cloud Run (Google)
- ECS Fargate (AWS)
- Azure Container Apps
- DigitalOcean App Platform

### 9.3 Persistence Options

**Since this is client-side only**:

**Option 1: Browser Storage** (Simplest)
- localStorage for small files
- IndexedDB for larger projects
- No backend required

**Option 2: Backend API** (For sharing)
- Firebase/Supabase for auth + storage
- AWS S3 + Lambda for serverless
- Traditional REST API

**Option 3: Hybrid**
- Local storage by default
- Optional cloud sync for sharing
- Export/import files

---

## 10. Risk Assessment

### 10.1 Technical Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Expression engine bugs | Medium | High | Comprehensive unit tests, reference old code |
| Performance issues | Medium | Medium | Benchmark early, optimize rendering |
| Browser compatibility | Low | Medium | Test on all major browsers, polyfills |
| Memory leaks in animation | Medium | Medium | Proper cleanup, profiling |
| MathML parsing errors | Medium | High | Extensive test cases, error handling |

### 10.2 Project Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Scope creep | High | High | Strict MVP definition, phase gates |
| Timeline overrun | Medium | Medium | Buffer time, prioritize ruthlessly |
| Incomplete migration | Medium | High | Focus on core features first |
| User adoption | Medium | High | Migration guide, import old files |
| Lack of testing | Medium | High | TDD approach, CI enforcement |

### 10.3 Success Criteria

**MVP Must-Haves**:
- [ ] Create objects
- [ ] Add equations to properties
- [ ] Animate objects over time
- [ ] Save/load projects
- [ ] Import old MoPiX 2 files

**Nice-to-Haves** (Post-MVP):
- Advanced equation editor
- Collaborative editing
- More shape types
- Export to video
- Mobile support

---

## 11. Advantages of Modern Stack

### 11.1 Technical Benefits

**vs GWT 1.7.0**:
- **Modern JavaScript**: Latest ECMAScript features
- **Fast builds**: Vite (seconds) vs GWT compiler (minutes)
- **Hot reload**: Instant feedback during development
- **Smaller bundles**: ~200KB vs 1-2MB GWT compiled
- **Better debugging**: Source maps, React DevTools
- **Rich ecosystem**: NPM has 2M+ packages

**vs No Framework**:
- **Type safety**: Catch errors at compile time
- **Component reusability**: DRY principle
- **State management**: Predictable, testable
- **Testing tools**: Mature ecosystem

### 11.2 Developer Experience

- **Fast iteration**: See changes instantly
- **Great tooling**: VSCode, TypeScript, ESLint
- **Easy onboarding**: Standard React patterns
- **Active community**: Stack Overflow, Discord

### 11.3 Performance

- **Lazy loading**: Load code on demand
- **Tree shaking**: Remove unused code
- **Optimized rendering**: Virtual DOM diffing
- **Web workers**: Offload computation

### 11.4 Maintainability

- **Clear separation**: UI vs logic vs state
- **Testable**: Mock dependencies easily
- **Documented**: TypeScript is self-documenting
- **Standard patterns**: Well-known architecture

---

## 12. Comparison: Upgrade vs Rewrite

| Aspect | GWT Upgrade | Modern Rewrite |
|--------|-------------|----------------|
| **Effort** | 6-9 months | 4-6 months |
| **Risk** | High (breaking changes) | Medium (new code) |
| **Code quality** | Inherits debt | Clean start |
| **Performance** | Similar | Better |
| **Bundle size** | 1-2MB | 200-500KB |
| **Developer experience** | Poor | Excellent |
| **Hiring** | Difficult (GWT rare) | Easy (React common) |
| **Long-term** | Still legacy | Modern, supported |
| **Recommendation** | ❌ Not recommended | ✅ Recommended |

---

## 13. Next Steps

### Immediate Actions

1. **Create new repository**:
   ```bash
   npx create-vite@latest mopix-modern --template react-ts
   cd mopix-modern
   git init
   git remote add origin https://github.com/ToonTalk/mopix-modern
   ```

2. **Set up project**:
   ```bash
   npm install zustand katex mathml-to-latex
   npm install -D vitest @testing-library/react
   ```

3. **Create initial structure**:
   ```bash
   mkdir -p src/{components,engine,store,models,utils}
   mkdir -p src/engine/{expression,animation,rendering,geometry}
   ```

4. **Start with core engine**:
   - Implement `Expression` base class
   - Add unit tests
   - Build up from there

5. **Document as you go**:
   - API documentation
   - Architecture decisions
   - User guide

---

## Conclusion

Creating a modern web version of MoPiX 2 is **highly recommended** over upgrading the legacy GWT codebase. The effort is comparable (4-6 months vs 6-9 months), but the result will be:

- **Cleaner codebase** with better architecture
- **Better performance** with smaller bundle size
- **Modern developer experience** with fast builds and great tools
- **Easier maintenance** with TypeScript and standard patterns
- **Future-proof** with active framework and ecosystem

The core algorithms and concepts from MoPiX 2 can be preserved while building on modern foundations that will be maintainable for years to come.

**Recommended First Step**: Create a proof-of-concept with the expression engine and basic rendering to validate the approach (2-3 weeks).

---

## Appendix: Resources

### Learning Resources
- React: https://react.dev
- TypeScript: https://www.typescriptlang.org/docs/
- Zustand: https://github.com/pmndrs/zustand
- Canvas API: https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API
- KaTeX: https://katex.org

### Example Projects
- Desmos (graphing calculator): Similar expression evaluation
- GeoGebra: Similar educational math tool
- Processing.js: Visual programming environment
- Scratch: Block-based programming (different approach)

### Community
- r/reactjs
- React Discord
- TypeScript Discord
- Stack Overflow

---

**Document Version**: 1.0
**Last Updated**: 2025-10-30
**Author**: Claude Code Analysis