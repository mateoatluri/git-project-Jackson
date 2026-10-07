import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.nio.file.Files;
import java.nio.file.Path;


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
            
            System.out.println("----making testFolder/test.txt-----");

            System.out.println(hashFile("testFolder/test.txt"));
            newGit.makeBlob("testFolder/test.txt");

            newGit.addFileEntry("testFolder/test.txt");


        } catch (Exception e) {
            System.out.println("Cannot Run SHA-1 Hash File");
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
            throw new IOException("no such file: " + filePath);
        }

        byte[] fileBytes = Files.readAllBytes(path);

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
        String fileText = br.readLine();
        br.close();

        FileWriter writer = new FileWriter(newBLOB.getPath().toString());
        writer.write(fileText + "\n");
        writer.close();
    }

    public void addFileEntry(String filePath) throws IOException { 
        String hash = hashFile(filePath);

        BufferedReader br = new BufferedReader(new FileReader(index));

        FileWriter writer = new FileWriter(index);

        if (br.readLine() == null) { 
            writer.write(hash + " " + filePath);
        } else { 
            writer.write("\n" + hash + " " + filePath);
        }
        writer.close();
        br.close();
    }
}
