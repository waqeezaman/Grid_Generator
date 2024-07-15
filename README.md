# Grid Generator
Given a set of tiles, and constraints on which tiles can be placed next to one another, creates a valid tile mapping that fills the grid.



## Creating Tiling 

In the grid_generator/Tilemaps directory there are existing tile maps that I have created. Change the tilemapDirectory variable to the path of whichever tilemap you wish to display. You can also configure the height and width of the grid. It should be noted that the program may take a long time to find a solution for large grids and tilemaps with lots of tiles. 

The Solver.setConfig function sets the parameters for the SAT Solver. You can experiment with different settings. The solver is my own and can be viewed in more detail here: https://github.com/waqeezaman/CDCL2 .




## Generating your own tile mapping 

In the grid_generator/Tilemaps directory there are many examples of how you might create your own tile maps and constraints. 

1. Create a folder with the name of your tilemap  
2. Create a file called constraints.json
3. Create a sub-folder called "Tiles", and place the .png images of your tiles in this directory   
4. In constraints.json create an object for each tile that looks like this: 

"tileA"{
    "up": [ LIST OF TILES THAT CAN GO ABOVE tileA  ],
    "left": [ LIST OF TILES THAT CAN GO TO THE LEFT OF tileA ],
    "down": [ LIST OF TILES THAT CAN GO BELOW tileA ],
    "right": [ LIST OF TILES THAT CAN GO TO THE RIGHT OF tileA ]
}


5. Change the tilemapDirectory variable in the Grid.java file to point to your new tilemap folder




