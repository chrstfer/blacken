/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blacken.terminal;

import java.util.EnumSet;

import com.googlecode.blacken.grid.Grid;
import com.googlecode.blacken.grid.Positionable;
import com.googlecode.blacken.grid.Regionlike;

/**
 * Lightweight view in to a terminal.
 *
 * This interface was pulled from {@link TerminalInterface} for Blacken 1.1.
 * Some of the functions list that they've been around since Blacken 1.0, and
 * that's true -- in the original interface.
 *
 * @author Steven Black
 * @since 1.1
 */
public interface TerminalViewInterface {

    /**
     * Set the cell to the value, assigning ownership.
     * @since EXPERIMENTAL
     * @param y
     * @param x
     * @param cell
     * @return previous cell at that location
     */
    public TerminalCellLike assign(int y, int x, TerminalCellLike cell);

    /**
     * Clear the screen.
     * @since 1.0
     */
    public void clear();

    /**
     * Clear the screen with a specific cell value.
     *
     * @param empty new empty cell value.
     * @since 1.0
     */
    public void clear(TerminalCellLike empty);

    /**
     * Copy from another TerminalViewInterface
     *
     * @param oterm original
     * @param numRows number of rows to copy
     * @param numCols number of columns to copy
     * @param startY starting Y offset in <code>oterm</code>
     * @param startX starting X offset in <code>oterm</code>
     * @param destY destination Y offset in <code>this</code>
     * @param destX destination X offset in <code>this</code>
     * @since 1.1
     */
    public void copyFrom(TerminalViewInterface oterm, int numRows, int numCols, 
            int startY, int startX, int destY, int destX);

    /**
     * Get a cell from the terminal
     *
     * @param y row
     * @param x column
     * @return the terminal cell
     * @since 1.0
     */
    public TerminalCellLike get(int y, int x);

    /**
     * We're entering a land of wrapper interfaces. This function is supposed
     * to return the backing TerminalInterface for wrapper classes.
     * @return the wrapped TerminalInterface
     * @since 1.1
     */
    public TerminalInterface getBackingTerminal();

    /**
     * We're entering a land of wrapper interfaces. This function is supposed
     * to return the backing TerminalInterface for wrapper classes.
     * @return the wrapped interface (may not be the same as {@link #getBackingTerminal()}
     * @since 1.1
     */
    public TerminalViewInterface getBackingTerminalView();

    /**
     * get the Grid's bounds
     * @return a concise representation of the bounds
     * @since 1.0
     */
    public Regionlike getBounds();

    /**
     * Get cursor position
     * @return cursor location as a concise Positionable
     * @since 1.0
     */
    public Positionable getCursorPosition();

    /**
     * Get the cursor's X location.
     *
     * @return cursor's X location.
     * @since 1.0
     */
    public int getCursorX();

    /**
     * Get the cursor's Y location.
     *
     * @return cursor's Y location.
     * @since 1.0
     */
    public int getCursorY();

    /**
     * Get the template cell used for new and clear cells.
     *
     * @return template cell
     * @since 1.0
     */
    public TerminalCellLike getEmpty();

    /**
     * Don't depend on this function.
     *
     * This exists to facilitate the
     * {@link #copyFrom(TerminalInterface, int, int, int, int, int, int)}
     * function. It allows direct modification of the underlying grid, but
     * such uses break the visual interface it is bound to.
     *
     * @return the underlying grid
     * @since 1.0
     */
    public Grid<TerminalCellLike> getGrid();

    /**
     * Get the current terminal max Y size
     *
     * @return terminal's max Y size
     * @since 1.1
     */
    public int getHeight();

    /**
     * Get the current locking states/modifiers
     *
     * If the locking states are available to the interface, this should
     * return them. It may or may not also include the other modifiers.
     *
     * @return set of locking states enabled
     * @since 1.0
     */
    public EnumSet<BlackenModifier> getLockingStates();

