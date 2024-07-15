package com.grid_generator;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;


import com.cdcl.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Grid extends PApplet{

    ConstraintHandler constraintHandler;

    char getNextSolutionKey ='1';
    char getNext10SolutionsKey = '2';
    char getNext100SolutionsKey = '3';
    char getNext1000SolutionsKey = '4';

    Formula formula;

    int rows = 7; 
    int columns = 7;

    int panelWidth = 500;

    int width = 1200;
    int height = 1200;

    List<List<Integer>> allSolutions;
    int currentSolution =0;
    boolean totalSolutionsReached = false;


    HashMap<String, PImage> TileImages = new HashMap<>();

    String tilemapDirectory = "Tilemaps/Tilemap1/";

   
    


   
    public void settings(){
		size(width+panelWidth, height);
	}
	


    public void setup(){
        frameRate(1);

        Solver.setConfig(1, 1.2f, 0.95f, 0.3f, 10, null,"random",null, null);

        try {
            constraintHandler =  new ConstraintHandler(tilemapDirectory+"constraints.json", rows, columns );
        } catch (Exception e) {
            System.out.println("Constraint File Not Found");
            System.exit(0);
        }


        // load all the images for the tiles
        for (String tile_name : constraintHandler.getTileStrings()) {
            if( !TileImages.containsKey(tile_name))
                TileImages.put(tile_name, loadImage(tilemapDirectory+"Tiles/"+tile_name+".png"));

        }

        
      
        allSolutions = new ArrayList<>();


        allSolutions = constraintHandler.getNSolutions(1);
        

    }



    public void draw(){
        background(225);
        currentSolution+=1;
        if(currentSolution%allSolutions.size()==0)currentSolution=0;

        drawSolution(constraintHandler.solutionToGrid(allSolutions.get(currentSolution)));

        updatePanel();
    }





    private void drawSolution(String[][] solution){


     


        float cell_width = width / constraintHandler.getColumns();
        float cell_height = height / constraintHandler.getRows();
        
        for(int r = 0 ; r< constraintHandler.getRows(); r++ ){

            for(int c = 0 ; c< constraintHandler.getColumns(); c++ ){
                
                image( TileImages.get(solution[r][c]) , c*cell_width, r*cell_height, cell_width, cell_height );
                
            }

        }


    }

    public void updatePanel(){
        updateSolutionsText();


        textSize(16);
        fill(0);



        text("Press 1 to Add The Next Solution", width+30, width-200);
        text("Press 2 to Add The Next 10 Solutions", width+30, width-160);
        text("Press 3 to Add The Next 100 Solutions", width+30, width-120);
        text("Press 4 to Add The Next 1000 Solutions", width+30, width-80);

        
    }

    public void updateSolutionsText(){

        textSize(32);
        fill(0);



        text("Current Solution: ", width+30, 50);
        text((currentSolution+1)+"/"+allSolutions.size(), width+30, 90);

        if(totalSolutionsReached){
            textSize(16);
            fill(220, 0, 0);
            text("Total Number Of Solutions Reached ", width+30, 110);
        }
    }






    

    public void keyReleased(){
        

        if( key == getNextSolutionKey){
            addSolutions(1);
        }
        else if( key == getNext10SolutionsKey){
            addSolutions(10);
        }
        else if( key == getNext100SolutionsKey){
            addSolutions(100);
        }
        else if( key == getNext1000SolutionsKey){
            addSolutions(1000);
        }





    }

    public void addSolutions(int n){
        List<List<Integer>> new_solutions = constraintHandler.getNSolutions(n);

        if(new_solutions.size()<n){
            totalSolutionsReached = true;
        }

        allSolutions.addAll(new_solutions);

    }

   



    public static void main(String[] args){
		String[] processingArgs = {"Grid"};
		Grid mySketch = new Grid();
		PApplet.runSketch(processingArgs, mySketch);
	}
}
