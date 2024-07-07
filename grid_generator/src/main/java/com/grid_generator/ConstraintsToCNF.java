package com.grid_generator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.security.cert.CertPathBuilderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cdcl.Formula;
import com.cdcl.Solver;

import javax.print.DocFlavor.STRING;



public class ConstraintsToCNF {


    private List<String> Tiles = new ArrayList<>();

    private JSONObject tileMappings ; 

    private int Width;
    private int Height;

    // List<HashSet<Integer>> Clauses = new ArrayList<>();


    Formula formula;
    

    public ConstraintsToCNF (String constraints_file, int width, int height) throws Exception{

        Object obj = new JSONParser().parse(new FileReader( constraints_file )); 
        tileMappings = (JSONObject) obj; 

        Width = width;
        Height = height;


        for (Object key : tileMappings.keySet()) {
            Tiles.add(key.toString());
        }


    }


    private void createCNF(){

        List<HashSet<Integer>> clause_list = new ArrayList<>();

        for (String tile : Tiles ) {

            for (int x = 0; x<Width; x++){

                for( int y =0 ; y<Height ; y++){

                    
                    addClauses(tile, x, y, clause_list);


                }
            }
        }

        clause_list.addAll( getOneTileInEachCellConstraints() );


        formula = new Formula(clause_list, Width*Height*Tiles.size());
                
        


    }

    private List<HashSet<Integer>> getOneTileInEachCellConstraints(){
        
        List<HashSet<Integer>> constraints = new ArrayList<>();

        for (int x = 0; x<Width; x++){
            for( int y =0 ; y<Height ; y++){
        
                HashSet<Integer> position_constraint = new HashSet<>();
                for (String tile : Tiles ) {
                    position_constraint.add( getLiteralID(tile, x, y) );
                }

                constraints.add(position_constraint);
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
        if(x!=Width-1)directions.add("right");
        if(y!=0)directions.add("up");
        if(y!=Height-1)directions.add("down");



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
    

    

    

    private int getLiteralID(String tile, int x, int y){

        if (((Tiles.indexOf(tile)*Width*Height) + (y*Width) + x + 1) ==0){
            System.out.println("ERROR X: " + x + " Y: " + y + " Tile Index: "+ Tiles.indexOf(tile));
        }

        return (Tiles.indexOf(tile)*Width*Height) + (y*Width) + x + 1;

    }

    public Formula getFormula(){
        return formula;
    }


    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );

        ConstraintsToCNF converter = new ConstraintsToCNF("/home/waqee/Grid_Generator/grid_generator/example.json", 3, 3);
        converter.createCNF();


        Solver solver = new Solver(converter.getFormula());


        converter.formula.OutputClauses();


        List<Integer> solution = solver.Solve();

        int positives = 0;
        for (Integer integer : solution) {
            if(integer>0){
                positives+=1;
            }
        }

        System.out.println(positives);


        System.out.println(solution);

        
        
    }




}
