# MoPiX 2 - Programming Using Algebra

An educational visual programming platform that enables students to create animations and interactive models using algebraic equations.

## Overview

MoPiX 2 allows users to:
- Create visual objects with algebraic equations defining their properties (position, size, color, rotation, etc.)
- Build temporal/time-based animations with frame-by-frame control
- Edit mathematical expressions using an interactive equation editor
- Save and load models
- Support for multiple languages (English, Greek, Portuguese)

## Technology Stack

- **Frontend**: Google Web Toolkit (GWT) 1.7.0
- **Backend**: Java with Google App Engine
- **Graphics**: HTML5 Canvas
- **Math**: MathML for mathematical expression rendering
- **Persistence**: DataNucleus JPA with JDO

## Project Status

This is a legacy educational project originally developed for Google Code and migrated to Git. The codebase uses technologies from 2010 and requires modernization for current production use.

## Project Structure

```
mopix2/
├── LICENSE                    # BSD 3-Clause License
├── README.md                  # This file
├── IMPROVEMENTS.md           # Comprehensive improvement suggestions
└── MoPiX/                    # Main project directory
    ├── src/                  # Java source code (~10,000 LOC)
    │   └── uk/ac/lkl/
    │       ├── client/       # GWT client-side code
    │       └── server/       # Server-side servlets
    └── war/                  # Web application resources
        └── WEB-INF/          # Configuration and libraries
```

## Key Features

- **Expression Engine**: Supports 29 mathematical operators including arithmetic, trigonometric, and logical operations
- **Visual Object System**: 57 specialized expression types for different visual properties
- **Temporal Navigation**: Frame-by-frame animation control with undo/redo support
- **Equation Editor**: Interactive UI for creating and editing mathematical expressions
- **Model Persistence**: XML/MathML-based save/load functionality

## Getting Started

**Note**: This project requires Eclipse IDE with GWT and Google App Engine plugins installed.

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd mopix2
   ```

2. Import into Eclipse as existing project

3. Ensure GWT SDK and App Engine SDK are configured

4. Run using Eclipse launch configurations

## Documentation

- See [IMPROVEMENTS.md](IMPROVEMENTS.md) for comprehensive suggestions on modernizing this project
- Original license: BSD 3-Clause (see [LICENSE](LICENSE) file)
- Copyright 2015 by Ken Kahn

## Modernization Needed

This project uses significantly outdated technologies and would benefit from:
- Testing infrastructure (currently has zero tests)
- Modern build system (Maven/Gradle)
- Updated dependencies (security vulnerabilities likely)
- Framework migration (GWT 1.7.0 is from 2010)
- Documentation improvements
- CI/CD pipeline

See [IMPROVEMENTS.md](IMPROVEMENTS.md) for detailed recommendations.

## Contributing

This appears to be an archived educational project. If you're interested in modernizing or maintaining this codebase, please refer to the improvement suggestions document.

## License

BSD 3-Clause License - see LICENSE file for details.
