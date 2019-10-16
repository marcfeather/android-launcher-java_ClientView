package com.coretera.clientview.utility;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class UnzipUtil
{
//    private String zipFile;
//    private String location;

//    public UnzipUtil(String zipFile, String location)
//    {
//        this.zipFile = zipFile;
//        this.location = location;
//
//        dirChecker("");
//    }

//    public void unzip()
//    {
//        try
//        {
//            FileInputStream fin = new FileInputStream(zipFile);
//            ZipInputStream zin = new ZipInputStream(fin);
//            ZipEntry ze = null;
//            while ((ze = zin.getNextEntry()) != null)
//            {
//                Log.v("Decompress", "Unzipping " + ze.getName());
//
//                if(ze.isDirectory())
//                {
//                    dirChecker(ze.getName());
//                }
//                else
//                {
//                    FileOutputStream fout = new FileOutputStream(location + ze.getName());
//
//                    byte[] buffer = new byte[8192];
//                    int len;
//                    while ((len = zin.read(buffer)) != -1)
//                    {
//                        fout.write(buffer, 0, len);
//                    }
//                    fout.close();
//
//                    zin.closeEntry();
//
//                }
//
//            }
//            zin.close();
//        }
//        catch(Exception e)
//        {
//            Log.e("Decompress", "unzip", e);
//        }
//
//    }
//
//    private void dirChecker(String dir)
//    {
//        File f = new File(location + dir);
//        if(!f.isDirectory())
//        {
//            f.mkdirs();
//        }
//    }


    public void doUnzip(String inputZipFile, String destinationDirectory)
            throws IOException {
        int BUFFER = 2048;
        List zipFiles = new ArrayList();
        File sourceZipFile = new File(inputZipFile);
        File unzipDestinationDirectory = new File(destinationDirectory);
        unzipDestinationDirectory.mkdir();

        ZipFile zipFile;
        // Open Zip file for reading
        zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

        // Create an enumeration of the entries in the zip file
        Enumeration zipFileEntries = zipFile.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

            String currentEntry = entry.getName();

            File destFile = new File(unzipDestinationDirectory, currentEntry);
//          destFile = new File(unzipDestinationDirectory, destFile.getName());

            if (currentEntry.endsWith(".zip")) {
                zipFiles.add(destFile.getAbsolutePath());
            }

            // grab file's parent directory structure
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            try {
                // extract file if not a directory
                if (!entry.isDirectory()) {
                    BufferedInputStream is =
                            new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest =
                            new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        zipFile.close();

        for (Iterator iter = zipFiles.iterator(); iter.hasNext();) {
            String zipName = (String)iter.next();
            doUnzip(
                    zipName,
                    destinationDirectory +
                            File.separatorChar +
                            zipName.substring(0,zipName.lastIndexOf(".zip"))
            );
        }

    }
}