    /**
     * Read a string from the screen.
     *
     * @param y
     * @param x
     * @param length length of string to read
     * @return new string
     * @since 1.0
     * @see SingleLine#getString(TerminalViewInterface, int, int, int, CodepointCallbackInterface)
     */
    public String getString(int y, int x, int length);

    /**
     * Write a string without attributes.
     *
     * <p>This exists to be symmetrical with {@link #getString(int, int, int)}.
     *
     * @param y
     * @param x
     * @param string
     * @return last position after string processing
     * @see SingleLine#putString(TerminalViewInterface, Positionable, Positionable, String, TerminalCellTemplate)
     * @see SingleLine#putString(TerminalViewInterface, int, int, String, int, int)
     * @since 1.2
     */
    public Positionable putString(int y, int x, String string);

    /**
     * Write a string without attributes.
     *
     * <p>This allows you to string multiple putString calls together to
     * function like you have a cursor position.
     *
     * @param pos starting position
     * @param string
     * @return last position after string processing
     * @see SingleLine#putString(TerminalViewInterface, Positionable, Positionable, String, TerminalCellTemplate)
     * @see SingleLine#putString(TerminalViewInterface, int, int, String, int, int)
     * @since 1.2
     */
    public Positionable putString(Positionable pos, String string);

    /**
     * Apply color, attributes, a texture, or some other template over part of
     * a line.
     *
     * @param y
     * @param x
     * @param template
     * @param length
     * @since 1.2
     */
    public void applyTemplate(int y, int x, TerminalCellTemplate template,
                              int length);

    /**
     * Get the current terminal max X size
     *
     * @return terminal's max X size
     * @since 1.1
     */
    public int getWidth();

    /**
     * Get the current min X position
     * @return smallest legal X value
     * @since 1.2
     */
    public int getX();
    /**
     * Get the current min Y position
     * @return smallest legal Y value
     * @since 1.2
     */
    public int getY();

    /**
     * Get a character without visible user-feedback.
     *
     * @return character returned.
     * @since 1.0
     */
    public int getch();

    /**
     * Get a character without visible user-feedback.
     *
     * @param millis amount to wait for key before continuing
     * @return character returned; NO_KEY if timeout occured
     * @since 1.1
     */
    public int getch(int millis);

    /**
     * Get the latest mouse event.
     *
     * This function should only be called after a KEY_MOUSE keycode
     * is returned by getch().
     *
     * @return new mouse event
     * @since 1.0
     */
    public BlackenMouseEvent getmouse();

    /**
     * Get the latest window event.
     *
     * This function should only be called after a KEY_WINDOW keycode
     * is returned by getch().
     *
     * @return new window event
     * @since 1.0
     */
    public BlackenWindowEvent getwindow();

    /**
     * Is a key currently waiting?
     * @return true if a key is waiting; false otherwise.
     * @since 1.1
     */
    public boolean keyWaiting();

    /**
     * Move a block of cells
     *
     * @param numRows number of rows to move
     * @param numCols number of columns to move
     * @param origY origin Y location
     * @param origX origin X location
     * @param newY new Y location
     * @param newX new X location
     * @since 1.0
     */
    public void moveBlock(int numRows, int numCols, int origY, int origX, int newY, int newX);

    /**
     * Scan for dirty cells, updating the for-display buffer, and refresh the
     * screen.
     *
     * <p>If you're using the {@link #get(int, int)} function and directly
     * modifying the cell values you need to call {@link #refresh()} so that
     * the dirty cells are noticed and the for-screen buffer is updated.
     *
     * <p>If you only use the set(...) family of calls to change the cell states
     * you can use {@link #doUpdate()} to refresh the display more efficiently.
     *
     * @see #doUpdate()
     * @since 1.0
     */
    public void refresh();

