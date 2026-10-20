import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.text.AbstractDocument.Content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.StringBuilder;


public class Git {
    
    private File git;
    private File objects;
    private File index;
    private File HEAD;


    public File getGit() {
        return git;
    }

    public void setGit(File git) {
        this.git = git;
    }

    public File getObjects() {
        return objects;
    }

    public void setObjects(File objects) {
        this.objects = objects;
    }

    public File getIndex() {
        return index;
    }

    public void setIndex(File index) {
        this.index = index;
    }

    public File getHEAD() {
        return HEAD;
    }

    public void setHEAD(File head) {
        HEAD = head;
    }

    public static void main(String[] args) {
        Git newGit = new Git();
        

        try {

            System.out.println("----making test.txt-----");

            System.out.println(hashFile("test.txt"));
            newGit.makeBlob("test.txt");

            newGit.addFileEntry("test.txt");

            /////////

            System.out.println("----making Hello.txt-----");

            System.out.println(hashFile("Hello.txt"));
            newGit.makeBlob("Hello.txt");

            newGit.addFileEntry("Hello.txt");

            /////////
            
            System.out.println("----making bigfile.txt-----");

            System.out.println(hashFile("bigfile.txt"));
            newGit.makeBlob("bigfile.txt");

            newGit.addFileEntry("bigfile.txt");

            /////////

            System.out.println("----making testFolder/test.txt-----");
            
            System.out.println(hashFile("testFolder/test.txt"));
            newGit.makeBlob("testFolder/test.txt");

            newGit.addFileEntry("testFolder/test.txt");
            
            /////////
            
            // System.out.println("----making emptyFile.txt-----");

            // System.out.println(hashFile("emptyFile.txt"));

            // newGit.makeBlob("emptyFile.txt"); 

            // newGit.addFileEntry("emptyFile.txt");

            // System.out.println("----making emptyFile2.txt-----");

            // System.out.println(hashFile("emptyFile2.txt"));

            // newGit.makeBlob("emptyFile2.txt");
            

            // newGit.addFileEntry("emptyFile2.txt");

            //System.out.println("----making emptyFolder-----");

            //System.out.println(hashFile("emptyFolder"));
            //newGit.makeBlob("testFolder/innerFolder/a.txt");

            //newGit.addFileEntry("testFolder/innerFolder/a.txt");

            



            ////////////

            System.out.println("----making testFolder/innerFolder/a.txt-----");

            System.out.println(hashFile("testFolder/innerFolder/a.txt"));
            newGit.makeBlob("testFolder/innerFolder/a.txt");

            newGit.addFileEntry("testFolder/innerFolder/a.txt");

            //////////////////////////

            System.out.println("-------testing the working list------");
            ArrayList<GitObject> testWorkingList = newGit.createWorkingList(); 

            for (int i = 0; i < testWorkingList.size(); i++) {
                GitObject testItem = testWorkingList.get(i);
                System.out.println("blob " + testItem.getHash() + " " + testItem.getPath());
            }

            //////////////////////////
            
            System.out.println("-------testing createTreeFromIndex----------");
            String rootDir = newGit.createTreeFromIndex();
            System.out.println("root: " + rootDir);
        

            

            // System.out.println("-----testing createTree------");
            // ArrayList<GitObject> testList = new ArrayList<GitObject>();
            
            // // adding hello.txt
            // testList.add(new BLOB(hashFile("Hello.txt"), "Hello.txt"));

            // // adding bigfile.txt
            // testList.add(new BLOB(hashFile("bigfile.txt"), "bigfile.txt"));

            // // adding test.txt
            // testList.add(new BLOB(hashFile("test.txt"), "test.txt"));

  

            // String root = newGit.createTree(testList, "");
            // System.out.println("root tree: " + root);
            

            
 


        } catch (Exception e) {
            System.out.println("Cannot Run SHA-1 Hash File" + " " + e);
        }

    }

    public Git() { 
        gitInitialize();
    }

    public void gitInitialize() { 
        try {
            int exisitingFileCount = 0;

            git = new File("git/");
            if (!git.mkdir()) { 
                exisitingFileCount++;
            }
            git.mkdir();

            objects = new File(git, "objects/");
            if (!objects.mkdir()) { 
                exisitingFileCount++;
            }

            index = new File(git, "index");
            if (!index.createNewFile()) { 
                exisitingFileCount++;
            }
            HEAD = new File(git, "HEAD");
            if (!HEAD.createNewFile()) { 
                exisitingFileCount++;
            }

            if (exisitingFileCount == 4) { 
                System.out.println("Git Repository Already Exists");
            } else { 
                System.out.println("Git Repository Created");
            }

        } catch (IOException e) { 
            System.out.println("File error: " + e.getMessage());
        }
    }

