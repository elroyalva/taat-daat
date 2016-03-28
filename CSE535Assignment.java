/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import irapplication.IndexList;
import irapplication.Postings;
import java.io.*;
import java.util.*;

/**
 *
 * @author Elroy
 */
public class CSE535Assignment {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    private static int DAATAndCount = 0;
    private static int DAATOrCount = 0;
    private static int TAATOrCount = 0;
    private static int TAATOrCountOpt = 0;

//    int DAATComp=0;
    public static void main(String[] args) throws IOException{
        
        ArrayList<IndexList> newIndex = new ArrayList<>();
        Postings post;
        
        String writeToFile;
        String sCurrentLine;
        
        BufferedReader inputReader = new BufferedReader(new FileReader(args[3]));
        BufferedReader indexReader = new BufferedReader(new FileReader(args[0]));

        while ((sCurrentLine = indexReader.readLine()) != null) {
            IndexList temp = new IndexList();
            String term[] = sCurrentLine.split("\\\\");
            temp.term = term[0];
            String out = sCurrentLine.substring(sCurrentLine.indexOf("[")+1,sCurrentLine.indexOf("]")); 
            String[] parts = out.split(", ");
//            System.out.println(temp.term + parts.length);
            int f=0;
            temp.count = parts.length;
            for (int k = 0; k<parts.length; k++) {
                post = new Postings();
                String temp1[] = parts[k].split("/");
                post.docID = temp1[0];
                post.times = Integer.parseInt(temp1[1]);
                if(temp.head == null){
                    temp.head = post;
                    temp.last = temp.head;           
                }
                else {
                    temp.last.next = post;
                    post.prev = temp.last;
                    post.next = null;
                    temp.last = post;
                }
            }
//            System.out.println(temp.term + " "+f);

            newIndex.add(temp);  
        }
        
        //To get top K Terms
        //System.out.println(getTopK(newIndex, Integer.parseInt(args[2])));
        newIndex = sortByCount(newIndex);
        writeToFile = getTopK(newIndex, Integer.parseInt(args[2]));
        
        HashMap<String, IndexList> IndexMap = new HashMap<>();
        for (IndexList tIndex : newIndex) {
        IndexMap.put(tIndex.getTerm(), tIndex);
        }
        
        
        while ((sCurrentLine = inputReader.readLine()) != null){
            if(!sCurrentLine.equals("")){
            String terms[] = sCurrentLine.split(" ");
            boolean abc = true;

            //Does getPosting for each Term
            for(String term :terms){
                if(IndexMap.get(term)!= null)
                {
                IndexList p = IndexMap.get(term);
                writeToFile += "\nFUNCTION: getPostings ";
                writeToFile += term+"\t";
                
                String sbl = sortedByDF_list1(p);
                writeToFile += "\nOrdered by doc IDs: ";
                writeToFile += sbl;
                
                sbl = sortedByTF_list_desc(p);
                writeToFile += "\nOrdered by TF: ";
                writeToFile += sbl;
                writeToFile += "\n";
                }
                else{
                writeToFile += "FUNCTION: getPostings ";
                writeToFile += term;
                writeToFile += "\nterm not found for postings\n";
                abc=false;
                
                }
            }
            if(abc){
                //call methods to perform TAAT and DAAT methods. Returned result is a string ready to be written to fil
            String TAATOr = termAtATimeOR(terms, IndexMap);
            String TAATAnd = termAtATimeAnd(terms, IndexMap);
            String DAATOr = documentAtATimeOr(terms, IndexMap);
            writeToFile += "\n"+TAATAnd+"\n";
            writeToFile += TAATOr+"\n";


            IndexMap.clear();
            indexReader = new BufferedReader(new FileReader(args[0]));
            newIndex.clear();

            while ((sCurrentLine = indexReader.readLine()) != null) {
                IndexList temp = new IndexList();
                String term[] = sCurrentLine.split("\\\\");
                temp.term = term[0];
                String out = sCurrentLine.substring(sCurrentLine.indexOf("[")+1,sCurrentLine.indexOf("]")); 
                String[] parts = out.split(", ");
    //            System.out.println(temp.term + parts.length);
                int f=0;
                temp.count = parts.length;
                for (int k = 0; k<parts.length; k++) {
                    post = new Postings();
                    String temp1[] = parts[k].split("/");
                    post.docID = temp1[0];
                    post.times = Integer.parseInt(temp1[1]);
                    if(temp.head == null){
                        temp.head = post;
                        temp.last = temp.head;           
                    }
                    else {
                        temp.last.next = post;
                        post.prev = temp.last;
                        post.next = null;
                        temp.last = post;
                    }
                }
    //            System.out.println(temp.term + " "+f);

                newIndex.add(temp);  
            }
            
            
            for (IndexList tIndex : newIndex) {
                IndexMap.put(tIndex.getTerm(), tIndex);
            }
            String DAATAnd =documentAtATimeAnd(terms, IndexMap);
            writeToFile += DAATAnd;
            writeToFile += DAATOr+"\n";
            }
            else
                writeToFile += "Term(s) not found in Index\n\n";


//            System.out.println(TAATOr);
//            System.out.println(TAATAnd);
//            System.out.println(DAATAnd);
//            System.out.println(DAATOr);



            //termAtATime
            }
        }
        System.out.println(writeToFile);

        
        String[] test = writeToFile.split("\n");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(args[1]), "utf-8"));
        for(String tes : test){
        writer.write(tes);
        writer.newLine();
        }
        
        writer.close();
}
    
    //Method to find top k terms in the index. Returns a string ready to be written to file
    public static String getTopK(ArrayList<IndexList> newIndex, int n){
        String test = "FUNCTION: getTopK " + n + "\nResult: "; 
        for(int i=0; i<n;i++){
            test += (newIndex.get(i)).term;
            if(i != n-1 && i!=n)
                test += ", ";
        }
        test += "\n";
        return test;
    }
    
    public static void printIndex(ArrayList<IndexList> anIndex)
    {
        for (IndexList tIndex : anIndex)
        {
            System.out.println(tIndex.term+"\t"+tIndex.count);
            System.out.println(tIndex.head.toString());
        }
    }
    //Method to sort all indices in the index by count. Used for getTopK
    public static ArrayList<IndexList> sortByCount(ArrayList<IndexList> a)
    {
        Collections.sort(a, new Comparator<IndexList>() {
            public int compare(IndexList a1, IndexList a2) {
                if(a1.count > a2.count)
                    return -1;
                else if (a1.count < a2.count)
                    return +1;
                else
                    return 0;
            }});
        return a;
    }
    Method to find sort indices in the index. used by getPostings
    public static ArrayList<IndexList> sortedByTF(ArrayList<IndexList> newIndex){
        for (IndexList tIndex : newIndex) 
        {    
            int size = 0;
            
            for(Postings p = tIndex.head; p != null; p = p.next)
                size++;
            
            boolean flag = true;
            String tempDocID;
            int tempTimes;
            while ( flag )
            {
                flag= false;
                for(int j=0;  j < size -1;  j++ )
                {
                    Postings p1 = tIndex.head.getAt(tIndex.head, j);
                    Postings p2 = tIndex.head.getAt(tIndex.head, j+1);

                    if ( (tIndex.head.getAt(tIndex.head, j)).getTimes() < (tIndex.head.getAt(tIndex.head, j+1)).getTimes() )
                    {
                        tempDocID = p1.docID;
                        tempTimes = p1.times;
                        p1.docID = p2.docID;
                        p1.times = p2.times;
                        p2.docID = tempDocID;
                        p2.times = tempTimes;
                        flag = true;
                    }
                }
            }
        }
        return newIndex;
    }

    public static String sortedByTF_list_desc(IndexList newIndex){

        int size = 0;
        for(Postings p = newIndex.head; p != null; p = p.next)
            size++;

        boolean flag = true;
        String tempDocID;
        int tempTimes;
        while ( flag )
        {
            flag= false;
            for(int j=0;  j < size -1;  j++ )
            {
                Postings p1 = newIndex.head.getAt(newIndex.head, j);
                Postings p2 = newIndex.head.getAt(newIndex.head, j+1);

                if ( (newIndex.head.getAt(newIndex.head, j)).getTimes() < (newIndex.head.getAt(newIndex.head, j+1)).getTimes() )
                {
                    tempDocID = p1.docID;
                    tempTimes = p1.times;
                    p1.docID = p2.docID;
                    p1.times = p2.times;
                    p2.docID = tempDocID;
                    p2.times = tempTimes;
                    flag = true;
                }
            }
        }
        String buf="";
        for(Postings p = newIndex.head; p != null; p = p.next){
           buf+=p.docID+", ";
       }

        return buf;
    }
   // Method to find sort indices in the index by document frequency. used by getPostings

    public static String sortedByDF_list(IndexList newIndex){

        int size = 0;
        for(Postings p = newIndex.head; p != null; p = p.next)
            size++;

        boolean flag = true;
        String tempDocID;
        int tempTimes;
        while ( flag )
        {
            flag= false;
            for(int j=0;  j < size -1;  j++ )
            {
                Postings p1 = newIndex.head.getAt(newIndex.head, j);
                Postings p2 = newIndex.head.getAt(newIndex.head, j+1);

                if ( (newIndex.head.getAt(newIndex.head, j)).getDocID() < (newIndex.head.getAt(newIndex.head, j+1)).getDocID() )
                {
                    tempDocID = p1.docID;
                    tempTimes = p1.times;
                    p1.docID = p2.docID;
                    p1.times = p2.times;
                    p2.docID = tempDocID;
                    p2.times = tempTimes;
                    flag = true;
                }
            }
        }
        String buf="";
        for(Postings p = newIndex.head; p != null; p = p.next){
           buf+=p.docID+", ";
       }

        return buf;
    }
    
    public static String sortedByDF_list1(IndexList newIndex){

        int size = 0;
        for(Postings p = newIndex.head; p != null; p = p.next)
            size++;

        boolean flag = true;
        String tempDocID;
        int tempTimes;
        while ( flag )
        {
            flag= false;
            for(int j=0;  j < size -1;  j++ )
            {
                Postings p1 = newIndex.head.getAt(newIndex.head, j);
                Postings p2 = newIndex.head.getAt(newIndex.head, j+1);

                if ( (newIndex.head.getAt(newIndex.head, j)).getDocID() > (newIndex.head.getAt(newIndex.head, j+1)).getDocID() )
                {
                    tempDocID = p1.docID;
                    tempTimes = p1.times;
                    p1.docID = p2.docID;
                    p1.times = p2.times;
                    p2.docID = tempDocID;
                    p2.times = tempTimes;
                    flag = true;
                }
            }
        }
        String buf="";
        for(Postings p = newIndex.head; p != null; p = p.next){
           buf+=p.docID+", ";
       }

        return buf;
    }
    
    public static IndexList sortedByTF_list_asce(IndexList newIndex){

        int size = 0;
        for(Postings p = newIndex.head; p != null; p = p.next)
            size++;

        boolean flag = true;
        String tempDocID;
        int tempTimes;
        while ( flag )
        {
            flag= false;
            for(int j=0;  j < size -1;  j++ )
            {
                Postings p1 = newIndex.head.getAt(newIndex.head, j);
                Postings p2 = newIndex.head.getAt(newIndex.head, j+1);

                if ( (newIndex.head.getAt(newIndex.head, j)).getTimes() < (newIndex.head.getAt(newIndex.head, j+1)).getTimes() )
                {
                    tempDocID = p1.docID;
                    tempTimes = p1.times;
                    p1.docID = p2.docID;
                    p1.times = p2.times;
                    p2.docID = tempDocID;
                    p2.times = tempTimes;
                    flag = true;
                }
            }
        }
        String buf="";
        for(Postings p = newIndex.head; p != null; p = p.next){
           buf+=p.docID+"-"+p.times+", ";
       }

        return newIndex;
    }
    
    public static IndexList sortedByDF_list_asce(IndexList newIndex){

        int size = 0;
        for(Postings p = newIndex.head; p != null; p = p.next)
            size++;

        boolean flag = true;
        String tempDocID;
        int tempTimes;
        while ( flag )
        {
            flag= false;
            for(int j=0;  j < size -1;  j++ )
            {
                Postings p1 = newIndex.head.getAt(newIndex.head, j);
                Postings p2 = newIndex.head.getAt(newIndex.head, j+1);

                if ( (newIndex.head.getAt(newIndex.head, j)).getDocID() < (newIndex.head.getAt(newIndex.head, j+1)).getDocID() )
                {
                    tempDocID = p1.docID;
                    tempTimes = p1.times;
                    p1.docID = p2.docID;
                    p1.times = p2.times;
                    p2.docID = tempDocID;
                    p2.times = tempTimes;
                    flag = true;
                }
            }
        }
        String buf="";
        for(Postings p = newIndex.head; p != null; p = p.next){
           buf+=p.docID+", ";
       }

        return newIndex;
    }
    
    public static IndexList sortedByDF_list_desc(IndexList newIndex){

        int size = 0;
        for(Postings p = newIndex.head; p != null; p = p.next)
            size++;

        boolean flag = true;
        String tempDocID;
        int tempTimes;
        while ( flag )
        {
            flag= false;
            for(int j=0;  j < size -1;  j++ )
            {
                Postings p1 = newIndex.head.getAt(newIndex.head, j);
                Postings p2 = newIndex.head.getAt(newIndex.head, j+1);

                if ( (newIndex.head.getAt(newIndex.head, j)).getDocID() > (newIndex.head.getAt(newIndex.head, j+1)).getDocID() )
                {
                    tempDocID = p1.docID;
                    tempTimes = p1.times;
                    p1.docID = p2.docID;
                    p1.times = p2.times;
                    p2.docID = tempDocID;
                    p2.times = tempTimes;
                    flag = true;
                }
            }
        }
        String buf="";
        for(Postings p = newIndex.head; p != null; p = p.next){
           buf+=p.docID+", ";
       }

        return newIndex;
    }


    //Method to get results by doing TAAT Comparisons in the index. Returns a string ready to be written to file


    public static String termAtATimeOR(String[] terms, HashMap<String, IndexList> indexMap){
        IndexList temp = new IndexList();
        TAATOrCount=0;
        int comp=0;
        long time = System.currentTimeMillis();

        for(String term :terms)
        {
            IndexList addToTemp = indexMap.get(term);
            if(addToTemp != null)
            {    for(Postings p = addToTemp.head; p != null; p = p.next)
                    temp = addTAATOr(p.docID, p.times, temp);
            }
            else
                System.out.println("term not found");
        }
        String results[]= sortedByDF_list(temp).split(", ");
        time = System.currentTimeMillis() - time;

        String last = "FUNCTION: termAtATimeQueryOr ";
        for(String term :terms)
        {
            last += term + ", ";
        }
        
        last += "\n" + results.length + " documents are found \n";
        for(String result:results){
            last += result + ", ";
        }
        last += "\n"+TAATOrCount + " comparisons are made\n";
        last += time + " milliseconds are used\n";
        last += termAtATimeOROptimized(terms, indexMap) + " comparisons are made with optimization";

        return last;
    }
    //Method to get results by doing Optimized TAAT Comparisons in the index. Returns number of comparisons

    public static int termAtATimeOROptimized(String[] terms, HashMap<String, IndexList> indexMap){
        IndexList temp = new IndexList();
        TAATOrCountOpt = 0;
        int comp = 0;
        for(String term :terms)
        {
            IndexList addToTemp = indexMap.get(term);
            addToTemp = sortedByTF_list_asce(addToTemp);
            if(addToTemp != null)
            {    for(Postings p = addToTemp.head; p != null; p = p.next)
                    temp = addTAATOr(p.docID, p.times, temp);
            }
            else
                System.out.println("term not found");
        }
        
        return TAATOrCountOpt;
    }
    //Method to get results by doing TAAT Comparisons in the index. Returns a string ready to be printed

    public static String termAtATimeAnd(String[] terms, HashMap<String, IndexList> indexMap){
        IndexList temp = new IndexList();
        int comp = 0;
        long time = System.currentTimeMillis();
        for(int i =0; i<terms.length;i++)
        {
            IndexList addToTemp = indexMap.get(terms[i]);
//            addToTemp = sortedByTF_list_asce(addToTemp);
            
            if(i==0){
                if(addToTemp != null)
                {    for(Postings p = addToTemp.head; p != null; p = p.next)
                        temp = addTAATOr(p.docID, p.times, temp);
                }
                else
                    System.out.println("term not found");
            }
            else
            {
                if(temp.head == null){
                    return null;
                }
                else{
                    boolean isPresent=false;
                    for(Postings p = temp.head; p!=null; p = p.next){
                        for(Postings m = addToTemp.head; m != null; m = m.next){
                            if(p.docID.equals(m.docID)){
                                isPresent = true;
                            }
                            if(!isPresent)
                                comp++;

                    }
                        if(!isPresent){
                            if(p.next != null)
                                p.next.prev = p.prev;
                            else{
                                temp.last = p.prev;
                                if(temp.last != null)
                                    temp.last.next = null;
                            }
                            if(p.prev != null)
                            {
                                p.prev.next = p.next;
                            }
                            else{
                                temp.head = p.next;
                                if(temp.head != null){
                                    temp.head.prev = null;
                                }
                            }
                        }
                        isPresent = false;
                    }
                }
                
            }
        }
        time = (System.currentTimeMillis() - time);
        int c=0;
        for(Postings p = temp.head; p!=null;p=p.next)
            c++;
        String TAATAnd = "FUNCTION: termAtATimeQueryAnd ";
        for(String term : terms){
            TAATAnd += term + ", ";
        }
        TAATAnd += "\n"+c+ " documents are found : " + sortedByDF_list(temp) + "\n";
        TAATAnd += comp + " comparisons are made\n";
        TAATAnd += time + " milliseconds are used\n";
        TAATAnd += termAtATimeAndOptimized(terms, indexMap) + " comparisons are made with optimization";

        return TAATAnd;
    }
    //Method to get results by doing Optimized TAAT Comparisons in the index. Returns number of comparisons

    public static int termAtATimeAndOptimized(String[] terms, HashMap<String, IndexList> indexMap){
        IndexList temp = new IndexList();
        int comp = 0;
        long time = System.currentTimeMillis();
        for(int i =0; i<terms.length;i++)
        {
            IndexList addToTemp = indexMap.get(terms[i]);
            addToTemp = sortedByDF_list_asce(addToTemp);
            
            if(i==0){
                if(addToTemp != null)
                {    for(Postings p = addToTemp.head; p != null; p = p.next)
                        temp = addTAATOr(p.docID, p.times, temp);
                }
                else
                    System.out.println("term not found");
            }
            else
            {
                if(temp.head == null){
                    return 0;
                }
                else{
                    boolean isPresent=false;
                    for(Postings p = temp.head; p!=null; p = p.next){
                        for(Postings m = addToTemp.head; m != null; m = m.next){
                            if(p.docID.equals(m.docID)){
                                isPresent = true;
                            }
                            if(!isPresent)
                                comp++;

                    }
                        if(!isPresent){
                            if(p.next != null)
                                p.next.prev = p.prev;
                            else{
                                temp.last = p.prev;
                                if(temp.last != null)
                                    temp.last.next = null;
                            }
                            if(p.prev != null)
                            {
                                p.prev.next = p.next;
                            }
                            else{
                                temp.head = p.next;
                                if(temp.head != null){
                                    temp.head.prev = null;
                                }
                            }
                        }
                        isPresent = false;
                    }
                }
                
            }
        }
        time = (System.currentTimeMillis() - time);
        int c=0;
        for(Postings p = temp.head; p!=null;p=p.next)
            c++;
        String TAATAnd = "FUNCTION: termAtATimeQueryAnd ";
        for(String term : terms){
            TAATAnd += term + ", ";
        }
        TAATAnd += "\n"+c+ " documents are found : " + sortedByDF_list(temp) + "\n";
        TAATAnd += comp + " comparisons are made\n";
        TAATAnd += time + " milliseconds are used\n";

        return comp;
    }
    //Method to get results by doing DAATAnd Comparisons in the index. Returns a string ready to be printed

    public static String documentAtATimeAnd(String[] terms, HashMap<String, IndexList> indexMap){
        long time = System.currentTimeMillis();
        DAATAndCount =0;
        ArrayList<IndexList> listOfLL = new ArrayList<>();
        for(int i =0; i<terms.length;i++)
        {
            IndexList addToTemp = sortedByDF_list_desc(indexMap.get(terms[i]));
            listOfLL.add(addToTemp);
        }
        ArrayList<String> s;
        s = documentAnd(listOfLL);
//        System.out.println(s.toString());
        time = System.currentTimeMillis() - time;
        String DAATAnd = "FUNCTION: documentAtATimeQueryAnd ";
        for(String term: terms){
            DAATAnd += term + ", ";
        }
        
        DAATAnd += "\n"+s.size()+" documents are found : \n";

        for(String term : s){
            DAATAnd += term + ", ";
        }
        DAATAnd += "\n"+ DAATAndCount + " comparisons are made";
        DAATAnd += "\n"+time + " milliseconds are used\n";

        return DAATAnd;
    }
    //Method to get results by doing DAATOr Comparisons in the index. Returns a string ready to be printed

    public static String documentAtATimeOr(String[] terms, HashMap<String, IndexList> indexMap){
        long time = System.currentTimeMillis();
        DAATOrCount = 0;
        ArrayList<IndexList> listOfLL = new ArrayList<>();
        for(int i =0; i<terms.length;i++)
        {
            IndexList addToTemp = sortedByDF_list_desc(indexMap.get(terms[i]));
            listOfLL.add(addToTemp);
        }
        ArrayList<String> s;
        s = documentOr(listOfLL);
//        System.out.println(s.toString());
        time = System.currentTimeMillis() - time;
        String DAATOr = "FUNCTION: documentAtATimeQueryOr ";
        for(String term: terms){
            DAATOr += term + ", ";
        }
        
        DAATOr += "\n"+s.size()+" documents are found : \n";

        for(String term : s){
            DAATOr += term + ", ";
        }
        DAATOr += "\n"+ DAATOrCount + " comparisons are made";
        DAATOr += "\n"+time + " milliseconds are used\n";

        return DAATOr;
    }


    //Below are supplementary methods for DAAT and TAAT operations
    public static IndexList addTAATOr(String docID,int times,IndexList abc)
    {
//        c1.addTAATOrCount = 0;
        Postings temp = new Postings(docID,times,null,null);
        if(abc.head == null)
        {
//            TAATOrCount++;
            abc.head = new Postings();
            abc.head.docID = docID;
            abc.head.times = times;
            abc.last = abc.head;
        }
        else
        {
            if(!ifInList(docID, abc))
            {            
                abc.last.next = temp;
                temp.prev = abc.last;
                temp.next = null;
                abc.last = temp;
                
            }
        }
        return abc;
    }
   
    public static boolean ifInList(String docID,IndexList abc)
    {
        for(Postings p = abc.head; p != null; p = p.next)
        {
            TAATOrCount++;
            TAATOrCountOpt++;
            if(p.docID.equals(docID))
                return true;
        }
        return false;
    }
    
    public static ArrayList<String> documentAnd(ArrayList<IndexList> al){
        ArrayList<String> res = new ArrayList<>();
        while(allNotNull(al))
        {
            if(allAreEqual(al)){
                
                res.add(al.get(0).head.docID);
                al = incrementLowest(al);

            }
            else
            {
                al = incrementLowest(al);
            }
        }
        
        return res;
    } 
    
    public static ArrayList<String> documentOr(ArrayList<IndexList> al){
        ArrayList<String> res = new ArrayList<>();
        String low = "0";
        while(atLeastOneNotNull(al))
        {
            low="0";
            boolean put = false;
            for(int i =0;i<al.size();i++){
                if(al.get(i).head != null)
                    if(Integer.parseInt(al.get(i).head.docID)<Integer.parseInt(low) || Integer.parseInt(low) == 0){
                        low = al.get(i).head.docID;
                        DAATOrCount++;
                    }
            }
            for(int i =0;i<al.size();i++){
                if(!put && al.get(i).head != null && Integer.parseInt(al.get(i).head.docID) == Integer.parseInt(low)){
                    res.add(al.get(i).head.docID);
                    al.get(i).head = al.get(i).head.next;
                    put = true;
                    DAATOrCount++;
                }
                else if(put && al.get(i).head != null && Integer.parseInt(al.get(i).head.docID) == Integer.parseInt(low)){
                    al.get(i).head = al.get(i).head.next;
                }
            }
        }
        
        return res;
    } 
    
    public static boolean allNotNull(ArrayList<IndexList> al){
        boolean flag = true;
        for(IndexList a : al){
            if(a.head == null)
                flag=false;
                break;
        }
        return flag;
    }
    
    public static boolean atLeastOneNotNull(ArrayList<IndexList> al){
        boolean flag = false;
        for(IndexList a : al){
            if(a.head != null)
                flag=true;
                break;
        }
        return flag;
    }
    
    public static boolean allAreEqual(ArrayList<IndexList> al){
//        DAATComp=0;
        boolean flag = true;
        for(int i=0;i< al.size()-1;i++){
            if(al.get(i).head == null || al.get(i+1).head== null){
                flag=false;
                DAATAndCount++;
                break;
                }
            if(!(al.get(i).head.docID.equals(al.get(i+1).head.docID))){
                flag = false;
                DAATAndCount++;
                break;
            }
//            DAATComp++;
        }

        return flag;
    }
    
    public static ArrayList<IndexList> incrementLowest(ArrayList<IndexList> al){
        int low =0;
        for(int i=0; i<al.size();i++){
            if(al.get(i).head == null)
                continue;
            int current = Integer.parseInt(al.get(i).head.docID);
            if(current< low || low == 0)
            {
                low=current;
            }
        }
         for(int i=0; i<al.size();i++){
             if(al.get(i).head == null)
                continue;
             if(Integer.parseInt(al.get(i).head.docID) == low){
                al.get(i).head = al.get(i).head.next;
             }
         }
        return al;
    }
   
}