    /**
     * Clean up a cell that has been updated outside of the set(...) family of
     * calls without scanning the whole grid.
     *
     * <p>This does the work of checking to see if a terminal cell is dirty,
     * and updating the for-display representation. It does <em>not</em> tell
     * the display to refresh, so you want to use this in conjunction with
     * {@link #doUpdate()}.
     *
     * <p>If you only use the set(...) family of calls to change the cell states
     * you can ignore this call and just use {@link #doUpdate()} to refresh the
     * display more efficiently.
     *
     * <p>The contract for this function has it explicitly refreshing the cell
     * regardless of whether the cell is marked "dirty" or not. If this is not
     * what you want, check the dirty status before you call.
     *
     * @param y row
     * @param x column
     * @see #doUpdate()
     * @since 1.1
     */
    public void refresh(int y, int x);

    /**
     * Set a cell to explicit contents
     *
     * Any of the arguments can be null. When they are null, that part of the
     * cell remains untouched.
     *
     * @param y row
     * @param x column
     * @param sequence UTF-16 sequence to display; null to leave untouched
     * @param foreground color index or 0xAARRGGBB value; null to not change
     * @param background color index or 0xAARRGGBB value; null to not change
     * @param style cell terminal style; can be null
     * @param walls cell walls; can be null
     * @since 1.0
     */
    public void set(int y, int x, String sequence, Integer foreground, Integer background, EnumSet<TerminalStyle> style, EnumSet<CellWalls> walls);

    /**
     * Set the bounds for this terminal.
     * 
     * <p>This causes two things to happen:
     * <ul>
     * <li>The upper-left corner can cease being located at 0,0
     * <li>The number of expected cells in the terminal can change
     * </ul>
     * 
     * <p>When called on a {@link TerminalView} that wraps an underlying
     * {@link TerminalInterface} this simply moves and resizes the view within
     * the other's backing store.
     * 
     * @param bounds new bounds
     * @since 1.1
     */
    public void setBounds(Regionlike bounds);

    /**
     * See {@link #setBounds(Regionlike)}. This
     * calls that function after wrapping its arguments in a {@link BoxRegion}.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param y1 starting row number
     * @param x1 starting column number
     * @see #setBounds(Regionlike)
     * @since 1.1
     */
    public void setBounds(int rows, int cols, int y1, int x1);

    /**
     * Simplified set cell command.
     *
     * Any of the arguments can be null. When they are null, that part of the
     * cell remains untouched.
     *
     * @param y row
     * @param x column
     * @param sequence UTF-16 sequence to display; null to leave untouched
     * @param foreground color index or 0xAARRGGBB value; null to not change
     * @param background color index or 0xAARRGGBB value; null to not change
     * @since 1.0
     */
    public void set(int y, int x, String sequence, Integer foreground, Integer background);

    /**
     * Set a cell to explicit contents, using a CellLike
     *
     * @param y cell Y location
     * @param x cell X location
     * @param cell example cell
     * @since 1.0
     */
    public void set(int y, int x, TerminalCellLike cell);

    /**
     * Set the cursor location.
     *
     * @param y new Y location
     * @param x new X location
     * @since 1.0
     */
    public void setCursorLocation(int y, int x);

    /**
     * Set the cursor position.
     * @param position
     * @since 1.0
     */
    public void setCursorPosition(Positionable position);

    /**
     * Set the template cell used for new and clear cells.
     *
     * @param empty new empty cell
     * @since 1.0
     */
    public void setEmpty(TerminalCellLike empty);

    /**
     * If you are using TerminalInterface functions to modify the screen we can
     * automatically track the changes to the for-display buffer, and in those
     * cases you can use doUpdate() instead of refresh().
     *
     * If you're using the {@link #get(int, int)} function and directly
     * modifying the cell values without doing a
     * {@link #set(int, int, TerminalCellLike)} (which is legal) you need to
     * call {@link #refresh()} so that the dirty cells are scanned and the
     * for-screen buffer is updated.
     *
     * @see #refresh()
     * @since 1.1
     */
    public void doUpdate();

    /**
     * Get the terminal's image loader instance.
     * @return
     * @since 1.2
     */
    public BlackenImageLoader getImageLoader();
}
