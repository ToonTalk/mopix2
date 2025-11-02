/**
 * Sample Models from Original MoPiX
 *
 * This file contains recreations of the sample models from the original MoPiX application.
 */

import { MoPiXObject } from '../types';
import {
  ConstantExpression,
  VariableExpression,
  BinaryOperatorExpression,
  UnaryOperatorExpression
} from '../engine/Expression';

export interface SampleModel {
  id: string;
  name: string;
  description: string;
  objects: Map<string, MoPiXObject>;
}

export const sampleModels: SampleModel[] = [
  {
    id: 'rotating-rectangle',
    name: 'Rotating Rectangle',
    description: 'A simple model of one rectangle that rotates.',
    objects: new Map([
      ['rect', {
        id: 'rect',
        name: 'Rotating Rectangle',
        x: 400,
        y: 300,
        width: 100,
        height: 60,
        rotation: 0,
        red: 100,
        green: 150,
        blue: 255,
        transparency: 100,
        appearance: 'square',
        equations: new Map([
          ['rotation', {
            attribute: 'rotation',
            expression: new BinaryOperatorExpression(
              '*',
              new VariableExpression('t'),
              new ConstantExpression(3)
            ),
            cache: new Map()
          }]
        ]),
        info: 'A simple rotating rectangle.\n\nEquation: rotation = t × 3\n\nThis demonstrates basic rotation animation. The rectangle spins continuously at 3 degrees per frame.'
      }]
    ])
  },
  {
    id: 'growing-rectangle',
    name: 'Growing Rectangle',
    description: 'A rectangle whose width and height increase.',
    objects: new Map([
      ['rect', {
        id: 'rect',
        name: 'Growing Rectangle',
        x: 340.7,
        y: 212.5,
        width: 10,
        height: 10,
        rotation: 0,
        red: 255,
        green: 100,
        blue: 100,
        transparency: 100,
        appearance: 'square',
        equations: new Map([
          ['width', {
            attribute: 'width',
            expression: new BinaryOperatorExpression(
              '*',
              new VariableExpression('t'),
              new ConstantExpression(2)
            ),
            cache: new Map()
          }],
          ['height', {
            attribute: 'height',
            expression: new VariableExpression('t'),
            cache: new Map()
          }]
        ]),
        info: 'A rectangle that grows over time.\n\nEquations:\nwidth = t × 2\nheight = t\n\nThe width grows twice as fast as the height, creating a rectangle that expands non-uniformly.'
      }]
    ])
  },
  {
    id: 'rotating-ellipse',
    name: 'Rotating Ellipse',
    description: 'A semi-transparent ellipse that rotates.',
    objects: new Map([
      ['ellipse', {
        id: 'ellipse',
        name: 'Rotating Ellipse',
        x: 379.75,
        y: 321.5,
        width: 200,
        height: 100,
        rotation: 0,
        red: 100,
        green: 100,
        blue: 100,
        transparency: 50,
        appearance: 'ellipse',
        equations: new Map([
          ['rotation', {
            attribute: 'rotation',
            expression: new BinaryOperatorExpression(
              '*',
              new VariableExpression('t'),
              new ConstantExpression(3)
            ),
            cache: new Map()
          }]
        ]),
        info: 'A semi-transparent rotating ellipse.\n\nEquation: rotation = t × 3\n\nThis ellipse rotates at 3 degrees per frame. The 50% transparency allows you to see through it.'
      }]
    ])
  },
  {
    id: 'three-rotating-rectangles',
    name: 'Three Rotating Rectangles',
    description: 'Three rectangles rotating at different speeds that are also changing colour.',
    objects: new Map([
      ['rect1', {
        id: 'rect1',
        name: 'Fast Spinner',
        x: 250,
        y: 300,
        width: 80,
        height: 50,
        rotation: 0,
        red: 255,
        green: 100,
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
          }],
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
                    new ConstantExpression(5)
                  )
                ),
                new ConstantExpression(127)
              )
            ),
            cache: new Map()
          }]
        ]),
        info: 'Fast rotating rectangle with color change.\n\nEquations:\nrotation = t × 5\nred = 128 + sin(t × 5) × 127\n\nRotates quickly while the red channel oscillates.'
      }],
      ['rect2', {
        id: 'rect2',
        name: 'Medium Spinner',
        x: 400,
        y: 300,
        width: 80,
        height: 50,
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
              new ConstantExpression(3)
            ),
            cache: new Map()
          }],
          ['greenColour', {
            attribute: 'greenColour',
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
                    new ConstantExpression(3)
                  )
                ),
                new ConstantExpression(127)
              )
            ),
            cache: new Map()
          }]
        ]),
        info: 'Medium rotating rectangle with color change.\n\nEquations:\nrotation = t × 3\ngreen = 128 + sin(t × 3) × 127\n\nRotates at medium speed while the green channel oscillates.'
      }],
      ['rect3', {
        id: 'rect3',
        name: 'Slow Spinner',
        x: 550,
        y: 300,
        width: 80,
        height: 50,
        rotation: 0,
        red: 100,
        green: 100,
        blue: 255,
        transparency: 100,
        appearance: 'square',
        equations: new Map([
          ['rotation', {
            attribute: 'rotation',
            expression: new BinaryOperatorExpression(
              '*',
              new VariableExpression('t'),
              new ConstantExpression(1.5)
            ),
            cache: new Map()
          }],
          ['blueColour', {
            attribute: 'blueColour',
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
                    new ConstantExpression(1.5)
                  )
                ),
                new ConstantExpression(127)
              )
            ),
            cache: new Map()
          }]
        ]),
        info: 'Slow rotating rectangle with color change.\n\nEquations:\nrotation = t × 1.5\nblue = 128 + sin(t × 1.5) × 127\n\nRotates slowly while the blue channel oscillates.'
      }]
    ])
  },
  {
    id: 'bouncing-balls-average',
    name: 'Average of Bouncing Balls',
    description: 'Two bouncing balls and a trail of their average position.',
    objects: new Map([
      ['ball1', {
        id: 'ball1',
        name: 'Ball 1',
        x: 300,
        y: 300,
        width: 40,
        height: 40,
        rotation: 0,
        red: 255,
        green: 100,
        blue: 100,
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
                  'abs',
                  new UnaryOperatorExpression(
                    'sin',
                    new BinaryOperatorExpression(
                      '*',
                      new VariableExpression('t'),
                      new ConstantExpression(8)
                    )
                  )
                ),
                new ConstantExpression(150)
              ),
              new ConstantExpression(150)
            ),
            cache: new Map()
          }]
        ]),
        info: 'First bouncing ball.\n\nEquation: y = abs(sin(t × 8)) × 150 + 150\n\nThe abs() function makes the ball bounce up from the bottom, creating a more realistic bounce effect.'
      }],
      ['ball2', {
        id: 'ball2',
        name: 'Ball 2',
        x: 500,
        y: 300,
        width: 40,
        height: 40,
        rotation: 0,
        red: 100,
        green: 100,
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
                  'abs',
                  new UnaryOperatorExpression(
                    'sin',
                    new BinaryOperatorExpression(
                      '*',
                      new BinaryOperatorExpression(
                        '+',
                        new VariableExpression('t'),
                        new ConstantExpression(5)
                      ),
                      new ConstantExpression(8)
                    )
                  )
                ),
                new ConstantExpression(150)
              ),
              new ConstantExpression(150)
            ),
            cache: new Map()
          }]
        ]),
        info: 'Second bouncing ball (phase shifted).\n\nEquation: y = abs(sin((t + 5) × 8)) × 150 + 150\n\nThis ball bounces out of sync with the first one due to the +5 phase shift.'
      }],
      ['average', {
        id: 'average',
        name: 'Average Position',
        x: 400,
        y: 300,
        width: 20,
        height: 20,
        rotation: 0,
        red: 100,
        green: 255,
        blue: 100,
        transparency: 60,
        appearance: 'circle',
        equations: new Map([
          ['y', {
            attribute: 'y',
            expression: new BinaryOperatorExpression(
              '/',
              new BinaryOperatorExpression(
                '+',
                new BinaryOperatorExpression(
                  '+',
                  new BinaryOperatorExpression(
                    '*',
                    new UnaryOperatorExpression(
                      'abs',
                      new UnaryOperatorExpression(
                        'sin',
                        new BinaryOperatorExpression(
                          '*',
                          new VariableExpression('t'),
                          new ConstantExpression(8)
                        )
                      )
                    ),
                    new ConstantExpression(150)
                  ),
                  new ConstantExpression(150)
                ),
                new BinaryOperatorExpression(
                  '+',
                  new BinaryOperatorExpression(
                    '*',
                    new UnaryOperatorExpression(
                      'abs',
                      new UnaryOperatorExpression(
                        'sin',
                        new BinaryOperatorExpression(
                          '*',
                          new BinaryOperatorExpression(
                            '+',
                            new VariableExpression('t'),
                            new ConstantExpression(5)
                          ),
                          new ConstantExpression(8)
                        )
                      )
                    ),
                    new ConstantExpression(150)
                  ),
                  new ConstantExpression(150)
                )
              ),
              new ConstantExpression(2)
            ),
            cache: new Map()
          }]
        ]),
        info: 'Shows the average Y position of both balls.\n\nEquation: y = (ball1.y + ball2.y) / 2\n\nThis small green circle follows the mathematical average of the two bouncing balls\' vertical positions.'
      }]
    ])
  },
  {
    id: 'rectangle-three-cycles',
    name: 'Rectangle with Three Cycles',
    description: 'A rectangle changing in three different ways with different cycles.',
    objects: new Map([
      ['rect', {
        id: 'rect',
        name: 'Multi-cycling Rectangle',
        x: 227.75,
        y: 241.5,
        width: 100,
        height: 200,
        rotation: 0,
        red: 128,
        green: 128,
        blue: 128,
        transparency: 100,
        appearance: 'square',
        equations: new Map([
          ['width', {
            attribute: 'width',
            expression: new BinaryOperatorExpression(
              '+',
              new BinaryOperatorExpression(
                '*',
                new UnaryOperatorExpression(
                  'sin',
                  new VariableExpression('t')
                ),
                new ConstantExpression(50)
              ),
              new ConstantExpression(50)
            ),
            cache: new Map()
          }],
          ['greenColour', {
            attribute: 'greenColour',
            expression: new BinaryOperatorExpression(
              '+',
              new BinaryOperatorExpression(
                '*',
                new UnaryOperatorExpression(
                  'sin',
                  new BinaryOperatorExpression(
                    '*',
                    new ConstantExpression(2),
                    new VariableExpression('t')
                  )
                ),
                new ConstantExpression(50)
              ),
              new ConstantExpression(50)
            ),
            cache: new Map()
          }],
          ['rotation', {
            attribute: 'rotation',
            expression: new BinaryOperatorExpression(
              '+',
              new BinaryOperatorExpression(
                '*',
                new UnaryOperatorExpression(
                  'sin',
                  new BinaryOperatorExpression(
                    '*',
                    new ConstantExpression(3),
                    new VariableExpression('t')
                  )
                ),
                new ConstantExpression(50)
              ),
              new ConstantExpression(50)
            ),
            cache: new Map()
          }]
        ]),
        info: 'A rectangle with three independent cycles.\n\nEquations:\nwidth = 50 × sin(t) + 50\ngreen = 50 × sin(2t) + 50\nrotation = 50 × sin(3t) + 50\n\nThree different periodic behaviors with sine waves at different frequencies.'
      }]
    ])
  },
  {
    id: 'falling-upward',
    name: 'Falling Upward',
    description: 'An exercise in negative gravity.',
    objects: new Map([
      ['ball', {
        id: 'ball',
        name: 'Anti-gravity Ball',
        x: 305.75,
        y: 500,
        width: 40,
        height: 40,
        rotation: 0,
        red: 255,
        green: 200,
        blue: 100,
        transparency: 100,
        appearance: 'circle',
        equations: new Map([
          ['y', {
            attribute: 'y',
            expression: new BinaryOperatorExpression(
              '-',
              new ConstantExpression(500),
              new BinaryOperatorExpression(
                '*',
                new ConstantExpression(0.049),
                new BinaryOperatorExpression(
                  '*',
                  new VariableExpression('t'),
                  new VariableExpression('t')
                )
              )
            ),
            cache: new Map()
          }]
        ]),
        info: 'A ball that falls upward!\n\nEquation: y = 500 - 0.049 × t²\n\nThis uses the physics equation for motion under constant acceleration. With positive upward acceleration (Ay = 0.098), the ball accelerates upward, moving faster as time goes on.'
      }]
    ])
  }
];
