package it.pagopa.pn.cucumber.utils;

import org.springframework.core.io.InputStreamSource;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compress {

    private static int BUFFER = 2048;
    private String[] files;
    private InputStream[] filesJson;
    private String zipFile;

    public Compress(InputStream[] filesJson,String[] files, String zipFile) {
        this.filesJson=filesJson;
        this.files = files;
        this.zipFile = zipFile;
    }

    public void zip() throws IOException {
        BufferedInputStream origin = null;
        FileOutputStream dest = new FileOutputStream(zipFile);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        byte data[] = new byte[BUFFER];
        System.out.println(zipFile);

        for (int i = 0; i < files.length; i++) {
            FileInputStream fi = new FileInputStream(files[i]);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }

        for (int i = 0; i < filesJson.length; i++) {
            origin = new BufferedInputStream(filesJson[i], BUFFER);
            ZipEntry entry = new ZipEntry(i==0?"destinatario":"delgato");
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }

        out.close();

    }



    public static void main(String[] args) {

        // These are the files to include in the ZIP file
        String[] filenames = new String[]{"AvvisoPagoPA.pdf", "AvvisoPagoPA1.pdf"};

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
        // Create the ZIP file
            String outFilename = "outfile.zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

        // Compress the files
            for (int i = 0; i < filenames.length; i++) {
                FileInputStream in = new FileInputStream(filenames[i]);

// Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filenames[i]));

// Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

// Complete the entry
                out.closeEntry();
                in.close();
            }

// Complete the ZIP file
            out.close();
        } catch (IOException e) {
        }

//Secondo approccio..............
        try {
            String[] files = {"AvvisoPagoPA.pdf", "AvvisoPagoPA1.pdf"};
            InputStream[] filesJson = {};
            Compress c = new Compress(filesJson,files, "file.zip");
            c.zip();
        } catch (IOException e) {
        }
        /**

         {
         // il metodo try/catch di permette di gestire le eccezioni, se qualcosa
         // non funziona in try allora catch notifica l'errore
         try
         {
         // definiamo l'output previsto che sarà un file in formato zip
         ZipOutputStream out = new ZipOutputStream(new
         BufferedOutputStream(new FileOutputStream("documento.zip")));

         // definiamo il buffer per lo stream di bytes
         byte[] data = new byte[1000];

         // indichiamo il nome del file che subirà la compressione
         BufferedInputStream in = new BufferedInputStream
         (new FileInputStream("AvvisoPagoPA.pdf"));
         int count;

         // processo di compressione
         out.putNextEntry(new ZipEntry("documento.zip"));
         while((count = in.read(data,0,1000)) != -1)
         {
         out.write(data, 0, count);
         }
         in.close();
         out.flush();
         out.close();

         // conferma della compressione
         System.out.println("File zippato con successo");
         }
         catch(Exception e)
         {
         e.printStackTrace();
         }

         } **/
    }

}