package App.ReadingAndWriting;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipAndUnzip {
    private static final int BUFFER_SIZE = 4096;

    public static void zipFolder(String zipFilePath, String pathName){
        byte[] buffer = new byte[1024];

        try{
            FileOutputStream fos = new FileOutputStream(zipFilePath + "s");
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze= new ZipEntry(pathName + "Circles.cir");
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(zipFilePath + "\\" + pathName + "Circles.cir");

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            ze= new ZipEntry(pathName + "Traj.line");
            zos.putNextEntry(ze);
            in = new FileInputStream(zipFilePath + "\\" + pathName + "Traj.line");

            int len2;
            while ((len2 = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len2);
            }
            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();

            deleteFolderContent(zipFilePath,pathName);
            deleteAndOrRename(zipFilePath, zipFilePath, zipFilePath,true,true);

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    private static void deleteFolderContent(String zipFilePath, String name){
        System.gc();
        File tempFolder = new File(zipFilePath + "\\" + name + "Circles.cir");
        tempFolder.setExecutable(true);
        tempFolder.setReadable(true);
        tempFolder.setWritable(true);
        tempFolder.delete();
        File tempFolder2 = new File(zipFilePath + "\\" + name + "Traj.line");
        tempFolder2.setExecutable(true);
        tempFolder2.setReadable(true);
        tempFolder2.setWritable(true);
        tempFolder2.delete();
    }
    public static void deleteAndOrRename(String deletePath, String renamePath, String renameFilePathTo, boolean delete, boolean rename){
        try {
            if(delete) {
                System.gc();
                File tempFolder3 = new File(deletePath);
                tempFolder3.setExecutable(true);
                tempFolder3.setReadable(true);
                tempFolder3.setWritable(true);
                tempFolder3.delete();
            }
            if(rename) {
                File zipFile = new File(renamePath + "s");
                File renamedFile = new File(renameFilePathTo);
                zipFile.setExecutable(true);
                zipFile.setReadable(true);
                zipFile.setWritable(true);
                zipFile.renameTo(renamedFile);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void unzipFolder(String zipFilePath, String extractedFilePath){
        try {
            File destDir = new File(extractedFilePath);
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = extractedFilePath + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath){
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
            bos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
