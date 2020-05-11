import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.security.MessageDigest; 

class DuplicateFileFinder
{
  static MessageDigest md;
  static Map<Long, List<File>> filesGroupedBySize;
  static int minSize;
  
  void groupFiles(File file) throws Exception
  {
    for(File i : file.listFiles())
    {
      if(i.isFile())
      {
        long fileLength = i.length();
        if(fileLength <= minSize)
        {
          continue;
        }
        List<File> fileGroup = filesGroupedBySize.get(fileLength);
        if(fileGroup == null)
        {
          fileGroup = new ArrayList<>();
          filesGroupedBySize.put(fileLength, fileGroup);
        }
        fileGroup.add(i);
      }
      else
      {
        groupFiles(i);
      }
    }
  }

  void listDuplicates() throws Exception
  {
    Map<String, List<File>> dupFiles = new HashMap<>();
    for(Map.Entry<Long, List<File>> e : filesGroupedBySize.entrySet())
    {
      if(e.getValue().size() < 2)
      {
        continue;
      }
      for(File i : e.getValue())
      {
        String fileHash =  new String(  md.digest(Files.readAllBytes(Paths.get(i.getPath()))));
        List<File> fileGroup = dupFiles.get(fileHash); 
        if(fileGroup == null)
        {
          fileGroup = new ArrayList<>();
          dupFiles.put(fileHash, fileGroup);
        }
        fileGroup.add(i);
      }
    }

    for(Map.Entry<String, List<File>> e : dupFiles.entrySet())
    {
      if(e.getValue().size() < 2)
      {
        continue;
      }

      for(File i : e.getValue())
      {
        System.out.println(i.getAbsoluteFile());
      }
      System.out.println();
    }
  }

  void listFiles(File file) throws Exception
  {
    for(File i : file.listFiles())
    {
      if(i.isFile())
      {
        String fileHash =  new String(  md.digest(Files.readAllBytes(Paths.get(i.getPath()))));
        long fileLength = i.length();
        File fileUniquePath = i.getAbsoluteFile();
        System.out.println(fileUniquePath + "\t" + fileLength + "\t" + fileHash);
      }
      else
      {
        listFiles(i);
      }
    }
  }
  
  void printFiles()
  {
    for(Map.Entry<Long, List<File>> e : filesGroupedBySize.entrySet())
    {
      for(File i : e.getValue())
      {
        System.out.println(e.getKey() + "\t" + i.getAbsoluteFile());
      }
    }
  }

  public static void main(String[] args) throws Exception
  {
    md = MessageDigest.getInstance("MD5");
    filesGroupedBySize = new HashMap<>();
    String path = args[0];
    minSize = Integer.parseInt(args[1]);
    DuplicateFileFinder obj = new DuplicateFileFinder();
    File file = new File(path);
    obj.groupFiles(file);
    if(args[2].equals("printFiles"))
    {
      obj.printFiles();
    }
    obj.listDuplicates();
  }
}
