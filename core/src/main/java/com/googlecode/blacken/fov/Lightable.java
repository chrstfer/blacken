/* blacken - a library for Roguelike games
 * Copyright © 2010-2012 Steven Black <yam655@gmail.com>
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

package com.googlecode.blacken.fov;

    /** 
	 *	A simple interface to allow cell grids to use the Blacken FOV toolkit.
     *  Any cell implementing it must provide the listed method for indicating that it is visible.
     *  @author xlambda
     */


public interface Lightable {

    /** 
	 *	When implemented by the cell, this method will be called to set visibility when running the FOV algorithm. 
     */ 
	  
    public void setVisible(boolean visible); 

}