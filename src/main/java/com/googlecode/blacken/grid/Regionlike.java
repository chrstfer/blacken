/* blacken - a library for Roguelike games
 * Copyright © 2010, 2011 Steven Black <yam655@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.googlecode.blacken.grid;

/**
 * A two-dimensional region.
 * 
 * @author yam655
 *
 */
public interface Regionlike extends Positionable, Sizable {
    /**
     * Is the coordinate in the region?
     * 
     * @param y coordinate
     * @param x coordinate
     * @return true on success; false on failure
     */
    public boolean contains(int y, int x);
    /**
     * Is the box in the region?
     * 
     * @param height height of box
     * @param width width of box
     * @param y1 starting Y coordinate
     * @param x1 starting X coordinate
     * @return true on success; false on failure
     */
    public boolean contains(int height, int width, int y1, int x1);
    /**
     * Is the positionable item in the region?
     * 
     * @param p postionable item
     * @return true on success; false on failure
     */
    public boolean contains(Positionable p);
    /**
     * Is the region in the region?
     * 
     * @param r region to check
     * @return true on success; false on failure
     */
    public boolean contains(Regionlike r);
    /**
     * Get the bounds of the region
     * @return bounds, expressed in a regionlike
     */
    public Regionlike getBounds();
    /**
     * Get the edge iterator.
     * @return iterator.
     */
    public RegionIterator getEdgeIterator();
    /**
     * Get the height
     * @return height
     */
    @Override
    public int getHeight();
    /**
     * Get the inside iterator
     * @return iterator
     */
    public RegionIterator getInsideIterator();
    /**
     * Get the "not outside" iterator.
     * 
     * <p>The not-outside iterator is literally everything not outside.
     * This includes both the edge and the inside.</p>
     * 
     * @return iterator
     */
    public RegionIterator getNotOutsideIterator();
    /**
     * Get the width.
     * @return width
     */
    @Override
    public int getWidth();
    /**
     * Does the box intersect with the region?
     * 
     * <p>An intersection is both inside and outside a region.</p>
     * 
     * @param height height of box
     * @param width width of box
     * @param y1 coordinate
     * @param x1 coordinate
     * @return true on success; false on failure
     */
    public boolean intersects(int height, int width, int y1, int x1);
    /**
     * Does the region intersect the region?
     * 
     * @param room intersecting region
     * @return true on success; false on failure
     */
    public boolean intersects(Regionlike room);
    /**
     * Set the height.
     * @param height the height
     */
    @Override
    public void setHeight(int height);
    /**
     * Set the width.
     * @param width the width
     */
    @Override
    public void setWidth(int width);
}
