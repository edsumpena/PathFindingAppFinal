package App.EditParameters;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Scans source code of ftc-app project to find parameters (using github api)
public class ParameterScanner {

    static ContentsService contentsService = new ContentsService();

    final static String repositoryName = "ftc_clueless_rr";
    final static String owner = "buggy213";

    public static void scan() {
        RepositoryService repositoryService = new RepositoryService();
        Repository repo;
        try {
            repo = repositoryService.getRepository(owner, repositoryName);
        }
        catch (IOException e) {
            System.err.println("Unable to get repository - check internet connection?");
            return;
        }

        List<RepositoryContents> sourceFiles = recursiveSearchByFiletype(repo);
        int a = 5;

    }



    // Searches a repositories contents recursively by filetype using DFS
    private static List<RepositoryContents> recursiveSearchByFiletype(Repository repo) {
        List<RepositoryContents> sourceFiles = new ArrayList<>();
        List<RepositoryContents> contents = new ArrayList<>();
        LinkedList<RepositoryContents> directoriesQueue = new LinkedList<>();
        try {
            contents = contentsService.getContents(() -> String.valueOf(repo.generateId()));
        }
        catch (IOException e) {
            System.err.println("Unable to get contents of repository");
            return contents;
        }
        do {
            for (RepositoryContents content : contents) {
                // Check if it is a source code file
                if (content.getName().endsWith(".java")) {
                    sourceFiles.add(content);
                }

                if (content.getType().equals("dir")) {
                    directoriesQueue.push(content);
                }
            }

            // Check next subdirectory in queue
            RepositoryContents subdirectory = directoriesQueue.pop();
            try {
                contents = contentsService.getContents(() -> String.valueOf(repo.generateId()), subdirectory.getPath());
            }
            catch (IOException e) {
                System.err.println("Unable to get contents of subdirectory: " + subdirectory.getName()
                        + " " + subdirectory.getPath());

                return contents;
            }
        }
        while (!directoriesQueue.isEmpty());
        return sourceFiles;
    }
}
