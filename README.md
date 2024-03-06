# Conway's Game of Life
I re-created is a cellular automation created by mathematician John Horton Conway. It is a zero-player game in which each new generation or stage in the simulation is determined by 4 preset rules. 

The 4 Rules:
1. Any live cell with fewer than two live neighbors dies, as if by underpopulation.
2. Any live cell with two or three live neighbors lives on to the next generation.
3. Any live cell with more than three live neighbors dies, as if by overpopulation.
4. Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.

Controls:
- Left Click: Selects or de-selects cells.
- WASD keys: allows user to pan around environment
- Scroll Wheel: allows user to zoom in and out.
- Spacebar: starts or pauses the game

How it works:
Every second, the game evaluates the positions of "live" cells, and determines which ones will survive or perish. Each second or "timestep" is known as a generation. By pausing the game, the user can select any number of cells to be "alive". Unpausing will continue the game, and the user will see how their initial cells change and affect the future generations. 

The Future?:
I plan to further optimize the program to allow for more and more cells to be simulated at a time. Currently, the program, however hard I tried, is still quite inefficient, which limits how many cells can be simulated at a time. I also plan to add a welcome screen as well as buttoms to make the controls more user friendly. 
