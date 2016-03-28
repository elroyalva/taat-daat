package irapplication;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Elroy
 */
public class IndexList {
    public String term;
    public int count;
//    Postings list;
    public Postings head;
    public Postings last;
    
    public int getCount(){
        return count;
    }
    
    public String getTerm(){
        return term;
    }
}