package com.imdeity.deityapi.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Do not reference this class Directly. All interactions can be done through
 * UtilAPI
 * 
 * @author vanZeben
 */
public class FileMgmt {
    
    /**
     * Checks if a file exists, if not, it will be created
     * 
     * @param files
     * @throws IOException
     */
    public void checkFiles(String[] files) throws IOException {
        for (String file : files) {
            File f = new File(file);
            if (!(f.exists() && f.isFile())) {
                f.createNewFile();
            }
        }
    }
    
    /**
     * Checks if a folder exists, if not, it will be created
     * 
     * @param folders
     */
    public void checkFolders(String[] folders) {
        for (String folder : folders) {
            File f = new File(folder);
            if (!(f.exists() && f.isDirectory())) {
                f.mkdir();
            }
        }
    }
    
    /**
     * Checks if a yml file exists
     * 
     * @param filePath
     * @param defaultRes
     * @return
     * @throws IOException
     */
    public File CheckYMLexists(String filePath, String defaultRes) throws IOException {
        // open a handle to yml file
        File file = new File(filePath);
        if (file.exists()) { return file; }
        
        String resString;
        
        // create the file as it doesn't exist
        this.checkFiles(new String[] { filePath });
        
        // file didn't exist so we need to populate a new one
        resString = this.convertStreamToString(defaultRes);
        if (resString != null) {
            // Save the string to file (*.yml)
            
            this.stringToFile(resString, filePath);
            
        }
        return file;
    }
    
    /**
     * Pass a file name and it will convert its contents to a string
     * 
     * @param name
     * @return
     * @throws IOException
     */
    public String convertStreamToString(String name) throws IOException {
        if (name != null) {
            Writer writer = new StringWriter();
            InputStream is = FileMgmt.class.getResourceAsStream(name);
            
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (IOException e) {
                System.out.println("Exception ");
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
    
    /**
     * Copies a directory
     * 
     * @param sourceLocation
     * @param targetLocation
     * @throws IOException
     */
    public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (String element : children) {
                this.copyDirectory(new File(sourceLocation, element), new File(targetLocation, element));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            // Copy the bits from in stream to out stream.
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
    
    /**
     * Downloads a file from a url
     * 
     * @param sourceLocation
     * @param destinationLocation
     */
    public void downloadFile(String sourceLocation, String destinationLocation) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL(sourceLocation);
            out = new BufferedOutputStream(new FileOutputStream(destinationLocation));
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];
            
            long numWritten = 0L;
            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * System file seperator
     * 
     * @return
     */
    public String fileSeparator() {
        return System.getProperty("file.separator");
    }
    
    /**
     * Moves a file
     * 
     * @param sourceFile
     * @param targetLocation
     * @throws IOException
     */
    public void moveFile(File sourceFile, String targetLocation) throws IOException {
        if (sourceFile.isFile()) {
            // check for an already existing file of that name
            File f = new File((sourceFile.getParent() + this.fileSeparator() + targetLocation));
            if ((f.exists() && f.isFile())) {
                f.delete();
            }
            // Move file to new directory
            boolean success = sourceFile.renameTo(new File((sourceFile.getParent() + this.fileSeparator() + targetLocation), sourceFile.getName()));
            if (!success) {
                // File was not successfully moved
            }
        }
    }
    
    /**
     * Deletes a file
     * 
     * @param filePath
     * @param recursive
     * @return
     */
    public boolean delete(String filePath, boolean recursive) {
        File file = new File(filePath);
        if (!file.exists()) { return true; }
        
        if (!recursive || !file.isDirectory()) return file.delete();
        
        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
            if (!delete(filePath + File.separator + list[i], true)) return false;
        }
        
        return file.delete();
    }
    
    /**
     * Zip a folder
     * 
     * @param sourceFolder
     * @param zipStream
     * @throws IOException
     */
    public void recursiveZipDirectory(File sourceFolder, ZipOutputStream zipStream) throws IOException {
        String[] dirList = sourceFolder.list();
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;
        for (String element : dirList) {
            File f = new File(sourceFolder, element);
            if (f.isDirectory()) {
                this.recursiveZipDirectory(f, zipStream);
                continue;
            } else {
                FileInputStream input = new FileInputStream(f);
                ZipEntry anEntry = new ZipEntry(f.getPath());
                zipStream.putNextEntry(anEntry);
                while ((bytesIn = input.read(readBuffer)) != -1) {
                    zipStream.write(readBuffer, 0, bytesIn);
                }
                input.close();
            }
        }
    }
    
    /**
     * Writes a string to a file
     * 
     * @param source
     * @param FileName
     * @return
     * @throws IOException
     */
    public boolean stringToFile(String source, String FileName) throws IOException {
        
        try {
            
            BufferedWriter out = new BufferedWriter(new FileWriter(FileName));
            
            source.replaceAll("\n", System.getProperty("line.separator"));
            
            out.write(source);
            out.close();
            return true;
            
        } catch (IOException e) {
            System.out.println("Exception ");
            return false;
        }
    }
    
    /**
     * Zip multiple folders
     * 
     * @param sourceFolders
     * @param destination
     * @throws IOException
     */
    public void zipDirectories(File[] sourceFolders, File destination) throws IOException {
        ZipOutputStream output = new ZipOutputStream(new FileOutputStream(destination));
        for (File sourceFolder : sourceFolders) {
            this.recursiveZipDirectory(sourceFolder, output);
        }
        output.close();
    }
    
    /**
     * Zip a single directory
     * 
     * @param sourceFolder
     * @param destination
     * @throws IOException
     */
    public void zipDirectory(File sourceFolder, File destination) throws IOException {
        ZipOutputStream output = new ZipOutputStream(new FileOutputStream(destination));
        this.recursiveZipDirectory(sourceFolder, output);
        output.close();
    }
    
}
