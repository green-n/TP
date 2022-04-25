/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import entity.Gruppyi;
//import entity.items;
import entity.Studentyi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import utils.NewHibernateUtil;

/**
 *
 * @author 19567
 */
public class Lab1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SessionFactory sf =  NewHibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction transaction = s.beginTransaction();
   /*   Studentyi d = new Studentyi( nomerZachetki, gruppyi, familiya, imya, otchestvo, gorod,  adres,  tel,  status, statusDate);
    s.persist(d);
 //   s.save(d);
*/
        transaction.commit();
        List <Gruppyi> grs = s.createQuery("from Gruppyi s").list();
        List <Studentyi> q = s.createQuery("from Studentyi s").list();
        //List <items> i = s.createQuery("from items s").list();
        Transaction t = s.beginTransaction();
//      for (Studentyi u : q)
//     {
////          System.out.print("works");
//          System.out.print(u.getImya()+"/");
////          u.setImya(new String());
//          s.update(u);
//     }
//for(items f : i){
//    System.out.println(f.getName());
//}

        int num = 0;
        for (Gruppyi g : grs){
            for(Studentyi u : q){
                if(u.getStatus().equals("expelled") && g.getShifr().equals(u.getGruppyi().getShifr())){num++;}
            }
            System.out.println("In group "  + g.getNazvanie()+ " EXPELLED " + num + " students");
            num=0;
        }
        Date date = new Date();
        long years;
        int debug;
        for(Studentyi u : q){
            years = (date.getYear() - u.getStatusDate().getYear());
            //debug =( date.getTime() - u.getStatus().getTime())/years;
            if(years>4){System.out.println(u.getImya()+" " + " " + u.getStatusDate());}
        }
        long count = 0;
        for(Studentyi u : q){
            if(u.getNomerZachetki()>count){count = u.getNomerZachetki();}
        }
        count++;

//
        for(long i = count;i<count+25;i++){
            Studentyi st = new Studentyi(i,grs.get(0),"Ivanov","NIck","Pavlovich","VItebsk","Moskow ave., 12, 4","+375333645324","enrolled",new Date(120,10,15));
            s.save(st);
        }
        t.commit();

        //for each element of groups
        List <Gruppyi> grs_2 = s.createQuery("from Gruppyi s").list();
        boolean is_there_any_group_with_more_than_25_students=true;
        while (is_there_any_group_with_more_than_25_students) {
            grs_2 = s.createQuery("from Gruppyi s").list();
            count = 0;
            for (Gruppyi g : grs_2) {

                if (g != null && count_students_in_group(g) > 25) {
                    //System.out.println("In group " + g.getNazvanie() + " " + " amount of students " + count_students_in_group(g));
                    separation_in_two_groups(g);
                    count++;
                }
            }
            //System.out.println("count = " + count);
            if (count == 0) {
                is_there_any_group_with_more_than_25_students = false;
            }

        }





        s.flush();
        s.close();
        sf.close();

        // TODO code application logic here
    }










    // create function for separating students from given group in two new groups










    public static void  separation_in_two_groups(Gruppyi g){
        SessionFactory sf =  NewHibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction transaction = s.beginTransaction();
        Date curentDate = new Date();
        //SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        List <Studentyi> q = s.createQuery("from Studentyi s").list();
        List <Gruppyi> grs = s.createQuery("from Gruppyi g").list();
        String groupName = g.getNazvanie();
        //creating string from groupName that starts from beggining and ends at "-"
        String partialName = groupName.substring(0,groupName.indexOf("-"));
        String secomdPartialName = groupName.substring(groupName.indexOf("-")+1,groupName.length());
        //consverting string to int
        int partialNameInt = Integer.parseInt(secomdPartialName);
        //creating new name for second group
        boolean isThisGroupExist = true;
        String newGroupName = "";
        while (isThisGroupExist) {
            newGroupName = partialName + "-" + (partialNameInt + 1);
            isThisGroupExist = false;
            for (Gruppyi gg : grs) {
                if (gg.getNazvanie().equals(newGroupName)) {
                    isThisGroupExist = true;
                    partialNameInt++;
                }
            }
        }

        int maxShifr = 0;
        for (Gruppyi gg : grs) {
          maxShifr++;
        }
        maxShifr++;


        Gruppyi newGroup = new Gruppyi(newGroupName,new Date(120,10,15),g.getKodPlana(),"created",curentDate);
        newGroup.setShifr(maxShifr);
        s.save(newGroup);



        //adding students to new list
        List<Studentyi> newList = new ArrayList<Studentyi>();
        for (Studentyi st : q){
            if (st.getGruppyi().getNazvanie().equals(g.getNazvanie())){
                newList.add(st);
            }
        }

        //adding random students to new group
        for (Studentyi st : newList) {
            if (Math.random() > 0.5) {
                st.setGruppyi(newGroup);
            }
        }

        //saving new list of students
        for (Studentyi st : newList) {
            s.update(st);
        }
        //saving new group
        s.update(newGroup);
//        List <Gruppyi> grf = s.createQuery("from Gruppyi g").list();
//        for (Gruppyi gg : grf) {
//            System.out.println(gg.getNazvanie());
//        }
        // show all students in new group
        int count = 0;
        List <Studentyi> stf = s.createQuery("from Studentyi s").list();
        for (Studentyi st : stf) {
            if(st.getGruppyi().getNazvanie().equals(newGroupName)){
                count++;
            }
        }

        g.setStatusDate(curentDate);

        System.out.println("group " + newGroupName + " contains " + count + " students");
            transaction.commit();
            s.close();
    }

    //function for counting students in group

    public static int count_students_in_group(Gruppyi g){
        SessionFactory sf =  NewHibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction transaction = s.beginTransaction();

        List <Studentyi> q = s.createQuery("from Studentyi s").list();


        int count = 0;
        for(Studentyi u : q){
            if(u.getGruppyi().getShifr().equals(g.getShifr())){count++;}
        }

        return count;


    }



}


