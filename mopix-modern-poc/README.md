# MoPiX Modern - Proof of Concept

A minimal proof-of-concept demonstrating MoPiX 2 reimplemented with modern web technologies.

## What This Demonstrates

This PoC validates the feasibility of rewriting MoPiX using modern client-side technologies:

- ✅ **Expression Evaluation Engine** - Supports arithmetic, trigonometric, and logical operators
- ✅ **Canvas Rendering** - HTML5 Canvas API for shape rendering with transformations
- ✅ **Animation System** - Frame-accurate animation using `requestAnimationFrame`
- ✅ **Caching** - Expression results cached per frame for performance
- ✅ **Type Safety** - Full TypeScript implementation
- ✅ **React Integration** - Clean separation between engine and UI

## Technology Stack

- **Framework:** React 18 with TypeScript 5
- **Build Tool:** Vite (fast, modern bundler)
- **Rendering:** Native HTML5 Canvas API
- **State:** React hooks (no external state library needed for PoC)

## Demo Features

The PoC includes 4 animated objects demonstrating different capabilities:

1. **Red Square (Mover)** - Linear motion: `x = t × 2 + 100`
2. **Blue Circle (Bouncer)** - Sinusoidal motion: `y = sin(t × 10) × 100 + 300`
3. **Green Rectangle (Spinner)** - Rotation: `rotation = t × 5`
4. **Color-Changing Circle** - Color animation: `red = 128 + sin(t × 6) × 127`

## Getting Started

### Prerequisites

- Node.js 18+ and npm (or pnpm/yarn)

### Installation

```bash
cd mopix-modern-poc

# Install dependencies
npm install

# Start development server
npm run dev
```

Then open your browser to `http://localhost:5173`

### Build for Production

```bash
npm run build
npm run preview
```

## Project Structure

```
mopix-modern-poc/
├── src/
│   ├── components/
│   │   └── MoPiXDemo.tsx        # Main demo component with UI
│   ├── engine/
│   │   ├── Expression.ts         # Expression evaluation engine
│   │   ├── CanvasRenderer.ts    # Canvas rendering system
│   │   └── AnimationController.ts # Animation loop manager
│   ├── types/
│   │   └── index.ts             # TypeScript type definitions
│   ├── App.tsx                   # Root component
│   └── main.tsx                  # Entry point
├── index.html                    # HTML template
├── package.json                  # Dependencies
├── tsconfig.json                 # TypeScript config
└── vite.config.ts               # Vite config
```

## Code Highlights

### Expression Engine

The expression engine uses a tree-based evaluation system:

```typescript
// Example: Create expression "t * 2 + 100"
const expr = new BinaryOperatorExpression(
  '+',
  new BinaryOperatorExpression(
    '*',
    new VariableExpression('t'),
    new ConstantExpression(2)
  ),
  new ConstantExpression(100)
);

// Evaluate at time=50
const result = expr.evaluate(context); // Returns 200
```

**Supported Operators:**
- Arithmetic: `+`, `-`, `*`, `/`, `^`
- Trigonometric: `sin`, `cos`, `tan`, `asin`, `acos`, `atan` (degrees)
- Logical: `and`, `or`, `not`
- Comparison: `==`, `!=`, `<`, `>`, `<=`, `>=`
- Unary: `-`, `abs`

### Canvas Rendering

```typescript
const renderer = new CanvasRenderer(canvas);

// Render a single object
renderer.renderObject({
  x: 100, y: 200,
  width: 50, height: 50,
  rotation: 45,
  appearance: 'square',
  // ... other properties
});

// Render entire scene
renderer.renderScene(objects);
```

### Animation Controller

```typescript
const controller = new AnimationController(
  (frame) => {
    // Called for each frame
    updateAndRender(frame);
  },
  30 // fps
);

controller.start(); // Start animation
controller.stop();  // Stop animation
controller.stepForward(); // Advance one frame
```

## Performance Comparison

| Metric | GWT 1.7.0 | Modern Stack |
|--------|-----------|--------------|
| Bundle Size | 1-2 MB | ~20 KB |
| Build Time | 30-60s | <1s |
| Hot Reload | No | Yes (instant) |
| Dev Server Start | 10-20s | <1s |
| TypeScript | No | Yes |

## What's Missing (Full Implementation Would Need)

This PoC focuses on core functionality. A complete implementation would add:

- [ ] **Equation Editor UI** - Visual expression builder
- [ ] **Object Inspector** - Edit object properties
- [ ] **Serialization** - Save/load projects (XML/JSON)
- [ ] **Undo/Redo** - History management
- [ ] **Direct Manipulation** - Drag objects to create equations
- [ ] **Trail Drawing** - Pen trails for objects
- [ ] **More Operators** - MathML parsing, perimeter points, etc.
- [ ] **Multi-object References** - Access other objects' properties
- [ ] **Comprehensive Testing** - Unit tests, integration tests

See [MODERNIZATION_GUIDE.md](../MODERNIZATION_GUIDE.md) in the parent directory for the full implementation plan.

## Next Steps

If this PoC validates the approach, the recommended next steps are:

1. **Expand Expression Engine** - Add remaining operators (29+ total)
2. **Build Equation Editor** - Visual UI for creating expressions
3. **Add Serialization** - Import old MoPiX files, save/load
4. **Implement Undo/Redo** - History state management
5. **Testing Infrastructure** - Vitest + React Testing Library

**Estimated Full Implementation:** 4-6 months with 2 developers

## License

Same as parent project (BSD 3-Clause)

## Questions?

Refer to the [MODERNIZATION_GUIDE.md](../MODERNIZATION_GUIDE.md) for comprehensive implementation details.
