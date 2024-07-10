package com.grid_generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.cdcl.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Grid extends PApplet{

    ConstraintsToCNF constraintHandler;

    char getNext10SolutionsKey = '1';
    char getNext100SolutionsKey = '2';
    char getNext1000SolutionsKey = '3';

    Formula formula;

    int rows = 3; 
    int columns = 3;

    int panelWidth = 500;

    int width = 1000;
    int height = 1000;

    List<List<Integer>> allSolutions;
    int currentSolution =0;

    HashMap<String, PImage> TileImages = new HashMap<>();

   
    


   
    public void settings(){
		size(width+panelWidth, height);
	}
	


    public void setup(){
        frameRate(100);

        try {
            constraintHandler =  new ConstraintsToCNF("/home/waqee/Grid_Generator/grid_generator/Tilemap2/constraints.json", rows, columns );
        } catch (Exception e) {
            System.out.println("Constraint File Not Found");
            System.exit(0);
        }


        for (String tile_name : constraintHandler.getTileStrings()) {
            
            if( !TileImages.containsKey(tile_name))
                TileImages.put(tile_name, loadImage("/home/waqee/Grid_Generator/grid_generator/Tilemap2/Tiles/"+tile_name+".jpg"));

        }

        
      
        allSolutions = new ArrayList<>();


        System.out.println("CREATED INITIAL FORMULA");
        allSolutions = constraintHandler.getNSolutions(1);
        System.out.println("SOLVED N TIMES");


        
       

        System.out.println("SOLUTIONS SIZE: " + allSolutions.size());




        String[][] string_grid = constraintHandler.solutionToGrid(allSolutions.get(0));
        drawSolution(string_grid);

       




        
        




    }

    public void draw(){

        currentSolution+=1;
        if(currentSolution%allSolutions.size()==0)currentSolution=0;
        System.out.println("CURRENT SOLUTION: "+ currentSolution);
        drawSolution(constraintHandler.solutionToGrid(allSolutions.get(currentSolution)));


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



    public void keyPressed(){
        drawSolution( constraintHandler.solutionToGrid(allSolutions.get(currentSolution))  );
        System.out.println("MOUSE PRESSED");
        currentSolution+=1;
    }

    public void keyReleased(){
        

        if( key == getNext10SolutionsKey){
            allSolutions.addAll( constraintHandler.getNSolutions(10) );
        }
        else if( key == getNext100SolutionsKey){
            allSolutions.addAll( constraintHandler.getNSolutions(100) );
        }
        else if( key == getNext1000SolutionsKey){
            allSolutions.addAll( constraintHandler.getNSolutions(1000) );
        }
    }

    public void mousePressed(){
        drawSolution( constraintHandler.solutionToGrid(allSolutions.get(currentSolution))  );
        System.out.println("MOUSE PRESSED");
        currentSolution+=1;
    }



    public static void main(String[] args){
		String[] processingArgs = {"Grid"};
		Grid mySketch = new Grid();
		PApplet.runSketch(processingArgs, mySketch);
	}
}
