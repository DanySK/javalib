/*******************************************************************************
 * Copyright (C) 2009, 2015, Danilo Pianini and contributors
 * listed in the project's build.gradle or pom.xml file.
 *
 * This file is distributed under the terms of the Apache License, version 2.0
 *******************************************************************************/
package org.danilopianini.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.ChildrenIterator;

/**
 * 
 * @author matheusdev
 * @author Danilo Pianini
 * 
 * @param <E>
 */
public final class FlexibleQuadTree<E> implements Serializable {

	private static final long serialVersionUID = -8765593946059102012L;

	/**
	 * Default maximum number of entries per node.
	 */
	public static final int DEFAULT_CAPACITY = 10;
	private final Rectangle2D bounds;
	private final Deque<QuadTreeEntry<E>> elements;
	private final int maxElements;
	private final List<Optional<FlexibleQuadTree<E>>> children = new ArrayList<>(Collections.nCopies(4, Optional.absent()));
	/**
	 * root is NOT consistent everywhere. It is only guaranteed to be consistent
	 * in the entry point node and in the current root.
	 */
	private FlexibleQuadTree<E> root;
	private FlexibleQuadTree<E> parent;
	private boolean childrenCreated;
	
	private static class Rectangle2D implements Serializable {
		
		private static final long serialVersionUID = -7890062202005580979L;
		private final double minx, miny, maxx, maxy;
		
		public Rectangle2D(final double sx, final double sy, final double fx, final double fy) {
			minx = Math.min(sx, fx);
			miny = Math.min(sy, fy);
			maxx = Math.max(sx, fx);
			maxy = Math.max(sy, fy);
		}

		public boolean contains(final double x, final double y) {
			return x >= minx && y >= miny && x < maxx && y < maxy;
		}

		public boolean intersects(final double sx, final double sy, final double fx, final double fy) {
			return fx >= minx && fy >= miny && sx < maxx && sy < maxy;
		}

		public double getCenterX() {
			return (maxx - minx) / 2;
		}

		public double getCenterY() {
			return (maxy - miny) / 2;
		}

		public double getMinY() {
			return miny;
		}

		public double getMaxX() {
			return maxx;
		}

		public double getMaxY() {
			return maxy;
		}

		public double getMinX() {
			return minx;
		}
		
		@Override
		public String toString() {
			return "[" + minx + "," + miny + " - " + maxx + "," + maxy + "]";
		}

	}

	private static class QuadTreeEntry<E> implements Serializable {
		private static final long serialVersionUID = 9021533648086596986L;
		private final E element;
		private final double x, y;

		public QuadTreeEntry(final E el, final double xp, final double yp) {
			element = el;
			x = xp;
			y = yp;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof QuadTreeEntry<?>) {
				final QuadTreeEntry<?> e = (QuadTreeEntry<?>) obj;
				if (samePosition(e)) {
					return element == e.element || element != null && element.equals(e.element);
				}
				return false;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return HashUtils.hash32(x, y, element);
		}
		
		public boolean samePosition(final QuadTreeEntry<?> target) {
			return x == target.x && y == target.y;
		}
		
		public String toString() {
			return element.toString() + "@[" + x + ", " + y + "]";
		}
		
		public boolean isIn(final double sx, final double sy, final double fx, final double fy) {
			return x >= sx && x < fx && y >= sy && y < fy;
		}
	}
	
	private enum Child {
		TR, BR, BL, TL;
	}
	
	public FlexibleQuadTree() {
		this(DEFAULT_CAPACITY);
	}
	
	private FlexibleQuadTree(
			final double minx,
			final double maxx,
			final double miny,
			final double maxy,
			final int elemPerQuad,
			final FlexibleQuadTree<E> rootNode,
			final FlexibleQuadTree<E> parentNode) {
		bounds = new Rectangle2D(minx, miny, maxx, maxy);
		elements = new LinkedList<>();
		maxElements = elemPerQuad;
		parent = parentNode;
		root = rootNode == null ? this : rootNode;
	}
	
	private double minX(final Child c) {
		switch (c) {
		case TR:
		case BR:
			return centerX();
		case BL:
		case TL:
			return minX();
		default:
			throw new IllegalStateException();
		}
	}
	
	private double maxX(final Child c) {
		switch (c) {
		case TR:
		case BR:
			return maxX();
		case BL:
		case TL:
			return centerX();
		default:
			throw new IllegalStateException();
		}
	}

	private double minY(final Child c) {
		switch (c) {
		case BL:
		case BR:
			return minY();
		case TR:
		case TL:
			return centerY();
		default:
			throw new IllegalStateException();
		}
	}

	private double maxY(final Child c) {
		switch (c) {
		case BL:
		case BR:
			return centerY();
		case TR:
		case TL:
			return maxY();
		default:
			throw new IllegalStateException();
		}
	}

