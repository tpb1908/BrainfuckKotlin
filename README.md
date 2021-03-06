# Brainfuck [![Build Status](https://travis-ci.org/tpb1908/BrainfuckKotlin.svg?branch=master)](https://travis-ci.org/tpb1908/BrainfuckKotlin)

A Brainfuck interpreter for Android, written in Kotlin.

## Features

- Default programs
	- Hello world
	- ROT 13 (Shifts input values by 13)
	- Fibonacci (Outputs the fibonacci sequence for values below 100)
	- Squares (Outputs the squares of numbers from 0 to 100)
	- Factorial (Outputs arbitrarily many factorials)
	- QWERTY to DVORAK (Converts QWERTY keycodes to DVORAK)
- Support for
	- Memory size
	- Maximum and minimum values
	- Error, wrap, or expand on pointer overflow
	- Error or wrap on pointer underflow
	- Error, wrap, or cap on value overflow
	- Error, wrap, or cap on value underflow
	- Input from array or keyboard input while running
	- Breakpoints 
	- Stepping
	- Debug information
- Light and dark themes	

| | |
| --- | --- |
| ![Main](./screenshots/home.png) | ![Main_dark](./screenshots/home_dark.png) |
| ![Editor](./screenshots/editor.png) | ![Runner](./screenshots/runner.png) |
