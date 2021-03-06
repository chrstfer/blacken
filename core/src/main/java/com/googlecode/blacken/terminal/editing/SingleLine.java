/* blacken - a library for Roguelike games
 * Copyright © 2012 Steven Black <yam655@gmail.com>
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
package com.googlecode.blacken.terminal.editing;

import com.googlecode.blacken.grid.Grid;
import com.googlecode.blacken.grid.Point;
import com.googlecode.blacken.grid.Positionable;
import com.googlecode.blacken.terminal.BlackenKeys;
import com.googlecode.blacken.terminal.TerminalCellLike;
import com.googlecode.blacken.terminal.TerminalCellTemplate;
import com.googlecode.blacken.terminal.TerminalViewInterface;
import com.googlecode.blacken.terminal.utils.CodePointUtils;

/**
 *
 * @author Steven Black
 */
public class SingleLine {
    
    /**
     * Get a string from the window.
     *
     * <p>This caches the previous cursor position, then moves the cursor to the
     * requested input location. It restores the cursor position after the
     * string has been entered.
     *
     * <p>The following are codepoints handled specially when they're returned
     * by <code>CodepointCallbackInterface</code>:
     * <ul>
     * <li> BlackenKeys.NO_KEY : ignore the event
     * <li> BlackenKeys.CMD_END_LOOP : done with loop, return string
     * <li> BlackenKeys.KEY_BACKSPACE : perform destructive backspace
     * <li> (all others) : replace with key
     * </ul>
     *
     * @param terminal
     * @param y
     * @param x
     * @param length
     * @param callback call back
     * @return
     */
    static public String getString(TerminalViewInterface terminal, int y, int x,
            int length, CodepointCallbackInterface callback) {
        // XXX needs updated for BreakableLoop class.
        if (callback == null) {
            callback = DefaultSingleLineCallback.getInstance();
        }
        int firstX = x;
        // int firstY = y;
        if (length < 0 || terminal.getWidth() - x - length <= 0) {
            length = terminal.getWidth() - x;
        }
        Positionable lastCursor = terminal.getCursorPosition();
        int cp = -1;
        StringBuffer out;
        if (length < 0) {
            out = new StringBuffer();
        } else {
            out = new StringBuffer(length);
        }
        int i = 0;
        int lastUpX = -1, lastUpY = -1;
        terminal.setCursorLocation(y, x);
        boolean doQuit = false;
        while (!doQuit) {
            cp = terminal.getch();
            int ec = BlackenKeys.NO_KEY;
            if (cp == BlackenKeys.KEY_MOUSE_EVENT) {
                callback.handleMouseEvent(terminal.getmouse());
            } else if (cp == BlackenKeys.KEY_WINDOW_EVENT) {
                callback.handleWindowEvent(terminal.getwindow());
            } else if (cp == BlackenKeys.RESIZE_EVENT) {
                callback.handleResizeEvent();
            } else {
                ec = callback.handleCodepoint(cp);
            }
            switch (ec) {
                case BlackenKeys.NO_KEY:
                    continue;
                case BlackenKeys.CMD_END_LOOP:
                    doQuit = true;
                    break;
                case BlackenKeys.RESIZE_EVENT:
                    // I have no idea why this would be returned
                    callback.handleResizeEvent();
                    break;
                case BlackenKeys.MOUSE_EVENT:
                    cp = BlackenKeys.NO_KEY; // illegal, so we ignore
                    break;
                case BlackenKeys.WINDOW_EVENT:
                    cp = BlackenKeys.NO_KEY; // illegal, so we ignore
                    break;
                default:
                    cp = ec;
                    break;
            }
            if(cp == BlackenKeys.NO_KEY) {
                continue;
            }
            if (cp == BlackenKeys.CMD_END_LOOP) {
                doQuit = true;
                continue;
            }
            TerminalCellLike c;
            // XXX add line-editing capabilities
            if (cp == BlackenKeys.KEY_BACKSPACE) {
                if (x > firstX) {
                    x--;
                }
                c = terminal.get(y, x);
                c.setSequence("");
                terminal.set(y, x, c);
                terminal.setCursorLocation(y, x);
                if (Character.isSurrogate(out.charAt(out.length()-1))) {
                    out.delete(out.length()-2, out.length());
                } else {
                    out.deleteCharAt(out.length()-1);
                }
            }
            // When the limit is reached, we don't force a return.
            // We should be able to incorporate some line-editing
            if (length != -1 && i >= length) {
                continue;
            }
            if (BlackenKeys.isSpecial(cp)) {
                continue;
            }
            if (Character.isValidCodePoint(cp)) {
                out.append(Character.toChars(cp));
            }
            if (doQuit) {
                continue;
            }
            switch (Character.getType(cp)) {
                case Character.COMBINING_SPACING_MARK:
                case Character.ENCLOSING_MARK:
                case Character.NON_SPACING_MARK:
                    c = terminal.get(lastUpY, lastUpX);
                    c.addSequence(cp);
                    break;
                default:
                    c = terminal.get(y, x);
                    c.setSequence(cp);
                    lastUpX = x++;
                    lastUpY = y;
                    terminal.setCursorLocation(y, x);
            }
            i++;
        }
        terminal.setCursorPosition(lastCursor);
        return out.toString();
    }

