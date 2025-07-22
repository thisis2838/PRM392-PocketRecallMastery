package com.prm392g2.prmapp.services;

import android.content.Context;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.entities.Learning;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class LearningService {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Context context;
    public LearningService(Context context)
    {
        this.context = context;
    }
    public void getByDeckId(int deckId, Consumer<List<Learning>> callback) {
        executor.execute(() -> {
            List<Learning> result = PRMDatabase.getInstance().learningDao().getByDeckId(deckId);
            if (callback != null) callback.accept(result);
        });
    }

    public void insert(Learning learning, Runnable onComplete) {
        executor.execute(() -> {
            PRMDatabase.getInstance().learningDao().insert(learning);
            if (onComplete != null) onComplete.run();
        });
    }

    public void updateCurrentCardIndex(int learningId, int index, Runnable onComplete) {
        executor.execute(() -> {
            PRMDatabase.getInstance().learningDao().updateCurrentLearningIndex(learningId, index);
            if (onComplete != null) onComplete.run();
        });
    }

    public void updateHardIndexes(int learningId, List<Integer> hardIndexes, Runnable onComplete) {
        executor.execute(() -> {
            PRMDatabase.getInstance().learningDao().updateHardIndexes(learningId, hardIndexes);
            if (onComplete != null) onComplete.run();
        });
    }

    private static LearningService instance;

    public static void initialize(Context context) {
        instance = new LearningService(context);
    }

    public static LearningService getInstance() {
        if (instance == null) throw new RuntimeException("LearningService not initialized");
        return instance;
    }
}
