package com.nick.wood.graphics_library.utils;

import com.nick.wood.maths.objects.vector.Vec2i;
import com.nick.wood.maths.objects.vector.Vecd;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class RecursiveBackTracker {

	private final int width;
	private final int height;
	private final Stack<Cell> visitedStack;
	private final ArrayList<Cell> visited;
	private final Random random;
	private final Vec2i[] directions = new Vec2i[]{
			new Vec2i(0, -1), // up
			new Vec2i(0, 1), // down
			new Vec2i(-1, 0), // west
			new Vec2i(1, 0), // east
	};

	public RecursiveBackTracker(int width, int height) {

		this.width = width;
		this.height = height;

		this.visitedStack = new Stack<>();
		this.visited = new ArrayList<>();
		this.random = new Random();

		int startX = 0;
		int startY = 0;

		Vec2i startPosition = new Vec2i(startX, startY);
		Cell cell = new Cell(startPosition);

		// choose a square at random
		visited.add(cell);
		visitedStack.push(cell);
		checkSquare(cell);


	}

	private void checkSquare(Cell startCell) {

		if (visited.size() == (width * height)) {
			return;
		}

		ArrayList<Vec2i> nextValidSquares = new ArrayList<>();
		ArrayList<MovementDirection> movementDirections = new ArrayList<>();

		for (int directionIndex = 0; directionIndex < directions.length; directionIndex++) {
			Vec2i direction = directions[directionIndex];

			// get the new positions of the next square given 4 directions they could be
			Vec2i nextSquare = startCell.getPosition().add(direction);

			// check for valid square
			if (nextSquare.getX() >= 0 && nextSquare.getX() < width && nextSquare.getY() >= 0 && nextSquare.getY() < height) {

				// check if its already been visited
				// check if already visted
				boolean found = false;
				for (Cell cell : visited) {
					if (cell.getPosition().equals(nextSquare)) {
						found = true;
						break;
					}
				}
				if (!found) {

					nextValidSquares.add(nextSquare);
					movementDirections.add(MovementDirection.values()[directionIndex]);

				}
			}
		}

		if (nextValidSquares.isEmpty()) {

			// find last space with free neighbours
			checkSquare(findLastFree(visitedStack));

		} else {

			int i = random.nextInt(nextValidSquares.size());
			Vec2i nextSquare = nextValidSquares.get(i);

			if (movementDirections.get(i).equals(MovementDirection.SOUTH)) {
				startCell.setSouthPath(true);
			} else if (movementDirections.get(i).equals(MovementDirection.EAST)) {
				startCell.setEastPath(true);
			} else if (movementDirections.get(i).equals(MovementDirection.NORTH)) {
				startCell.setNorthPath(true);
			} else if (movementDirections.get(i).equals(MovementDirection.WEST)) {
				startCell.setWestPath(true);
			}

			// check if already visted
			boolean found = false;
			Cell nextCell = null;
			for (Cell cell : visited) {
				if (cell.getPosition().equals(nextSquare)) {
					found = true;
					nextCell = cell;
					break;
				}
			}

			if (!found) {
				nextCell = new Cell(nextSquare);
				visited.add(nextCell);
				visitedStack.push(nextCell);
			}
			checkSquare(nextCell);

		}
	}

	private Cell findLastFree(Stack<Cell> visitedStack) {

		ArrayList<Vec2i> nextValidSquares = new ArrayList<>();
		while (true) {
			Cell pop = visitedStack.pop();
			for (int directionIndex = 0; directionIndex < directions.length; directionIndex++) {
				Vec2i direction = directions[directionIndex];

				// get the new positions of the next square given 4 directions they could be
				Vec2i nextSquare = pop.getPosition().add(direction);

				// check for valid square
				if (nextSquare.getX() >= 0 && nextSquare.getX() < width && nextSquare.getY() >= 0 && nextSquare.getY() < height) {

					// check if its already been visited
					// check if already visted
					boolean found = false;
					for (Cell cell : visited) {
						if (cell.getPosition().equals(nextSquare)) {
							found = true;
							break;
						}
					}
					if (!found) {

						nextValidSquares.add(nextSquare);

					}
				}
			}

			if (!nextValidSquares.isEmpty()) {
				return pop;
			}
		}
	}

	public static void main(String[] args) {
		RecursiveBackTracker recursiveBackTracker = new RecursiveBackTracker(3, 3);

		System.out.println();
	}

	public ArrayList<Cell> getVisited() {
		return visited;
	}
}
