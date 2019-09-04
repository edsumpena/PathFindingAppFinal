package App.ReadingAndWriting;

import java.io.*;
import java.util.ArrayList;

public class SerializeAndDeserialize {
    public static void serialize(ArrayList<Integer> circles, ArrayList<ArrayList<String>> lines, String filePath, String pathName){
        File file = new File(filePath + "\\" + pathName + ".path");
        file.getParentFile().mkdir();
        file.setExecutable(true);
        file.setReadable(true);
        file.setWritable(true);
        try {
            file.createNewFile();
        } catch (Exception e){

        }
        if (!file.exists()) {
            if (file.mkdir()) {
            } else {
            }
        }
        try{
            if (file.delete()) {
            } else {
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            FileOutputStream fos= new FileOutputStream(filePath + "\\" + pathName + "Circles.cir");
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(circles);
            oos.flush();
            oos.close();
            fos.flush();
            fos.close();
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        try{
            FileOutputStream fos2= new FileOutputStream(filePath + "\\" + pathName + "Traj.line");
            ObjectOutputStream oos2= new ObjectOutputStream(fos2);
            oos2.writeObject(lines);
            oos2.flush();
            oos2.close();
            fos2.flush();
            fos2.close();
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static ArrayList deserialize(String filePathAndName, Boolean stringArrayList){

        FileInputStream fis;
        ObjectInputStream ois;

        // creating List reference to hold AL values
        // after de-serialization
        if(stringArrayList) {
            ArrayList<ArrayList<String>> deseralizedStringArrayList = null;

            try {
                // reading binary data
                fis = new FileInputStream(filePathAndName);

                // converting binary-data to java-object
                ois = new ObjectInputStream(fis);

                // reading object's value and casting ArrayList<String>
                deseralizedStringArrayList = (ArrayList<ArrayList<String>>) ois.readObject();
            } catch (FileNotFoundException fnfex) {
                fnfex.printStackTrace();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            } catch (ClassNotFoundException ccex) {
                ccex.printStackTrace();
            }
            return deseralizedStringArrayList;
        } else {
            ArrayList<Integer> deseralizedIntArrayList = null;

            try {
                // reading binary data
                fis = new FileInputStream(filePathAndName);

                // converting binary-data to java-object
                ois = new ObjectInputStream(fis);

                // reading object's value and casting ArrayList<String>
                deseralizedIntArrayList = (ArrayList<Integer>) ois.readObject();
            } catch (FileNotFoundException fnfex) {
                fnfex.printStackTrace();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            } catch (ClassNotFoundException ccex) {
                ccex.printStackTrace();
            }
            return deseralizedIntArrayList;
        }
    }
}