    public static String hashFile(String filePath) throws IOException {
        // TODO (FH-4): read the whole file, digest it, convert the bytes to hex

        Path path = Path.of(filePath);
        if (!Files.isRegularFile(path)) {
            throw new IOException("No such file: " + filePath);
        }

        byte[] fileBytes = Files.readAllBytes(path);

        return (hashText(fileBytes));

        // MessageDigest digest;

        // try {
        //     digest = MessageDigest.getInstance("SHA-1");
        // } catch (NoSuchAlgorithmException e) {
        //     throw new IllegalStateException("SHA-1 is not available", e);
        // }

        // byte[] hashByteArray = digest.digest(fileBytes);

        // return HexFormat.of().formatHex(hashByteArray);
    }

    public static String hashText(byte[] fileBytes) throws IOException {
        
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is not available", e);
        }

        byte[] hashByteArray = digest.digest(fileBytes);

        return HexFormat.of().formatHex(hashByteArray);
    }

    public void makeBlob(String filePath) throws IOException { 
        
        String hash = Git.hashFile(filePath);

        File newBLOB = new File(objects, hash);
        newBLOB.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(filePath));

        FileWriter writer = new FileWriter(newBLOB);

        int character = br.read();

        if (character != -1){

        }

        while (character != -1) {
            writer.write((char) character);
            character = br.read();
        }

        br.close();
        writer.close();
        
        // StringBuilder fileContents = new StringBuilder();
        // String fileLine = br.readLine();
        
        

        // while (fileLine != null) {
        //     fileContents.append(fileLine);

        //     String nextFileLine = br.readLine();
        //     if (nextFileLine != null) {
        //         fileContents.append("\n");
        //     }

        //     fileLine = nextFileLine;
        // }

        // br.close();

        // String finalContents = fileContents.toString();

        // FileWriter writer = new FileWriter(newBLOB);
        // writer.write(finalContents);
        //writer.close();
    }

    public void addFileEntry(String filePath) throws IOException { 
        String hash = hashFile(filePath);


        StringBuilder fileContents = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(index));

        String newContents = hash + " " + filePath;

       
        String contents = br.readLine();

        while (contents != null) {
            if (contents.equals(newContents)) {
                br.close();
                return;
            } else {
                fileContents.append(contents + "\n");
                contents = br.readLine();
            }
        }

        
        fileContents.append(newContents);

        String finalContents = fileContents.toString();
        
        FileWriter writer = new FileWriter(index);
        writer.write(finalContents);

        writer.close();
        br.close();

       
    }


    public String createTree(ArrayList<GitObject> workingList, String dirPath) throws IOException {
        
        ArrayList<GitObject> children = new ArrayList<GitObject>();

        // this for loop will compare the last / in each path inside the working list
        // it will take the substring of that path from its start to that last /
        // if that substring equals the dirPath, then it must be a child
        // all children get added to the children array
        
        for (int i = 0; i < workingList.size(); i++) {
            GitObject currentObject = workingList.get(i);
            int objectLastSlash = currentObject.getPath().lastIndexOf("/");

            String objectFolderPath;

            if (objectLastSlash == -1) {
                objectFolderPath = "";
            } else {
                objectFolderPath = currentObject.getPath().substring(0, objectLastSlash);
            }

            
            if (dirPath.equals(objectFolderPath)) {
                children.add(currentObject);
            }
            
        }

        StringBuilder directoryContents = new StringBuilder();

        for (int j = 0; j < children.size(); j++) {
            GitObject child = children.get(j);
            
            String objectType = getObjectType(child); // see method below
            String objectHash = child.getHash(); // hash of child
            String objectPath = child.getPath(); // path of child

            String childName = objectPath.substring(objectPath.lastIndexOf("/") + 1); // gives just the local path, not absolute one
            
            if (j == 0) { // if, then used to account for new lines between appends
                directoryContents.append(objectType + " " + objectHash + " " + childName);
            } else {
                directoryContents.append("\n" + objectType + " " + objectHash + " " + childName);
            }
        }

        //below lines get the hash of this whole file for the name
        String treeContents = directoryContents.toString(); // turning the stringbuilder into a string to then hash
        String hashedFile = hashText(treeContents.getBytes());

        // below lines will actually write into objects folder
        File newFile = new File(objects, hashedFile);
        FileWriter writer = new FileWriter(newFile);
        writer.write(treeContents);
        writer.close();

        return hashedFile;



    }

    private String getObjectType(GitObject unknownObject) {
        if (unknownObject instanceof BLOB) {
            return "blob";
        } else {
            return "tree";
        }
    }


    public ArrayList<GitObject> createWorkingList() throws IOException {
        ArrayList<String> toSort = new ArrayList<String>();
        
        

        BufferedReader br = new BufferedReader(new FileReader(index));
        String contents = br.readLine();

        while (contents != null) {

            if (!(contents.isEmpty())) { /// for empty lines
                String[] result = contents.split(" ", 2); // in case of any weird situations like ex if the path had a space in it
            
                String hash = result[0];
                String path = result[1];

                int numberOfSlash = (path.split("/").length) - 1;

                toSort.add(numberOfSlash + " " + path + " " + hash);

                //newList.add(new BLOB(hash, path)); // see the BLOB.java file where I made blob objects
            }
        
            contents = br.readLine();
            
        }

        br.close();

        //sorted by paths in order of deepest (most number of slashes) at tippity top
        Collections.sort(toSort, Collections.reverseOrder());

        //now built into a new list bc it needs to be hash first then path
        // originally did path then hash just for the Collections to be able to sort it
        
        
        ArrayList<GitObject> newList = new ArrayList<GitObject>();
        for (int i = 0; i < toSort.size(); i++) {
            String line = toSort.get(i);
            int indexOfFirstSpace = line.indexOf(" ");
            int indexOfLastSpace = line.lastIndexOf(" ");

            String realPath = line.substring(indexOfFirstSpace + 1, indexOfLastSpace);
            String realHash = line.substring(indexOfLastSpace + 1);

            BLOB toAdd = new BLOB(realHash, realPath);
            newList.add(toAdd);
        }

        return newList;

    }

    public String createTreeFromIndex() throws IOException {
        ArrayList<GitObject> workingList = createWorkingList();
        return recursiveBuildTree(workingList, "");

    }

    public String recursiveBuildTree(ArrayList<GitObject> workingList, String currentPath) throws IOException {

        String parentPathStarter;
        if (currentPath.isEmpty()) {
            parentPathStarter = "";
        } else {
            parentPathStarter = currentPath + "/";
        }
        // we want the above thing bc then when we check for children directories we can see
        // if it has a slash in it, bc then like the presence of a slash would indicate another nested directory
        // but if we just kept current path as is, then there would always be a / at the beginning, and it
        // would just be a lil messier to vibe with

        boolean deeperSubDir = true;

        // not base case

        while (deeperSubDir == true) {
            
            deeperSubDir = false;
            
            for (int i = 0; i < workingList.size(); i++) {
                String workingListPath = workingList.get(i).getPath();

                //the code below basiclaly checks if the thing in workinglist is related by path to our currentPath
                // if not, then it'll just be skipped and not thought about
                // eventually at the very end, when all things are trees or blobs for the root, none of them will be related (kinda)
                // so then it'll just skip all the way, and since at the beginning i set deeperSubDir to be false, the next 
                // iteration the loop will end
                if (workingListPath.startsWith(parentPathStarter)) {
                    String restOfPath = workingListPath.substring(parentPathStarter.length());

                    int slashIndex = restOfPath.indexOf("/");
                    // ^ if u think about it, if there is a slash present, we know there is a deeper subDir

                    if (slashIndex != -1) {
                        String subDirectoryFullName = parentPathStarter + restOfPath.substring(0, slashIndex);

                        // since we know we have a deeper subDir, we want to tree-ify that one first
                        // so we recursive call

                        String subDirectoryHash = recursiveBuildTree(workingList, subDirectoryFullName);

                        // we not only want to build this tree, but we now have to get rid of the babies from the WL

                        for (int j = workingList.size() - 1; j >= 0; j--) {
                            // u gotta start from left bc you're removing, so you don't want the indices to be messed up
                            
                            String parentDir = getParentDirectory(workingList.get(j).getPath());
                            if (parentDir.equals(subDirectoryFullName)) {
                                workingList.remove(j);
                            }
                        }

                        Directory newDir = new Directory(subDirectoryHash, subDirectoryFullName);
                        workingList.add(newDir);
                        deeperSubDir = true;
                        break; 
                        // ^^ gets out of the loops to start this whole while loop again with reset i and j variables

                        
                    }
                }
            }

        }

        

        //base case
        // base case happens if the given path in the parameter is the deepest path possible
        // when this is true, we simply create the tree

        return createTree(workingList, currentPath);
    }

    public String getParentDirectory(String path) {
        int indexOfSlash = path.lastIndexOf("/");
        if (indexOfSlash != -1) {
            return path.substring(0, indexOfSlash);
        } else {
            return "";
        }
    }



}
