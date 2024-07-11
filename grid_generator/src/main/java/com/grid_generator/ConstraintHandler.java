package com.grid_generator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cdcl.Formula;
import com.cdcl.Solver;




public class ConstraintHandler {


    private List<String> Tiles = new ArrayList<>();

    private JSONObject tileMappings ; 

    private int Columns;
    private int Rows;

    private Formula formula;
    

    public ConstraintHandler (String constraints_file,  int rows, int columns) throws Exception{



        Object obj = new JSONParser().parse(new FileReader( constraints_file )); 
        tileMappings = (JSONObject) obj; 

        

        Columns = columns;
        Rows = rows;


        for (Object key : tileMappings.keySet()) {
            Tiles.add(key.toString());

            
        }


        createCNF();

        




    }


    private void createCNF(){

        List<HashSet<Integer>> clause_list = new ArrayList<>();

        for (String tile : Tiles ) {

            for (int x = 0; x<Columns; x++){

                for( int y =0 ; y<Rows ; y++){

                    
                    addClauses(tile, x, y, clause_list);


                }
            }
        }

        clause_list.addAll( getOneTileInEachCellConstraints() );


        formula = new Formula(clause_list, Columns*Rows*Tiles.size());
                
        


    }

    private List<HashSet<Integer>> getOneTileInEachCellConstraints(){
        
        List<HashSet<Integer>> constraints = new ArrayList<>();

        for (int x = 0; x<Columns; x++){
            for( int y =0 ; y<Rows ; y++){
        
                HashSet<Integer> at_least_one_literal_true_constraint = new HashSet<>();
                for (String tile : Tiles ) {
                    at_least_one_literal_true_constraint.add( getLiteralID(tile, x, y) );

                    // adds the constraint that at most one tile is placed at this location
                    for(String tile2: Tiles){
                        if( tile!=tile2){
                            constraints.add( new HashSet<Integer>( Arrays.asList(-getLiteralID(tile, x, y) , -getLiteralID(tile2, x, y)) ) );
                        }
                    }



                }

                constraints.add(at_least_one_literal_true_constraint);
            }
        }


        

        return constraints;
                
    }


   
    private void addClauses(String current_tile,  int x, int y, List<HashSet<Integer>> clause_list){

        // get mapping from current tile to allowed tiles in all four directions
        Map position_to_tiles = (Map)tileMappings.get(current_tile);

        List<String> directions = new ArrayList<>();

        // omit certain directions if poisition on border
        if(x!=0)directions.add("left");
        if(x!=Columns-1)directions.add("right");
        if(y!=0)directions.add("up");
        if(y!=Rows-1)directions.add("down");



        for (String direction : directions) {
            
            HashSet<Integer> clause = new HashSet<>();

            clause.add(  -getLiteralID( current_tile , x, y)  );

            Iterator<String> valid_tiles = ((JSONArray)position_to_tiles.get(direction)).iterator();

            while( valid_tiles.hasNext()){
                

                if( direction == "up"){
                    clause.add(  getLiteralID( valid_tiles.next()  , x, y-1 ) );
                }
                else if( direction =="left"){
                    clause.add(  getLiteralID( valid_tiles.next() , x-1, y ) );
                }
                else if ( direction== "down"){
                    clause.add(  getLiteralID( valid_tiles.next() , x, y+1 ) );
                }
                else if( direction =="right"){
                    clause.add(  getLiteralID( valid_tiles.next()  , x+1, y ) );
                }

            

            }

            clause_list.add(clause);

        }


        

    }
    

    

    
    // clean this up, check consistency of tile mapping 
    private int getLiteralID(String tile, int x, int y){

        if (((Tiles.indexOf(tile)*Columns*Rows) + (y*Columns) + x + 1) ==0){
            System.out.println("ERROR X: " + x + " Y: " + y + " Tile Index: "+ Tiles.indexOf(tile));
        }

        return to1d(x, y, Tiles.indexOf(tile));//(Tiles.indexOf(tile)*Width*Height) + (y*Width) + x + 1;

    }

    

    private int to1d(int x , int y , int z){

        return (z * Columns * Rows) + (y * Columns) + x +1;

    }

    private int[] to3d(int idx){
        idx-=1;
        final int z = idx / (Columns * Rows);
        idx -= (z * Columns * Rows);
        final int y = idx / Columns;
        final int x = idx % Columns;
        return new int[]{ x, y, z };
    }

    public  String[][] solutionToGrid(List<Integer> solution){


        List<Integer> positive_literals = new ArrayList<>();
        
        for (Integer literal : solution) {
            if(literal>0)positive_literals.add(literal);
        }


        String[][] grid = new String[Rows][Columns];
        
        
        for (Integer positive_literal : positive_literals) {
            
            int[] tile_and_position = to3d(positive_literal);

           
            grid[ tile_and_position[1] ] [tile_and_position[0] ] = Tiles.get(tile_and_position[2]);

        }

        return grid;
    
    }


    public List<String> getTileStrings(){
        return Tiles;
    }

    public Formula getFormula(){
        return formula;
    }


    public List<Integer> getNextSolution( List<Integer> current_solution){
        if (current_solution==null) return null;

        // add inverse of solution to clause list, only add inverse of positive literals 
        HashSet<Integer> inverse = new HashSet<>();
        for (Integer literal : current_solution) {
            if(literal>0) inverse.add(-literal);
        }


        formula.AddClause(inverse);

        Solver solver = new Solver(formula);

        return solver.Solve();
    }

    public  List<List<Integer>> getNSolutions( int N){
        
        Solver solver = new Solver(formula);

        List< List<Integer>> solutions = new ArrayList<>();
        List<Integer> current_solution = solver.Solve();


        while(current_solution!=null && solutions.size()< N){

            solutions.add(current_solution);
            current_solution = getNextSolution( current_solution);

        }


        return solutions;
        
        
    }

    

    public int getRows(){
        return Rows;
    }

    public int getColumns(){
        return Columns;
    }
    



    public static Formula convertToCNF(String path, int width, int height ) throws Exception{

        ConstraintHandler converter = new ConstraintHandler(path, width, height);

        converter.createCNF();

        return converter.formula;
    }
   




}
