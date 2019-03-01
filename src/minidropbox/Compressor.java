package minidropbox;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor {
  
  
    public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
      if (fileToZip == null || !fileToZip.exists()) {
          return;
      }

      String zipEntryName = fileToZip.getName();
      if (parrentDirectoryName!=null && !parrentDirectoryName.isEmpty()) {
          zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
      }

      if (fileToZip.isDirectory()) {
          System.out.println("+" + zipEntryName);
          for (File file : fileToZip.listFiles()) {
              addDirToZipArchive(zos, file, zipEntryName);
          }
      } else {
          System.out.println("   " + zipEntryName);
          byte[] buffer = new byte[1024];
          FileInputStream fis = new FileInputStream(fileToZip);
          zos.putNextEntry(new ZipEntry(zipEntryName));
          int length;
          while ((length = fis.read(buffer)) > 0) {
              zos.write(buffer, 0, length);
          }
          zos.closeEntry();
          fis.close();
      }
  }
}