	private FlexibleQuadTree<E> getChild(final Child c) {
		return children.get(c.ordinal()).get();
	}
	
	private void setChild(final Child c, final FlexibleQuadTree<E> child) {
		if (children.set(c.ordinal(), Optional.of(child)).isPresent()) {
			throw new IllegalStateException();
		}
		child.parent = this;
	}

	private void createChildIfAbsent(final Child c) {
		if (!children.get(c.ordinal()).isPresent()) {
			setChild(c, create(minX(c), maxX(c), minY(c), maxY(c), this));
		}
	}

	private void subdivide() {
		for (final Child c: Child.values()) {
			createChildIfAbsent(c);
		}
	}

	/**
	 * @param elemPerQuad
	 *            maximum number of elements per quad
	 */
	public FlexibleQuadTree(final int elemPerQuad) {
		this(0, 1, 0, 1, elemPerQuad, null, null);
	}
	
	private boolean contains(final double x, final double y) {
		return bounds.contains(x, y);
	}
	
	private FlexibleQuadTree<E> create(final double minx,
			final double maxx,
			final double miny,
			final double maxy,
			final FlexibleQuadTree<E> parent) {
		return new FlexibleQuadTree<E>(minx, maxx, miny, maxy, getMaxElementsNumber(), root, parent);
	}

	/**
	 * Deletes an element from the QuadTree.
	 * 
	 * @param e
	 *            The element to delete
	 * @param x
	 *            the x position of the element
	 * @param y
	 *            the y position of the element
	 * @return true if the element is found and removed
	 */
	public boolean remove(final E e, final double x, final double y) {
		return root.localRemove(e, x, y);
	}
	
	private boolean localRemove(final E e, final double x, final double y) {
		if (contains(x, y)) {
			return removeHere(e, x, y) || removeInChildren(e, x, y);
		}
		return false;
	}
	
	private boolean removeInChildren(final E e, final double x, final double y) {
		return children.parallelStream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(c -> c.localRemove(e, x, y))
				.findAny().isPresent();
	}
	
	/**
	 * @return the maximum number of elements per node
	 */
	public int getMaxElementsNumber() {
		return maxElements;
	}
	
	private double centerX() {
		return bounds.getCenterX();
	}

	private double centerY() {
		return bounds.getCenterY();
	}

	private double minY() {
		return bounds.getMinY();
	}

	private double maxY() {
		return bounds.getMaxY();
	}

	private double minX() {
		return bounds.getMinX();
	}

	private double maxX() {
		return bounds.getMaxX();
	}

	private boolean hasChildren() {
		if (!childrenCreated) {
			childrenCreated = children.stream().allMatch(Optional::isPresent);
		}
		return childrenCreated;
	}

	private boolean hasSpace() {
		return elements.size() < maxElements;
	}

	/**
	 * Inserts an element in the {@link FlexibleQuadTree}. If the element is
	 * outside the space managed by this quadtree, a new quadtree root will be
	 * created and returned. If not enough space is available in this node, new
	 * children will get created. Remember to ALWAYS operate on the returned
	 * object.
	 * 
	 * @param e
	 *            The element to add
	 * @param x
	 *            the x position of the element
	 * @param y
	 *            the y position of the element
	 */
	public void insert(final E e, final double x, final double y) {
		/*
		 * I must insert starting from the root. If the root does not contain
		 * the coordinates, then the tree should be expanded upwards
		 */
		for (; !root.contains(x, y); root = root.root) {
			root.createParent(x, y);
		}
		root.localInsert(e, x, y);
	}
	
	private void localInsert(final E e, final double x, final double y) {
		if (hasSpace()) {
			insertHere(e, x, y);
		} else {
			if (!hasChildren()) {
				subdivide();
			}
			insertInChild(e, x, y);
		}
	}
	
	private void createParent(final double x, final double y) {
		/*
		 * Determine where the parent should be
		 */
		if (x < centerX()) {
			final double minx = 2 * minX() - maxX();
			if (y < centerY()) {
				/*
				 * This will be TR child of the new parent
				 */
				root = create(minx, maxX(), 2 * minY() - maxY(), maxY(), null);
				root.setChild(Child.TR, this);
			} else {
				/*
				 * This will be BR child of the new parent
				 */
				root = create(minx, maxX(), minY(), 2 * maxY() - minY(), null);
				root.setChild(Child.BR, this);
			}
		} else {
			final double maxx = 2 * maxX() - minX();
			if (y < centerY()) {
				/*
				 * This will be TL child of the new parent
				 */
				root = create(minX(), maxx, 2 * minY() - maxY(), maxY(), null);
				root.setChild(Child.TL, this);
			} else {
				/*
				 * This will be BL child of the new parent
				 */
				root = create(minX(), maxx, minY(), 2 * maxY() - minY(), null);
				root.setChild(Child.BL, this);
			}
		}
		/*
		 * A bit cryptic, but the root of the new root is the root itself.
		 * Otherwise, the root would point to the previous root.
		 */
		root.root = root;
		root.subdivide();
	}
	
