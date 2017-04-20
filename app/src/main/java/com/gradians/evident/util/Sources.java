package com.gradians.evident.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by adamarla on 3/20/17.
 */

public class Sources {

    public Sources(Context context) {
        File vault = init(context);
        try {
            if (vault.exists()) {
                gitPull(vault);
            } else {
                gitClone(vault);
            }
        } catch (Exception e) {
            Log.d("EvidentApp", "Likely NPE " + e.getClass().toString());
        }
    }

    public void gitPull(File vault) {
        Repository repo = null;
        try {
            repo = new FileRepositoryBuilder()
                    .setMustExist(true)
                    .setGitDir(new File(vault, ".git")).build();
        } catch (IOException e) {
            Log.e("EvidentApp", e.getMessage());
        }
        Git git = new Git(repo);
        final PullCommand pullCommand = git.pull().setRemote("master");
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d("EvidentApp", "git pulling...");
                try {
                    pullCommand.call();
                } catch (GitAPIException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean cloned) {
                Log.d("EvidentApp", "Pulled!");
            }
        };
        task.execute();
    }

    public void gitClone(File vault) {
        final CloneCommand cloneCommand = Git.cloneRepository()
                .setURI("https://github.com/adamarla/alt-bank.git")
                .setDirectory(vault);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d("EvidentApp", "git cloning...");
                try {
                    cloneCommand.call();
                } catch (GitAPIException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean cloned) {
                Log.d("EvidentApp", "Cloned!");
            }
        };
        task.execute();
    }

    private File init(Context context) {
        String deviceState = Environment.getExternalStorageState();
        boolean writeable = Environment.MEDIA_MOUNTED.equals(deviceState);

        Log.d("EvidentApp", "Writeable " + writeable);
        if (!writeable) return null;
        return new File(context.getExternalFilesDir(null), "vault");
    }

}

