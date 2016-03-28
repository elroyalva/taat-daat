/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irapplication;

/**
 *
 * @author Elroy
 */
public class Postings
{
    public String docID;
    public int times;
    public Postings next;
    public Postings prev;

    
    public Postings(String docID, int times, Postings next, Postings prev){
        this.docID = docID;
        this.times = times;
        this.prev = prev;
        this.next = next;
    }
    
    public Postings()
    {
        this.docID = null;
        this.times = 0;
        this.prev = null;
        this.next = null;
    }
    
    public int getTimes(){
        return times;
    }
    
    public int getDocID(){
        return Integer.parseInt(docID);
    }
    
    public Postings getAt(Postings head, int index)
    {
        Postings current = head;
        int count = 0;
        while (current != null)
        {
           if (count == index)
              return(current);
           count++;
           current = current.next;
        }
        return null;              
    }
}