	private void insertHere(final E e, final double x, final double y) {
		assert elements.size() < maxElements : "Bug in " + getClass() + ". Forced insertion over the container size.";
		elements.push(new QuadTreeEntry<>(e, x, y));
	}

	private void insertInChild(final E e, final double x, final double y) {
		if (x < centerX()) {
			if (y < centerY()) {
				getChild(Child.BL).localInsert(e, x, y);
			} else {
				getChild(Child.TL).localInsert(e, x, y);
			}
		} else {
			if (y < centerY()) {
				getChild(Child.BR).localInsert(e, x, y);
			} else {
				getChild(Child.TL).localInsert(e, x, y);
			}
		}
	}

	/**
	 * If an element is moved, updates the QuadTree accordingly.
	 * 
	 * @param e
	 *            the element
	 * @param sx
	 *            the start x
	 * @param sy
	 *            the start y
	 * @param fx
	 *            the final x
	 * @param fy
	 *            the final y
	 * @return true if the element is found and no error occurred
	 */
	public boolean move(final E e, final double sx, final double sy, final double fx, final double fy) {
		return root.localMove(e, sx, sy, fx, fy);
	}
	
	private boolean localMove(final E e, final double sx, final double sy, final double fx, final double fy) {
		if (contains(sx, sy)) {
			if (elements.remove(new QuadTreeEntry<E>(e, sx, sy))) {
				if (contains(fx, fy)) {
					insertHere(e, fx, fy);
				} else if (parent == null || !swapMostStatic(e, fx, fy)) {
					/*
					 * Root may be inconsistent here. Roll back through parent nodes
					 */
					while (root.parent != null) {
						root = root.parent;
					}
					root.localInsert(e, fx, fy);
				}
				return true;
			} else if (hasChildren()) {
				return children.stream()
				.anyMatch(c -> c.get().localMove(e, sx, sy, fx, fy));
			}
		}
		return false;
	}
	
	private static final <E> boolean localMove(final FlexibleQuadTree<E> target, final E e, final double sx, final double sy, final double fx, final double fy) {
		for (FlexibleQuadTree<E> cur = target; cur.contains(sx, sy);) {
			if (cur.elements.remove(new QuadTreeEntry<E>(e, sx, sy))) {
				if (cur.contains(fx, fy)) {
					cur.insertHere(e, fx, fy);
				} else if (cur.parent == null || !cur.swapMostStatic(e, fx, fy)) {
					/*
					 * Root may be inconsistent here. Roll back through parent nodes
					 */
					while (cur.root.parent != null) {
						cur.root = cur.root.parent;
					}
					cur.root.localInsert(e, fx, fy);
					
				}
				return true;
			}
			if (cur.hasChildren()) {
				if (sx < cur.centerX()) {
					// VEDI ANGOLO, usa getChild()
				}
			}
		}
		return false;
	}
	
	private boolean swapMostStatic(final E e, final double fx, final double fy) {
		assert parent != null : "Tried to swap on a null parent.";
		final Iterator<QuadTreeEntry<E>> iterator = parent.elements.descendingIterator();
		while (iterator.hasNext()) {
			final QuadTreeEntry<E> target = iterator.next();
			if (contains(target.x, target.y)) {
				/*
				 * There is a swappable node
				 */
				elements.push(target);
				parent.insertHere(e, fx, fy);
				return true;
			}
		}
		return false;
	}

	/**
	 * @return a list of the objects in the range
	 */
	public List<E> query(final double fromx, final double fromy, final double tox, final double toy) {
		final List<E> result = new ArrayList<>();
		query(fromx, fromy, tox, toy, Collections.synchronizedList(result));
		return result;
	}
	
	private void query(final double sx, final double sy, final double fx, final double fy, final List<E> results) {
		if (bounds.intersects(sx, sy, fx, fy)) {
			for (final QuadTreeEntry<E> entry : elements) {
				if (entry.isIn(sx, sy, fx, fy)) {
					results.add(entry.element);
				}
			}
			if (hasChildren()) {
				children.parallelStream()
				.map(Optional::get)
				.forEach(c -> c.query(sx, sy, fx, fy, results));
			}
		}
	}

	private boolean removeHere(final E e, final double x, final double y) {
		return elements.remove(new QuadTreeEntry<E>(e, x, y));
	}

	@Override
	public String toString() {
		return bounds.toString() + ' ' + elements.toString();
	}
}