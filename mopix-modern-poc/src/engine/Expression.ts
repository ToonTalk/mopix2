/**
 * Expression Evaluation Engine
 *
 * Supports arithmetic, trigonometric, logical operators
 * Compatible with MoPiX 2 expression system
 */

import { Expression, EvaluationContext, MoPiXObject, Equation } from '../types';

/**
 * Constant numeric value
 */
export class ConstantExpression extends Expression {
  constructor(private value: number) {
    super();
  }

  evaluate(_context: EvaluationContext): number {
    return this.value;
  }

  toString(): string {
    return this.value.toString();
  }
}

/**
 * Variable reference - special case for 't' (time)
 */
export class VariableExpression extends Expression {
  constructor(private name: string) {
    super();
  }

  evaluate(context: EvaluationContext): number {
    if (this.name === 't') {
      return context.time;
    }
    // Could look up constants/variables here
    return 0;
  }

  toString(): string {
    return this.name;
  }
}

/**
 * Attribute access - get property value from object
 * e.g., accessing 'x' property
 */
export class AttributeExpression extends Expression {
  constructor(
    private attribute: string,
    private objectName: string = 'ME'
  ) {
    super();
  }

  evaluate(context: EvaluationContext): number {
    const obj = this.objectName === 'ME'
      ? context.object
      : context.allObjects.get(this.objectName);

    if (!obj) return 0;

    // Get the current value of the attribute
    switch (this.attribute) {
      case 'x': return obj.x;
      case 'y': return obj.y;
      case 'width': return obj.width;
      case 'height': return obj.height;
      case 'rotation': return obj.rotation;
      case 'redColour': return obj.red;
      case 'greenColour': return obj.green;
      case 'blueColour': return obj.blue;
      case 'transparency': return obj.transparency;
      default: return 0;
    }
  }

  toString(): string {
    return `${this.attribute}(${this.objectName})`;
  }
}

/**
 * Binary operators: +, -, *, /, ^
 */
export class BinaryOperatorExpression extends Expression {
  constructor(
    private operator: '+' | '-' | '*' | '/' | '^' | 'and' | 'or' |
                 '==' | '!=' | '<' | '>' | '<=' | '>=',
    private left: Expression,
    private right: Expression
  ) {
    super();
  }

  evaluate(context: EvaluationContext): number {
    const leftValue = this.left.evaluate(context);
    const rightValue = this.right.evaluate(context);

    switch (this.operator) {
      // Arithmetic
      case '+': return leftValue + rightValue;
      case '-': return leftValue - rightValue;
      case '*': return leftValue * rightValue;
      case '/': return rightValue !== 0 ? leftValue / rightValue : 0;
      case '^': return Math.pow(leftValue, rightValue);

      // Logical (return 1 for true, 0 for false)
      case 'and': return (leftValue !== 0 && rightValue !== 0) ? 1 : 0;
      case 'or': return (leftValue !== 0 || rightValue !== 0) ? 1 : 0;

      // Comparison
      case '==': return leftValue === rightValue ? 1 : 0;
      case '!=': return leftValue !== rightValue ? 1 : 0;
      case '<': return leftValue < rightValue ? 1 : 0;
      case '>': return leftValue > rightValue ? 1 : 0;
      case '<=': return leftValue <= rightValue ? 1 : 0;
      case '>=': return leftValue >= rightValue ? 1 : 0;

      default: return 0;
    }
  }

  toString(): string {
    return `(${this.left.toString()} ${this.operator} ${this.right.toString()})`;
  }
}

/**
 * Unary operators: -, abs, sin, cos, tan, etc.
 */
export class UnaryOperatorExpression extends Expression {
  constructor(
    private operator: '-' | 'abs' | 'sin' | 'cos' | 'tan' | 'asin' | 'acos' | 'atan' | 'not',
    private operand: Expression
  ) {
    super();
  }

  evaluate(context: EvaluationContext): number {
    const value = this.operand.evaluate(context);

    switch (this.operator) {
      case '-': return -value;
      case 'abs': return Math.abs(value);

      // Trigonometric (convert degrees to radians)
      case 'sin': return Math.sin((value * Math.PI) / 180);
      case 'cos': return Math.cos((value * Math.PI) / 180);
      case 'tan': return Math.tan((value * Math.PI) / 180);

      // Inverse trig (convert radians to degrees)
      case 'asin': return (Math.asin(value) * 180) / Math.PI;
      case 'acos': return (Math.acos(value) * 180) / Math.PI;
      case 'atan': return (Math.atan(value) * 180) / Math.PI;

      // Logical
      case 'not': return value === 0 ? 1 : 0;

      default: return 0;
    }
  }

  toString(): string {
    return `${this.operator}(${this.operand.toString()})`;
  }
}

/**
 * Helper to evaluate an equation with caching
 */
export function evaluateEquation(
  equation: { expression: Expression; cache: Map<number, number> },
  context: EvaluationContext
): number {
  const time = context.time;

  // Check cache
  if (equation.cache.has(time)) {
    return equation.cache.get(time)!;
  }

  // Evaluate and cache
  const result = equation.expression.evaluate(context);
  equation.cache.set(time, result);

  return result;
}

/**
 * Update object properties based on equations at given time
 */
export function updateObjectAtTime(
  obj: MoPiXObject,
  time: number,
  allObjects: Map<string, MoPiXObject>
): MoPiXObject {
  const context: EvaluationContext = {
    object: obj,
    time,
    allObjects
  };

  // Create updated object
  const updated = { ...obj };

  // Evaluate each equation and update corresponding property
  obj.equations.forEach((equation: Equation, attribute: string) => {
    const value = evaluateEquation(equation, context);

    switch (attribute) {
      case 'x': updated.x = value; break;
      case 'y': updated.y = value; break;
      case 'width': updated.width = value; break;
      case 'height': updated.height = value; break;
      case 'rotation': updated.rotation = value; break;
      case 'redColour': updated.red = Math.round(Math.max(0, Math.min(255, value))); break;
      case 'greenColour': updated.green = Math.round(Math.max(0, Math.min(255, value))); break;
      case 'blueColour': updated.blue = Math.round(Math.max(0, Math.min(255, value))); break;
      case 'transparency': updated.transparency = Math.max(0, Math.min(100, value)); break;
    }
  });

  return updated;
}
