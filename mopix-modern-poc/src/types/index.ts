/**
 * Core type definitions for MoPiX Modern
 */

export interface MoPiXObject {
  id: string;
  name: string;

  // Position and size
  x: number;
  y: number;
  width: number;
  height: number;
  rotation: number; // degrees

  // Color (0-255)
  red: number;
  green: number;
  blue: number;
  transparency: number; // 0-100 (percentage)

  // Shape type
  appearance: 'square' | 'circle';

  // Equations defining dynamic behavior
  equations: Map<string, Equation>;
}

export interface Equation {
  attribute: string; // e.g., 'x', 'width', 'rotation'
  expression: Expression;
  cache: Map<number, number>; // time -> evaluated value
}

export interface EvaluationContext {
  object: MoPiXObject;
  time: number;
  allObjects: Map<string, MoPiXObject>;
}

/**
 * Expression base class - all expressions implement this
 */
export abstract class Expression {
  abstract evaluate(context: EvaluationContext): number;
  abstract toString(): string;
}
