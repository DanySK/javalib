package org.danilopianini.lang;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @param <E> content of the index
 */
public interface SpatialIndex<E> extends Serializable {
	
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
	void insert(E element, double... position);

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
	boolean remove(E element, double... position);
	
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
	boolean move(E element, double[] start, double[] end);
	
	/**
	 * @param fromx start x
	 * @param fromy start y
	 * @param tox end x
	 * @param toy end y
	 * @return a list of objects in range
	 */
	List<E> query(double... parallelotope);
	
	int getDimensions();
	
}