    /**
     * Write a string.
     *
     * @param terminal
     * @param start
     * @param end
     * @param string
     * @param template
     * @return
     */
    static public Positionable putString(TerminalViewInterface terminal, 
            Positionable start, Positionable end, String string, TerminalCellTemplate template) {
        return putString(terminal, start, end, string, template, Alignment.FIRST);
    }
    static public Positionable putString(TerminalViewInterface terminal,
            Positionable start, Positionable end, String string,
            TerminalCellTemplate template, Alignment align) {

        Grid<TerminalCellLike> grid = terminal.getGrid();
        if (grid == null) {
            throw new NullPointerException("TerminalInterface wasn't initialized.");
        }
        if (start == null) {
            throw new NullPointerException("start position needs to be created");
        }
        Positionable ret = end;
        if (ret == null) {
            ret = new Point();
        }
        int y = start.getY();
        int x = start.getX();
        if (align.equals(Alignment.CENTER)) {
            int[] a = CodePointUtils.findAdvancingCodepoints(string, 0);
            x -= a[0] / 2;
        } else if (align.equals(Alignment.RIGHT)) {
            int[] a = CodePointUtils.findAdvancingCodepoints(string, 0);
            x -= a[0];
        } else if (align.equals(Alignment.FIRST)) {
            // nothing here.
        }
        int cp;
        if (start.getX() >= grid.getWidth()) {
            start.setX(grid.getWidth() - 1);
        }
        if (start.getY() >= grid.getHeight()) {
            start.setY(grid.getHeight() - 1);
        }
        int lastUpX = start.getX() - 1;
        int lastUpY = start.getY() - 1;
        for (int i = 0; i < string.length(); i++) {
            cp = string.codePointAt(i);
            if (cp > 0xffff) {
                if (Character.isLowSurrogate(string.charAt(i))) {
                    continue;
                }
            }
            if (x >= grid.getWidth()) {
                x = 0;
                y++;
            }
            if (y >= grid.getHeight()) {
                terminal.moveBlock(grid.getHeight() - 1, grid.getWidth(), 1, 0, 0, 0);
                y = grid.getHeight() - 1;
            }
            TerminalCellLike c;
            switch (Character.getType(cp)) {
                case Character.COMBINING_SPACING_MARK:
                case Character.ENCLOSING_MARK:
                case Character.NON_SPACING_MARK:
                    if (lastUpX >= 0 && lastUpY >= 0) {
                        c = terminal.get(lastUpY, lastUpX);
                        c.addSequence(cp);
                        terminal.refresh(lastUpY, lastUpX);
                    }
                    break;
                default:
                    lastUpX = x;
                    lastUpY = y;
                    TerminalCellLike cell;
                    if (cp == '\n' || cp == BlackenKeys.KEY_ENTER || 
                            cp == BlackenKeys.KEY_NP_ENTER) {
                        y++;
                        x = start.getX();
                        if (align.equals(Alignment.CENTER)) {
                            int[] a = CodePointUtils.findAdvancingCodepoints(string, i+1);
                            x -= a[0] / 2;
                        } else if (align.equals(Alignment.RIGHT)) {
                            int[] a = CodePointUtils.findAdvancingCodepoints(string, i+1);
                            x -= a[0];
                        } else if (align.equals(Alignment.FIRST)) {
                            x = 0;
                        }

                    } else if (cp == '\r') {
                        x = start.getX();
                        if (align.equals(Alignment.FIRST)) {
                            x = 0;
                        }
                    } else if (cp == '\b' || cp == BlackenKeys.KEY_BACKSPACE) {
                        if (x > 0) {
                            x--;
                        }
                        cell = terminal.get(y, x);
                        cell.setSequence("");
                        terminal.refresh(y, x);
                    } else if (cp == '\t' || cp == BlackenKeys.KEY_TAB) {
                        x += 8;
                        x -= x % 8;
                    } else if (x >= 0 && y >= 0) {
                        cell = terminal.get(y, x);
                        cell.setSequence(cp);
                        if (template != null) {
                            template.applyOn(cell, terminal.getBounds(), y, x);
                        }
                        terminal.refresh(y, x);
                        x++;
                    } else {
                        x++;
                    }
            }
        }
        ret.setY(y);
        ret.setX(x);
        return ret;

    }

    /**
     * A simple method to write a processed string to a terminal.
     * 
     * <p>The sequence is processed for common codepoints that have meaning
     * such as TAB, NL, CR.
     * 
     * @param terminal
     * @param y
     * @param x
     * @param string
     * @param fore
     * @param back
     * @return int[] {y, x}
     * @since 1.1
     */
    static public int[] putString(TerminalViewInterface terminal, int y, int x, String string, int fore, int back) {
        TerminalCellTemplate colorTemplate = new TerminalCellTemplate();
        colorTemplate.setBackground(back);
        colorTemplate.setForeground(fore);
        Positionable out = putString(terminal, new Point(y, x), null, string, colorTemplate);
        return new int[] {out.getY(), out.getX()};
    }


}
